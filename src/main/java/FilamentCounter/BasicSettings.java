package FilamentCounter;

import FilamentCounter.modell.SettingForCalculationDTO;

public class BasicSettings {
    static int NUMBER_OF_LINES_TO_CALCULATE_FILAMENTS=5;
    private double pixelSize;
    private String directoryName;
    private SettingForCalculationDTO settingForCalculation;

    public BasicSettings(double pixelSize, double amplitude, double minHeight, double minDistance) {
        this.pixelSize = pixelSize;
        this.settingForCalculation=new SettingForCalculationDTO(pixelSize,amplitude,minHeight,minDistance);
    }



    public double getPixelSize() {
        return pixelSize;
    }


    public String getDirectoryName() {
        return directoryName;
    }

    public void setDirectoryName(String directoryName) {
        this.directoryName = directoryName;
    }

    public SettingForCalculationDTO getSettingForCalculation() {
        return settingForCalculation;
    }
}
