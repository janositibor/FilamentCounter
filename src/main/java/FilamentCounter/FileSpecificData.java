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

    private List<Coordinates> RoiCoordinatesList=new ArrayList<>();



    public FileSpecificData(String fileNameAndPath) {
        this.fileNameAndPath = fileNameAndPath;
        loadImage();
        enhance();
        setRoi();
        setRoiCoordinates();
    }

    private void loadImage() {
        image = opener.openImage(fileNameAndPath);
        image.show();
    }

    private void setRoiCoordinates(){
//        Polygon boundary=roi.getConvexHull();
        Polygon boundary=roi.getPolygon();

        for (int i=0;i<boundary.npoints;i++) {
            System.out.println(boundary.xpoints[i]+"; "+boundary.ypoints[i]);
        }

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
