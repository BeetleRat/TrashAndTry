package ru.beetlerat.shift.mergesort;

import ru.beetlerat.shift.fileaccess.AccessFile;
import ru.beetlerat.shift.fileaccess.ConventionalFileName;

import java.util.*;

public class MyMergeSortInt extends MyMergeSort {
    private Integer[] intArray;


    private void mergeSort(Integer[] array) {
        int size = array.length;
        if (size > 1) {
            // Дробим массив на два подмассива
            Integer[] firstSubArray = new Integer[size / 2];
            System.arraycopy(array, 0, firstSubArray, 0, size / 2);
            Integer[] secondSubArray = new Integer[size - size / 2];
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
                        if (firstSubArray[firstSubArrayIndex] * ascendingSort > (secondSubArray[secondSubArrayIndex]) * ascendingSort) {
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

    private void sortSubArray(Integer[] subArray) {
        int subArraySize = subArray.length;
        // Если в подмассиве два элемента
        if (subArraySize == 2) {
            // Отсортировать эти два элемента относительно друг друга
            if (subArray[0] * ascendingSort > subArray[1] * ascendingSort) {
                int swap; // Переменная, для замены элементов в массиве

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

    private Integer[] stringBuilderToIntArray(StringBuilder stringBuilder) {
        String[] stringArray = stringBuilder.toString().split("\n");
        List<Integer> intArrayList = new ArrayList<>();
        for (String stringNumber : stringArray) {
            try {
                intArrayList.add(Integer.parseInt(stringNumber.trim()));
            } catch (NumberFormatException e) {

            }
        }
        Integer[] numbersArray = new Integer[intArrayList.size()];
        for (int i = 0; i < intArrayList.size(); i++) {
            numbersArray[i] = intArrayList.get(i);
        }
        return numbersArray;
    }

    @Override
    public StringBuilder sort(StringBuilder sortedString) {
        StringBuilder resultString = new StringBuilder();
        intArray = stringBuilderToIntArray(sortedString);

        mergeSort(intArray);
        for (Integer integer : intArray) {
            resultString.append(integer).append("\n");
        }

        if (resultString.length() > 0) {
            resultString.setLength(resultString.length() - 1);
        }
        return resultString;
    }

    @Override
    public ConventionalFileName fileSort(ConventionalFileName fileName1, ConventionalFileName fileName2, boolean isFilesStoreInResources, int bufferSize, String outputFilePrefix) {

        ConventionalFileName outputFileName = getOutputConventionalFileName(fileName1, fileName2, outputFilePrefix);

        AccessFile[] accessFile = new AccessFile[3];
        StringBuilder[] stringBuffer = new StringBuilder[3];
        for (int i = 0; i < 3; i++) {
            accessFile[i] = new AccessFile();
            accessFile[i].setFilesStoreInResources(isFilesStoreInResources);
            accessFile[i].clearCurrentReadString();
        }

        stringBuffer[OUTPUT_FILE] = new StringBuilder();
        boolean isFirstFileEmpty = false;
        boolean isSecondFileEmpty = false;
        Queue<Integer> firstFileQueue = new ArrayDeque<>();
        Queue<Integer> secondFileQueue = new ArrayDeque<>();


        boolean isFirstCycle = true;
        while (!isFirstFileEmpty || !isSecondFileEmpty) {
            int outputBuilderLinesCount = 0;
            while (outputBuilderLinesCount < bufferSize) {
                if (firstFileQueue.isEmpty()) {
                    if (!isFirstFileEmpty) {
                        if ((stringBuffer[FIRST_FILE] = accessFile[FIRST_FILE].readFromFiles(bufferSize / 2, Collections.singletonList(fileName1.getFileName()))) == null) {
                            isFirstFileEmpty = true;
                        } else {
                            firstFileQueue.addAll(List.of(stringBuilderToIntArray(stringBuffer[FIRST_FILE])));
                        }
                    } else {
                        if (secondFileQueue.isEmpty()) {
                            if (!isSecondFileEmpty) {
                                if ((stringBuffer[SECOND_FILE] = accessFile[SECOND_FILE].readFromFiles(bufferSize / 2, Collections.singletonList(fileName2.getFileName()))) == null) {
                                    isSecondFileEmpty = true;
                                } else {
                                    secondFileQueue.addAll(List.of(stringBuilderToIntArray(stringBuffer[SECOND_FILE])));
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
                                secondFileQueue.addAll(List.of(stringBuilderToIntArray(stringBuffer[SECOND_FILE])));
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
