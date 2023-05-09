package FilamentCounter;

import FilamentCounter.modell.FoundPeaksDTO;
import ij.IJ;
import ij.gui.Plot;
import ij.plugin.filter.MaximumFinder;

import java.awt.*;

public class BarFindPeaks {
    private double tolerance = 0d;
    private double minPeakDistance =0d;
    private double minMaximaValue = Double.NaN;
    private double maxMinimaValue = -1;
    private boolean excludeOnEdges = false;
    boolean listValues = false;

    private double[] xvalues;
    private double[] yvalues;

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

    private int[] findPositions(double[] values, double tolerance, boolean minima) {
        int[] positions = null;
        MaximumFinder maxFinder = new MaximumFinder();
        if (minima)
            positions = maxFinder.findMinima(values, tolerance, excludeOnEdges);
        else
            positions = maxFinder.findMaxima(values, tolerance, excludeOnEdges);
        return positions;
    }

    private int[] findMaxima(double[] values, double tolerance) {
        return findPositions(values, tolerance, false);
    }

    private int[] findMinima(double[] values, double tolerance) {
        return findPositions(values, tolerance, true);
    }

    private double[] getCoordinates(double[] values, int[] positions) {
        int size = positions.length;
        double[] cc = new double[size];
        for (int i=0; i<size; i++)
            cc[i] = values[ positions[i] ];
        return cc;
    }



    private int[] trimPeakHeight(int[] positions, boolean minima) {
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

    private boolean filteredHeight(double height, boolean minima) {
        if (minima)
            return (height < maxMinimaValue);
        else
            return (height > minMaximaValue);
    }

    private int[] trimPeakDistance(int[] positions) {
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


    public FoundPeaksDTO run() {

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

//        plot.draw();
//        plot.show();
        FoundPeaksDTO output=new FoundPeaksDTO(xMaxima.length,plot);
        return output;

//        if (plotTitle.startsWith("Peaks in"))
//            pw.drawPlot(plot);
//        else
//            pw = plot.show();
//        if (listValues)
//            pw.getResultsTable().show("Plot Values");

    }
}
