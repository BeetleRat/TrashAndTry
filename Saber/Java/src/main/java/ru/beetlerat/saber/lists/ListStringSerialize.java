package main.java.ru.beetlerat.saber.lists;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ListStringSerialize extends ListRand {

    private static final int NO_LINK = -1, BROKEN_LINK = -2;
    NodeDTO[] nodesDTO;

    @Override
    public void Serialize(FileOutputStream s) {
        nodesDTO = new NodeDTO[Count];
        setDTOPointers();
        writeDTOToFile(s);
        nodesDTO = null;
    }

    private void setDTOPointers() {
        ListNode saveElement = Head;
        ListNode linkElement = Head;
        int saveElementIndex = 0;
        int linkElementIndex = 0;
        while (saveElementIndex < Count) {
            if (saveElement.Rand == null) {
                nodesDTO[saveElementIndex] =
                        new NodeDTO(
                                saveElementIndex,
                                saveElement.Data,
                                saveElementIndex - 1,
                                saveElement.Next == null ? -1 : saveElementIndex + 1,
                                NO_LINK
                        );
                saveElementIndex++;
                saveElement = saveElement.Next;
            } else {
                if (linkElementIndex >= Count) {
                    linkElementIndex = 0;
                    linkElement = Head;
                    saveElement.Rand = null;
                }
                if (linkElement == saveElement.Rand) {
                    nodesDTO[saveElementIndex] =
                            new NodeDTO(
                                    saveElementIndex,
                                    saveElement.Data,
                                    saveElementIndex - 1,
                                    saveElement.Next == null ? -1 : saveElementIndex + 1,
                                    linkElementIndex
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

    private void writeDTOToFile(FileOutputStream s) {
        try {
            BufferedWriter writeFileBuffer =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    s, StandardCharsets.UTF_8)
                    );

            writeFileBuffer.write("Count: " + nodesDTO.length + "\n");
            for (NodeDTO struct : nodesDTO) {
                writeFileBuffer.write(struct.toString());
            }

            writeFileBuffer.close();
        } catch (IOException e) {
            System.out.println("Write to file exception: " + e);
        }
    }

    @Override
    public void Deserialize(FileInputStream s) {
        clear();
        nodesDTO = null;
        ReadNodesDTOFromFile(s);
        setPrevNextPointers(0);
        setRandPointers();
        nodesDTO = null;
    }

    private void setPrevNextPointers(int currentIndex) {
        if (currentIndex >= Count) {
            return;
        }
        ListNode newNode = new ListNode();
        if (currentIndex == 0) {
            Head = newNode;
        } else {
            newNode.Prev = Tail;
            Tail.Next = newNode;
        }
        Tail = newNode;
        newNode.Data = nodesDTO[currentIndex].Data;
        setPrevNextPointers(currentIndex + 1);
    }

    private void setRandPointers() {
        ListNode elementWithBrokenLink = Head;
        ListNode currentElement = Head;
        int elementWithBrokenLinkIndex = 0;
        int currentElementIndex = 0;
        while (elementWithBrokenLinkIndex < Count) {
            if (nodesDTO[elementWithBrokenLinkIndex].RandID != NO_LINK) {
                if (currentElementIndex == nodesDTO[elementWithBrokenLinkIndex].RandID) {
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

    private void ReadNodesDTOFromFile(FileInputStream s) {
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
                if (nodesDTO == null && oneLine.contains("Count: ")) {
                    int count = Integer.parseInt(oneLine.substring(oneLine.indexOf(":") + 1).trim());
                    nodesDTO = new NodeDTO[count];
                    Count = count;
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
            // Если корректно спарсилось, сохраняем в массив DTO
            if (newNode != null && newNode.ID >= 0 && newNode.ID < nodesDTO.length) {
                nodesDTO[newNode.ID] = newNode;
            }
            // Очищаем строку JSON
            json.setLength(0);
            return false;
        }
        return true;
    }

    private NodeDTO parseFromJSON(String json) {
        String[] lines = json.split("\n");
        if (lines.length != 6) {
            return null;
        }
        NodeDTO newNode =
                new NodeDTO(
                        parseLineToInt(lines, 1),
                        parseLineToString(lines, 2),
                        parseLineToInt(lines, 3),
                        parseLineToInt(lines, 4),
                        parseLineToInt(lines, 5)
                );

        return newNode;
    }

    private static String parseLineToString(String[] lines, int x) {
        return lines[x].substring(lines[x].indexOf(':') + 2);
    }

    private static int parseLineToInt(String[] lines, int lineNumber) {
        return Integer.parseInt(parseLineToString(lines, lineNumber).trim());
    }

}
