package ru.beetlerat.shift;

import ru.beetlerat.shift.fileaccess.AccessFile;
import ru.beetlerat.shift.mergesort.IntSort;
import ru.beetlerat.shift.mergesort.MyMergeSort;
import ru.beetlerat.shift.mergesort.StringSort;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class FileSort {
    private static final int SORT_TYPE = 0;
    private static final int DATA_TYPE = 1;
    private static final int OUTPUT_FILE = 2;
    private static final int INPUT_FILE = 3;

    private List<String> inputFilesName;
    private boolean[] hasFlag;
    private MyMergeSort myMergeSort;
    private boolean isFilesStoreInResources;

    public FileSort(String[] sortParams, boolean isFilesStoreInResources) {
        this.isFilesStoreInResources = isFilesStoreInResources;
        this.hasFlag = new boolean[4];
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
                    } else {
                        System.out.printf("Unknown parameter: \"%s\"\n", param);
                    }
            }
        }


        if (this.myMergeSort != null) {
            this.myMergeSort.setFilesStoreInResources(isFilesStoreInResources);
            this.myMergeSort.setAscendingSort(ascendingSort);
            this.myMergeSort.setOutputFilePrefix(outputFileNamePrefix);

            // Достаем значения конфигурационного файла
            AccessFile accessFile = new AccessFile();
            accessFile.setFilesStoreInResources(isFilesStoreInResources);
            Map<String,String> properties=accessFile.readProperties();

            int bufferSize=250000000;
            try {
                bufferSize=Integer.parseInt(properties.get("bufferSize"));
            }catch (NumberFormatException e){
                System.out.printf("Can not parse property bufferSize: %s\n",properties.get("bufferSize"));
            }

            this.myMergeSort.setBufferSize(bufferSize);
            this.myMergeSort.setSaveTmpFiles(Boolean.parseBoolean(properties.get("saveTmpFiles")));
            this.myMergeSort.setWithoutSpaces(Boolean.parseBoolean(properties.get("withoutSpaces")));
        }
        return hasFlag[DATA_TYPE] & hasFlag[OUTPUT_FILE] & hasFlag[INPUT_FILE];
    }
}
