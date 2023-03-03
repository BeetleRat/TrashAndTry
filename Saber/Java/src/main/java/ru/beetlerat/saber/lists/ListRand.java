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
            Tail = node;
            Count = 0;
        } else {
            Tail.Next = node;
            node.Prev = Tail;
            Tail = node;
        }
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

    public void recalculateRandomPointer() {
        if (Head == null) {
            return;
        }
        ListNode currentNode = Head;
        while (currentNode.Next != null) {
            int randomIndex = ThreadLocalRandom.current().nextInt(Count + 1);
            currentNode.Rand =
                    randomIndex == Count
                            ? null
                            : getNodeByIndex(Head, randomIndex);
            currentNode = currentNode.Next;
        }
    }

    public void clear() {
        clear(Head);
        Count=0;
    }

    private void clear(ListNode currentNode) {
        if (currentNode == null) {
            return;
        }
        clear(currentNode.Next);
        currentNode = null;
    }

    private ListNode getNodeByIndex(ListNode currentNode, int indexAfterThisNode) {
        if (currentNode == null || indexAfterThisNode <= 0) {
            return currentNode;
        }
        return getNodeByIndex(currentNode.Next, indexAfterThisNode - 1);
    }

    private StringBuilder getData(ListNode currentNode, StringBuilder dataCollector) {
        if (currentNode == null) {
            return dataCollector;
        }
        dataCollector.append(currentNode.Data).append(", ");
        return getData(currentNode.Next, dataCollector);
    }

    public String toString() {
        StringBuilder outputString = new StringBuilder().append("[");
        outputString = getData(Head, outputString);
        if (outputString.length() > 2) {
            outputString.setLength(outputString.length() - 2);
        }
        outputString.append("]");
        return outputString.toString();
    }
}
