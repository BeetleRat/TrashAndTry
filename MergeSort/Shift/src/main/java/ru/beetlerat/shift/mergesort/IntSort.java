package ru.beetlerat.shift.mergesort;

import java.util.*;

public class IntSort extends MyMergeSort<Integer> {
    public IntSort() {
        super();
    }

    @Override
    protected ArrayList<Integer> convertReadDataToArrayList(StringBuilder data) {
        String[] stringArray = data.toString().split("\n");
        ArrayList<Integer> intArrayList = new ArrayList<>();
        if (withoutSpaces) {
            for (String stringNumber : stringArray) {
                try {
                    intArrayList.add(Integer.parseInt(stringNumber));
                } catch (NumberFormatException e) {
                    System.out.printf("Warning. Can not parse \"%s\" to int. It will not be included in the final result.\n", stringNumber);
                }
            }
        } else {
            for (String stringNumber : stringArray) {
                try {
                    intArrayList.add(Integer.parseInt(stringNumber.trim()));
                } catch (NumberFormatException e) {
                    System.out.printf("Warning. Can not parse \"%s\" to int.\n", stringNumber);
                }
            }
        }

        return intArrayList;
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
