package FilamentCounter;

import FilamentCounter.modell.Coordinates;
import FilamentCounter.modell.FoundPeaksDTO;
import FilamentCounter.modell.LineForFilamentsCounter;
import FilamentCounter.modell.SettingForCalculationDTO;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.*;
import ij.io.Opener;
import ij.plugin.frame.RoiManager;
import io.scif.services.DatasetIOService;
import io.scif.services.DefaultDatasetIOService;
import net.imagej.Dataset;
import net.imagej.ImageJ;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import ij.measure.ResultsTable;

public class FileSpecificData {

    static final String SEPARATOR=";";
    private String fileNameAndPath;
    private String fileName;
    private String extension;

    private double length=1;
    private int numberOfPeaks=0;
    private ImageJ imageJ=new ImageJ();
//    private DatasetIOService datasetIO;
    private IJ ij;

    private Dataset dataset;

    private DatasetIOService datasetIO=new DefaultDatasetIOService();

    Opener opener = new Opener();
    private ImagePlus image;

    private Roi roi;

    private List<LineForFilamentsCounter> sides =new ArrayList<>();
    private LineForFilamentsCounter controlLine1;
    private LineForFilamentsCounter controlLine2;

    private List<LineForFilamentsCounter> linesToCalculateFilaments =new ArrayList<>();

    private RoiManager roiManager = new RoiManager();

    private ResultsTable resultsTable = new ResultsTable();

    private double[] xValues;
    private double[] yValues;
    private List<Double> limitsList=new ArrayList<>();
    private Plot IntensityProfilePlot;
    private SettingForCalculationDTO settingForCalculation;



    public FileSpecificData(String fileNameAndPath,SettingForCalculationDTO settingForCalculation) {
        this.fileNameAndPath = fileNameAndPath;
        this.settingForCalculation=settingForCalculation;
//        roiManager = new RoiManager();
//        System.out.println("RoiManager konstruktor meghívva");
        roiManager.setVisible(true);
        setFileName();
        setExtension();
        loadImage();
        enhance();
        setRoi();
        setRoiCoordinates();
        setControlLines();
        setLinesToCalculateFilaments();
        getProfiles();
        findPeaks();
        finalizeIntensityProfile();
        saveUsedRois();
        closeFile();
    }

    private void finalizeIntensityProfile() {
        addLimitsToIntensityProfile(IntensityProfilePlot);
        saveIntensityProfile();
    }

    private void saveIntensityProfile() {
        ImagePlus plotImage=IntensityProfilePlot.getImagePlus();
        String newNameAndPath=fileNameAndPath.replace(fileName,"IntensityProfile_"+fileName).replace(extension,"bmp");
        IJ.saveAs(plotImage, "BMP", newNameAndPath);
    }

    private void addLimitsToIntensityProfile(Plot plot) {
        for (int i = 0; i < limitsList.size(); i++) {
            plot.setColor(Color.RED);
            plot.add("line",new double[]{limitsList.get(i),limitsList.get(i)},new double[]{0,255});
        }
    }

    private void findPeaks() {
        BarFindPeaks barFindPeaks=new BarFindPeaks(0d,0d,200);
        barFindPeaks.setXvalues(xValues);
        barFindPeaks.setYvalues(yValues);

        FoundPeaksDTO foundPeaks=barFindPeaks.run();
        IntensityProfilePlot=foundPeaks.getIntensityProfileWithSelectedPeaks();
        setNumberOfPeaks(foundPeaks.getNumberOfPeaks());
    }

    private void closeFile() {
        image.clone();
        roiManager.close();
    }

    private void setFileName(){
        int indexOfLastSlash=fileNameAndPath.lastIndexOf("/");
        if(indexOfLastSlash<0){
            indexOfLastSlash=fileNameAndPath.lastIndexOf("\\");
        }
        fileName=fileNameAndPath.substring(indexOfLastSlash+1);
    }

    private void setExtension(){
        int indexOfLastDot=fileNameAndPath.lastIndexOf(".");
        if(indexOfLastDot<0){
            extension="";
        }
//        System.out.println(fileNameAndPath + "->" + fileNameAndPath.substring(indexOfLastDot+1));
        extension=fileNameAndPath.substring(indexOfLastDot+1);
    }
    private void saveUsedRois() {
//        IJ.run(image, "Properties... ", "  width=20");

        for (int i = 0; i < roiManager.getCount(); i++) {
            // get roi line profile and add to results table
//            image.setRoi(roiManager.getRoi(i));
            roiManager.select(image,i);
            IJ.run(image, "Properties... ", "  width=10");
            image=image.flatten();

//            profiler = new ProfilePlot(imp)
//            profile = profiler.getProfile()
//            for (j = 0; j < profile.length; j++)
//                rt.setValue("line" + i, j, profile[j])
        }
//        roiManager.runCommand(image,"Deselect");
//        roiManager.select(2);

//        roiManager.deselect();
//        roiManager.setSelectedIndexes(new int[]{0,1,2,3,4});
//        roiManager.select(image,2);
//        IJ.run(image, "Properties... ", "  width=20");
//         image=image.flatten();
//        roiManager.select(image,4);
//        IJ.run(image, "Properties... ", "  width=20");
//        image=image.flatten();

//        roiManager.runCommand("Show All");
//        ImagePlus imp=image.flatten();
        String newNameAndPath=fileNameAndPath.replace(fileName,"ROIs_"+fileName).replace(extension,"bmp");
        IJ.saveAs(image, "BMP", newNameAndPath);
    }

    private void getProfiles() {
        ProfilePlot profiler ;
        double[] profile;
//        ImagePlus plot;
        List<Double> totalProfile=new ArrayList<>();
//        double[] totalProfile=new double[]{};
        for (int i = 0; i < roiManager.getCount(); i++) {
            // get roi line profile and add to results table
            image.setRoi(roiManager.getRoi(i));
            profiler = new ProfilePlot(image);
            profile = profiler.getProfile();
            for (int j = 0; j < profile.length; j++) {
                resultsTable.setValue("line" + i, j, profile[j]);
                totalProfile.add(profile[j]);
            }
        }
//        resultsTable.show("ResultsTable");

//        System.out.println(totalProfile);
//        System.out.println("Total length: "+totalProfile.size());
//        double[] yValues = totalProfile.toArray();
        xValues = IntStream.rangeClosed(1, totalProfile.size()).mapToDouble(i->settingForCalculation.getPixelSize()*i).toArray();
        yValues = totalProfile.stream().mapToDouble(d -> 255.0-d).toArray();

        setLength(settingForCalculation.getPixelSize()*totalProfile.size());

//        Plot.create("Simple Plot", "X", "Y", yValues);
//        System.out.println(resultsTable.getLastColumn());
        for (int i = 0; i <resultsTable.getLastColumn(); i++) {
            if(i==0){
                limitsList.add(settingForCalculation.getPixelSize()*resultsTable.getColumnAsDoubles(i).length);
            }
            else{
                limitsList.add(limitsList.get(i-1)+settingForCalculation.getPixelSize()*resultsTable.getColumnAsDoubles(i).length);
            }
        }
////        int limit = resultsTable.getColumn(0);
//        Plot plot=new Plot("Intensity profile","Distance (pixel)","Intensity");
//
//        plot.setColor(Color.BLACK);
//        plot.add("line",xValues,yValues);
//        //        java.lang.String title, java.lang.String xLabel, java.lang.String yLabel, double[] xValues, double[] yValues
////        plot.add("line",new double[]{2800.1,2800.2},new double[]{0,255});
//        for (int i = 0; i < limitsList.size(); i++) {
//            plot.setColor(Color.RED);
//            plot.add("line",new double[]{limitsList.get(i),limitsList.get(i)},new double[]{0,255});
//        }
////        plot.setColor(Color.RED);
////        plot.add("line",new double[]{0.1,12800.2},new double[]{0,255});
//        plot.draw();
////        plot.show();
//        ImagePlus plotImage=plot.getImagePlus();
//        String newNameAndPath=fileNameAndPath.replace(fileName,"IntensityProfile_"+fileName).replace(extension,"bmp");
//        IJ.saveAs(plotImage, "BMP", newNameAndPath);

//        resultsTable.show("results");
//        System.out.println(resultsTable);

//        roiManager.deselect();
//        profiler.getProfile();
//        profiler.createWindow();

//        roiManager.runCommand(image,"Multi Plot");

//        roiManager.runCommand("Select All");
//        profile = getProfile();
//
//        Plot.create("Intensity Profile", "Distance in Pixels", "Intensity", profile);
//        Plot.setLimits(0, profile.length, 0, 256);

    }

    private void setLinesToCalculateFilaments() {
        Coordinates begin;
        Coordinates end;
        LineForFilamentsCounter line;
        for (int i = 0; i <BasicSettings.NUMBER_OF_LINES_TO_CALCULATE_FILAMENTS; i++) {
            begin=controlLine1.equalPartCoordinates(i+1,BasicSettings.NUMBER_OF_LINES_TO_CALCULATE_FILAMENTS+1);
            end=controlLine2.equalPartCoordinates(i+1,BasicSettings.NUMBER_OF_LINES_TO_CALCULATE_FILAMENTS+1);
            line=new LineForFilamentsCounter(begin,end);
//            System.out.println(line);
            line.shrink(10);
//            System.out.println("Shrink után:");
//            System.out.println(line);
            linesToCalculateFilaments.add(line);

            image.setRoi(new Line(line.getBegin().getX(),line.getBegin().getY(),line.getEnd().getX(),line.getEnd().getY()));
            roiManager.addRoi(image.getRoi());
//            image.show();
        }
//        roiManager.save(fileNameAndPath+"_roi.zip");
    }

    private void setControlLines() {
        controlLine1=new LineForFilamentsCounter(sides.get(0).getBegin(),sides.get(1).getEnd());
        controlLine2=new LineForFilamentsCounter(sides.get(0).getEnd(),sides.get(1).getBegin());
    }

    private void loadImage() {
        image = opener.openImage(fileNameAndPath);
        image.show();
    }

    private void setRoiCoordinates(){
        Polygon boundary=roi.getPolygon();
        Coordinates begin;
        Coordinates end;
        LineForFilamentsCounter line;

        for (int i=0;i<boundary.npoints-1;i++) {
//            System.out.println(boundary.xpoints[i]+"; "+boundary.ypoints[i]);
            begin=new Coordinates(boundary.xpoints[i],boundary.ypoints[i]);
            end=new Coordinates(boundary.xpoints[i+1],boundary.ypoints[i+1]);
            line=new LineForFilamentsCounter(begin,end);
//            System.out.println("oldal"+i+": "+line);
            sides.add(line);
        }
        Collections.sort(sides);
//        System.out.println(sides.get(0).getLength());
//        System.out.println(sides.get(1).getLength());
    }

    private void setRoi(){
        roi=image.getRoi();
    }

    private void enhance(){
        int blocksize = 127;
        int histogram_bins = 256;
        int maximum_slope = 3;
        String mask = "*None*";
        boolean fast = true;

        String parameters =
                "blocksize=" + blocksize +
                        " histogram=" + histogram_bins +
                        " maximum=" + maximum_slope +
                        " mask=" + mask;

        if ( fast )
            parameters += " fast_(less_accurate)";

        ij.run(image,"Enhance Local Contrast (CLAHE)", parameters );
//        image.show();
    }

    public String getFileNameAndPath() {
        return fileNameAndPath;
    }

    public int getNumberOfPeaks() {
        return numberOfPeaks;
    }

    public void setNumberOfPeaks(int numberOfPeaks) {
        this.numberOfPeaks = numberOfPeaks;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getFilamentDensity() {
        return 1.0*numberOfPeaks/length;
    }

    @Override
    public String toString() {
        return fileNameAndPath + SEPARATOR + length + SEPARATOR + numberOfPeaks + SEPARATOR + getFilamentDensity();
    }
}
