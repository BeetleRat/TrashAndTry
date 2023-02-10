package ru.beetlerat.shift;

import ru.beetlerat.shift.fileaccess.AccessFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 */
public class Shift {
    // 1. режим сортировки (-a или -d), необязательный, по умолчанию сортируем по возрастанию;
    // 2. тип данных (-s или -i), обязательный;
    // 3. имя выходного файла, обязательное;
    // 4. остальные параметры – имена входных файлов, не менее одного.
    public static void main(String[] args) {
        new FileSort(args,2);


    }
}
