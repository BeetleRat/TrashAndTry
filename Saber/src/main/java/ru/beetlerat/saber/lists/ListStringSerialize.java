package main.java.ru.beetlerat.saber.lists;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ListStringSerialize extends ListRand {


    private static final int NO_LINK = -1;
    int[] randLinks;

    @Override
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
                                NO_LINK
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
                    writeFileBuffer.write(new NodeDTO(
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

    @Override
    public void Deserialize(FileInputStream s) {
        clear();
        randLinks = null;
        readListFromFile(s);
        setRandPointers();
        randLinks = null;
    }

    private void setRandPointers() {
        ListNode elementWithBrokenLink = Head;
        int elementWithBrokenLinkIndex = 0;
        ListNode currentElement = Head;
        int currentElementIndex = 0;

        while (elementWithBrokenLinkIndex < Count) {

            if (randLinks[elementWithBrokenLinkIndex] != NO_LINK) {

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

    private void readListFromFile(FileInputStream s) {
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
                        isJsonFound = accumulateJSON(json, oneLine);
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
    }

    private boolean accumulateJSON(StringBuilder json, String oneLine) {
        // Накапливаем строки в JSON
        json.append(oneLine).append("\n");
        // Если в JSON 6 и больше строк
        if (json.toString().split("\n").length >= 6) {
            // Парсим строковый JSON в DTO
            NodeDTO newNode = parseFromJSON(json.toString());
            // Если корректно спарсилось, сохраняем без ссылки Rand
            if (newNode != null && newNode.ID >= 0 && newNode.ID < randLinks.length) {
                addWithoutRandLink(newNode);
            }
            // Очищаем строку JSON
            json.setLength(0);
            return false;
        }
        return true;
    }

    private void addWithoutRandLink(NodeDTO nodeDTO) {
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

}
