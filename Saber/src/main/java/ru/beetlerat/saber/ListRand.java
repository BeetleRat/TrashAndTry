package main.java.ru.beetlerat.saber;

import java.io.*;
import java.nio.charset.StandardCharsets;

// Класс для записи объекта в файл
record NodeDTO(int ID, String data, int prevID, int nextID, int randID) {
    public static final int NO_LINK = -1;

    @Override
    public String toString() {
        StringBuilder json = new StringBuilder();

        json.append("{\n");
        json.append("   \"ID\": ").append(ID).append("\n");
        json.append("   \"Data\": ").append(data).append("\n");
        json.append("   \"PreviousID\": ").append(prevID).append("\n");
        json.append("   \"NextID\": ").append(nextID).append("\n");
        json.append("   \"RandID\": ").append(randID).append("\n");
        json.append("}\n");

        return json.toString();
    }
}

// Элемент списка
class ListNode {
    public ListNode Prev;
    public ListNode Next;
    public ListNode Rand; // произвольный элемент внутри списка
    public String Data;
}


public class ListRand {
    public ListNode Head;
    public ListNode Tail;
    public int Count;

    public void clear() {
        Count = 0;
        Head = null;
        Tail = null;
    }

    public void Serialize(FileOutputStream s) {
        try {
            BufferedWriter writeFileBuffer = new BufferedWriter(
                    new OutputStreamWriter(s, StandardCharsets.UTF_8)
            );

            writeFileBuffer.write("Count: " + Count + "\n");
            executeSerialize(writeFileBuffer);

            writeFileBuffer.close();
        } catch (IOException e) {
            System.out.println("Write to file exception: " + e);
        }

    }

    public void Deserialize(FileInputStream s) {
        clear();
        int[] randLinks = readListFromFile(s);
        setRandPointers(randLinks);
    }

    private void executeSerialize(BufferedWriter writeFileBuffer) throws IOException {
        ListNode saveElement = Head;
        int saveElementIndex = 0;
        ListNode linkElement = Head;
        int linkElementIndex = 0;
        while (saveElementIndex < Count) {

            if (saveElement.Rand == null) {
                NodeDTO nodeDTO = new NodeDTO(
                        saveElementIndex, saveElement.Data, saveElementIndex - 1,
                        saveElement.Next == null ? -1 : saveElementIndex + 1, NodeDTO.NO_LINK
                );

                // Записываем DTO в файл
                writeFileBuffer.write(nodeDTO.toString());

                saveElementIndex++;
                saveElement = saveElement.Next;
            } else {
                // Удаление битой ссылки
                if (linkElementIndex >= Count) {
                    linkElementIndex = 0;
                    linkElement = Head;
                    saveElement.Rand = null;
                }

                if (linkElement == saveElement.Rand) {
                    NodeDTO nodeDTO = new NodeDTO(
                            saveElementIndex, saveElement.Data, saveElementIndex - 1,
                            saveElement.Next == null ? -1 : saveElementIndex + 1, linkElementIndex
                    );

                    // Записываем DTO в файл
                    writeFileBuffer.write(nodeDTO.toString());

                    saveElementIndex++;
                    saveElement = saveElement.Next;
                    linkElement = Head;
                    linkElementIndex = 0;
                } else {
                    linkElementIndex++;
                    linkElement = linkElement.Next;
                }
            }
        }
    }

    private int[] readListFromFile(FileInputStream s) {
        // Массив ссылок на rand в каждом элементе
        int[] randLinks = null;
        try {
            BufferedReader readFileBuffer = new BufferedReader(
                    new InputStreamReader(s, StandardCharsets.UTF_8)
            );

            String oneLine;
            StringBuilder json = new StringBuilder();
            boolean isJsonFound = false;
            while ((oneLine = readFileBuffer.readLine()) != null) {
                // Файл должен начинастья с "Count: "
                if (randLinks == null) {
                    if (oneLine.contains("Count: ")) {
                        int count = Integer.parseInt(oneLine.substring(oneLine.indexOf(":") + 1).trim());
                        randLinks = new int[count];
                        Count = count;
                    }
                } else {
                    // Проверка корректности сохраненного JSON
                    if (oneLine.equals("{")) {
                        isJsonFound = true;
                    }
                    if (isJsonFound) {
                        isJsonFound = accumulateJSON(json, oneLine, randLinks);
                    }
                    if (oneLine.equals("}")) {
                        isJsonFound = false;
                    }
                }
            }
            readFileBuffer.close();
        } catch (IOException e) {
            System.out.println("Read from file exception: " + e);
        }
        return randLinks;
    }

    private void setRandPointers(int[] randLinks) {
        ListNode elementWithBrokenLink = Head;
        int elementWithBrokenLinkIndex = 0;
        ListNode currentElement = Head;
        int currentElementIndex = 0;

        while (elementWithBrokenLinkIndex < Count) {

            if (randLinks[elementWithBrokenLinkIndex] != NodeDTO.NO_LINK) {

                if (currentElementIndex == randLinks[elementWithBrokenLinkIndex]) {
                    elementWithBrokenLink.Rand = currentElement;

                    currentElement = Head;
                    currentElementIndex = 0;

                    elementWithBrokenLinkIndex++;
                    elementWithBrokenLink = elementWithBrokenLink.Next;
                } else {
                    currentElementIndex++;
                    currentElement = currentElement.Next;
                }
            } else {
                elementWithBrokenLink = elementWithBrokenLink.Next;
                elementWithBrokenLinkIndex++;
            }
        }
    }

    private boolean accumulateJSON(StringBuilder json, String oneLine, int[] randLinks) {
        // Накапливаем строки в JSON
        json.append(oneLine).append("\n");
        // Если в JSON 6 и больше строк
        if (json.toString().split("\n").length >= 6) {
            // Парсим строковый JSON в DTO
            NodeDTO newNode = parseFromJSON(json.toString());
            // Если корректно спарсилось, сохраняем без ссылки Rand
            if (newNode != null && newNode.ID() >= 0 && newNode.ID() < randLinks.length) {
                addWithoutRandLink(newNode, randLinks);
            }
            // Очищаем строку JSON
            json.setLength(0);
            return false;
        }
        return true;
    }

    // Добавить элемент в список при этом не восстанавливая rand ссылку
    private void addWithoutRandLink(NodeDTO nodeDTO, int[] randLinks) {
        ListNode newNode = new ListNode();
        newNode.Data = nodeDTO.data();
        if (nodeDTO.ID() == 0) {
            Head = newNode;
        } else {
            newNode.Prev = Tail;
            Tail.Next = newNode;
        }
        Tail = newNode;
        randLinks[nodeDTO.ID()] = nodeDTO.randID();
    }

    private NodeDTO parseFromJSON(String json) {
        String[] lines = json.split("\n");
        if (lines.length != 6) {
            return null;
        }

        return new NodeDTO(
                parseLineToInt(lines, 1),
                parseLineToString(lines, 2),
                parseLineToInt(lines, 3),
                parseLineToInt(lines, 4),
                parseLineToInt(lines, 5)
        );
    }

    private static String parseLineToString(String[] lines, int x) {
        return lines[x].substring(lines[x].indexOf(':') + 2);
    }

    private static int parseLineToInt(String[] lines, int lineNumber) {
        return Integer.parseInt(parseLineToString(lines, lineNumber).trim());
    }

    private StringBuilder getListAsStringBuilder(ListNode currentNode,
                                                 StringBuilder dataCollector,
                                                 int currentID) {
        if (currentNode == null) {
            return dataCollector;
        }

        dataCollector
                .append("{ID: ")
                .append(currentID)
                .append(", Data: ")
                .append(currentNode.Data)
                .append("; Rand: ")
                .append(currentNode.Rand == null ? "null" : currentNode.Rand.Data)
                .append("}, ");

        return getListAsStringBuilder(currentNode.Next, dataCollector, currentID + 1);
    }

    @Override
    public String toString() {
        StringBuilder outputString = new StringBuilder().append("[");
        outputString = getListAsStringBuilder(Head, outputString, 0);
        if (outputString.length() > 2) {
            outputString.setLength(outputString.length() - 2);
        }
        outputString.append("]");
        return outputString.toString();
    }
}
