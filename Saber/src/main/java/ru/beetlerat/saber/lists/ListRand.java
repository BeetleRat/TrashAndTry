package main.java.ru.beetlerat.saber.lists;

import java.io.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class ListRand {
    public ListNode Head;
    public ListNode Tail;
    public int Count;

    public abstract void Serialize(FileOutputStream s);

    public abstract void Deserialize(FileInputStream s);

    public void add(String data) {
        ListNode node = new ListNode();
        node.Data = data;
        if (Head == null) {
            Head = node;
            Count = 0;
        } else {
            Tail.Next = node;
            node.Prev = Tail;
        }
        Tail = node;
        Count++;
    }

    public void add(String data, boolean recalculateRandomPointer) {
        add(data);
        if (recalculateRandomPointer) {
            recalculateRandomPointer();
        }
    }

    public void add(String... data) {
        for (String oneString : data) {
            add(oneString);
        }
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

    public ListNode get(int index) {
        return getNodeByIndex(Head, index);
    }

    public boolean remove(int index) {
        ListNode removeNode = getNodeByIndex(Head, index);
        if (removeNode != null) {
            Count--;
            if (removeNode == Head) {
                Head = removeNode.Next;
            } else {
                removeNode.Prev = removeNode.Next;
            }
            if (removeNode == Tail) {
                Tail = removeNode.Prev;
            }
            removeNode = null;
            return true;
        }
        return false;
    }

    public void set(int index, String newData) {
        ListNode updatedNode = getNodeByIndex(Head, index);
        if (updatedNode != null) {
            updatedNode.Data = newData;
        }
    }

    public void recalculateRandomPointer() {
        if (Head == null) {
            return;
        }
        ListNode currentNode = Head;
        while (currentNode != null) {
            int randomIndex = ThreadLocalRandom.current().nextInt(Count + 1);
            currentNode.Rand =
                    randomIndex == Count
                            ? null
                            : getNodeByIndex(Head, randomIndex);
            currentNode = currentNode.Next;
        }
    }

    private ListNode getNodeByIndex(ListNode currentNode, int indexAfterThisNode) {
        if (currentNode == null || indexAfterThisNode <= 0) {
            return currentNode;
        }
        return getNodeByIndex(currentNode.Next, indexAfterThisNode - 1);
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
