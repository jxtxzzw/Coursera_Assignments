import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private final int[][] directions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    // 虚拟起点，减少 UnionFind 的调用
    private static final int TOP = 0;
    private boolean[][] openStatus;
    private final int n;
    private final WeightedQuickUnionUF disjointSet;
    // 为了解决 backwash，这里只用于 TOP 连接
    private final WeightedQuickUnionUF disjointSetBackwash;
    // 虚拟终点，减少 UnionFind 的调用
    private final int bottom;
    // 直接计入 counter，避免 isOpen() 去遍历
    private int counter;

    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        this.n = n;
        openStatus = new boolean[n + 1][n + 1];
        bottom = n * n + 1;
        disjointSet = new WeightedQuickUnionUF(bottom + 1);
        disjointSetBackwash = new WeightedQuickUnionUF(bottom + 1);
    }

    public void open(int row, int col) {
        if (isValid(row, col)) {
            if (!openStatus[row][col]) {
                // 没有打开过才能加 1
                counter++;
                openStatus[row][col] = true;
                // 将第一层与虚拟起点连接，最后一层与虚拟终点连接
                int unionIndex = (row - 1) * n + col;
                if (row == 1) {
                    disjointSet.union(TOP, unionIndex);
                    disjointSetBackwash.union(TOP, unionIndex);
                }
                // 这里不是 else if 因为 n = 1 的时候，头尾都要连通
                if (row == n) {
                    disjointSet.union(bottom, unionIndex);
                    // 不在 backwash 中记录
                }
            }
            for (int[] ints : directions) {
                int newRow = row + ints[0];
                int newCol = col + ints[1];
                if (isValid(newRow, newCol) && isOpen(newRow, newCol)) {
                    int unionIndexP = (row - 1) * n + col;
                    int unionIndexQ = (newRow - 1) * n + newCol;
                    disjointSet.union(unionIndexP, unionIndexQ);
                    disjointSetBackwash.union(unionIndexP, unionIndexQ);
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    private boolean connectedWithTop(int row, int col) {
        int unionIndex = (row - 1) * n + col;
        return disjointSetBackwash.connected(TOP, unionIndex);
    }

    private boolean isValid(int row, int col) {
        return row >= 1 && row <= n && col >= 1 && col <= n;
    }

    public boolean isOpen(int row, int col) {
        if (isValid(row, col)) {
            return openStatus[row][col];
        } else {
            throw new IllegalArgumentException();
        }
    }

    public boolean isFull(int row, int col) {
        if (isValid(row, col)) {
            // A full site is an open site that can ....
            // 所以 isFull 必须要先满足 isOpen
            return isOpen(row, col) && connectedWithTop(row, col);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public int numberOfOpenSites() {
        return counter;
    }

    public boolean percolates() {
        return disjointSet.connected(TOP, bottom);
    }

    public static void main(String[] args) {
        Percolation p;
        p= new Percolation(2);
        p.open(2, 1);
        p.open(2, 2);
        p.open(1, 2);
        assert p.percolates();

        // Backwash
        // (3, 1) 通过虚拟终点与起点连接，但它其实不是 full 的
        p = new Percolation(3);
        p.open(1, 1);
        p.open(1, 2);
        p.open(1, 3);
        p.open(2, 3);
        p.open(2, 2);
        p.open(3, 3);
        p.open(3, 1);
        assert !p.isFull(3, 1);
        assert p.percolates();
    }

}