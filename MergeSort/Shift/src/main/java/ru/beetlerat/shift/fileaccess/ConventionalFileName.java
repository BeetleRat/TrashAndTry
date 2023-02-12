package ru.beetlerat.shift.fileaccess;

public class ConventionalFileName implements Comparable<ConventionalFileName> {
    private int minFileNumber;
    private int maxFileNumber;
    private String fileName;
    private String clearFileName;

    public static ConventionalFileName createConventionalFileNameFromStringBuilder(StringBuilder data, String outputFilePrefix, int fileCount) {
        String firstString = null;
        String lastString = null;

        if (data.indexOf("\n") == -1) {
            if (data.length() == 0) {
                return null;
            } else {
                firstString = data.toString();
                lastString = firstString;
            }
        } else {
            firstString = data.substring(0, Math.min(data.indexOf("\n"), 20));
            lastString = data.substring(data.lastIndexOf("\n") + 1, data.length());
        }
        if (firstString == null | lastString == null || firstString.length() == 0 | lastString.length() == 0) {
            return null;
        }

        return new ConventionalFileName(firstString.hashCode(), lastString.hashCode(), outputFilePrefix + "_" + fileCount);
    }

    public static ConventionalFileName createConventionalFileNameFromOtherNames(ConventionalFileName fileName1, ConventionalFileName fileName2, int ascendingSort, String outputFilePrefix) {
        return new ConventionalFileName(fileName1.getMinFileNumber() * ascendingSort > fileName2.getMinFileNumber() * ascendingSort ? fileName1.getMinFileNumber() : fileName2.getMinFileNumber(),
                fileName1.getMaxFileNumber() * ascendingSort > fileName2.getMaxFileNumber() * ascendingSort ? fileName1.getMaxFileNumber() : fileName2.getMaxFileNumber(),
                outputFilePrefix);
    }

    public ConventionalFileName(int minFileNumber, int maxFileNumber, String fileNamePrefix) {
        this.minFileNumber = minFileNumber;
        this.maxFileNumber = maxFileNumber;
        this.fileName = fileNamePrefix + "_" + minFileNumber + "!" + maxFileNumber + ".txt";
        this.clearFileName = fileNamePrefix + ".txt";
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
        this.fileName = fileNamePrefix + "_" + minFileNumber + "!" + maxFileNumber + ".txt";
        this.clearFileName = fileNamePrefix + ".txt";
    }

    @Override
    public int compareTo(ConventionalFileName o) {
        return o.getMinFileNumber() - this.minFileNumber;
    }
}
