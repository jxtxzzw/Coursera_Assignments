/****************************************************************************
 *  Compilation:  javac PercolationVisualizerRandom.java
 *  Execution:    java PercolationVisualizerRandom N
 *  Dependencies: Percolation.java PercolationVisualizer.java
 *                StdDraw.java StdRandom.java
 *
 *  This program takes the grid size N as a command-line argument and
 *  generates random sites until the system percolates.
 *
 *  After each site is opened, it draws full sites in light blue,
 *  open sites (that aren't full) in white, and blocked sites in black,
 *  with with site (1, 1) in the upper left-hand corner.
 *
 ****************************************************************************/

public class PercolationVisualizerRandom {
    // delay in miliseconds (controls animation speed)
    private static final int DELAY = 100;

    public static void main(String[] args) {
        int N = Integer.parseInt(args[0]);

        // repeatedly generate sites at random and draw resulting system
        Percolation perc = new Percolation(N);
        PercolationVisualizer.draw(perc, N);
        StdDraw.show(DELAY);
        while (!perc.percolates()) {
            StdDraw.show(0);          // turn on animation mode
            int i = 1 + StdRandom.uniform(N);
            int j = 1 + StdRandom.uniform(N);
            if (!perc.isOpen(i, j)) {
                perc.open(i, j);
                PercolationVisualizer.draw(perc, N);
                StdDraw.show(DELAY);
            }
        }
    }
}
