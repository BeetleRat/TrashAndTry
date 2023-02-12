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
        return List.of(data.toString().split("\n"));
    }

    @Override
    protected Object[] convertReadDataToArray(StringBuilder data) {
        return data.toString().split("\n");
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
