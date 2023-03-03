package main.java.ru.beetlerat.saber;

import main.java.ru.beetlerat.saber.lists.ListBinSerialize;
import main.java.ru.beetlerat.saber.lists.ListRand;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        boolean isNewList = false;

        ListRand list = createRandList(isNewList);
        fileWork(isNewList, list,"src/main/resources/list.txt");
    }

    private static ListRand createRandList(boolean isNewList) {
        ListRand list = new ListBinSerialize();
        if (isNewList) {
            list.add("Peter", "Vasiliy", "George", "Ivan", "Egor");
            list.add("Sofia", true);
        }
        return list;
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
