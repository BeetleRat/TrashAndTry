package ru.beetlerat.shift.mergesort;

public class MyMergeSortString extends MyMergeSort{
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
}
