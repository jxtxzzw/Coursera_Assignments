import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {
    private static final double CONFIDENCE = 1.96;
    private final double[] thresholds;
    // 缓存，以减少 Stats 的调用，否则会扣分
    private final double mean;
    private final double stddev;

    public PercolationStats(int n, int trials) {
        if (!isValid(n, trials)) {
            throw new IllegalArgumentException();
        }
        thresholds = new double[trials];
        for (int i = 0; i < trials; ++i) {
          Percolation p = new Percolation(n);
            while (!p.percolates()) {
                int possibleRow = StdRandom.uniform(1, n + 1);
                int possibleCol = StdRandom.uniform(1, n + 1);
                p.open(possibleRow, possibleCol);
            }
            thresholds[i] = (double) (p.numberOfOpenSites()) / (double) (n * n);
        }
        mean = StdStats.mean(thresholds);
        stddev = StdStats.stddev(thresholds);
    }

    private boolean isValid(int n, int trials) {
        return n > 0 && trials > 0;
    }

    public double mean() {
        return mean;
    }

    public double stddev() {
        return stddev;
    }

    public double confidenceLo() {
        return mean - CONFIDENCE * stddev / Math.sqrt(thresholds.length);
    }

    public double confidenceHi() {
        return mean + CONFIDENCE * stddev / Math.sqrt(thresholds.length);
    }

    public static void main(String[] args) {
        int n = Integer.parseInt(args[0]);
        int trials = Integer.parseInt(args[1]);
        PercolationStats ps = new PercolationStats(n, trials);
        System.out.println("mean=" + ps.mean());
        System.out.println("stddev=" + ps.stddev());
        System.out.println("95% confidence interval=[" + ps.confidenceLo() + "," + ps.confidenceHi() + "]");
    }
}