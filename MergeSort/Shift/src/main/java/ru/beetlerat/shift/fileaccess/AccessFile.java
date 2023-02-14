package ru.beetlerat.shift.fileaccess;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class AccessFile {
    private static final String PROPERTIES_FILE_NAME = "application.properties";
    private final Path resourceDirectory;
    private ReadStringCounter currentReadLine;
    private boolean isFilesStoreInResources;

    public AccessFile() {
        this.currentReadLine = new ReadStringCounter();
        this.isFilesStoreInResources = true;
        this.resourceDirectory = getResourcesDirectory();
    }

    public boolean isFilesStoreInResources() {
        return isFilesStoreInResources;
    }

    public void setFilesStoreInResources(boolean filesStoreInResources) {
        isFilesStoreInResources = filesStoreInResources;
    }

    public void clearCurrentReadString() {
        this.currentReadLine.clear();
    }

    public String readFirstStringFromFile(String fileName) {
        currentReadLine.clear();
        StringBuilder resultString = readFromFiles(1, Collections.singletonList(fileName));
        return resultString == null ? null : resultString.toString();
    }

    public String readLastStringFromFile(String fileName) {
        if (isFilesStoreInResources & resourceDirectory == null) {
            System.out.println("Error! Could not get resources directory.");
            return null;
        }

        File file = new File(isFilesStoreInResources ? resourceDirectory + "/" + fileName : fileName);
        String result = null;
        try (RandomAccessFile readableFile = new RandomAccessFile(file, "r")) {
            long lineNumber = file.length() - 1;
            while (result == null || result.length() == 0) {
                readableFile.seek(lineNumber);
                readableFile.readLine();
                result = readableFile.readLine();
                lineNumber--;
            }
        } catch (FileNotFoundException e) {
            System.out.printf("Error! Could not open file: %s\n", file.getPath());
            return null;
        } catch (IOException e) {
            System.out.printf("Error! Could write data to file: %s\n", file.getPath());
            return null;
        }

        return result;
    }

    public StringBuilder readFromFile(String fileName) {
        clearCurrentReadString();
        return readFromFiles(Integer.MAX_VALUE, Collections.singletonList(fileName));
    }

    public StringBuilder readFromFiles(List<String> fileNames) {
        clearCurrentReadString();
        return readFromFiles(Integer.MAX_VALUE, fileNames);
    }

    public StringBuilder readFromFile(int lines, String fileName) {
        return readFromFiles(lines, Collections.singletonList(fileName));
    }

    public StringBuilder readFromFiles(int lines, List<String> fileNames) {
        StringBuilder resultString = new StringBuilder();

        if (isFilesStoreInResources & resourceDirectory == null) {
            System.out.println("Error! Could not get resources directory.");
            return null;
        }

        long maxReadStrings = Math.max(lines, 0) + currentReadLine.getTotalLinesRead();
        int fileIndex = currentReadLine.getFileID();

        while (fileIndex < fileNames.size() & currentReadLine.getTotalLinesRead() < maxReadStrings) {
            StringBuilder readFromFile = fileToStringBuilder(isFilesStoreInResources ? resourceDirectory + "/" + fileNames.get(fileIndex) : fileNames.get(fileIndex), maxReadStrings);
            if (readFromFile != null) {
                resultString.append(readFromFile);
            } else {
                currentReadLine.increaseFileID();
            }
            fileIndex = currentReadLine.getFileID();
        }

        if (resultString.length() == 0) {
            return null;
        } else {
            resultString.setLength(resultString.length() - 1);
            return resultString;
        }
    }

    public boolean writeToFile(StringBuilder stringToFile, String fileName) {
        return writeToFile(stringToFile, fileName, false);
    }

    public boolean appendToFile(StringBuilder stringToFile, String fileName) {
        return writeToFile(stringToFile, fileName, true);
    }

    public boolean deleteFile(String fileName) {
        try {
            Files.delete(Paths.get(isFilesStoreInResources ? resourceDirectory + "/" + fileName : fileName));
        } catch (IOException e) {
            System.out.printf("Can not delete file: %s\n", fileName);
            System.out.println(e);
            return false;
        }
        return true;
    }

    public boolean renameFile(String fileName, String newName) {
        File file = new File(isFilesStoreInResources ? resourceDirectory + "/" + fileName : fileName);
        File renamedFile = new File(isFilesStoreInResources ? resourceDirectory + "/" + newName : newName);
        if (renamedFile.exists()) {
            deleteFile(newName);
        }
        return file.renameTo(renamedFile);
    }

    public Map<String,String> readProperties() {
        // Устанавливаем значения по умолчанию
        Map<String,String> properties=new HashMap<>();
        properties.put("bufferSize","2500000");
        properties.put("saveTmpFiles","false");
        properties.put("withoutSpaces","true");

        // Считываем значения из свойств
        String filePath = isFilesStoreInResources
                ? resourceDirectory + "/" + PROPERTIES_FILE_NAME
                : PROPERTIES_FILE_NAME;
        try (FileInputStream propertiesFile =
                     new FileInputStream(filePath)
        ) {
            Properties property = new Properties();
            property.load(propertiesFile);

            properties.put("bufferSize",property.getProperty("numberOfLinesReadFromFilePerRequest".trim()));
            properties.put("saveTmpFiles",property.getProperty("saveTmpFiles".trim()));
            properties.put("withoutSpaces",property.getProperty("sortedStringWithoutSpaces".trim()));
        } catch (IOException e) {
            System.out.printf("Сan not find file %s. Start with default settings.\n", filePath);
        }
        return properties;
    }

    private boolean writeToFile(StringBuilder stringToFile, String fileName, boolean append) {
        File writableFile;

        if (isFilesStoreInResources & resourceDirectory == null) {
            System.out.println("Error! Could not get resources directory.");
            return false;
        }

        writableFile = new File(isFilesStoreInResources ? resourceDirectory + "/" + fileName : fileName);
        return stringBuilderToFile(stringToFile, writableFile, append);
    }

    private boolean stringBuilderToFile(StringBuilder stringBuilder, File file, boolean append) {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            BufferedWriter writeFileBuffer =
                    new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(file.getPath(), append),
                                    StandardCharsets.UTF_8));

            writeFileBuffer.write(stringBuilder.toString());
            writeFileBuffer.close();
        } catch (FileNotFoundException e) {
            System.out.printf("Error! Could not open file: %s\n", file.getPath());
            return false;
        } catch (IOException e) {
            System.out.printf("Error! Could write data to file: %s\n", file.getPath());
            return false;
        }
        return true;
    }

    private StringBuilder fileToStringBuilder(String filePath, long maxReadStrings) {
        StringBuilder resultString = new StringBuilder();

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
                resultString.append(oneLine).append("\n");
            }
            if (currentReadLine.getTotalLinesRead() < maxReadStrings) {
                currentReadLine.increaseFileID();
            }
            readFileBuffer.close();
        } catch (FileNotFoundException e) {
            System.out.printf("Error! Could not open file: %s\n", filePath);
            return null;
        } catch (IOException e) {
            System.out.printf("Error! Could not get data from file: %s\n", filePath);
            return null;
        }

        return resultString;
    }

    private Path getResourcesDirectory() {
        Path resourcesDirectoryPath = null;
        if (getClass().getClassLoader().getResource("") != null) {
            try {
                resourcesDirectoryPath = Paths.get(getClass().getClassLoader().getResource("").toURI());
            } catch (InvalidPathException | URISyntaxException e) {
                System.out.println("Warning. Could not get resources directory: " + e);
            }
        } else {
            System.out.println("Warning. Could not get resources directory");
        }

        return resourcesDirectoryPath;
    }
}
