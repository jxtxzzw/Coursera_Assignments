import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;

import java.util.ArrayList;
import java.util.TreeSet;

public class PointSET {

    private final TreeSet<Point2D> set;

    public PointSET() {
        set = new TreeSet<>();
    }

    public boolean isEmpty() {
        return set.isEmpty();
    }

    public int size() {
        return set.size();
    }

    public void insert(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        if (!contains((p))) {
            set.add(p);
        }
    }

    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        return set.contains(p);
    }

    public void draw() {
        for (Point2D p : set) {
            p.draw();
        }
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException();
        }
        ArrayList<Point2D> list = new ArrayList<>();
        for (Point2D p : set) {
            if (rect.contains(p)) {
                list.add(p);
            }
        }
        return list;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException();
        }
        Point2D ans = null;
        if (!isEmpty()) {
            double min = Double.POSITIVE_INFINITY;
            for (Point2D pp : set) {
                // Do not call 'distanceTo()' in this program; instead use 'distanceSquaredTo()'. [Performance]
                double d = pp.distanceSquaredTo(p);
                if (d < min) {
                    min = d;
                    ans = pp;
                }
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        PointSET ps = new PointSET();
        Point2D p1 = new Point2D(1, 1);
        Point2D p2 = new Point2D(1, 2);
        Point2D p3 = new Point2D(2, 1);
        Point2D p4 = new Point2D(0, 0);
        ps.insert(p1);
        ps.insert(p2);
        ps.insert(p3);
        ps.insert(p4);
        System.out.println(ps.nearest(p4));
        for (Point2D p : ps.range(new RectHV(1, 1, 3, 3))) {
            System.out.println(p);
        }
    }

}


