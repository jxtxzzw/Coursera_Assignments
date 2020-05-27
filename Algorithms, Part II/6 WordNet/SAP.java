import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;


/**
 * @author jxtxzzw
 */
public class SAP {

    private final Digraph g;
    private final int vertexNumber;

    public SAP(Digraph g) {
        // 时刻记得做成不可变的
        this.g = new Digraph(g);
        vertexNumber = g.V();
    }

    public int length(int v, int w) {
        validate(v);
        validate(w);
        return sap(v, w)[0];
    }

    public int ancestor(int v, int w) {
        validate(v);
        validate(w);
        return sap(v, w)[1];
    }

    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        validate(v);
        validate(w);
        return sap(v, w)[0];
    }

    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        validate(v);
        validate(w);
        return sap(v, w)[1];
    }

    private void validate(int v) {
        // Any vertex argument is outside its prescribed range
        if (v < 0 || v >= vertexNumber) {
            throw new IllegalArgumentException();
        }
    }

    private void validate(Iterable<Integer> v) {
        // Any argument is null
        if (v == null) {
            throw new IllegalArgumentException();
        }
        for (Integer i : v) {
            // Any iterable argument contains a null item
            if (i == null) {
                throw new IllegalArgumentException();
            }
            // Call validate(int v)
            validate(i);
        }
    }

    private int[] sap(int v, int w) {
        ArrayList<Integer> vv = new ArrayList<>();
        vv.add(v);
        ArrayList<Integer> ww = new ArrayList<>();
        ww.add(w);
        return sap(vv, ww);
    }

    private int[] sap(Iterable<Integer> v, Iterable<Integer> w) {
        // 0 表示 length，1 表示 ancestor
        int[] sap = new int[2];
        // -1 if no such path
        Arrays.fill(sap, -1);
        // 记录点 x 到点 v 之间的距离
        HashMap<Integer, Integer> distanceV = new HashMap<>();
        HashMap<Integer, Integer> distanceW = new HashMap<>();

        // 遍历 q 的所有父结点
        LinkedList<Integer> q = new LinkedList<>();
        boolean[] visited = new boolean[vertexNumber];
        Arrays.fill(visited, false);
        for (int vx : v) {
            q.add(vx);
            visited[vx] = true;
            distanceV.put(vx, 0);
        }
        while (!q.isEmpty()) {
            int x = q.poll();
            Iterable<Integer> bag = g.adj(x);
            // 加入后面的点
            for (int vv : bag) {
                if (!visited[vv]) {
                    q.add(vv);
                    visited[vv] = true;
                    // 更新距离
                    int d = distanceV.get(x);
                    int dd = distanceV.getOrDefault(vv, d + 1);
                    distanceV.put(vv, dd);
                }
            }
        }
        Arrays.fill(visited, false);
        // 遍历 w 的所有父结点，找到最先遇到的
        for (int wx : w) {
            q.add(wx);
            visited[wx] = true;
            distanceW.put(wx, 0);
        }
        while (!q.isEmpty()) {
            int x = q.poll();
            if (distanceV.containsKey(x)) {
                // 更新最短的 LCA
                int minDistance = distanceV.get(x) + distanceW.get(x);
                if (sap[0] == -1 || minDistance < sap[0]) {
                    sap[0] = minDistance;
                    sap[1] = x;
                }
            }
            // 这里不是 else 的关系，要继续往上找
            Iterable<Integer> bag = g.adj(x);
            for (int vv : bag) {
                if (!visited[vv]) {
                    q.add(vv);
                    visited[vv] = true;
                    // 更新距离
                    int d = distanceW.get(x);
                    int dd = distanceW.getOrDefault(vv, d + 1);
                    distanceW.put(vv, dd);
                }
            }
        }
        return sap;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In("digraph2.txt");
        Digraph g = new Digraph(in);
        SAP sap = new SAP(g);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
//        while (!StdIn.isEmpty()) {
//            ArrayList<Integer> vv = new ArrayList<>();
//            ArrayList<Integer> ww = new ArrayList<>();
//            String[] v = StdIn.readLine().split(" ");
//            String[] w = StdIn.readLine().split(" ");
//            for (String s : v) {
//                vv.add(Integer.parseInt(s));
//            }
//            for (String s : w) {
//                ww.add(Integer.parseInt(s));
//            }
//            int length = sap.length(vv, ww);
//            int ancestor = sap.ancestor(vv, ww);
//            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
//        }
    }
}
