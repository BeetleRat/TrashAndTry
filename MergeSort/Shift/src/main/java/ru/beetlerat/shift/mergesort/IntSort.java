package ru.beetlerat.shift.mergesort;

import java.util.*;

public class IntSort extends MyMergeSort {
    public IntSort() {
        super();
    }

    @Override
    protected List<Queue> createQueue() {
        List<Queue> newList = new ArrayList<>();
        newList.add(new ArrayDeque<Integer>());
        newList.add(new ArrayDeque<Integer>());
        return newList;
    }

    @Override
    protected Collection convertReadDataToCollection(StringBuilder data) {
        String[] stringArray = data.toString().split("\n");
        List<Integer> intArrayList = new ArrayList<>();
        for (String stringNumber : stringArray) {
            try {
                intArrayList.add(Integer.parseInt(stringNumber.trim()));
            } catch (NumberFormatException e) {
                System.out.printf("Warning. Can not parse \"%s\" to int.\n", stringNumber);
            }
        }
        return intArrayList;
    }

    @Override
    protected Object[] convertReadDataToArray(StringBuilder data) {
        String[] stringArray = data.toString().split("\n");
        List<Integer> intArrayList = new ArrayList<>();
        for (String stringNumber : stringArray) {
            try {
                intArrayList.add(Integer.parseInt(stringNumber.trim()));
            } catch (NumberFormatException e) {
                System.out.printf("Warning. Can not parse \"%s\" to int.\n", stringNumber);
            }
        }
        Integer[] numbersArray = new Integer[intArrayList.size()];
        for (int i = 0; i < intArrayList.size(); i++) {
            numbersArray[i] = intArrayList.get(i);
        }
        return numbersArray;
    }

    @Override
    protected Integer compareElements(Object first, Object second) {
        Integer firstString = (Integer) first;
        Integer secondString = (Integer) second;
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
