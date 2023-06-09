package FilamentCounter;

import FilamentCounter.modell.*;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.*;
import ij.io.Opener;
import ij.plugin.frame.RoiManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import ij.measure.ResultsTable;

public class FileSpecificData {

    private String fileNameAndPath;
    private String fileName;
    private String extension;
    private double length=1;
    private int numberOfPeaks=0;
    private Opener opener = new Opener();
    private ImagePlus image;
    private StartingROI startingROI;
    private RoiManager roiManager = new RoiManager();
    private ResultsTable resultsTable = new ResultsTable();
    private double[] xValues;
    private double[] yValues;
    private List<Double> limitsList=new ArrayList<>();
    private Plot intensityProfilePlot;
    private SettingForCalculationDTO settingForCalculation;
    private ImageWindow imageWindow;

    public FileSpecificData(String fileNameAndPath,SettingForCalculationDTO settingForCalculation) {
        this.fileNameAndPath = fileNameAndPath;
        this.settingForCalculation=settingForCalculation;
        roiManager.setVisible(true);
        setFileName();
        setExtension();
        loadImage();
        enhance();
        setStartingROI();
        setNewROIs();
        saveUsedRois();
        getProfiles();
        findPeaks();
        finalizeIntensityProfile();
        closeFile();
    }

    private void finalizeIntensityProfile() {
        addLimitsToIntensityProfile(intensityProfilePlot);
        saveIntensityProfile();
    }

    private void saveIntensityProfile() {
        ImagePlus plotImage=intensityProfilePlot.getImagePlus();
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
        BarFindPeaks barFindPeaks=new BarFindPeaks(settingForCalculation.getAmplitude(),settingForCalculation.getMinDistance(), settingForCalculation.getMinHeight());
        barFindPeaks.setXvalues(xValues);
        barFindPeaks.setYvalues(yValues);

        FoundPeaksDTO foundPeaks=barFindPeaks.run();
        intensityProfilePlot=foundPeaks.getIntensityProfileWithSelectedPeaks();
        setNumberOfPeaks(foundPeaks.getNumberOfPeaks());
    }

    private void closeFile() {
        roiManager.close();
        image.hide();
        image.close();
        imageWindow.close();

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
            throw new IllegalStateException("No extension sound for this file:"+fileNameAndPath);
        }
        extension=fileNameAndPath.substring(indexOfLastDot+1);
    }
    private void saveUsedRois() {
        ImagePlus outputImage=(ImagePlus) image.clone();
        for (int i = 0; i < roiManager.getCount(); i++) {
            roiManager.select(outputImage,i);
            IJ.run(outputImage, "Properties... ", "  width=10");
            outputImage=outputImage.flatten();
        }
        String newNameAndPath=fileNameAndPath.replace(fileName,"ROIs_"+fileName).replace(extension,"bmp");
        IJ.saveAs(outputImage, "BMP", newNameAndPath);
    }

    private void getProfiles() {
        ProfilePlot profiler ;
        double[] profile;
        List<Double> totalProfile=new ArrayList<>();
        for (int i = 0; i < roiManager.getCount(); i++) {
            image.setRoi(roiManager.getRoi(i));
            profiler = new ProfilePlot(image);
            profile = profiler.getProfile();
            for (int j = 0; j < profile.length; j++) {
                resultsTable.setValue("line" + i, j, profile[j]);
                totalProfile.add(profile[j]);
            }
        }
        xValues = IntStream.rangeClosed(1, totalProfile.size()).mapToDouble(i->settingForCalculation.getPixelSize()*i).toArray();
        yValues = totalProfile.stream().mapToDouble(d -> d).toArray();

        setLength(settingForCalculation.getPixelSize()*totalProfile.size());

        for (int i = 0; i <resultsTable.getLastColumn(); i++) {
            if(i==0){
                limitsList.add(settingForCalculation.getPixelSize()*resultsTable.getColumnAsDoubles(i).length);
            }
            else{
                limitsList.add(limitsList.get(i-1)+settingForCalculation.getPixelSize()*resultsTable.getColumnAsDoubles(i).length);
            }
        }
   }

   private void setNewROIs(){
        LineForFilamentsCounter line;
        for (int i = 0; i < startingROI.getNewROIs().size(); i++) {
            line=startingROI.getNewROIs().get(i);
            image.setRoi(new Line(line.getBegin().getX(),line.getBegin().getY(),line.getEnd().getX(),line.getEnd().getY()));
            roiManager.addRoi(image.getRoi());
        }
        roiManager.save(fileNameAndPath+"_roi.zip");
    }

    private void loadImage() {
        image = opener.openImage(fileNameAndPath);
        imageWindow=new ImageWindow(image);
        image.show();
    }

    public void setStartingROI() {
        this.startingROI = new StartingROI(image.getRoi());
    }

    private void enhance(){
        int blocksize = 127;
        int histogramBins = 256;
        int maximumSlope = 3;
        String mask = "*None*";
        boolean fast = true;

        String parameters =
                "blocksize=" + blocksize +
                        " histogram=" + histogramBins +
                        " maximum=" + maximumSlope +
                        " mask=" + mask;

        if (fast) {
            parameters += " fast_(less_accurate)";
        }
        IJ.run(image,"Enhance Local Contrast (CLAHE)", parameters );
    }

    public void setNumberOfPeaks(int numberOfPeaks) {
        this.numberOfPeaks = numberOfPeaks;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public FileResultDTO getResult(){
        return new FileResultDTO(fileNameAndPath,length,numberOfPeaks);
    }
}
