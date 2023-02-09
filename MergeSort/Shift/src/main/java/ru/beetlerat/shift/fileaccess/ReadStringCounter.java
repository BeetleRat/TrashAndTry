package ru.beetlerat.shift.fileaccess;

public class ReadStringCounter {
    private int fileID;
    private int lineNumber;
    private long totalLinesRead;

    public ReadStringCounter() {
        fileID = 0;
        lineNumber = 0;
        totalLinesRead = 0;
    }

    public void clear() {
        fileID = 0;
        lineNumber = 0;
        totalLinesRead = 0;
    }

    public int getFileID() {
        return fileID;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public long getTotalLinesRead() {
        return totalLinesRead;
    }

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void setTotalLinesRead(int totalLinesRead) {
        this.totalLinesRead = totalLinesRead;
    }

    public void increaseLine() {
        lineNumber++;
        totalLinesRead++;
    }

    public void increaseFileID() {
        fileID++;
        lineNumber = 0;
    }
}
