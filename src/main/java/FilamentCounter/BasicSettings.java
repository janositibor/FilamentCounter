package FilamentCounter;

public class BasicSettings {
    static int NUMBER_OF_LINES_TO_CALCULATE_FILAMENTS=5;
    private double pixelSize;
    private String directoryName;
    private PeakCounterSettings peakCounterSettings;

    public BasicSettings(double pixelSize, double alpha, double beta, double gamma) {
        this.pixelSize = pixelSize;
        this.peakCounterSettings=new PeakCounterSettings(alpha, beta, gamma);
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

    public PeakCounterSettings getPeakCounterSettings() {
        return peakCounterSettings;
    }
}
