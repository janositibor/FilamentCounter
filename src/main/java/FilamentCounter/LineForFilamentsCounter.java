package FilamentCounter;

public class LineForFilamentsCounter implements Comparable<LineForFilamentsCounter>{
    private Coordinates begin;
    private Coordinates end;
    private double length;

    public LineForFilamentsCounter(Coordinates begin, Coordinates end) {
        this.begin = begin;
        this.end = end;
        calculateLength();
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
        double x=begin.getX()+((end.getX()- begin.getX())*(1.0*nth/total));
        double y=begin.getY()+((end.getY()- begin.getY())*(1.0*nth/total));
//        System.out.println(nth+": "+x+" "+y);
        return new Coordinates(x,y);
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
                "begin=" + begin +
                ", end=" + end +
                '}';
    }
}
