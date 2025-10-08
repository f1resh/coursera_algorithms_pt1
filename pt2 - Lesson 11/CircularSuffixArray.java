/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

import java.util.Arrays;

public class CircularSuffixArray {

    private final Integer[] index;
    private final int length;
    private final String text;

    // circular suffix array of s
    public CircularSuffixArray(String s) {
        if (s == null) throw new IllegalArgumentException("s is null");
        text = s;
        length = s.length();
        index = new Integer[length];

        for (int i = 0; i < length; i++) {
            index[i] = i;
        }

        Arrays.sort(index, (a, b) -> compareSuffixes(a, b));
    }

    private int compareSuffixes(int i, int j) {
        for (int k = 0; k < length; k++) {
            char charI = text.charAt((i + k) % length);
            char charJ = text.charAt((j + k) % length);
            if (charI != charJ) {
                return charI - charJ;
            }
        }
        return 0;
    }

    // length of s
    public int length() {
        return length;
    }

    // returns index of ith sorted suffix
    public int index(int i) {
        if (i >= length || i < 0) throw new IllegalArgumentException("i is out of range");
        return index[i];
    }

    // unit testing (required)
    public static void main(String[] args) {
        CircularSuffixArray car = new CircularSuffixArray("ABRACADABRA!");
        StdOut.println(car.index(4));
    }

}
