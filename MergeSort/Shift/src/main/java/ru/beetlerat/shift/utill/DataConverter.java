package ru.beetlerat.shift.utill;

public class DataConverter {
    public static StringBuilder arrayToStringBuilder(Object[] array) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object object : array) {
            stringBuilder.append(object).append("\n");
        }
        stringBuilder.setLength(stringBuilder.length() - 1);
        return stringBuilder;
    }
}
