package FilamentCounter;

//import javafx.stage.DirectoryChooser;
import ij.io.DirectoryChooser;
import ij.io.LogStream;
import net.imagej.Dataset;
import net.imagej.ImageJ;
import net.imagej.ops.OpService;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Plugin(type = Command.class, menuPath = "Plugins>Filament Counter")
public class FilamentCounter<T extends RealType<T>> implements Command {
    //
    // Feel free to add more parameters here...
    //

//    @Parameter
//    private Dataset currentData;

    @Parameter
    private UIService uiService;

    @Parameter
    private OpService opService;

    @Override
    public void run() {
//        Opener fo = new Opener();
        DirectoryChooser dc = new DirectoryChooser("Choose directory");

        String directory=dc.getDirectory();

//        LogStream logStream=public LogStream();
        System.out.println(directory);

//        DirectoryChooser chooser = new DirectoryChooser();


//        final Img<T> image = (Img<T>) currentData.getImgPlus();
//
//        //
//        // Enter image processing code here ...
//        // The following is just a Gauss filtering example
//        //
//        final double[] sigmas = {1.0, 3.0, 5.0};
//
//        List<RandomAccessibleInterval<T>> results = new ArrayList<>();
//
//        for (double sigma : sigmas) {
//            results.add(opService.filter().gauss(image, sigma));
//        }
//
//        // display result
//        for (RandomAccessibleInterval<T> elem : results) {
//            uiService.show(elem);
//        }
    }
}
