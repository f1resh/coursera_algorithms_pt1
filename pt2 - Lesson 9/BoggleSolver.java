/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */


import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.TreeSet;

public class BoggleSolver {
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
    private static final int[] DX = { -1, 1, 0, 0, -1, -1, 1, 1 };
    private static final int[] DY = { 0, 0, -1, 1, -1, 1, -1, 1 };

    private static final int R = 26;
    private static final int ASCII_A = 65; // ASCII value for 'A'

    private class Node {
        private boolean value;
        private Node[] next = new Node[R];
    }

    private class MyTrieST {

        private Node root = new Node();

        public void put(String key, boolean val) {
            root = put(root, key, val, 0);
        }

        private Node put(Node x, String key, boolean val, int d) {
            if (x == null) x = new Node();
            if (d == key.length()) {
                x.value = val;
                return x;
            }
            char c = key.charAt(d);
            x.next[c - ASCII_A] = put(x.next[c - ASCII_A], key, val, d + 1);
            return x;
        }

        public boolean contains(String key) {
            return get(key);
        }


        public boolean get(String key) {
            Node x = get(root, key, 0);
            if (x == null) return false;
            return x.value;
        }

        private Node get(Node x, String key, int d) {
            if (x == null) return null;
            if (d == key.length()) return x;
            char c = key.charAt(d);
            return get(x.next[c - ASCII_A], key, d + 1);
        }

        public Node getNextNode(Node currentNode, char key) {
            if (currentNode == null) return null;
            return currentNode.next[key - ASCII_A];
        }

        public boolean currentNodeHasKeyPrefix(Node currentNode) {
            if (currentNode == null)
                return false;
            for (int c = 0; c < R; c++) {
                if (currentNode.next[c] != null) return true;
            }
            return false;
        }
    }


    private final MyTrieST words;

    public BoggleSolver(String[] dictionary) {
        if (dictionary.length == 0) {
            throw new IllegalArgumentException("Dictionary cannot be empty");
        }
        words = new MyTrieST();
        for (int i = 0; i < dictionary.length; ++i) {
            words.put(dictionary[i], true);
        }

    }

    private void dfs(int x, int y, BoggleBoard board, boolean[][] visited, String currentString,
                     Node currentNode, TreeSet<String> result) {
        visited[x][y] = true;
        char newChar = board.getLetter(x, y);
        Node newNode = words.getNextNode(currentNode, newChar);

        if (newNode == null) {
            // If the current character does not lead to any valid words, backtrack
            visited[x][y] = false; // backtrack
            return;
        }

        if (newChar == 'Q') {
            // If the letter is 'Q', we treat it as "QU"
            currentString += "QU";
            newNode = words.getNextNode(newNode, 'U');
            if (newNode == null) {
                // If the current character does not lead to any valid words, backtrack
                visited[x][y] = false; // backtrack
                return;
            }
        }
        else {
            currentString += newChar;
        }

        if (currentString.length() >= 3 && newNode.value) {
            result.add(currentString);
        }

        if (!words.currentNodeHasKeyPrefix(newNode)) {
            // If no words start with the current prefix, stop exploring this path
            visited[x][y] = false; // backtrack
            return;
        }

        for (int dir = 0; dir < 8; dir++) {
            int nx = x + DX[dir];
            int ny = y + DY[dir];
            if (nx >= 0 && nx < board.rows() && ny >= 0 && ny < board.cols() && !visited[nx][ny]) {
                dfs(nx, ny, board, visited, currentString, newNode, result);
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
        Node currentNode = words.root;


        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                boolean[][] visited = new boolean[n][m];
                String currentString = "";
                dfs(i, j, board, visited, currentString, currentNode, result);
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
