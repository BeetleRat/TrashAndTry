package ru.beetlerat.shift.fileaccess;

public class ConventionalFileName implements Comparable<ConventionalFileName> {
    private int minFileNumber;
    private int maxFileNumber;
    private String fileName;
    private String clearFileName;

    public ConventionalFileName(int minFileNumber, int maxFileNumber, String fileNamePrefix) {
        this.minFileNumber = minFileNumber;
        this.maxFileNumber = maxFileNumber;
        this.fileName = fileNamePrefix+"_"+minFileNumber+"!"+maxFileNumber+".txt";
        this.clearFileName=fileNamePrefix+".txt";
    }

    public int getMinFileNumber() {
        return minFileNumber;
    }
    public int getMaxFileNumber() {
        return maxFileNumber;
    }
    public String getFileName() {
        return fileName;
    }
    public String getClearFileName() {
        return clearFileName;
    }

    public void setMinFileNumber(int minFileNumber) {
        this.minFileNumber = minFileNumber;
    }
    public void setMaxFileNumber(int maxFileNumber) {
        this.maxFileNumber = maxFileNumber;
    }
    public void setFileName(String fileNamePrefix) {
        this.fileName = fileNamePrefix+"_"+minFileNumber+"!"+maxFileNumber+".txt";
        this.clearFileName=fileNamePrefix+".txt";
    }

    @Override
    public int compareTo(ConventionalFileName o) {
        return o.getMinFileNumber()-this.minFileNumber;
    }
}
