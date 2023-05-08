package FilamentCounter;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.gui.ImageWindow;
import ij.gui.Plot;
import ij.gui.PlotWindow;
import ij.plugin.filter.MaximumFinder;
import ij.util.ArrayUtil;
import ij.util.Tools;

import java.awt.*;

public class BarFindPeaks {
    double tolerance = 0d;
    double minPeakDistance =0d;
    double minMaximaValue = Double.NaN;
    double maxMinimaValue = -1;
    boolean excludeOnEdges = false;
    boolean listValues = false;

    double[] xvalues;
    double[] yvalues;

    public BarFindPeaks(double tolerance, double minPeakDistance, double minMaximaValue) {
        this.tolerance = tolerance;
        this.minPeakDistance = minPeakDistance;
        this.minMaximaValue = minMaximaValue;
    }

    public void setXvalues(double[] xvalues) {
        this.xvalues = xvalues;
    }

    public void setYvalues(double[] yvalues) {
        this.yvalues = yvalues;
    }

    int[] findPositions(double[] values, double tolerance, boolean minima) {
        int[] positions = null;
        MaximumFinder maxFinder = new MaximumFinder();
        if (minima)
            positions = maxFinder.findMinima(values, tolerance, excludeOnEdges);
        else
            positions = maxFinder.findMaxima(values, tolerance, excludeOnEdges);
        return positions;
    }

    int[] findMaxima(double[] values, double tolerance) {
        return findPositions(values, tolerance, false);
    }

    int[] findMinima(double[] values, double tolerance) {
        return findPositions(values, tolerance, true);
    }

    double[] getCoordinates(double[] values, int[] positions) {
        int size = positions.length;
        double[] cc = new double[size];
        for (int i=0; i<size; i++)
            cc[i] = values[ positions[i] ];
        return cc;
    }

    boolean prompt() {
        GenericDialog gd = new GenericDialog("Find Local Maxima/Minima...");
        gd.addNumericField("Min._peak_amplitude:", tolerance, 2);
        gd.addNumericField("Min._peak_distance:", minPeakDistance, 2);
        gd.addNumericField("Min._value of maxima:", minMaximaValue, 2, 6, "(NaN: no filtering)");
        gd.addNumericField("Max._value of minima:", maxMinimaValue, 2, 6, "(NaN: no filtering)");
        gd.addCheckbox("Exclude peaks on edges of plot", excludeOnEdges);
        gd.addCheckbox("List values", listValues);
        gd.addHelp("http://imagej.net/Find_Peaks");
        gd.showDialog();
        tolerance = gd.getNextNumber();
        minPeakDistance = gd.getNextNumber();
        minMaximaValue = gd.getNextNumber();
        maxMinimaValue = gd.getNextNumber();
        excludeOnEdges = gd.getNextBoolean();
        listValues = gd.getNextBoolean();
        return !gd.wasCanceled();
    }

    int[] trimPeakHeight(int[] positions, boolean minima) {
        int size1 = positions.length; int size2 = 0;
        for (int i=0; i<size1; i++) {
            if ( filteredHeight(yvalues[positions[i]], minima) )
                size2++;
            else
                break; // positions are sorted by amplitude
        }
        int[] newpositions = new int[size2];
        for (int i=0; i<size2; i++)
            newpositions[i] = positions[i];
        return newpositions;
    }

    boolean filteredHeight(double height, boolean minima) {
        if (minima)
            return (height < maxMinimaValue);
        else
            return (height > minMaximaValue);
    }

    int[] trimPeakDistance(int[] positions) {
        int size = positions.length;
        int[] temp = new int[size];
        int newsize = 0;
        for (int i=size-1; i>=0; i--) {
            int pos1 = positions[i];
            boolean trim = false;
            for (int j=i-1; j>=0; j--) {
                int pos2 = positions[j];
                if (Math.abs(xvalues[pos2] - xvalues[pos1]) < minPeakDistance)
                { trim = true; break; }
            }
            if (!trim) temp[newsize++] = pos1;
        }
        int[] newpositions = new int[newsize];
        for (int i=0; i<newsize; i++)
            newpositions[i] = temp[i];
        return newpositions;
    }


    void run() {

//        PlotWindow pw;
//
//        ImagePlus imp = WindowManager.getCurrentImage();
//        if (imp==null)
//        { IJ.error("There are no plots open."); return; }
//        ImageWindow win = imp.getWindow();
//        if (win!=null && (win instanceof PlotWindow)) {
//            pw = (PlotWindow)win;
//            float[] fyvalues = pw.getYValues();
//            ArrayUtil stats = new ArrayUtil(fyvalues);
//            tolerance = Math.sqrt(stats.getVariance());
//            yvalues = Tools.toDouble(fyvalues);
//            xvalues = Tools.toDouble(pw.getXValues());
//        } else {
//            IJ.error(imp.getTitle() +" is not a plot window.");
//            return;
//        }

        int[] maxima = findMaxima(yvalues, tolerance);
        int[] minima = findMinima(yvalues, tolerance);
        if (!Double.isNaN(minMaximaValue))
            maxima = trimPeakHeight(maxima, false);
        if (!Double.isNaN(maxMinimaValue))
            minima = trimPeakHeight(minima, true);
        if (minPeakDistance>0) {
            maxima = trimPeakDistance(maxima);
            minima = trimPeakDistance(minima);
        }
        double[] xMaxima = getCoordinates(xvalues, maxima);
        double[] yMaxima = getCoordinates(yvalues, maxima);
        double[] xMinima = getCoordinates(xvalues, minima);
        double[] yMinima = getCoordinates(yvalues, minima);

        String plotTitle = "IntensityProfile";
        Plot plot = new Plot("Peaks in "+ plotTitle, "", "");
        plot.add("Line",xvalues, yvalues);
        plot.setLineWidth(2);
        plot.setColor(Color.RED);
        plot.addPoints(xMaxima, yMaxima, Plot.CIRCLE);
        plot.addLabel(0.00, 0, maxima.length +" maxima");
        plot.setColor(Color.BLUE);
        plot.addPoints(xMinima, yMinima, Plot.CIRCLE);
        plot.addLabel(0.25, 0, minima.length +" minima");
        plot.setColor(Color.BLACK);
        plot.addLabel(0.50, 0, "Min. amp.: "+ IJ.d2s(tolerance,2) +"  Min. dx.: "+ IJ.d2s(minPeakDistance,2) );
        plot.setLineWidth(1);

        plot.draw();
        plot.show();

//        if (plotTitle.startsWith("Peaks in"))
//            pw.drawPlot(plot);
//        else
//            pw = plot.show();
//        if (listValues)
//            pw.getResultsTable().show("Plot Values");

    }
}
