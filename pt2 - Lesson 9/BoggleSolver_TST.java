/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */


import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.TreeSet;

public class BoggleSolver_TST {
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    private static final int[] DX = { -1, 1, 0, 0, -1, -1, 1, 1 };
    private static final int[] DY = { 0, 0, -1, 1, -1, 1, -1, 1 };

    private class myTST<Value> {
        private Node root;

        private class Node {
            private Value val;
            private char c;
            private Node left, mid, right;
        }

        public void put(String key, Value val) {
            root = put(root, key, val, 0);
        }

        private Node put(Node x, String key, Value val, int d) {
            char c = key.charAt(d);
            if (x == null) {
                x = new Node();
                x.c = c;
            }
            if (c < x.c) x.left = put(x.left, key, val, d);
            else if (c > x.c) x.right = put(x.right, key, val, d);
            else if (d < key.length() - 1) x.mid = put(x.mid, key, val, d + 1);
            else x.val = val;
            return x;
        }

        public boolean contains(String key) {
            return get(key) != null;
        }

        public Value get(String key) {
            Node x = get(root, key, 0);
            if (x == null) return null;
            return (Value) x.val;
        }

        private Node get(Node x, String key, int d) {
            if (x == null) return null;
            char c = key.charAt(d);
            if (c < x.c) return get(x.left, key, d);
            else if (c > x.c) return get(x.right, key, d);
            else if (d < key.length() - 1) return get(x.mid, key, d + 1);
            else return x;
        }

        public boolean hasKeyPrefix(String key) {
            Node x = get(root, key, 0);
            if (x == null) return false;
            return x.left != null || x.mid != null || x.right != null;
        }
    }

    private final myTST<Boolean> words;

    public BoggleSolver_TST(String[] dictionary) {
        if (dictionary.length == 0) {
            throw new IllegalArgumentException("Dictionary cannot be empty");
        }
        words = new myTST<Boolean>();
        for (int i = 0; i < dictionary.length; ++i) {
            words.put(dictionary[i], Boolean.TRUE);
        }
    }

    private void dfs(int x, int y, BoggleBoard board, boolean[][] visited, String currentString,
                     TreeSet<String> result) {
        visited[x][y] = true;
        // Do something with the current path here
        currentString += (
                board.getLetter(x, y) == 'Q'
                ? "QU"
                : board.getLetter(x, y)
        );

        if (currentString.length() >= 3 && words.contains(currentString)) {
            result.add(currentString);
        }

        if (!words.hasKeyPrefix(currentString)) {
            // If no words start with the current prefix, stop exploring this path
            visited[x][y] = false; // backtrack
            return;
        }

        for (int dir = 0; dir < 8; dir++) {
            int nx = x + DX[dir];
            int ny = y + DY[dir];
            if (nx >= 0 && nx < board.rows() && ny >= 0 && ny < board.cols() && !visited[nx][ny]) {
                dfs(nx, ny, board, visited, currentString, result);
            }
        }
        visited[x][y] = false;
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        if (board == null) {
            throw new IllegalArgumentException("Board cannot be null");
        }
        if (board.rows() == 0 || board.cols() == 0) {
            throw new IllegalArgumentException("Board cannot be empty");
        }

        TreeSet<String> result = new TreeSet<>();

        int n = board.rows();
        int m = board.cols();


        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                boolean[][] visited = new boolean[n][m];
                String currentString = "";
                dfs(i, j, board, visited, currentString, result);
            }
        }

        return result;

    }

    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (word == null) {
            throw new IllegalArgumentException("Word cannot be null");
        }
        if (!words.contains(word)) {
            return 0;
        }
        int length = word.length();
        switch (length) {
            case 0:
            case 1:
            case 2:
                return 0;
            case 3:
            case 4:
                return 1;
            case 5:
                return 2;
            case 6:
                return 3;
            case 7:
                return 5;
            default: // length >= 8
                return 11;
        }
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
