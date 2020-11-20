import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;
import java.util.ArrayList;

public class BoggleSolver {

    private final Trie trie = new Trie();
    private ArrayList<String> answers = null;
    private int uid = 0;

    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null) {
            throw new IllegalArgumentException();
        }
        for (String word : dictionary) {
            trie.insert(word);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) {
            throw new IllegalArgumentException();
        }
        answers = new ArrayList<>();
        uid++;
        boolean[][] visited = new boolean[board.rows()][board.cols()];
        for (int row = 0; row < board.rows(); row++) {
            for (int col = 0; col < board.cols(); col++) {
                clearVisited(visited);
                dfs(board, row, col, visited, null);
            }
        }
        return new ArrayList<>(answers);
    }

    private void clearVisited(boolean[][] visited) {
        for (boolean[] b : visited) {
            Arrays.fill(b, false);
        }
    }

    private boolean isValid(BoggleBoard board, int row, int col) {
        return row >= 0 && row < board.rows() && col >= 0 && col < board.cols();
    }

    private void dfs(BoggleBoard board, int row, int col, boolean[][] visited, TrieNode cache) {
        char c = board.getLetter(row, col);
        visited[row][col] = true;
        TrieNode node = trie.prefixNode(c, cache);
        // Add a sign in TrieNode, to know if this word has already been added
        // Instead of using a Set
        if (node != null) {
            if (node.isEnd() && node.getUid() != uid) {
                String word = node.getWord();
                if (word.length() > 2) {
                    answers.add(word);
                    node.setUid(uid);
                }
            }
            // Make sure that you have implemented the critical backtracking optimization
            // Means when next trie node is null, no need to dfs more
            if (node.hasChild()) {
                for (int x = -1; x <= 1; x++) {
                    if ((x == -1 && row == 0) || (x == 1 && row == board.rows() - 1)) {
                        continue;
                    }
                    for (int y = -1; y <= 1; y++) {
                        if ((y == -1 && col == 0) || (y == 1 && col == board.cols())) {
                            continue;
                        }
                        if (x == 0 && y == 0) {
                            continue;
                        }
                        int newRow = row + x;
                        int newCol = col + y;
                        if (isValid(board, newRow, newCol) && !visited[newRow][newCol]) {
                            dfs(board, newRow, newCol, visited, node);
                        }
                    }
                }
            }
        }
        visited[row][col] = false;
    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (trie.search(word)) {
            int length = word.length();
            if (length < 3) {
                return 0;
            } else if (length <= 4) {
                return 1;
            } else if (length == 5) {
                return 2;
            } else if (length == 6) {
                return 3;
            } else if (length == 7) {
                return 5;
            } else {
                return 11;
            }
        } else {
            return 0;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        int count = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
            count++;
        }
        StdOut.println("Score = " + score);
        StdOut.println("Count = " + count);
    }

}