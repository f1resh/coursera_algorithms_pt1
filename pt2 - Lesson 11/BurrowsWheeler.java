/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class BurrowsWheeler {

    // apply Burrows-Wheeler transform,
    // reading from standard input and writing to standard output
    public static void transform() {
        String text = BinaryStdIn.readString();
        CircularSuffixArray csa = new CircularSuffixArray(text);
        int length = text.length();
        char[] result = new char[length];
        for (int i = 0; i < length; i++) {
            int index = csa.index(i);
            if (index == 0) {
                BinaryStdOut.write(i);
            }
            char ch = text.charAt((index + length - 1) % length);
            result[i] = ch;
        }
        BinaryStdOut.write(String.valueOf(result));
        BinaryStdOut.close();
    }

    // apply Burrows-Wheeler inverse transform,
    // reading from standard input and writing to standard output
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String lastColumn = BinaryStdIn.readString();
        int length = lastColumn.length();

        // key-indexed counting to sort lastColumn to get firstColumn
        int radix = 256; // extended ASCII
        int[] count = new int[radix + 1];
        for (int i = 0; i < length; i++) {
            count[lastColumn.charAt(i) + 1]++;
        }
        for (int r = 0; r < radix; r++) {
            count[r + 1] += count[r];
        }
        char[] firstColumn = new char[length];
        int[] next = new int[length];
        for (int i = 0; i < length; i++) {
            char ch = lastColumn.charAt(i);
            int pos = count[ch]++;
            firstColumn[pos] = ch;
            next[pos] = i;
        }

        // reconstruct original string
        int currentIndex = first;
        for (int i = 0; i < length; i++) {
            BinaryStdOut.write(firstColumn[currentIndex]);
            currentIndex = next[currentIndex];
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply Burrows-Wheeler transform
    // if args[0] is "+", apply Burrows-Wheeler inverse transform
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            transform();
        }
        else if (args[0].equals("+")) {
            inverseTransform();
        }
        else {
            throw new IllegalArgumentException("Illegal command line argument");
        }

    }

}
