package FilamentCounter;

import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Dialog implements PlugIn {
    static String PARAMETER_FILE_AND_PATH="C:\\ProgramData\\Fiji\\FilamentCounter\\params.txt";
    static String DEFAULT_TITLE="Example";
    static double DEFAULT_PIXELSIZE=1.3;
    static double DEFAULT_ALPHA=3.3;
    static double DEFAULT_BETA=4.5;
    static double DEFAULT_GAMMA=0.1;

    static final String PARAMETER1_NAME="Pixelsize";
    static final String PARAMETER2_NAME="Alpha";
    static final String PARAMETER3_NAME="Beta";
    static final String PARAMETER4_NAME="Gamma";

    private String title=DEFAULT_TITLE;
    private double pixelSize=DEFAULT_PIXELSIZE;
    private double alpha=DEFAULT_ALPHA;
    private double beta=DEFAULT_BETA;
    private double gamma=DEFAULT_GAMMA;

    private BasicSettings basicSettings;
    public void run(String arg) {
        loadParameters();
        GenericDialog gd = new GenericDialog(arg);
        gd.addStringField("Title: ", title);
        gd.addNumericField(PARAMETER1_NAME+": ", pixelSize, 3);
        gd.addNumericField(PARAMETER2_NAME+": ", alpha, 3);
        gd.addNumericField(PARAMETER3_NAME+": ", beta, 3);
        gd.addNumericField(PARAMETER4_NAME+": ", gamma, 3);
        gd.showDialog();
        if (gd.wasCanceled()) return;
        title = gd.getNextString();
        pixelSize = gd.getNextNumber();
        alpha = gd.getNextNumber();
        beta = gd.getNextNumber();
        gamma = gd.getNextNumber();
        System.out.println(title+ "= " +pixelSize+" * "+alpha);
        WriteParametersIntoFile();
        basicSettings =new BasicSettings(pixelSize, alpha, beta, gamma);
    }

    private void loadParameters() {
        File f = new File(PARAMETER_FILE_AND_PATH);
        if (f.exists()) {
            loadParametersFromFile(PARAMETER_FILE_AND_PATH);
        }
    }

    private void loadParametersFromFile(String fileNameAndPath) {
        List<String> lines;
        try {
            lines= Files.readAllLines(Paths.get(fileNameAndPath));
        } catch (IOException ioe) {
            throw new IllegalStateException("Can not read file: "+fileNameAndPath, ioe);
        }
        process(lines);
    }

    private void process(List<String> lines) {
        for(int i=0;i< lines.size();i++){
            processLine(lines.get(i));
        }
    }

    private void processLine(String line) {
        String[] fieldsArray;
        fieldsArray=line.split(":");
        System.out.println(fieldsArray[0]+": "+fieldsArray[1].trim());
        Double value=Double.parseDouble(fieldsArray[1].trim());
        switch(fieldsArray[0]){
            case PARAMETER1_NAME:
                pixelSize=value;
                break;
            case PARAMETER2_NAME:
                alpha=value;
                break;
            case PARAMETER3_NAME:
                beta=value;
                break;
            case PARAMETER4_NAME:
                gamma=value;
                break;
        }
    }

    private void WriteParametersIntoFile(){
        List<String> writeOutArray = Arrays.asList(PARAMETER1_NAME+": "+pixelSize,
                PARAMETER2_NAME+": "+alpha,
                PARAMETER3_NAME+": "+beta,
                PARAMETER4_NAME+": "+gamma);
        try {
            Files.write(Paths.get(PARAMETER_FILE_AND_PATH), writeOutArray);
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public BasicSettings getBasicData() {
        return basicSettings;
    }
}
