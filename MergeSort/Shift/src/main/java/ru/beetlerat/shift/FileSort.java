package ru.beetlerat.shift;

import ru.beetlerat.shift.fileaccess.AccessFile;
import ru.beetlerat.shift.mergesort.MyMergeSort;
import ru.beetlerat.shift.mergesort.MyMergeSortInt;
import ru.beetlerat.shift.mergesort.MyMergeSortString;

import java.io.File;
import java.util.*;

public class FileSort {
    private final int SORT_TYPE = 0;
    private final int RESULT_FILE = 0;
    private final int DATA_TYPE = 1;
    private final int OUTPUT_FILE = 2;
    private final int INPUT_FILE = 3;

    private List<File> outputFilesName;
    private String tempFileNamePrefix;

    private List<String> inputFilesName;
    private boolean[] hasFlag;
    private int ascendingSort;
    private boolean isFilesStoredInResources;
    private MyMergeSort myMergeSort;
    private AccessFile accessFile;
    private int bufferSize;

    public FileSort(String[] sortParams, int bufferSize) {
        this.bufferSize = bufferSize > 1 ? bufferSize : 10;
        this.inputFilesName = new ArrayList<>();
        this.outputFilesName = new LinkedList<>();
        this.accessFile = new AccessFile();
        this.hasFlag = new boolean[4];
        this.tempFileNamePrefix = "";
        this.ascendingSort = 1;
        this.isFilesStoredInResources=false;


        if (parseParams(sortParams)) {

            createSortedTmpFiles();
            uniteTempFiles();
            deleteTempFiles();

            System.out.printf("Program is complete. Result in %s\n",outputFilesName.get(0).getPath());


        } else {
            StringBuilder errorMessage = new StringBuilder()
                    .append(hasFlag[DATA_TYPE] ? "" : "no data flag(-i or -s); ")
                    .append(hasFlag[OUTPUT_FILE] ? "" : "no output file name (name must end with .txt); ")
                    .append(hasFlag[INPUT_FILE] ? "" : "no input file name (name must end with .txt);");

            System.out.printf("Invalid args: %s\nPlease enter the flags according to the template: sort-it.exe -d -s output.txt input1.txt input2.txt", errorMessage);
        }
    }

    private boolean parseParams(String[] params) {

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
                        this.ascendingSort = -1;
                    }
                    break;
                case "-s":
                    if (!hasFlag[DATA_TYPE]) {
                        hasFlag[DATA_TYPE] = true;
                        this.myMergeSort = new MyMergeSortString();
                    }
                    break;
                case "-i":
                    if (!hasFlag[DATA_TYPE]) {
                        hasFlag[DATA_TYPE] = true;
                        this.myMergeSort = new MyMergeSortInt();
                    }
                    break;
                default:
                    if (param.endsWith(".txt")) {
                        if (!hasFlag[OUTPUT_FILE]) {
                            hasFlag[OUTPUT_FILE] = true;
                            outputFilesName.add(new File(param));
                            tempFileNamePrefix = param.substring(0, param.length() - 4);
                        } else {
                            hasFlag[INPUT_FILE] = true;
                            inputFilesName.add(param);
                        }
                    }
            }
        }

        if (this.ascendingSort == -1 & this.myMergeSort != null) {
            this.myMergeSort.setAscendingSort(false);
        }
        return hasFlag[DATA_TYPE] & hasFlag[OUTPUT_FILE] & hasFlag[INPUT_FILE];
    }

    private void createSortedTmpFiles() {
        StringBuilder sortedSting = new StringBuilder();

        while (accessFile.readFromFiles(sortedSting,
                isFilesStoredInResources, bufferSize,
                inputFilesName)) {
            createTmpFile(sortedSting);
            sortedSting.setLength(0);
        }
        if (sortedSting.length() > 0) {
            createTmpFile(sortedSting);
            sortedSting.setLength(0);
        }
    }

    private void createTmpFile(StringBuilder sortedSting) {
        StringBuilder resultSting = myMergeSort.sort(sortedSting);

        int i = outputFilesName.size() - 1;
        AccessFile tempAccessFile = new AccessFile();
        while (i > 0) {
            String firstStringFromFile = tempAccessFile.readFirstStringFromFile(isFilesStoredInResources, outputFilesName.get(i).getPath());
            if (firstStringFromFile.compareTo(resultSting.toString()) * ascendingSort < 0) {
                outputFilesName.add(i + 1, new File(tempFileNamePrefix + "Temp" + outputFilesName.size() + ".txt"));
                tempAccessFile.writeToFile(resultSting, isFilesStoredInResources, outputFilesName.get(i + 1).getPath());
                break;
            }
            i--;
        }
        if (i == 0) {
            outputFilesName.add(i + 1, new File(tempFileNamePrefix + "Temp" + outputFilesName.size() + ".txt"));
            tempAccessFile.writeToFile(resultSting, isFilesStoredInResources, outputFilesName.get(i + 1).getPath());
        }
    }

    private void uniteTempFiles() {
        String mainOutputFile = outputFilesName.get(RESULT_FILE).getPath();
        StringBuilder outBuilder = new StringBuilder();

        // Очистить выходной файл
        accessFile.clearCurrentReadString();
        accessFile.writeToFile(outBuilder, isFilesStoredInResources, mainOutputFile);

        for (int i = 1; i < outputFilesName.size(); i++) {

            File tempFile = outputFilesName.get(i);
            accessFile.clearCurrentReadString();
            while (accessFile.readFromFiles(outBuilder, isFilesStoredInResources, bufferSize, Collections.singletonList(tempFile.getPath()))) {
                accessFile.appendToFile(outBuilder, isFilesStoredInResources, mainOutputFile);
                outBuilder.setLength(0);
            }
            if (outBuilder.length() > 0) {
                accessFile.appendToFile(outBuilder, isFilesStoredInResources, mainOutputFile);
                outBuilder.setLength(0);
            }
        }
    }

    private void deleteTempFiles() {
        for (int i = outputFilesName.size() - 1; i >= 1; i--) {
            accessFile.deleteFile(isFilesStoredInResources, outputFilesName.get(i).getPath());
        }
    }
}
