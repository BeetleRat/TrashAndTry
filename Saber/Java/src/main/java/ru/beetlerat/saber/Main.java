package main.java.ru.beetlerat.saber;

import main.java.ru.beetlerat.saber.lists.ListBinSerialize;
import main.java.ru.beetlerat.saber.lists.ListRand;
import main.java.ru.beetlerat.saber.lists.ListStringSerialize;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        boolean isNewList = true;

        ListRand list1 = createRandList(isNewList, new ListStringSerialize());
        fileWork(isNewList, list1, "src/main/resources/stringList.txt");
    }

    private static ListRand createRandList(boolean isNewList, ListRand listImplementation) {
        if (isNewList) {
            listImplementation.add("Peter", "Vasiliy", "George", "Ivan", "Egor");
            listImplementation.add("Sofia", true);
        }
        return listImplementation;
    }

    private static void fileWork(boolean serializeNewList, ListRand list, String fileName) {
        try {
            if (serializeNewList) {
                FileOutputStream fos = new FileOutputStream(fileName);
                list.Serialize(fos);
                fos.close();
            } else {
                FileInputStream fis = new FileInputStream(fileName);
                list.Deserialize(fis);
                fis.close();
            }
            System.out.printf("List: %s\n", list);
        } catch (FileNotFoundException e) {
            System.out.println("Open file exception");
        } catch (IOException e) {
            System.out.println("Close file exception");
        }
    }
}
