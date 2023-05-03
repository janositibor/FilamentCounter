package FilamentCounter;

//import javafx.stage.DirectoryChooser;
import ij.io.DirectoryChooser;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;


import java.io.File;

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

    private BasicSettings basicSettings;

    @Override
    public void run() {
//        Opener fo = new Opener();


        Dialog dialog=new Dialog();
        dialog.run("Data required");

        basicSettings =dialog.getBasicData();
        System.out.println("pixelSize:"+ basicSettings.getPixelSize()+"micro m");
        DirectoryChooser dc = new DirectoryChooser("Choose directory");

        String directory=dc.getDirectory();

//        LogStream logStream=public LogStream();
        System.out.println(directory);

        processDirectory(directory);

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

    private void processDirectory(String directoryName) {
        File[] content= new File(directoryName).listFiles();
        for(File file:content){
           if(file.isDirectory()){
               processDirectory(file.getAbsolutePath());
           }
        }
        processFilesInADirectory(directoryName);
    }

    private void processFilesInADirectory(String directoryName) {
        System.out.println(directoryName);
        File[] content= new File(directoryName).listFiles();
        for(File file:content){
            if(!file.isDirectory()){
                System.out.println(" - "+file.getAbsolutePath());
            }
        }
    }
}
