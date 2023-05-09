package FilamentCounter.modell;

import ij.gui.Plot;

public class FoundPeaksDTO {
    private int numberOfPeaks;
    private Plot intensityProfileWithSelectedPeaks;

    public FoundPeaksDTO(int numberOfPeaks, Plot intensityProfileWithSelectedPeaks) {
        this.numberOfPeaks = numberOfPeaks;
        this.intensityProfileWithSelectedPeaks = intensityProfileWithSelectedPeaks;
    }

    public int getNumberOfPeaks() {
        return numberOfPeaks;
    }

    public Plot getIntensityProfileWithSelectedPeaks() {
        return intensityProfileWithSelectedPeaks;
    }
}
