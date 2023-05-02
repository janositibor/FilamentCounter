package FilamentCounter;

import ij.gui.GenericDialog;
import ij.plugin.PlugIn;

public class Dialog implements PlugIn {
    static String title="Example";
    static double width=1.3,height=3.3;
    private BasicData basicData;
    public void run(String arg) {
        GenericDialog gd = new GenericDialog(arg);
        gd.addStringField("Title: ", title);
        gd.addNumericField("Width: ", width, 3);
        gd.addNumericField("Height: ", height, 3);
        gd.showDialog();
        if (gd.wasCanceled()) return;
        title = gd.getNextString();
        width = gd.getNextNumber();
        height = gd.getNextNumber();
        System.out.println(title+ "= " +width+" * "+height);
        basicData=new BasicData(width);
    }

    public BasicData getBasicData() {
        return basicData;
    }
}
