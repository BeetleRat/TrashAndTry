package main.java.ru.beetlerat.saber;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        ListRand list = new ListRand();
        System.out.println("New list was created: " + list);
        fileWork(false, list, "src/main/resources/stringListFrom.txt");
        System.out.println("List after deserialization: " + list);
        fileWork(true, list, "src/main/resources/stringListTo.txt");
    }

    // Сериализация/Десериализация
    private static void fileWork(boolean serializeList, ListRand list, String fileName) {
        try {
            if (serializeList) {
                FileOutputStream fos = new FileOutputStream(fileName);
                list.Serialize(fos);
                fos.close();
                System.out.println("Serialization was successful.");
            } else {
                FileInputStream fis = new FileInputStream(fileName);
                list.clear();
                list.Deserialize(fis);
                fis.close();
                System.out.println("Deserialization was successful.");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File " + fileName + " is not found. " + e);
        } catch (IOException e) {
            System.out.println("Close file exception: " + e);
        }
    }
}
