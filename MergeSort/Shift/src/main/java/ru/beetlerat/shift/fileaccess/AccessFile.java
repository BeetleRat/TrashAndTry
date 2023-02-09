package ru.beetlerat.shift.fileaccess;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class AccessFile {
    private Path resourceDirectory;
    private ReadStringCounter currentReadLine;

    public AccessFile() {
        this.currentReadLine = new ReadStringCounter();

        // Получаем путь к директории resources
        if (getClass().getClassLoader().getResource("") != null) {
            try {
                this.resourceDirectory = Paths.get(getClass().getClassLoader().getResource("").toURI());
            } catch (InvalidPathException | URISyntaxException e) {
                this.resourceDirectory = null;
                System.out.println("Could not get resources directory: " + e);
            }
        } else {
            this.resourceDirectory = null;
            System.out.println("Could not get resources directory");
        }

    }

    public void clearCurrentReadString() {
        this.currentReadLine.clear();
    }

    public String readFirstStringFromFile( boolean isFilesStoreInResources, String fileName) {
        currentReadLine.clear();
        StringBuilder firstStringFromFile=new StringBuilder();
        readFromFiles(firstStringFromFile, isFilesStoreInResources, 1, Collections.singletonList(fileName));
        return firstStringFromFile.toString();
    }

    public boolean readFromFiles(StringBuilder stringFromFile, boolean isFilesStoreInResources, List<String> fileNames) {

        clearCurrentReadString();

        readFromFiles(stringFromFile, isFilesStoreInResources, Integer.MAX_VALUE, fileNames);

        return currentReadLine.getTotalLinesRead() >= Integer.MAX_VALUE;
    }

    public boolean readFromFiles(StringBuilder stringFromFile, boolean isFilesStoreInResources, int lines, List<String> fileNames) {
        long maxReadStrings = Math.max(lines, 0) + currentReadLine.getTotalLinesRead();
        int fileIndex = currentReadLine.getFileID();

        if (isFilesStoreInResources) {
            while (fileIndex < fileNames.size() & currentReadLine.getTotalLinesRead() < maxReadStrings) {
                // Получить файл из папки с ресурсами
                if (resourceDirectory != null) {
                    File readableFile = new File(resourceDirectory + "/" + fileNames.get(fileIndex));
                    fileToStringBuilder(readableFile.getPath(), stringFromFile, maxReadStrings);
                    fileIndex = currentReadLine.getFileID();
                } else {
                    System.out.printf("Error! Could not find file in resources: %s\n", fileNames.get(fileIndex));
                    return false;
                }
            }
        } else {
            while (fileIndex < fileNames.size() & currentReadLine.getTotalLinesRead() < maxReadStrings) {
                fileToStringBuilder(fileNames.get(fileIndex), stringFromFile, maxReadStrings);
                fileIndex = currentReadLine.getFileID();
            }
        }

        return fileIndex < fileNames.size();
    }

    public void writeToFile(StringBuilder stringToFile, boolean isFileStoreInResources, String fileName) {
        writeToFile(stringToFile, isFileStoreInResources, fileName, false);
    }

    public void appendToFile(StringBuilder stringToFile, boolean isFileStoreInResources, String fileName) {
        writeToFile(stringToFile, isFileStoreInResources, fileName, true);
    }

    public boolean deleteFile(boolean isFileStoreInResources, String fileName){
        try {
            if(isFileStoreInResources){
                Files.delete(Paths.get(resourceDirectory+"/"+fileName));
            }
            else {
                Files.delete(Paths.get(fileName));
            }
        } catch (IOException e) {
            System.out.printf("Can not delete file: %s\n",fileName);
            System.out.println(e);
            return false;
        }
        return true;
    }

    private void writeToFile(StringBuilder stringToFile, boolean isFileStoreInResources, String fileName, boolean append) {
        File writableFile;
        if (isFileStoreInResources) {
            if (resourceDirectory != null) {
                writableFile = new File(resourceDirectory + "/" + fileName);
                stringBuilderToFile(stringToFile, writableFile, append);
            } else {
                System.out.println("Error! Could not get resources directory.");
            }
        } else {
            writableFile = new File(fileName);
            stringBuilderToFile(stringToFile, writableFile, append);
        }
    }

    private void stringBuilderToFile(StringBuilder stringBuilder, File file, boolean append) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            // Создаем объект BufferedReader для чтения файла filePath с кодировкой UTF_8
            BufferedWriter writeFileBuffer =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(file.getPath(), append),
                                    StandardCharsets.UTF_8));

            writeFileBuffer.write(stringBuilder.toString());
            writeFileBuffer.close();
        } catch (FileNotFoundException e) {
            System.out.printf("Error! Could not open file: %s\n", file.getPath());
        } catch (IOException e) {
            System.out.printf("Error! Could write data to file: %s\n", file.getPath());
        }
    }

    private void fileToStringBuilder(String filePath, StringBuilder stringBuilder, long maxReadStrings) {
        try {
            BufferedReader readFileBuffer =
                    new BufferedReader(
                            new InputStreamReader(
                                    new FileInputStream(filePath),
                                    StandardCharsets.UTF_8));


            for (int i = 0; i < currentReadLine.getLineNumber(); i++) {
                readFileBuffer.readLine();
            }

            String oneLine;
            while ((oneLine = readFileBuffer.readLine()) != null & currentReadLine.getTotalLinesRead() < maxReadStrings) {
                currentReadLine.increaseLine();
                stringBuilder.append(oneLine).append("\n");
            }
            if (currentReadLine.getTotalLinesRead() < maxReadStrings) {
                currentReadLine.increaseFileID();
            }
            readFileBuffer.close();
        } catch (FileNotFoundException e) {
            System.out.printf("Error! Could not open file: %s\n", filePath);
        } catch (IOException e) {
            System.out.printf("Error! Could not get data from file: %s\n", filePath);
        }
    }
}
