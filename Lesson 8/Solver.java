/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class Solver {

    private class Node implements Comparable<Node> {
        public Board board;
        public int moves;
        public Node pNode;
        public int distance;

        public Node(Board currentBoard, int moves, Node prevoiousNode) {
            board = currentBoard;
            this.moves = moves;
            pNode = prevoiousNode;
            distance = board.manhattan();
        }

        public int compareTo(Node other) {
            return Integer.compare(this.distance + this.moves, other.distance + other.moves);
        }
    }

    private Node goal;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException("Initial board is empty");
        Node first = new Node(initial, 0, null);
        Node twinfirst = new Node(initial.twin(), 0, null);
        MinPQ<Node> pq = new MinPQ<Node>();
        MinPQ<Node> twinpq = new MinPQ<Node>();

        pq.insert(first);
        twinpq.insert(twinfirst);

        while (!pq.min().board.isGoal() && !twinpq.min().board.isGoal()) {
            Node curNode = pq.delMin();
            Node twinNode = twinpq.delMin();

            for (Board n : curNode.board.neighbors()) {
                if (curNode.pNode == null || !n.equals(curNode.pNode.board)) {
                    Node newNode = new Node(n, curNode.moves + 1, curNode);
                    pq.insert(newNode);
                }
            }

            for (Board n : twinNode.board.neighbors()) {
                if (twinNode.pNode == null || !n.equals(twinNode.pNode.board)) {
                    Node newNode = new Node(n, twinNode.moves + 1, twinNode);
                    twinpq.insert(newNode);
                }
            }
        }
        if (pq.min().board.isGoal()) goal = pq.min();
        else goal = null;
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return goal != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!isSolvable()) return -1;
        return goal.moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable()) return null;
        Stack<Board> result = new Stack<Board>();
        for (Node t = goal; t != null; t = t.pNode) {
            result.push(t.board);
        }
        return result;
    }

    // test client (see below)
    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // StdOut.println(initial.twin());
        // StdOut.println(initial.twin());

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
