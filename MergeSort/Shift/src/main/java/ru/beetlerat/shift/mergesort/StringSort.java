package ru.beetlerat.shift.mergesort;

import java.util.*;

public class StringSort extends MyMergeSort {
    public StringSort() {
        super();
    }

    @Override
    protected List<Queue> createQueue() {
        List<Queue> newList = new ArrayList<>();
        newList.add(new ArrayDeque<String>());
        newList.add(new ArrayDeque<String>());
        return newList;
    }

    @Override
    protected Collection<String> convertReadDataToCollection(StringBuilder data) {
        return convertReadDataToArrayList(data);
    }

    @Override
    protected Object[] convertReadDataToArray(StringBuilder data) {
        ArrayList<String> stringArrayList = convertReadDataToArrayList(data);
        String[] stringsArray = new String[stringArrayList.size()];
        for (int i = 0; i < stringArrayList.size(); i++) {
            stringsArray[i] = stringArrayList.get(i);
        }
        return stringsArray;
    }

    private ArrayList<String> convertReadDataToArrayList(StringBuilder data) {
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
