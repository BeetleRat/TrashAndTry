package main.java.ru.beetlerat.saber.lists;

import java.io.*;
import java.nio.charset.StandardCharsets;

// Класс для записи объекта в файл
class NodeDTO {
    public static final int NO_LINK = -1;
    public int ID; // Добавлено для поддержания ссылочной целостности при строковой реализации
    public int PrevID;
    public int NextID;
    public int RandID;
    public String Data;

    public NodeDTO(int ID, String data, int prevID, int nextID, int randID) {
        this.ID = ID;
        this.PrevID = prevID;
        this.NextID = nextID;
        this.RandID = randID;
        this.Data = data;
    }

    public String toString() {
        StringBuilder json = new StringBuilder();

        json.append("{\n");
        json.append("   \"ID\": ").append(ID).append("\n");
        json.append("   \"Data\": ").append(Data).append("\n");
        json.append("   \"PreviousID\": ").append(PrevID).append("\n");
        json.append("   \"NextID\": ").append(NextID).append("\n");
        json.append("   \"RandID\": ").append(RandID).append("\n");
        json.append("}\n");

        return json.toString();
    }
}

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

    public void Serialize(FileOutputStream s) {
        try {
            BufferedWriter writeFileBuffer =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    s, StandardCharsets.UTF_8)
                    );

            writeFileBuffer.write("Count: " + Count + "\n");
            executeSerialize(writeFileBuffer);

            writeFileBuffer.close();
        } catch (IOException e) {
            System.out.println("Write to file exception: " + e);
        }

    }

    private void executeSerialize(BufferedWriter writeFileBuffer) throws IOException {
        ListNode saveElement = Head;
        int saveElementIndex = 0;
        ListNode linkElement = Head;
        int linkElementIndex = 0;
        while (saveElementIndex < Count) {

            if (saveElement.Rand == null) {

                // Сразу записываем DTO в файл
                writeFileBuffer.write(
                        new NodeDTO(
                                saveElementIndex,
                                saveElement.Data,
                                saveElementIndex - 1,
                                saveElement.Next == null ? -1 : saveElementIndex + 1,
                                NodeDTO.NO_LINK
                        ).toString()
                );

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

                    // Сразу записываем DTO в файл
                    writeFileBuffer.write(
                            new NodeDTO(
                                    saveElementIndex,
                                    saveElement.Data,
                                    saveElementIndex - 1,
                                    saveElement.Next == null ? -1 : saveElementIndex + 1,
                                    linkElementIndex
                            ).toString()
                    );

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

    public void Deserialize(FileInputStream s) {
        clear();
        int[] randLinks = readListFromFile(s);
        setRandPointers(randLinks);
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

    private int[] readListFromFile(FileInputStream s) {
        int[] randLinks = null;
        try {
            BufferedReader readFileBuffer =
                    new BufferedReader(
                            new InputStreamReader(
                                    s, StandardCharsets.UTF_8)
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

    private boolean accumulateJSON(StringBuilder json, String oneLine, int[] randLinks) {
        // Накапливаем строки в JSON
        json.append(oneLine).append("\n");
        // Если в JSON 6 и больше строк
        if (json.toString().split("\n").length >= 6) {
            // Парсим строковый JSON в DTO
            NodeDTO newNode = parseFromJSON(json.toString());
            // Если корректно спарсилось, сохраняем без ссылки Rand
            if (newNode != null && newNode.ID >= 0 && newNode.ID < randLinks.length) {
                addWithoutRandLink(newNode, randLinks);
            }
            // Очищаем строку JSON
            json.setLength(0);
            return false;
        }
        return true;
    }

    private void addWithoutRandLink(NodeDTO nodeDTO, int[] randLinks) {
        ListNode newNode = new ListNode();
        newNode.Data = nodeDTO.Data;
        if (nodeDTO.ID == 0) {
            Head = newNode;
        } else {
            newNode.Prev = Tail;
            Tail.Next = newNode;
        }
        Tail = newNode;
        randLinks[nodeDTO.ID] = nodeDTO.RandID;
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

    public void clear() {
        clear(Head);
        Count = 0;
        Head = null;
    }

    private void clear(ListNode currentNode) {
        if (currentNode == null) {
            return;
        }
        if (currentNode == Tail) {
            Tail = currentNode.Prev;
        }
        clear(currentNode.Next);
        currentNode.Next = null;
        currentNode.Rand = null;
    }

    private StringBuilder getData(ListNode currentNode, StringBuilder dataCollector, int currentID) {
        if (currentNode == null) {
            return dataCollector;
        }
        dataCollector.append("{ID: ").append(currentID).append(", Data: ").append(currentNode.Data).append("; Rand: ").append(currentNode.Rand == null ? "null" : currentNode.Rand.Data).append("}, ");
        return getData(currentNode.Next, dataCollector, currentID + 1);
    }

    public String toString() {
        StringBuilder outputString = new StringBuilder().append("[");
        outputString = getData(Head, outputString, 0);
        if (outputString.length() > 2) {
            outputString.setLength(outputString.length() - 2);
        }
        outputString.append("]");
        return outputString.toString();
    }
}
