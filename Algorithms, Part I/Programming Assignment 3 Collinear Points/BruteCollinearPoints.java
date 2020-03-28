import java.util.ArrayList;
import java.util.Arrays;
 
public class BruteCollinearPoints {
    private final Point[] points;
    private final LineSegment[] cached;
 
    public BruteCollinearPoints(Point[] points) {
        if (points == null) {
            throw new IllegalArgumentException();
        }
        // Points array passsed to the constructor can be changed by some other parts of the code while construction is still in progress.
        this.points = Arrays.copyOf(points, points.length);
        for (Point point : this.points) {
            if (point == null) {
                throw new IllegalArgumentException();
            }
        }
        Arrays.sort(this.points);
        for (int i = 0; i < this.points.length; i++) {
            if (i > 0 && Double.compare(this.points[i].slopeTo(this.points[i - 1]), Double.NEGATIVE_INFINITY) == 0) {
                throw new IllegalArgumentException();
            }
        }
        // Stores a reference to an externally mutable object in the instance variable 'points', exposing the internal representation of the class.
        // Instead, create a defensive copy of the object referenced by the parameter variable 'points' and store that copy in the instance variable 'points'.
        cached = cache();
    }
 
    private LineSegment[] cache() {
        ArrayList<LineSegment> list = new ArrayList<>();
        int n = points.length;
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                for (int k = j + 1; k < n; k++) {
                    for (int m = k + 1; m < n; m++) {
                        Point p = points[i];
                        Point q = points[j];
                        Point r = points[k];
                        Point s = points[m];
                        double slope1 = p.slopeTo(q);
                        double slope2 = p.slopeTo(r);
                        double slope3 = p.slopeTo(s);
                        if (Double.compare(slope1, slope2) == 0 && Double.compare(slope2, slope3) == 0) {
                            list.add(new LineSegment(p, s));
                        }
                    }
                }
            }
        }
        LineSegment[] segments = new LineSegment[list.size()];
        return list.toArray(segments);
    }
 
    public int numberOfSegments() {
        return cached.length;
    }
 
    public LineSegment[] segments() {
        // check that data type is immutable by testing whether each method
        // returns the same value, regardless of any intervening operations
        return Arrays.copyOf(cached, cached.length);
    }
}