import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {

    private static final byte OPEN = 1;
    private static final byte BOTTOM = 2;
    private static final byte TOP = BOTTOM << 1;


    private byte[] states;
    private final WeightedQuickUnionUF grid;
    private final int size;
    private int openSitesNumber = 0;
    private boolean percolateFlag = false;

    public Percolation(int n) {
        if (n <= 0) throw new IllegalArgumentException("Incorrect grid-size, should be >0");
        size = n;
        // 0 element is virtual top
        // n^2 +1 element is virtual bottom
        grid = new WeightedQuickUnionUF(size * size);

        // grid for states
        states = new byte[size * size];
    }

    private void validate(int row, int col) {
        if (row > size || row < 1 || col > size || col < 1)
            throw new IllegalArgumentException("Incorrect grid-size, should be >0");
    }

    private int convertIndices(int row, int col) {
        validate(row, col);
        return (row - 1) * size + col - 1;
    }

    private int connect(int row, int col) {
        int idx = convertIndices(row, col);
        int root = grid.find(idx);

        int above = convertIndices(Math.max(row - 1, 1), col);
        int below = convertIndices(Math.min(row + 1, size), col);
        int left = convertIndices(row, Math.max(col - 1, 1));
        int right = convertIndices(row, Math.min(col + 1, size));

        if (row == 1) states[root] = (byte) (states[root] | TOP);
        if (row == size) states[root] = (byte) (states[root] | BOTTOM);

        byte aboveState = unionState(above, idx, root);
        byte belowState = unionState(below, idx, root);
        byte leftState = unionState(left, idx, root);
        byte rightState = unionState(right, idx, root);

        int newRoot = grid.find(idx);

        states[newRoot] = (byte) (states[root] | states[newRoot] | aboveState | belowState
                | leftState | rightState);

        return newRoot;
    }

    private byte unionState(int adj, int index, int root) {
        if (adj != index && states[adj] != 0) {
            int adjRoot = grid.find(adj);
            grid.union(adj, index);
            return states[adjRoot];
        }
        else {
            return states[root];
        }
    }

    public void open(int row, int col) {
        if (isOpen(row, col)) return;
        int idx = convertIndices(row, col);

        states[idx] = OPEN;
        openSitesNumber++;

        int root = connect(row, col);

        if (states[root] > (TOP | BOTTOM)) percolateFlag = true;
    }

    public boolean isOpen(int row, int col) {
        return states[convertIndices(row, col)] > 0;
    }

    public boolean isFull(int row, int col) {
        validate(row, col);
        return (states[grid.find(convertIndices(row, col))] & TOP) == TOP;

    }

    public int numberOfOpenSites() {
        return openSitesNumber;
    }

    public boolean percolates() {
        return percolateFlag;
    }

    public static void main(String[] args) {
        Percolation p = new Percolation(3);
        p.open(1, 1);
        p.open(2, 1);
        p.open(3, 1);
        p.open(2, 3);
        StdOut.printf("3-2 is Full: %b\n", p.isFull(2, 3));
        p.open(3, 3);
        StdOut.printf("3-3 is Full: %b\n", p.isFull(3, 3));
        StdOut.printf("3-2 is Full: %b\n", p.isFull(2, 3));
    }
}
