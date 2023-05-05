package FilamentCounter;

public class Line implements Comparable<Line>{
    private Coordinates begin;
    private Coordinates end;
    private double length;

    public Line(Coordinates begin, Coordinates end) {
        this.begin = begin;
        this.end = end;
        calculateLength();
    }

    private void calculateLength() {
        double dx=Math.abs(begin.getX()-end.getX());
        double dy=Math.abs(begin.getY()-end.getY());
        length=Math.sqrt(Math.pow(dx, 2)+Math.pow(dy,2));
    }

    public Coordinates equalPartCoordinates(int nth,int total){
        double x=begin.getX()+((end.getX()- begin.getX())*(nth/total));
        double y=begin.getY()+((end.getY()- begin.getY())*(nth/total));
        return new Coordinates(x,y);
    }

    @Override
    public int compareTo(Line o) {
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
}
