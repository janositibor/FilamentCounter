package FilamentCounter.modell;

public class FileResultDTO {
    public static final String SEPARATOR=";";
    private String fileNameAndPath;
    private double length;
    private int numberOfPeaks;
    private double filamentsDensity;

    public FileResultDTO(String fileNameAndPath, double length, int numberOfPeaks) {
        this.fileNameAndPath = fileNameAndPath;
        this.length = length;
        this.numberOfPeaks = numberOfPeaks;
        filamentsDensity=numberOfPeaks/length;
    }

    @Override
    public String toString() {
        return fileNameAndPath + SEPARATOR + length + SEPARATOR + numberOfPeaks + SEPARATOR + filamentsDensity;
    }
}
