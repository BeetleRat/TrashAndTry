package ru.beetlerat.shift.mergesort;

import java.util.*;

public class StringSort extends MyMergeSort<String> {
    public StringSort() {
        super();
    }

    @Override
    protected ArrayList<String> convertReadDataToArrayList(StringBuilder data) {
        ArrayList<String> stringList = new ArrayList<>(List.of(data.toString().split("\n")));
        if (withoutSpaces) {
            for (int i = stringList.size() - 1; i >= 0; i--) {
                if (stringList.get(i).contains(" ")) {
                    System.out.printf("Warning. String have spaces \"%s\". It will not be included in the final result.\n", stringList.get(i));
                    stringList.remove(i);
                }
            }
        }
        return stringList;
    }

    @Override
    protected Integer compareElements(Object first, Object second) {
        String firstString = String.valueOf(first);
        String secondString = String.valueOf(second);
        if (firstString == null && secondString == null) {
            return null;
        }
        if (firstString == null) {
            return -1;
        }
        if (secondString == null) {
            return 1;
        }
        return firstString.compareTo(secondString) * ascendingSort;
    }
}
