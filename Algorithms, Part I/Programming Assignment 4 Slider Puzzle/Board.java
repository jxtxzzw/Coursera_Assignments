import java.util.ArrayList;
import java.util.Arrays;
 
public class Board {
 
    private final int[][] tiles;
    private final int n;
    // 缓存每一个位置的距离，需要的时候可以不用每次都重新遍历计算
    private final int hamming;
    private final int manhattan;
 
    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        n = tiles.length;
        this.tiles = new int[n][n];
        int hammingSum = 0;
        int manhattanSum = 0;
        // 复制值，而不是令 this.tiles = tiles，确保 Immutable
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                this.tiles[i][j] = tiles[i][j];
                // 反正这里都是要遍历一遍的，不如直接把空格位置记录下来，方便后面查找，就不需要再遍历去找那个 0 了
                if (tiles[i][j] != 0) {
                    // 这里根据定义，空位 0 是不需要再加到距离上的
                    // 顺便也一起做了 cache
                    // 这是 hamming 的，计算 shouldAt 和 nowAt 是不是相等
                    // 应该在的位置就是自己的数值（由于下标从 0 开始，减 1），如果是空位，就在最后
                    int targetAt = tiles[i][j] - 1;
                    // 这是现在在的位置，把二维的转化为一维的
                    int nowAt = i * n + j;
                    hammingSum += targetAt != nowAt ? 1 : 0;
                    // 这是 manhattan 的，计算横纵坐标距离差的绝对值的和
                    int vertical = Math.abs(i - targetAt / n);
                    int horizontal = Math.abs(j - targetAt % n);
                    manhattanSum += vertical + horizontal;
                }
            }
        }
        hamming = hammingSum;
        manhattan = manhattanSum;
    }
 
    // string representation of this board
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(n).append("\n");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                sb.append(tiles[i][j]).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
 
    // board dimension n
    public int dimension() {
        return n;
    }
 
    // number of tiles out of place
    public int hamming() {
        return hamming;
    }
 
    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhattan;
    }
 
    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }
 
    // does this board equal y?
    @Override
    public boolean equals(Object y) {
        // The equals() method is inherited from java.lang.Object, so it must obey all of Java’s requirements.
        if (y == null) {
            return false;
        }
        if (this == y) {
            return true;
        }
        if (y.getClass() != this.getClass()) {
            return false;
        }
        Board board = (Board) y;
        // 这里二维数组的相等做 deepEquals
        return Arrays.deepEquals(tiles, board.tiles);
    }
 
    // 本题不允许重写 hashCode()
 
    // all neighboring boards
    public Iterable<Board> neighbors() {
        ArrayList<Board> neighbors = new ArrayList<>();
        int x = 0, y = 0;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (tiles[i][j] == 0) {
                    x = i;
                    y = j;
                }
            }
        }
        int[][] directions = {{-1, 0}, {0, -1}, {0, 1}, {1, 0}};
        for (int[] direction : directions) {
            int xx = x + direction[0];
            int yy = y + direction[1];
            if (isValid(xx, yy)) {
                neighbors.add(new Board(swap(x, y, xx, yy)));
            }
        }
        return neighbors;
    }
 
    // 判断是否越界
    private boolean isValid(int x, int y) {
        return x >= 0 && x < n && y >= 0 && y < n;
    }
 
    // 复制数组并交换指定位置
    private int[][] swap(int x, int y, int xx, int yy) {
        int[][] newTiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            System.arraycopy(tiles[i], 0, newTiles[i], 0, n);
        }
        int tmp = newTiles[x][y];
        newTiles[x][y] = newTiles[xx][yy];
        newTiles[xx][yy] = tmp;
        return newTiles;
    }
 
    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        Board b = null;
        // 随便找两个相邻的位置就可以了，只要不越界，只要不是 0，就可以交换
        for (int i = 0; i < n * n - 1; i++) {
            int x = i / n;
            int y = i % n;
            int xx = (i + 1) / n;
            int yy = (i + 1) % n;
            if (tiles[x][y] != 0 && tiles[xx][yy] != 0) {
                b = new Board(swap(x, y, xx, yy));
                break;
            }
        }
        return b;
    }
 
    // unit testing (not graded)
    public static void main(String[] args) {
        int[][] t = {{1, 2, 3}, {4, 5, 0}, {8, 7, 6}};
        Board b = new Board(t);
//        System.out.println(b.dimension());
//        System.out.println(b);
//        System.out.println(b.hamming());
//        System.out.println(b.manhattan());
//        System.out.println(b.isGoal());
//        System.out.println(b.twin());
//        System.out.println(b.equals(b.twin()));
        for (Board bb : b.neighbors()) {
            System.out.println(bb);
        }
    }
 
}