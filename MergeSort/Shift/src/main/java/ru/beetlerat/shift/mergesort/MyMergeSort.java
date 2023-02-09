package ru.beetlerat.shift.mergesort;

public abstract class MyMergeSort {
    protected int ascendingSort;

    public MyMergeSort() {
        ascendingSort = 1;
    }

    public MyMergeSort(boolean isAscendingSort) {
        this.ascendingSort = isAscendingSort ? 1 : -1;
    }

    public void setAscendingSort(boolean isAscendingSort) {
        this.ascendingSort = isAscendingSort ? 1 : -1;
    }


    public abstract StringBuilder sort(StringBuilder sortedString);

}
