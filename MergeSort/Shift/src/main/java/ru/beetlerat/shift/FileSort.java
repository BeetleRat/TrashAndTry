package ru.beetlerat.shift;

import ru.beetlerat.shift.mergesort.IntSort;
import ru.beetlerat.shift.mergesort.MyMergeSort;
import ru.beetlerat.shift.mergesort.StringSort;

import java.util.*;

public class FileSort {
    private final int SORT_TYPE = 0;
    private final int DATA_TYPE = 1;
    private final int OUTPUT_FILE = 2;
    private final int INPUT_FILE = 3;

    private List<String> inputFilesName;
    private boolean[] hasFlag;
    private MyMergeSort myMergeSort;
    private int bufferSize;
    private boolean isFilesStoreInResources;

    public FileSort(String[] sortParams, int bufferSize, boolean isFilesStoreInResources) {
        this.bufferSize = bufferSize > 1 ? bufferSize : 10;
        this.hasFlag = new boolean[4];
        this.isFilesStoreInResources = isFilesStoreInResources;
        this.inputFilesName = new ArrayList<>();


        if (parseParams(sortParams)) {
            myMergeSort.sort(inputFilesName);
        } else {
            StringBuilder errorMessage = new StringBuilder()
                    .append(hasFlag[DATA_TYPE] ? "" : "no data flag(-i or -s); ")
                    .append(hasFlag[OUTPUT_FILE] ? "" : "no output file name (name must end with .txt); ")
                    .append(hasFlag[INPUT_FILE] ? "" : "no input file name (name must end with .txt);");

            System.out.printf("Invalid args: %s\nPlease enter the flags according to the template: sort-it.exe -d -s output.txt input1.txt input2.txt", errorMessage);
        }
    }

    // 1. режим сортировки (-a или -d), необязательный, по умолчанию сортируем по возрастанию;
    // 2. тип данных (-s или -i), обязательный;
    // 3. имя выходного файла, обязательное;
    // 4. остальные параметры – имена входных файлов, не менее одного.
    private boolean parseParams(String[] params) {
        int ascendingSort = MyMergeSort.ASCENDING_SORT;
        String outputFileNamePrefix = "";
        for (String param : params) {
            switch (param) {
                case "-a":
                    if (!hasFlag[SORT_TYPE]) {
                        hasFlag[SORT_TYPE] = true;
                    }
                    break;
                case "-d":
                    if (!hasFlag[SORT_TYPE]) {
                        hasFlag[SORT_TYPE] = true;
                        ascendingSort = MyMergeSort.DESCENDING_SORT;
                    }
                    break;
                case "-s":
                    if (!hasFlag[DATA_TYPE]) {
                        hasFlag[DATA_TYPE] = true;
                        this.myMergeSort = new StringSort();
                    }
                    break;
                case "-i":
                    if (!hasFlag[DATA_TYPE]) {
                        hasFlag[DATA_TYPE] = true;
                        this.myMergeSort = new IntSort();
                    }
                    break;
                default:
                    if (param.endsWith(".txt")) {
                        if (!hasFlag[OUTPUT_FILE]) {
                            hasFlag[OUTPUT_FILE] = true;
                            outputFileNamePrefix = param.substring(0, param.length() - 4);
                        } else {
                            hasFlag[INPUT_FILE] = true;
                            inputFilesName.add(param);
                        }
                    }
            }
        }

        if (this.myMergeSort != null) {
            this.myMergeSort.setAscendingSort(ascendingSort);
            this.myMergeSort.setFilesStoreInResources(isFilesStoreInResources);
            this.myMergeSort.setBufferSize(bufferSize);
            this.myMergeSort.setOutputFilePrefix(outputFileNamePrefix);
            this.myMergeSort.setSaveTmpFiles(false);
            this.myMergeSort.setWithoutSpaces(true);
        }
        return hasFlag[DATA_TYPE] & hasFlag[OUTPUT_FILE] & hasFlag[INPUT_FILE];
    }
}
