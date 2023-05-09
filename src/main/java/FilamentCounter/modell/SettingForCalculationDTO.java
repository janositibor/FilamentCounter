package FilamentCounter.modell;

public class SettingForCalculationDTO {
    private double pixelSize;
    private double amplitude;
    private double minHeight;
    private double minDistance;

    public SettingForCalculationDTO(double pixelSize, double amplitude, double minHeight, double minDistance) {
        this.pixelSize = pixelSize;
        this.amplitude = amplitude;
        this.minHeight = minHeight;
        this.minDistance = minDistance;
    }

    public double getPixelSize() {
        return pixelSize;
    }

    public double getAmplitude() {
        return amplitude;
    }

    public double getMinHeight() {
        return minHeight;
    }

    public double getMinDistance() {
        return minDistance;
    }
}
