package FilamentCounter;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.Roi;
import ij.io.FileInfo;
import ij.io.FileOpener;
import ij.io.Opener;
import ij.process.FloatPolygon;
import io.scif.Metadata;
import io.scif.config.SCIFIOConfig;
import io.scif.services.DatasetIOService;
import io.scif.services.DefaultDatasetIOService;
import net.imagej.Dataset;
import net.imagej.ImageJ;
import org.scijava.Context;
import org.scijava.command.Command;
import org.scijava.io.IOService;
import org.scijava.io.location.Location;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.PluginInfo;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FileSpecificData {

    static final String SEPARATOR=";";
    private String fileNameAndPath;
    private double length=0;
    private double filamentDensity=0;
    private ImageJ imageJ=new ImageJ();
//    private DatasetIOService datasetIO;
    private IJ ij;

    private Dataset dataset;

    private DatasetIOService datasetIO=new DefaultDatasetIOService();

    Opener opener = new Opener();
    private ImagePlus image;

    private Roi roi;

    private List<Line> sides =new ArrayList<>();
    private Line controlLine1;
    private Line controlLine2;

    private List<Line> linesToCalculateFilaments =new ArrayList<>();




    public FileSpecificData(String fileNameAndPath) {
        this.fileNameAndPath = fileNameAndPath;
        loadImage();
        enhance();
        setRoi();
        setRoiCoordinates();
        setControlLines();
        setLinesToCalculateFilaments();
    }

    private void setLinesToCalculateFilaments() {
        Line line;
        for (int i = 0; i <BasicSettings.NUMBER_OF_LINES_TO_CALCULATE_FILAMENTS-1; i++) {
            line=new Line(controlLine1.equalPartCoordinates(i+1,BasicSettings.NUMBER_OF_LINES_TO_CALCULATE_FILAMENTS),controlLine2.equalPartCoordinates(i+1,BasicSettings.NUMBER_OF_LINES_TO_CALCULATE_FILAMENTS));
            linesToCalculateFilaments.add(line);
        }
    }

    private void setControlLines() {
        controlLine1=new Line(sides.get(0).getBegin(),sides.get(1).getEnd());
        controlLine2=new Line(sides.get(1).getBegin(),sides.get(0).getEnd());
    }

    private void loadImage() {
        image = opener.openImage(fileNameAndPath);
        image.show();
    }

    private void setRoiCoordinates(){
        Polygon boundary=roi.getPolygon();
        Coordinates begin;
        Coordinates end;
        Line line;

        for (int i=0;i<boundary.npoints-1;i++) {
//            System.out.println(boundary.xpoints[i]+"; "+boundary.ypoints[i]);
            begin=new Coordinates(boundary.xpoints[i],boundary.ypoints[i]);
            end=new Coordinates(boundary.xpoints[i+1],boundary.ypoints[i+1]);
            line=new Line(begin,end);
            sides.add(line);
        }
        Collections.sort(sides);
        System.out.println(sides.get(0).getLength());
        System.out.println(sides.get(1).getLength());
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
        image.show();
    }

    public String getFileNameAndPath() {
        return fileNameAndPath;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getFilamentDensity() {
        return filamentDensity;
    }

    public void setFilamentDensity(double filamentDensity) {
        this.filamentDensity = filamentDensity;
    }

    @Override
    public String toString() {
        return fileNameAndPath + SEPARATOR + length + SEPARATOR + filamentDensity;
    }
}
