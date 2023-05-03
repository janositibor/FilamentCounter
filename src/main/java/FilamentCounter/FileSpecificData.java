package FilamentCounter;

public class FileSpecificData {
    static final String SEPARATOR=";";
    private String fileNameAndPath;
    private double length=0;
    private double filamentDensity=0;

    public FileSpecificData(String fileNameAndPath) {
        this.fileNameAndPath = fileNameAndPath;
    }

    public String getFileNameAndPath() {
        return fileNameAndPath;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getFilamentDensity() {
        return filamentDensity;
    }

    public void setFilamentDensity(double filamentDensity) {
        this.filamentDensity = filamentDensity;
    }

    @Override
    public String toString() {
        return fileNameAndPath + SEPARATOR + length + SEPARATOR + filamentDensity;
    }
}
