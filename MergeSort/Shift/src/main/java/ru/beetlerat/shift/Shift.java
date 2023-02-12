package ru.beetlerat.shift;

public class Shift {

    public static void main(String[] args) {
        int numberOfLinesReadFromFilePerRequest=100000000;
        boolean isFileStoredInIDEAResourcesFolder=false;

        new FileSort(args,numberOfLinesReadFromFilePerRequest,isFileStoredInIDEAResourcesFolder);
    }
}
