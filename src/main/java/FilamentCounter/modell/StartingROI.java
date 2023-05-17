package FilamentCounter.modell;

import ij.gui.Roi;
import ij.plugin.frame.RoiManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StartingROI {
    private Roi roi;
    private List<LineForFilamentsCounter> sides =new ArrayList<>();
    private List<LineForFilamentsCounter> newROIs =new ArrayList<>();
    private LineForFilamentsCounter controlLine1;
    private LineForFilamentsCounter controlLine2;
    private RoiManager roiManager = new RoiManager();

    public StartingROI(Roi roi) {
        this.roi = roi;
        setRoiCoordinates();
        setControlLines();
        setLinesToCalculateFilaments();
    }

    private void setLinesToCalculateFilaments() {
        Coordinates begin;
        Coordinates end;
        LineForFilamentsCounter line;
        for (int i = 0; i < BasicSettings.NUMBER_OF_LINES_TO_CALCULATE_FILAMENTS; i++) {
            begin=controlLine1.equalPartCoordinates(i+1,BasicSettings.NUMBER_OF_LINES_TO_CALCULATE_FILAMENTS+1);
            end=controlLine2.equalPartCoordinates(i+1,BasicSettings.NUMBER_OF_LINES_TO_CALCULATE_FILAMENTS+1);
            line=new LineForFilamentsCounter(begin,end);
            line.shrink(10);
            line.extend(roi);
            newROIs.add(line);
        }
    }

    private void setRoiCoordinates(){
        Polygon boundary=roi.getPolygon();
        Coordinates begin;
        Coordinates end;
        LineForFilamentsCounter line;

        for (int i=0;i<boundary.npoints;i++) {
            begin=new Coordinates(boundary.xpoints[i],boundary.ypoints[i]);
            if(i==boundary.npoints-1){
                end=new Coordinates(boundary.xpoints[0],boundary.ypoints[0]);
            }
            else{
                end=new Coordinates(boundary.xpoints[i+1],boundary.ypoints[i+1]);
            }
            line=new LineForFilamentsCounter(begin,end);
            sides.add(line);
        }
        Collections.sort(sides);
    }

    private boolean areTheSidesParalel(){
        Optional<Double> slope1=sides.get(0).gradient();
        Optional<Double> slope2=sides.get(1).gradient();

        if(!slope1.isPresent() && !slope2.isPresent()){
            return true;
        }
        if(slope1.isPresent() ^ slope2.isPresent()){
            return false;
        }
        return Math.abs(slope1.get()-slope2.get())<BasicSettings.EPSILON_FOR_SIDES_DEVIATION;
    }

    private void setControlLines() {
        controlLine1=new LineForFilamentsCounter(sides.get(0).getBegin(),sides.get(1).getEnd());
        controlLine2=new LineForFilamentsCounter(sides.get(0).getEnd(),sides.get(1).getBegin());
    }

    public List<LineForFilamentsCounter> getNewROIs() {
        return newROIs;
    }
}
