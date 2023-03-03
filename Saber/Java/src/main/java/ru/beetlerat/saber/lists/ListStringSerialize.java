package main.java.ru.beetlerat.saber.lists;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ListStringSerialize extends ListRand {

    private static final int NO_LINK = -1, BROKEN_LINK = -2;
    NodeDTO[] nodesDTO;

    public class NodeDTO {
        public int ID; // Добавлено для поддержания ссылочной целостности при строковой реализации
        public int PrevID;
        public int NextID;
        public int RandID; // произвольный элемент внутри списка
        public String Data;

        private ListNode originalNode;

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

    @Override
    public void Serialize(FileOutputStream s) {
        nodesDTO = new NodeDTO[Count];
        setDTOPrevNextPointers(Head, 0);
        setRandPointers();
        writeSaveStructsToFile(s);
        nodesDTO = null;
    }

    private void setDTOPrevNextPointers(ListNode currentNode, int currentIndex) {
        if (currentNode == null) {
            return;
        }
        nodesDTO[currentIndex] =
                new NodeDTO(
                        currentIndex,
                        currentNode.Data,
                        currentIndex - 1,
                        currentNode.Next == null ? NO_LINK : currentIndex + 1,
                        currentNode.Rand == null ? NO_LINK : BROKEN_LINK);
        setDTOPrevNextPointers(currentNode.Next, currentIndex + 1);
    }

    private void setRandPointers() {
        ListNode elementWithBrokenLink = Head;
        ListNode currentElement = Head;
        int elementWithBrokenLinkIndex = 0;
        int currentElementIndex = 0;
        while (elementWithBrokenLinkIndex < Count) {
            if (nodesDTO[elementWithBrokenLinkIndex].RandID == BROKEN_LINK) {
                if (currentElement == elementWithBrokenLink.Rand) {
                    nodesDTO[elementWithBrokenLinkIndex].RandID = currentElementIndex;
                    currentElement = Head;
                    currentElementIndex = 0;
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

    private void writeSaveStructsToFile(FileOutputStream s) {
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
        restoreRandomLinks();
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

    private void restoreRandomLinks() {
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
            StringBuilder sevenLines = new StringBuilder();
            boolean isJsonFound = false;
            while ((oneLine = readFileBuffer.readLine()) != null) {
                if (nodesDTO == null && oneLine.contains("Count: ")) {
                    int count = Integer.parseInt(oneLine.substring(oneLine.indexOf(":") + 1).trim());
                    nodesDTO = new NodeDTO[count];
                    Count = count;
                } else {
                    if (oneLine.equals("{")) {
                        isJsonFound = true;
                    }
                    if (isJsonFound) {
                        isJsonFound = accumulateJSON(sevenLines, oneLine);
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

    private boolean accumulateJSON(StringBuilder sevenLines, String oneLine) {
        sevenLines.append(oneLine).append("\n");
        if (sevenLines.toString().split("\n").length >= 6) {
            NodeDTO newNode = parseFromJSON(sevenLines.toString());
            if (newNode != null && newNode.ID >= 0 && newNode.ID < nodesDTO.length) {
                nodesDTO[newNode.ID] = newNode;
            }
            sevenLines.setLength(0);
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
