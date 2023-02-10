package ru.beetlerat.shift;

import ru.beetlerat.shift.fileaccess.AccessFile;
import ru.beetlerat.shift.mergesort.MyMergeSort;
import ru.beetlerat.shift.mergesort.MyMergeSortInt;
import ru.beetlerat.shift.mergesort.MyMergeSortString;
import ru.beetlerat.shift.fileaccess.ConventionalFileName;

import java.util.*;

public class FileSort {
    private final int SORT_TYPE = 0;
    private final int RESULT_FILE = 0;
    private final int DATA_TYPE = 1;
    private final int OUTPUT_FILE = 2;
    private final int INPUT_FILE = 3;

    private List<ConventionalFileName> outputFilesName;
    private String tempFileNamePrefix;

    private List<String> inputFilesName;
    private boolean[] hasFlag;
    private int ascendingSort;
    private MyMergeSort myMergeSort;
    private AccessFile accessFile;
    private int bufferSize;

    public FileSort(String[] sortParams, int bufferSize) {
        this.bufferSize = bufferSize > 1 ? bufferSize : 10;
        this.inputFilesName = new ArrayList<>();
        this.outputFilesName = new LinkedList<>();
        this.accessFile = new AccessFile();
        this.accessFile.setFilesStoreInResources(true);
        this.hasFlag = new boolean[4];
        this.tempFileNamePrefix = "";
        this.ascendingSort = 1;


        if (parseParams(sortParams)) {

            createSortedTmpFiles();
            uniteTempFiles();
            //deleteTempFiles();

            System.out.printf("Program is complete. Result in %s\n", outputFilesName.get(0).getClearFileName());


        } else {
            StringBuilder errorMessage = new StringBuilder()
                    .append(hasFlag[DATA_TYPE] ? "" : "no data flag(-i or -s); ")
                    .append(hasFlag[OUTPUT_FILE] ? "" : "no output file name (name must end with .txt); ")
                    .append(hasFlag[INPUT_FILE] ? "" : "no input file name (name must end with .txt);");

            System.out.printf("Invalid args: %s\nPlease enter the flags according to the template: sort-it.exe -d -s output.txt input1.txt input2.txt", errorMessage);
        }
    }

    private ConventionalFileName createConventionalFileNameFromFile(String filePath) {
        AccessFile tempAccessFile = new AccessFile();
        tempAccessFile.setFilesStoreInResources(accessFile.isFilesStoreInResources());

        String firstString = tempAccessFile.readFirstStringFromFile(filePath);
        String lastString = tempAccessFile.readLastStringFromFile(filePath);
        if (firstString == null | lastString == null || firstString.length() == 0 | lastString.length() == 0) {
            return null;
        }

        return new ConventionalFileName(firstString.hashCode(), lastString.hashCode(), tempFileNamePrefix);
    }

    private ConventionalFileName createConventionalFileNameFromStringBuilder(StringBuilder data, int fileCount) {
        String firstString = null;
        String lastString = null;

        if (data.indexOf("\n") == -1) {
            if (data.length() == 0) {
                return null;
            } else {
                firstString = data.toString();
                lastString = firstString;
            }
        } else {
            firstString = data.substring(0, Math.min(data.indexOf("\n"), 20));
            lastString = data.substring(data.lastIndexOf("\n") + 1, data.length());
        }
        if (firstString == null | lastString == null || firstString.length() == 0 | lastString.length() == 0) {
            return null;
        }

        return new ConventionalFileName(firstString.hashCode(), lastString.hashCode(), tempFileNamePrefix + "_" + fileCount);
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
                            tempFileNamePrefix = param.substring(0, param.length() - 4);
                            ConventionalFileName mainOutputFileName = new ConventionalFileName(0, 0, tempFileNamePrefix);
                            hasFlag[OUTPUT_FILE] = accessFile.writeToFile(new StringBuilder(), mainOutputFileName.getClearFileName());
                            if (hasFlag[OUTPUT_FILE]) {
                                outputFilesName.add(mainOutputFileName);
                            } else {
                                tempFileNamePrefix = "";
                            }
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
        StringBuilder sortSting;
        while ((sortSting = accessFile.readFromFiles(bufferSize, inputFilesName)) != null) {
            createTmpFile(sortSting);
            sortSting.setLength(0);
        }
    }


    private void createTmpFile(StringBuilder sortedSting) {
        StringBuilder resultSting = myMergeSort.sort(sortedSting);

        ConventionalFileName tempFileName = createConventionalFileNameFromStringBuilder(resultSting, outputFilesName.size());

        if (tempFileName != null) {
            int index = outputFilesName.size() - 1;
            while (index > 0) {
                if (outputFilesName.get(index).compareTo(tempFileName)  > 0) {
                    outputFilesName.add(index +1, tempFileName);
                    break;
                }
                index--;
            }
            if (index == 0) {
                outputFilesName.add(index + 1, tempFileName);
            }

            AccessFile tempAccessFile = new AccessFile();
            tempAccessFile.setFilesStoreInResources(accessFile.isFilesStoreInResources());
            accessFile.writeToFile(resultSting, tempFileName.getFileName());
        }
    }

    private void uniteTempFiles() {
        String mainOutputFile = outputFilesName.get(RESULT_FILE).getClearFileName();
        int index = 1;
        while (index < outputFilesName.size() - 1) {
            ConventionalFileName newSortFile = myMergeSort.fileSort(outputFilesName.get(index), outputFilesName.get(index + 1), accessFile.isFilesStoreInResources(), bufferSize, tempFileNamePrefix + "_" + outputFilesName.size());
            if (newSortFile != null) {
                outputFilesName.add(newSortFile);
            }
            for(int i=0;i<2;i++){
                accessFile.deleteFile(outputFilesName.get(index).getFileName());
                outputFilesName.remove(index);
            }
            //index += 2;
        }
        accessFile.clearCurrentReadString();
        AccessFile writeFile = new AccessFile();
        writeFile.setFilesStoreInResources(accessFile.isFilesStoreInResources());
        StringBuilder buffer;
        boolean isFirstCycle = true;
        while ((buffer = accessFile.readFromFiles(bufferSize, Collections.singletonList(outputFilesName.get(index).getFileName()))) != null) {
            if (isFirstCycle) {
                isFirstCycle = false;
            } else {
                buffer.insert(0, "\n");
            }
            writeFile.appendToFile(buffer, mainOutputFile);
        }
        accessFile.deleteFile(outputFilesName.get(index).getFileName());
        outputFilesName.remove(index);

    }

    private void deleteTempFiles() {
        for (int i = outputFilesName.size() - 1; i >= 1; i--) {
            accessFile.deleteFile(outputFilesName.get(i).getFileName());
        }
    }
}
