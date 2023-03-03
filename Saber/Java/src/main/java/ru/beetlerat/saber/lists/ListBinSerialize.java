package main.java.ru.beetlerat.saber.lists;

import java.io.*;

public class ListBinSerialize extends ListRand {
    @Override
    public void Serialize(FileOutputStream s) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(s);
            recursiveSerialize(Head, oos);
            oos.close();
        } catch (IOException e) {
            System.out.println("ObjectOutputStream exception: " + e);
        }
    }

    @Override
    protected void recursiveSerialize(ListNode currentNode, ObjectOutputStream oos) throws IOException {
        if (currentNode == null) {
            return;
        }
        oos.writeObject(currentNode);
        recursiveSerialize(currentNode.Next, oos);
    }

    @Override
    public void Deserialize(FileInputStream s) {
        clear();
        try {
            ObjectInputStream ois = new ObjectInputStream(s);
            ListNode newNode;
            while (true) {
                newNode = (ListNode) ois.readObject();
                if (Head == null) {
                    Head = newNode;
                    Tail = newNode;
                } else {
                    Tail = newNode;
                }
            }

        } catch (ClassNotFoundException e) {
            System.out.println("Deserialize exception. ClassNotFoundException" + e);
        } catch (EOFException e) {
            System.out.println("Deserialize was ended.");
        } catch (IOException e) {
            System.out.println("ObjectInputStream exception." + e);
        }
    }
}
