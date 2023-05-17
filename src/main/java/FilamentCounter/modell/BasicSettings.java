package FilamentCounter.modell;

import FilamentCounter.modell.SettingForCalculationDTO;

public class BasicSettings {
    public static int NUMBER_OF_LINES_TO_CALCULATE_FILAMENTS=5;
    public static double EPSILON_FOR_LINE_VERTICALITY=0.00000001;
    public static double EPSILON_FOR_SIDES_DEVIATION=0.0000001;
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
