package ru.beetlerat.shift.mergesort;

import ru.beetlerat.shift.fileaccess.ConventionalFileName;

import java.io.File;

public abstract class MyMergeSort {
    protected final int OUTPUT_FILE = 0;
    protected final int FIRST_FILE = 1;
    protected final int SECOND_FILE = 2;
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

    protected ConventionalFileName getOutputConventionalFileName(ConventionalFileName fileName1, ConventionalFileName fileName2, String outputFilePrefix){
        return new ConventionalFileName(fileName1.getMinFileNumber() * ascendingSort > fileName2.getMinFileNumber() * ascendingSort ? fileName1.getMinFileNumber() : fileName2.getMinFileNumber(),
                fileName1.getMaxFileNumber() * ascendingSort > fileName2.getMaxFileNumber() * ascendingSort ? fileName1.getMaxFileNumber() : fileName2.getMaxFileNumber(),
                outputFilePrefix);
    }
    public abstract ConventionalFileName fileSort(ConventionalFileName file1, ConventionalFileName file2, boolean isFilesStoreInResources, int bufferSize,String outputFilePrefix);

}
