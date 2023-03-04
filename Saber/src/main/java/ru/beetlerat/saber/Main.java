package main.java.ru.beetlerat.saber;

import main.java.ru.beetlerat.saber.lists.ListNode;
import main.java.ru.beetlerat.saber.lists.ListRand;
import main.java.ru.beetlerat.saber.lists.ListStringSerialize;

import java.awt.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static final int ADD_ELEMENT = 1, REMOVE_ELEMENT = 2, FIND_ELEMENT = 3, UPDATE_ELEMENT = 4, CLEAR_ARRAY = 5, CREATE_RANDOM_LINKS = 6, SERIALIZE_ARRAY = 7, DESERIALIZE_ARRAY = 8, CREATE_TEST_ARRAY = 9, EXIT = 0, NOT_CORRECT_NUMBER = -9999999;

    public static void main(String[] args) {
        ListRand list = new ListStringSerialize();
        Scanner inputData = new Scanner(System.in);
        while (showMenu(list, inputData)) ;
    }

    private static boolean showMenu(ListRand list, Scanner inputData) {
        String menuText = "List: " + list + "\n1)Add element to list;\n2)Remove element from list;\n3)Find element in list;\n4)Update element in list;\n5)Clear list;\n6)Create random links in list;\n7)Serialize list;\n8)Deserialize list;\n9)Create test list;\n0)Exit.\nInput action: ";
        String fileName, newData = "";
        int menuItem = -1;

        System.out.print(menuText);
        String answer = inputData.nextLine();
        try {
            menuItem = Integer.parseInt(answer.trim());
        } catch (NumberFormatException e) {
            menuItem = NOT_CORRECT_NUMBER;
        }

        printDivider();

        switch (menuItem) {
            case ADD_ELEMENT:
                System.out.print("Enter new data: ");
                newData = inputData.nextLine();
                list.add(newData);
                break;
            case REMOVE_ELEMENT:
                removeElementFromList(list, inputData);
                break;
            case FIND_ELEMENT:
                findListElement(list, inputData);
                break;
            case UPDATE_ELEMENT:
                updateListElement(list, inputData);
                break;
            case CLEAR_ARRAY:
                list.clear();
                System.out.println("List was cleared.");
                break;
            case CREATE_RANDOM_LINKS:
                list.recalculateRandomPointer();
                System.out.println("Random links was created.");
                break;
            case SERIALIZE_ARRAY:
                System.out.print("Enter file name: ");
                fileName = inputData.nextLine();
                fileWork(true, list, fileName);
                break;
            case DESERIALIZE_ARRAY:
                System.out.print("Enter file name: ");
                fileName = inputData.nextLine();
                fileWork(false, list, fileName);
                break;
            case CREATE_TEST_ARRAY:
                list.clear();
                list.add("Kirill", "Egor", "Evgeniy", "Georg", "Dante", "Maria", "Timur", "Alena", "Lev", "Vladimir");
                list.add("Sofia", true);
                System.out.printf("Previous list was cleared.\nNew test list was created.\nTest list count=%d.", list.Count);
                break;
            case EXIT:
                System.out.println("Exit from program...");
                return false;
            default:
                System.out.println("Action number is not correct.");
        }
        printDivider();
        return true;
    }

    private static void removeElementFromList(ListRand list, Scanner inputData) {
        int elementIndex;
        System.out.print("Enter element index: ");
        elementIndex = readInt(inputData);
        if (list.Count <= elementIndex || elementIndex < 0) {
            System.out.println("Index is not correct.");
        } else {
            if (list.remove(elementIndex)) {
                System.out.println("Element " + elementIndex + " was removed.");
            } else {
                System.out.println("Element " + elementIndex + " not found");
            }
        }
    }

    private static void findListElement(ListRand list, Scanner inputData) {
        int elementIndex = readListIndex(list, inputData);
        if (elementIndex != NOT_CORRECT_NUMBER) {
            ListNode foundElement = list.get(elementIndex);
            if (foundElement == null) {
                System.out.println("Element not found");
            } else {
                System.out.println("Element: {ID: " + elementIndex + ", Data: " + foundElement.Data + "; Rand: " + (foundElement.Rand == null ? "null" : foundElement.Rand.Data + "}"));
            }
        }
    }

    private static void updateListElement(ListRand list, Scanner inputData) {
        String newData;
        int elementIndex = readListIndex(list, inputData);
        if (elementIndex != NOT_CORRECT_NUMBER) {
            System.out.print("Enter new data: ");
            newData = inputData.nextLine();
            list.set(elementIndex, newData);
        }
    }

    private static int readListIndex(ListRand list, Scanner inputData) {
        int elementIndex;
        System.out.print("Enter element index: ");
        elementIndex = readInt(inputData);
        if (list.Count <= elementIndex || elementIndex < 0) {
            System.out.println("Index is not correct.");
            return NOT_CORRECT_NUMBER;
        }
        return elementIndex;
    }

    private static int readInt(Scanner inputData) {
        int readIntFromConsole;
        try {
            String answer = inputData.nextLine();
            readIntFromConsole = Integer.parseInt(answer.trim());
        } catch (NumberFormatException e) {
            return NOT_CORRECT_NUMBER;
        }
        return readIntFromConsole;
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

    private static void printDivider() {
        System.out.print("\n=======================================================================================================================\n");
    }

}
