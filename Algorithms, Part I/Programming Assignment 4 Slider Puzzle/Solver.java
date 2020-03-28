import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.StdOut;
 
import java.util.ArrayList;
import java.util.Collections;
 
 
public class Solver {
 
    // 定义一个搜索树，方便进行 A* 搜索
    // 搜索树的结点，递归的定义
    private static class GameTreeNode implements Comparable<GameTreeNode> {
        private final Board board; // 结点
        private final GameTreeNode parent; // 父亲
        private final boolean twin;
        private final int moves;
        // Caching the Hamming and Manhattan priorities.
        // To avoid recomputing the Manhattan priority of a search node from scratch each time during various priority queue operations, pre-compute its value when you construct the search node;
        // save it in an instance variable; and return the saved value as needed.
        // This caching technique is broadly applicable:
        // consider using it in any situation where you are recomputing the same quantity many times and for which computing that quantity is a bottleneck operation.
        //
        // rejecting if doesn't adhere to stricter caching limits
        private final int distance;
        // The efficacy of this approach hinges on the choice of priority function for a search node.
        // We consider two priority functions:
        //
        // The Hamming priority function is the Hamming distance of a board plus the number of moves made so far to get to the search node.
        // Intuitively, a search node with a small number of tiles in the wrong position is close to the goal, and we prefer a search node if has been reached using a small number of moves.
        //
        // The Manhattan priority function is the Manhattan distance of a board plus the number of moves made so far to get to the search node.
        private final int priority;
 
        // 初始节点，parent 为 null，需要区分是不是双胞胎
        public GameTreeNode(Board board, boolean twin) {
            this.board = board;
            parent = null;
            this.twin = twin;
            moves = 0;
            distance = board.manhattan();
            priority = distance + moves;
        }
 
        // 之后的结点，twin 状态跟从 parent
        public GameTreeNode(Board board, GameTreeNode parent) {
            this.board = board;
            this.parent = parent;
            twin = parent.twin;
            moves = parent.moves + 1;
            distance = board.manhattan();
            priority = distance + moves;
        }
 
        public Board getBoard() {
            return board;
        }
 
        public GameTreeNode getParent() {
            return parent;
        }
 
        public boolean isTwin() {
            return twin;
        }
 
        @Override
        public int compareTo(GameTreeNode node) {
            // Using Manhattan() as a tie-breaker helped a lot.
            // Using Manhattan priority, then using Manhattan() to break the tie if two boards tie, and returning 0 if both measurements tie
            if (priority == node.priority) {
                return Integer.compare(distance, distance);
            } else {
                return Integer.compare(priority, node.priority);
            }
        }
 
        @Override
        public boolean equals(Object node) {
            if (node == null) {
                return false;
            }
            if (this == node) {
                return true;
            }
            if (node.getClass() != this.getClass()) {
                return false;
            }
            GameTreeNode that = (GameTreeNode) node;
            return getBoard().equals(that.getBoard());
        }
 
        @Override
        public int hashCode() {
            return 1;
        }
    }
 
    private int moves;
    private boolean solvable;
    private Iterable<Board> solution;
    private final Board initial;
 
    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) {
            throw new IllegalArgumentException();
        }
        this.initial = initial;
        cache();
    }
 
    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return solvable;
    }
 
    // min number of moves to solve initial board
    public int moves() {
        return moves;
    }
 
    // sequence of boards in a shortest solution
    public Iterable<Board> solution() {
        return this.solution;
    }
 
    // 构造的时候直接跑出结果，然后缓存，否则没有 solution 的话，moves 和 solvable 也拿不到
    private void cache() {
        // To implement the A* algorithm, you must use the MinPQ data type for the priority queue.
        MinPQ<GameTreeNode> pq = new MinPQ<>();
        // 把当前状态和双胞胎状态一起压入队列，做 A* 搜索
        pq.insert(new GameTreeNode(initial, false));
        pq.insert(new GameTreeNode(initial.twin(), true));
        GameTreeNode node = pq.delMin();
        Board b = node.getBoard();
        //  要么是棋盘本身，要么是棋盘的双胞胎，总有一个会做到 isGoal()
        while (!b.isGoal()) {
            for (Board bb : b.neighbors()) {
                // The critical optimization.
                // A* search has one annoying feature: search nodes corresponding to the same board are enqueued on the priority queue many times.
                // To reduce unnecessary exploration of useless search nodes, when considering the neighbors of a search node, don’t enqueue a neighbor if its board is the same as the board of the previous search node in the game tree.
                if (node.getParent() == null || !bb.equals(node.getParent().getBoard())) {
                    pq.insert(new GameTreeNode(bb, node));
                }
            }
            // 理论上这里 pq 永远不可能为空
            node = pq.delMin();
            b = node.getBoard();
        }
        // 如果是自己做出了结果，那么就是可解的，如果是双胞胎做出了结果，那么就是不可解的
        solvable = !node.isTwin();
 
        if (!solvable) {
            // 注意不可解的地图，moves 是 -1，solution 是 null
            moves = -1;
            solution = null;
        } else {
            // 遍历，沿着 parent 走上去
            ArrayList<Board> list = new ArrayList<>();
            while (node != null) {
                list.add(node.getBoard());
                node = node.getParent();
            }
            // 有多少个状态，减 1 就是操作次数
            moves = list.size() - 1;
            // 做一次反转
            Collections.reverse(list);
            solution = list;
        }
    }
 
    // test client (see below)
    public static void main(String[] args) {
 
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                tiles[i][j] = in.readInt();
            }
        }
        Board initial = new Board(tiles);
 
        // solve the puzzle
        Solver solver = new Solver(initial);
 
        // print solution to standard output
        if (!solver.isSolvable()) {
            StdOut.println("No solution possible");
        } else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution()) {
                StdOut.println(board);
            }
        }
    }
 
}