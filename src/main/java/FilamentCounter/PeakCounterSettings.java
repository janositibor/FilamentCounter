package FilamentCounter;

public class PeakCounterSettings {
    private double alpha;
    private double beta;
    private double gamma;

    public PeakCounterSettings(double alpha, double beta, double gamma) {
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getBeta() {
        return beta;
    }

    public double getGamma() {
        return gamma;
    }
}
