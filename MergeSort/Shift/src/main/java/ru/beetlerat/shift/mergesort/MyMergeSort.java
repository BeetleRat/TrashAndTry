package ru.beetlerat.shift.mergesort;

import ru.beetlerat.shift.fileaccess.AccessFile;
import ru.beetlerat.shift.fileaccess.ConventionalFileName;
import ru.beetlerat.shift.utill.DataConverter;

import java.util.*;

public abstract class MyMergeSort<T extends Comparable<T>> {
    public static final int ASCENDING_SORT = 1;
    public static final int DESCENDING_SORT = -1;

    private static final int FIRST_FILE = 0;
    private static final int SECOND_FILE = 1;
    private static final int OUTPUT_FILE = 2;

    protected int ascendingSort;
    protected boolean withoutSpaces;

    private List<ConventionalFileName> outputFilesName;
    private AccessFile[] accessFile;
    private String outputFilePrefix;
    private StringBuilder[] dataBuffer;
    private boolean isFilesStoreInResources;
    private boolean saveTmpFiles;
    private boolean[] isFileEmpty;
    private List<Queue<T>> fileQueue;
    private int bufferSize;

    public MyMergeSort() {
        this.bufferSize = 10000000;
        this.ascendingSort = ASCENDING_SORT;
        this.saveTmpFiles = false;
        this.withoutSpaces = true;
        this.outputFilePrefix = "";
        this.isFilesStoreInResources = true;
        this.isFileEmpty = new boolean[2];
        this.dataBuffer = new StringBuilder[3];
        this.dataBuffer[OUTPUT_FILE] = new StringBuilder();
        this.accessFile = new AccessFile[3];
        this.outputFilesName = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            this.accessFile[i] = new AccessFile();
            this.accessFile[i].setFilesStoreInResources(isFilesStoreInResources);
        }
        this.fileQueue = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            this.fileQueue.add(new ArrayDeque<>());
        }
    }

    protected abstract ArrayList<T> convertReadDataToArrayList(StringBuilder data);

    protected abstract Integer compareElements(Object first, Object second);

    public void sort(List<String> inputFilesName) {
        clearOldFileReadData();
        outputFilesName.clear();
        createTmpFiles(inputFilesName);
        outMergeSort();
    }

    private Collection<T> convertReadDataToCollection(StringBuilder data) {
        return convertReadDataToArrayList(data);
    }

    private Object[] convertReadDataToArray(StringBuilder data) {
        return convertReadDataToArrayList(data).toArray();
    }

    private void createTmpFiles(List<String> inputFilesName) {
        StringBuilder sortSting;
        while ((sortSting = accessFile[FIRST_FILE].readFromFiles(bufferSize, inputFilesName)) != null) {

            StringBuilder resultSting = DataConverter.arrayToStringBuilder(mergeSort(convertReadDataToArray(sortSting)));

            ConventionalFileName tempFileName = ConventionalFileName.createConventionalFileNameFromStringBuilder(resultSting, outputFilePrefix, outputFilesName.size());

            if (tempFileName != null) {
                int index = outputFilesName.size() - 1;
                while (index >= 0) {
                    if (outputFilesName.get(index).compareTo(tempFileName) > 0) {
                        outputFilesName.add(index + 1, tempFileName);
                        break;
                    }
                    index--;
                }
                if (index == -1) {
                    outputFilesName.add(0, tempFileName);
                }
                accessFile[OUTPUT_FILE].writeToFile(resultSting, tempFileName.getFileName());
            }
            sortSting.setLength(0);
        }
        accessFile[OUTPUT_FILE].clearCurrentReadString();
    }

    private Object[] mergeSort(Object[] array) {
        int size = array.length;
        if (size > 1) {
            // ???????????? ???????????? ???? ?????? ????????????????????
            Object[] firstSubArray = new Object[size / 2];
            System.arraycopy(array, 0, firstSubArray, 0, size / 2);
            Object[] secondSubArray = new Object[size - size / 2];
            System.arraycopy(array, size / 2, secondSubArray, 0, size - size / 2);

            // ?????????????????? ???????????? ??????????????????
            firstSubArray = sortSubArray(firstSubArray);
            // ?????????????????? ???????????? ??????????????????
            secondSubArray = sortSubArray(secondSubArray);

            int firstSubArrayIndex = 0;
            int secondSubArrayIndex = 0;

            // ?????????????? ?????????????????????? ????????????
            for (int i = 0; i < size; i++) {
                // ???????? ???? ???????????? ???????? ???????????? ??????????????????
                if (firstSubArrayIndex == size / 2) {
                    array[i] = secondSubArray[secondSubArrayIndex];
                    secondSubArrayIndex++;
                } else {
                    // ???????? ???? ???????????? ???????? ???????????? ??????????????????
                    if (secondSubArrayIndex == size - size / 2) {
                        array[i] = firstSubArray[firstSubArrayIndex];
                        firstSubArrayIndex++;
                    } else {
                        // ???????????????????? ???????????????? ?????????????????????? ?????????? ??????????
                        // ?? ???????????????????? ?? ???????????????? ???????????? ?????????????? ??????????????
                        Integer compareResult;
                        if ((compareResult = compareElements(firstSubArray[firstSubArrayIndex], secondSubArray[secondSubArrayIndex])) != null) {
                            if (compareResult > 0) {
                                array[i] = secondSubArray[secondSubArrayIndex];
                                secondSubArrayIndex++;
                            } else {
                                array[i] = firstSubArray[firstSubArrayIndex];
                                firstSubArrayIndex++;
                            }
                        }
                    }
                }
            }
        }
        return array;
    }

    private Object[] sortSubArray(Object[] subArray) {
        // ???????? ?? ???????????????????? ?????? ????????????????
        if (subArray.length == 2) {
            // ?????????????????????????? ?????? ?????? ???????????????? ???????????????????????? ???????? ??????????
            Integer compareResult;
            if ((compareResult = compareElements(subArray[0], subArray[1])) != null && compareResult > 0) {
                Object swap;

                swap = subArray[0];
                subArray[0] = subArray[1];
                subArray[1] = swap;
            }
            return subArray;
        } else {
            // ???????? ?? ???????????????????? ???????????? ???????? ??????????????????
            if (subArray.length > 1) {
                //  ???????????????????? ?????????????????????????? ???????????? ??????????????????
                return mergeSort(subArray);
            }
        }
        return subArray;
    }

    private void outMergeSort() {
        int index = 0;
        while (index < outputFilesName.size() - 1) {
            ConventionalFileName newSortFile = uniteSortedFiles(outputFilesName.get(index), outputFilesName.get(index + 1));
            if (newSortFile != null) {
                outputFilesName.add(newSortFile);
            }
            if (saveTmpFiles) {
                index += 2;
            } else {
                for (int i = 0; i < 2; i++) {
                    accessFile[OUTPUT_FILE].deleteFile(outputFilesName.get(index).getFileName());
                    outputFilesName.remove(index);
                }
            }
        }

        String outputFileName = outputFilePrefix + ".txt";
        if (outputFilesName.size() > 0 && accessFile[OUTPUT_FILE].renameFile(outputFilesName.get(index).getFileName(), outputFileName)) {
            System.out.printf("\nProgram completed. Results in file: %s", outputFileName);
        } else {
            System.out.println("\nProgram ended with errors. No sorted data.");
        }
        accessFile[OUTPUT_FILE].clearCurrentReadString();
    }

    public ConventionalFileName uniteSortedFiles(ConventionalFileName fileName1, ConventionalFileName fileName2) {

        ConventionalFileName outputFileName =
                ConventionalFileName.createConventionalFileNameFromOtherNames(
                        fileName1,
                        fileName2,
                        ascendingSort,
                        outputFilePrefix + "_" + outputFilesName.size()
                );

        clearOldFileReadData();

        showCurrentFileName(outputFileName.getFileName());

        boolean isFirstCycle = true;
        while (!isFileEmpty[FIRST_FILE] || !isFileEmpty[SECOND_FILE]) {
            int outputBuilderLinesCount = 0;
            while (outputBuilderLinesCount < bufferSize / 2) {
                if (fileQueue.get(FIRST_FILE).isEmpty()) {
                    if (!isFileEmpty[FIRST_FILE]) {
                        if ((dataBuffer[FIRST_FILE] = accessFile[FIRST_FILE].readFromFile(bufferSize / 4, fileName1.getFileName())) == null) {
                            isFileEmpty[FIRST_FILE] = true;
                        } else {
                            fileQueue.get(FIRST_FILE).addAll(convertReadDataToCollection(dataBuffer[FIRST_FILE]));
                        }
                    } else {
                        if (fileQueue.get(SECOND_FILE).isEmpty()) {
                            if (!isFileEmpty[SECOND_FILE]) {
                                if ((dataBuffer[SECOND_FILE] = accessFile[SECOND_FILE].readFromFile(bufferSize / 4, fileName2.getFileName())) == null) {
                                    isFileEmpty[SECOND_FILE] = true;
                                } else {
                                    fileQueue.get(SECOND_FILE).addAll(convertReadDataToCollection(dataBuffer[SECOND_FILE]));
                                }
                            } else {
                                break;
                            }
                        } else {
                            dataBuffer[OUTPUT_FILE].append(fileQueue.get(SECOND_FILE).remove()).append("\n");
                            outputBuilderLinesCount++;
                        }
                    }
                } else {
                    if (fileQueue.get(SECOND_FILE).isEmpty()) {
                        if (!isFileEmpty[SECOND_FILE]) {
                            if ((dataBuffer[SECOND_FILE] = accessFile[SECOND_FILE].readFromFile(bufferSize / 4, fileName2.getFileName())) == null) {
                                isFileEmpty[SECOND_FILE] = true;
                            } else {
                                fileQueue.get(SECOND_FILE).addAll(convertReadDataToCollection(dataBuffer[SECOND_FILE]));
                            }
                        } else {
                            dataBuffer[OUTPUT_FILE].append(fileQueue.get(FIRST_FILE).remove()).append("\n");
                            outputBuilderLinesCount++;
                        }
                    } else {
                        Integer compareResult;
                        if ((compareResult = compareElements(fileQueue.get(FIRST_FILE).element(), fileQueue.get(SECOND_FILE).element())) != null) {
                            if (compareResult > 0) {
                                dataBuffer[OUTPUT_FILE].append(fileQueue.get(SECOND_FILE).remove()).append("\n");
                                outputBuilderLinesCount++;
                            } else {
                                dataBuffer[OUTPUT_FILE].append(fileQueue.get(FIRST_FILE).remove()).append("\n");
                                outputBuilderLinesCount++;
                            }
                        }
                    }
                }
            }
            if (dataBuffer[OUTPUT_FILE].length() > 0) {
                dataBuffer[OUTPUT_FILE].setLength(dataBuffer[OUTPUT_FILE].length() - 1);
                if (!isFirstCycle) {
                    dataBuffer[OUTPUT_FILE].insert(0, "\n");
                } else {
                    isFirstCycle = false;
                }
                accessFile[0].appendToFile(dataBuffer[OUTPUT_FILE], outputFileName.getFileName());
                dataBuffer[OUTPUT_FILE].setLength(0);
            }
        }
        return isFirstCycle ? null : outputFileName;
    }

    private void clearOldFileReadData() {
        for (int i = 0; i < 3; i++) {
            this.accessFile[i].clearCurrentReadString();
        }
        this.dataBuffer[OUTPUT_FILE].setLength(0);
        for (int i = 0; i < 2; i++) {
            this.isFileEmpty[i] = false;
            this.fileQueue.get(i).clear();
        }
    }

    private static void showCurrentFileName(String fileName) {
        for (int i = 0; i < 8; i++) {
            System.out.print("\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b\b");
        }
        System.out.printf("Out of buffer. Perform out merge sort. Current file: %s", fileName);
    }

    public int getAscendingSort() {
        return ascendingSort;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public String getOutputFilePrefix() {
        return outputFilePrefix;
    }

    public boolean isFilesStoreInResources() {
        return isFilesStoreInResources;
    }

    public boolean isSaveTmpFiles() {
        return saveTmpFiles;
    }

    public boolean isWithoutSpaces() {
        return withoutSpaces;
    }

    public void setAscendingSort(int ascendingSort) {
        this.ascendingSort = ascendingSort;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = Math.max(bufferSize, 16);
    }

    public void setFilesStoreInResources(boolean filesStoreInResources) {
        this.isFilesStoreInResources = filesStoreInResources;
        for (int i = 0; i < 3; i++) {
            this.accessFile[i].setFilesStoreInResources(this.isFilesStoreInResources);
        }
    }

    public void setOutputFilePrefix(String outputFilePrefix) {
        this.outputFilePrefix = outputFilePrefix;
    }

    public void setSaveTmpFiles(boolean saveTmpFiles) {
        this.saveTmpFiles = saveTmpFiles;
    }

    public void setWithoutSpaces(boolean withoutSpaces) {
        this.withoutSpaces = withoutSpaces;
    }
}
