package FilamentCounter;

import FilamentCounter.modell.BasicSettings;
import FilamentCounter.modell.FileResultDTO;
import ij.io.DirectoryChooser;
import net.imagej.ops.OpService;
import net.imglib2.type.numeric.RealType;
import org.scijava.command.Command;
import org.scijava.plugin.Parameter;
import org.scijava.plugin.Plugin;
import org.scijava.ui.UIService;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Plugin(type = Command.class, menuPath = "Plugins>Filament Counter")
public class FilamentCounter<T extends RealType<T>> implements Command {
    @Parameter
    private UIService uiService;

    @Parameter
    private OpService opService;

    private BasicSettings basicSettings;
    private List<FileResultDTO> resultList=new ArrayList<>();

    @Override
    public void run() {
        Dialog dialog=new Dialog();
        dialog.run("Required data for peak identification");
        basicSettings =dialog.getBasicData();

        DirectoryChooser dc = new DirectoryChooser("Choose directory");
        String directory=dc.getDirectory();
        System.out.println(directory);
        processDirectory(directory);
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
        resultList.clear();
        FileSpecificData fileSpecificData;
        System.out.println(directoryName);
        File[] content= new File(directoryName).listFiles();
        for(File file:content){
            if(!file.isDirectory() && validFile(file)){
                System.out.println(" - "+file.getAbsolutePath());
                fileSpecificData=new FileSpecificData(file.getAbsolutePath(), basicSettings.getSettingForCalculation());
                resultList.add(fileSpecificData.getResult());
            }
        }
        writeResultFromADirectory(directoryName);
    }

    private boolean validFile(File file) {
        String fileNameAndPath=file.getAbsolutePath();
        int indexOfLastDot=fileNameAndPath.lastIndexOf(".");
        if(indexOfLastDot<0){
            return false;
        }
        return ("tif".equals(fileNameAndPath.substring(indexOfLastDot+1)));
    }

    private void writeResultFromADirectory(String directoryName) {
        if(resultList.size()>0) {
            List<String> writeOutArray = new ArrayList<>(Arrays.asList(
                    "Date and time: " + LocalDateTime.now(),
                    Dialog.PARAMETER1_NAME + ": " + basicSettings.getPixelSize(),
                    Dialog.PARAMETER2_NAME + ": " + basicSettings.getSettingForCalculation().getAmplitude(),
                    Dialog.PARAMETER3_NAME + ": " + basicSettings.getSettingForCalculation().getMinHeight(),
                    Dialog.PARAMETER4_NAME + ": " + basicSettings.getSettingForCalculation().getMinDistance(),
                    "",
                    "Filename and path" + FileResultDTO.SEPARATOR + "Length (microm)" + FileResultDTO.SEPARATOR + "Number of Filaments" + FileResultDTO.SEPARATOR + "Filament Density (1/microm)"
            ));

            for (int i = 0; i < resultList.size(); i++) {
                writeOutArray.add(resultList.get(i).toString());
            }

            try {
                Files.write(Paths.get(directoryName + "\\result.csv"), writeOutArray);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }
}
