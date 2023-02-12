package ru.beetlerat.shift.mergesort;

import ru.beetlerat.shift.fileaccess.AccessFile;
import ru.beetlerat.shift.fileaccess.ConventionalFileName;

import java.util.*;

public abstract class MyMergeSort {
    public static final int ASCENDING_SORT = 1;
    public static final int DESCENDING_SORT = -1;

    private final int OUTPUT_FILE = 2;
    private final int FIRST_FILE = 0;
    private final int SECOND_FILE = 1;

    protected int ascendingSort;

    private List<ConventionalFileName> outputFilesName;
    private AccessFile[] accessFile;
    private String outputFilePrefix;
    private StringBuilder[] dataBuffer;
    private boolean isFilesStoreInResources;
    private boolean[] isFileEmpty;
    private List<Queue> fileQueue;
    private int bufferSize;

    public MyMergeSort() {
        this.bufferSize = 100;
        this.ascendingSort = ASCENDING_SORT;

        this.outputFilePrefix = "";
        this.isFilesStoreInResources = true;
        this.dataBuffer = new StringBuilder[3];
        this.dataBuffer[OUTPUT_FILE] = new StringBuilder();
        this.isFileEmpty = new boolean[2];
        this.accessFile = new AccessFile[3];
        this.outputFilesName = new LinkedList<>();
        for (int i = 0; i < 3; i++) {
            this.accessFile[i] = new AccessFile();
            this.accessFile[i].setFilesStoreInResources(isFilesStoreInResources);
        }
        this.fileQueue = createQueue();
    }

    protected abstract List<Queue> createQueue();

    protected abstract Collection convertReadDataToCollection(StringBuilder data);

    protected abstract Object[] convertReadDataToArray(StringBuilder data);

    protected abstract Integer compareElements(Object first, Object second);

    public void sort(List<String> inputFilesName) {
        clearOldFileReadData();
        outputFilesName.clear();
        createTmpFiles(inputFilesName);
        outMergeSort();
    }

    private void createTmpFiles(List<String> inputFilesName) {
        StringBuilder sortSting;
        while ((sortSting = accessFile[FIRST_FILE].readFromFiles(bufferSize, inputFilesName)) != null) {
            StringBuilder resultSting = arrayToStringBuilder(mergeSort(convertReadDataToArray(sortSting)));
            ConventionalFileName tempFileName = createConventionalFileNameFromStringBuilder(resultSting, outputFilesName.size());

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
            // Дробим массив на два подмассива
            Object[] firstSubArray = new Object[size / 2];
            System.arraycopy(array, 0, firstSubArray, 0, size / 2);
            Object[] secondSubArray = new Object[size - size / 2];
            System.arraycopy(array, size / 2, secondSubArray, 0, size - size / 2);

            // Сортируем первый подмассив
            firstSubArray = sortSubArray(firstSubArray);
            // Сортируем второй подмассив
            secondSubArray = sortSubArray(secondSubArray);

            int firstSubArrayIndex = 0;
            int secondSubArrayIndex = 0;

            // Обходим изначальный массив
            for (int i = 0; i < size; i++) {
                // Если мы обошли весь первый подмассив
                if (firstSubArrayIndex == size / 2) {
                    array[i] = secondSubArray[secondSubArrayIndex];
                    secondSubArrayIndex++;
                } else {
                    // Если мы обошли весь второй подмассив
                    if (secondSubArrayIndex == size - size / 2) {
                        array[i] = firstSubArray[firstSubArrayIndex];
                        firstSubArrayIndex++;
                    } else {
                        // Сравниваем элементы подмассивов между собой
                        // и записываем в исходный массив меньший элемент
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
        // Если в подмассиве два элемента
        if (subArray.length == 2) {
            // Отсортировать эти два элемента относительно друг друга
            Integer compareResult;
            if ((compareResult = compareElements(subArray[0], subArray[1])) != null && compareResult > 0) {
                Object swap;

                swap = subArray[0];
                subArray[0] = subArray[1];
                subArray[1] = swap;
            }
            return subArray;
        } else {
            // Если в подмассиве больше двух элементов
            if (subArray.length > 1) {
                //  Рекурсивно отсортировать первый подмассив
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
            for (int i = 0; i < 2; i++) {
                accessFile[OUTPUT_FILE].deleteFile(outputFilesName.get(index).getFileName());
                outputFilesName.remove(index);
            }
            //index += 2;
        }
        String outputFileName = outputFilePrefix + ".txt";
        if (accessFile[OUTPUT_FILE].renameFile(outputFilesName.get(index).getFileName(), outputFileName)) {
            System.out.printf("Program completed. Results in file: %s", outputFileName);
        }
        accessFile[OUTPUT_FILE].clearCurrentReadString();

    }

    public ConventionalFileName uniteSortedFiles(ConventionalFileName fileName1, ConventionalFileName fileName2) {

        ConventionalFileName outputFileName = getOutputConventionalFileName(fileName1, fileName2, outputFilePrefix + "_" + outputFilesName.size());
        clearOldFileReadData();

        boolean isFirstCycle = true;
        while (!isFileEmpty[FIRST_FILE] || !isFileEmpty[SECOND_FILE]) {
            int outputBuilderLinesCount = 0;
            while (outputBuilderLinesCount < bufferSize) {
                if (fileQueue.get(FIRST_FILE).isEmpty()) {
                    if (!isFileEmpty[FIRST_FILE]) {
                        if ((dataBuffer[FIRST_FILE] = accessFile[FIRST_FILE].readFromFile(bufferSize / 2, fileName1.getFileName())) == null) {
                            isFileEmpty[FIRST_FILE] = true;
                        } else {
                            fileQueue.get(FIRST_FILE).addAll(convertReadDataToCollection(dataBuffer[FIRST_FILE]));
                        }
                    } else {
                        if (fileQueue.get(SECOND_FILE).isEmpty()) {
                            if (!isFileEmpty[SECOND_FILE]) {
                                if ((dataBuffer[SECOND_FILE] = accessFile[SECOND_FILE].readFromFile(bufferSize / 2, fileName2.getFileName())) == null) {
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
                            if ((dataBuffer[SECOND_FILE] = accessFile[SECOND_FILE].readFromFile(bufferSize / 2, fileName2.getFileName())) == null) {
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

    protected void clearOldFileReadData() {
        for (int i = 0; i < 3; i++) {
            this.accessFile[i].clearCurrentReadString();
        }
        this.dataBuffer[OUTPUT_FILE].setLength(0);
        for (int i = 0; i < 2; i++) {
            this.isFileEmpty[i] = false;
            this.fileQueue.get(i).clear();
        }
    }

    protected ConventionalFileName getOutputConventionalFileName(ConventionalFileName fileName1, ConventionalFileName fileName2, String outputFilePrefix) {
        return new ConventionalFileName(fileName1.getMinFileNumber() * ascendingSort > fileName2.getMinFileNumber() * ascendingSort ? fileName1.getMinFileNumber() : fileName2.getMinFileNumber(),
                fileName1.getMaxFileNumber() * ascendingSort > fileName2.getMaxFileNumber() * ascendingSort ? fileName1.getMaxFileNumber() : fileName2.getMaxFileNumber(),
                outputFilePrefix);
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

        return new ConventionalFileName(firstString.hashCode(), lastString.hashCode(), outputFilePrefix + "_" + fileCount);
    }

    private StringBuilder arrayToStringBuilder(Object[] array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object object : array) {
            stringBuilder.append(object).append("\n");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder;
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

    public void setAscendingSort(int ascendingSort) {
        this.ascendingSort = ascendingSort;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
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
}
