package FilamentCounter.modell;

import ij.gui.Roi;

import java.util.Optional;

public class LineForFilamentsCounter implements Comparable<LineForFilamentsCounter>{
    private Coordinates begin;
    private Coordinates end;
    private double length;

    public LineForFilamentsCounter(Coordinates begin, Coordinates end) {
        this.begin = begin;
        this.end = end;
        calculateLength();
    }

    private boolean isVertical(){
        return Math.abs(begin.getX()- end.getX())<BasicSettings.EPSILON_FOR_LINE_VERTICALITY;
    }

    public Optional<Double> gradient(){
        if(isVertical()){
            return Optional.empty();
        }
        return Optional.of((end.getY()- begin.getY())/(end.getX()- begin.getX()));
    }

    public void shrink(int percent) {
        begin=equalPartCoordinates(percent,2*100);
        end=equalPartCoordinates((200-percent),2*100);
        calculateLength();
    }

    private void calculateLength() {
        double dx=Math.abs(begin.getX()-end.getX());
        double dy=Math.abs(begin.getY()-end.getY());
        length=Math.sqrt(Math.pow(dx, 2)+Math.pow(dy,2));
    }

    public Coordinates equalPartCoordinates(int nth,int total){
        double x=begin.getX()+((end.getX()-begin.getX())*(1.0*nth/total));
        double y=begin.getY()+((end.getY()-begin.getY())*(1.0*nth/total));
        return new Coordinates(x,y);
    }

    public Coordinates coordinatesForBeginExtension(int nth, int total){
        return equalPartCoordinates(-1*nth,total);
    }
    public Coordinates coordinatesForEndExtension(int nth, int total){
        return equalPartCoordinates(nth+total,total);
    }

    @Override
    public int compareTo(LineForFilamentsCounter o) {
        double difference=o.getLength()-this.getLength();
        return (int)(difference/Math.abs(difference));
    }

    public Coordinates getBegin() {
        return begin;
    }

    public Coordinates getEnd() {
        return end;
    }

    public double getLength() {
        return length;
    }

    @Override
    public String toString() {
        return "LineForFilamentsCounter{" +
                "length=" + length +
                "begin=" + begin +
                ", end=" + end +
                '}';
    }

    public void extendBegin(Roi roi){
        int rate=16;
        Coordinates candidateCoordinates;
        while(rate>0){
            candidateCoordinates= coordinatesForBeginExtension(rate,100);
            if(roi.contains((int)candidateCoordinates.getX(),(int)candidateCoordinates.getY())){
                begin=candidateCoordinates;
                rate*=2;
            }
            else{
                rate/=2;
            }
        }
    }


    public void extend(Roi roi) {
        extendBegin(roi);
        extendEnd(roi);
    }

    private void extendEnd(Roi roi) {
        int rate=16;
        Coordinates candidateCoordinates;
        while(rate>0){
            candidateCoordinates= coordinatesForEndExtension(rate,100);
            if(roi.contains((int)candidateCoordinates.getX(),(int)candidateCoordinates.getY())){
                end=candidateCoordinates;
                rate*=2;
            }
            else{
                rate/=2;
            }
        }
    }
}
