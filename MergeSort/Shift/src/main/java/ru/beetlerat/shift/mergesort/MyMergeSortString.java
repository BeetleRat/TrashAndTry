package ru.beetlerat.shift.mergesort;

import ru.beetlerat.shift.fileaccess.AccessFile;
import ru.beetlerat.shift.fileaccess.ConventionalFileName;

import java.io.File;
import java.util.*;

public class MyMergeSortString extends MyMergeSort {
    private String[] stringArray;

    private void mergeSort(String[] array) {
        int size = array.length;
        if (size > 1) {
            // Дробим массив на два подмассива
            String[] firstSubArray = new String[size / 2];
            System.arraycopy(array, 0, firstSubArray, 0, size / 2);
            String[] secondSubArray = new String[size - size / 2];
            System.arraycopy(array, size / 2, secondSubArray, 0, size - size / 2);

            // Сортируем первый подмассив
            sortSubArray(firstSubArray);
            // Сортируем второй подмассив
            sortSubArray(secondSubArray);

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
                        if (firstSubArray[firstSubArrayIndex].compareTo(secondSubArray[secondSubArrayIndex]) * ascendingSort > 0) {
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

    private void sortSubArray(String[] subArray) {
        int subArraySize = subArray.length;
        // Если в подмассиве два элемента
        if (subArraySize == 2) {
            // Отсортировать эти два элемента относительно друг друга
            if (subArray[0].compareTo(subArray[1]) * ascendingSort > 0) {
                String swap; // Переменная, для замены элементов в массиве

                swap = subArray[0];
                subArray[0] = subArray[1];
                subArray[1] = swap;
            }
        } else {
            // Если в подмассиве больше двух элементов
            if (subArraySize > 1) {
                //  Рекурсивно отсортировать первый подмассив
                mergeSort(subArray);
            }
        }
    }

    @Override
    public StringBuilder sort(StringBuilder sortedString) {
        StringBuilder resultString = new StringBuilder();
        stringArray = sortedString.toString().split("\n");

        mergeSort(stringArray);
        for (String s : stringArray) {
            resultString.append(s).append("\n");
        }

        resultString.delete(resultString.length() - 1, resultString.length());
        return resultString;
    }

    @Override
    public ConventionalFileName fileSort(ConventionalFileName fileName1, ConventionalFileName fileName2, boolean isFilesStoreInResources, int bufferSize, String outputFilePrefix) {

        ConventionalFileName outputFileName = getOutputConventionalFileName(fileName1,fileName2,outputFilePrefix);

        AccessFile[] accessFile = new AccessFile[3];
        StringBuilder[] stringBuffer = new StringBuilder[3];
        stringBuffer[OUTPUT_FILE] = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            accessFile[i] = new AccessFile();
            accessFile[i].setFilesStoreInResources(isFilesStoreInResources);
        }

        boolean isFirstFileEmpty = false;
        boolean isSecondFileEmpty = false;
        Queue<String> firstFileQueue = new ArrayDeque<>();
        Queue<String> secondFileQueue = new ArrayDeque<>();


        boolean isFirstCycle = true;
        while (!isFirstFileEmpty || !isSecondFileEmpty) {
            int outputBuilderLinesCount = 0;
            while (outputBuilderLinesCount < bufferSize) {
                if (firstFileQueue.isEmpty()) {
                    if (!isFirstFileEmpty) {
                        if ((stringBuffer[FIRST_FILE] = accessFile[FIRST_FILE].readFromFiles(bufferSize / 2, Collections.singletonList(fileName1.getFileName()))) == null) {
                            isFirstFileEmpty = true;
                        } else {
                            firstFileQueue.addAll(List.of(stringBuffer[FIRST_FILE].toString().split("\n")));
                        }
                    } else {
                        if (secondFileQueue.isEmpty()) {
                            if (!isSecondFileEmpty) {
                                if ((stringBuffer[SECOND_FILE] = accessFile[SECOND_FILE].readFromFiles(bufferSize / 2, Collections.singletonList(fileName2.getFileName()))) == null) {
                                    isSecondFileEmpty = true;
                                } else {
                                    secondFileQueue.addAll(List.of(stringBuffer[SECOND_FILE].toString().split("\n")));
                                }
                            } else {
                                break;
                            }
                        } else {
                            stringBuffer[OUTPUT_FILE].append(secondFileQueue.remove()).append("\n");
                            outputBuilderLinesCount++;
                        }
                    }
                } else {
                    if (secondFileQueue.isEmpty()) {
                        if (!isSecondFileEmpty) {
                            if ((stringBuffer[SECOND_FILE] = accessFile[SECOND_FILE].readFromFiles(bufferSize / 2, Collections.singletonList(fileName2.getFileName()))) == null) {
                                isSecondFileEmpty = true;
                            } else {
                                secondFileQueue.addAll(List.of(stringBuffer[SECOND_FILE].toString().split("\n")));
                            }
                        } else {
                            stringBuffer[OUTPUT_FILE].append(firstFileQueue.remove()).append("\n");
                            outputBuilderLinesCount++;
                        }
                    } else {
                        if (firstFileQueue.element().compareTo(secondFileQueue.element()) * ascendingSort > 0) {
                            stringBuffer[OUTPUT_FILE].append(secondFileQueue.remove()).append("\n");
                            outputBuilderLinesCount++;
                        } else {
                            stringBuffer[OUTPUT_FILE].append(firstFileQueue.remove()).append("\n");
                            outputBuilderLinesCount++;
                        }
                    }
                }
            }
            if (stringBuffer[OUTPUT_FILE].length() > 0) {
                stringBuffer[OUTPUT_FILE].setLength(stringBuffer[OUTPUT_FILE].length() - 1);
                if (!isFirstCycle) {
                    stringBuffer[OUTPUT_FILE].insert(0, "\n");
                } else {
                    isFirstCycle = false;
                }
                accessFile[0].appendToFile(stringBuffer[OUTPUT_FILE], outputFileName.getFileName());
                stringBuffer[OUTPUT_FILE].setLength(0);
            }
        }
        return isFirstCycle ? null : outputFileName;
    }
}
