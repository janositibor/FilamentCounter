package FilamentCounter;

public class BasicData {
    private double pixelSize;
    private String directoryName;

    public BasicData(double pixelSize) {
        this.pixelSize = pixelSize;
    }

    public BasicData(double pixelSize, String directoryName) {
        this.pixelSize = pixelSize;
        this.directoryName = directoryName;
    }

    public double getPixelSize() {
        return pixelSize;
    }

    public void setPixelSize(double pixelSize) {
        this.pixelSize = pixelSize;
    }

    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }
}
