/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class Board {

    private int[][] field;
    private int size;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        size = tiles.length;
        field = new int[size][];
        for (int i = 0; i < size; i++) {
            field[i] = tiles[i].clone();
        }
    }

    // string representation of this board
    public String toString() {
        String rowSeparator = " ";
        String columnSeparator = "\n";
        StringBuilder sb = new StringBuilder();
        sb.append(size).append(columnSeparator);
        for (int[] row : field) {
            for (int value : row) {
                sb.append(value).append(rowSeparator);
            }
            sb.append(columnSeparator);
        }
        return sb.toString();
    }

    // board dimension n
    public int dimension() {
        return size;
    }

    private int placeColumn(int value) {
        return value % (size) - (value % size == 0 ? -size + 1 : 1);
    }

    private int placeRow(int value) {
        return value / size - (value % size == 0 ? 1 : 0);
    }

    // number of tiles out of place
    public int hamming() {
        int counter = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (field[i][j] == 0) continue;
                if (field[i][j] != i * size + j + 1) {
                    counter++;
                }
            }
        }
        return counter;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int counter = 0;
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int value = field[i][j];
                if (value == 0) continue;
                counter += Math.abs(placeRow(value) - i) + Math.abs(placeColumn(value) - j);
            }
        }
        return counter;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return (hamming() == 0);
    }

    @Override
    // does this board equal y?
    public boolean equals(Object y) {
        if (this == y) return true;
        if (y == null || getClass() != y.getClass()) return false;

        if (dimension() != ((Board) y).dimension()) return false;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (field[i][j] != ((Board) y).field[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    private void swapElements(int i1, int j1, int i2, int j2) {
        int tmp = field[i1][j1];
        field[i1][j1] = field[i2][j2];
        field[i2][j2] = tmp;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        ArrayList<Board> result = new ArrayList<Board>();

        int zeroI = 0;
        int zeroJ = 0;
        outer:
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (field[i][j] == 0) {
                    zeroI = i;
                    zeroJ = j;
                    break outer;
                }
            }
        }

        if (zeroI > 0) {
            Board left = new Board(field);
            left.swapElements(zeroI, zeroJ, zeroI - 1, zeroJ);
            result.add(left);
        }

        if (zeroJ > 0) {
            Board top = new Board(field);
            top.swapElements(zeroI, zeroJ, zeroI, zeroJ - 1);
            result.add(top);
        }

        if (zeroI < size - 1) {
            Board right = new Board(field);
            right.swapElements(zeroI, zeroJ, zeroI + 1, zeroJ);
            result.add(right);
        }

        if (zeroJ < size - 1) {
            Board down = new Board(field);
            down.swapElements(zeroI, zeroJ, zeroI, zeroJ + 1);
            result.add(down);
        }
        return result;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        if (dimension() == 1) return null;
        Board twin = new Board(field);
        if (twin.field[0][0] != 0) {
            if (twin.field[0][1] != 0) {
                twin.swapElements(0, 0, 0, 1);
            }
            else {
                twin.swapElements(0, 0, 1, 0);
            }
        }
        else {
            twin.swapElements(0, 1, 1, 0);
        }
        return twin;
    }


    public static void main(String[] args) {
        int[][] array = {
                { 0, 1, 3 },
                { 8, 4, 2 },
                { 7, 6, 5 }
        };

        Board b = new Board(array);
        StdOut.println(b.toString());
        StdOut.println(b.hamming());
        StdOut.println(b.manhattan());
        StdOut.println(b.isGoal());

        for (Board bd : b.neighbors()) {
            StdOut.println(bd.toString());
        }

    }
}
