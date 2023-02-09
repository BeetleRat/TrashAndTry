package ru.beetlerat.shift.mergesort;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MyMergeSortInt extends MyMergeSort {
    private int[] intArray;


    private void mergeSort(int[] array) {
        int size = array.length;
        if (size > 1) {
            // Дробим массив на два подмассива
            int[] firstSubArray = new int[size / 2];
            System.arraycopy(array, 0, firstSubArray, 0, size / 2);
            int[] secondSubArray = new int[size - size / 2];
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

    private void sortSubArray(int[] subArray) {
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

    private int[] stringBuilderToIntArray(StringBuilder stringBuilder) {
        String[] stringArray = stringBuilder.toString().split("\n");
        List<Integer> intArrayList = new ArrayList<>();
        for (String stringNumber : stringArray) {
            try {
                intArrayList.add(Integer.parseInt(stringNumber));
            } catch (NumberFormatException e) {

            }
        }
        int[] numbersArray = new int[intArrayList.size()];
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
            resultString.delete(resultString.length() - 1, resultString.length());
        }
        return resultString;
    }
}
