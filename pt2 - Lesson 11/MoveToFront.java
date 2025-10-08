/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.ArrayList;
import java.util.List;


public class MoveToFront {

    private static final int RADIX = 256;

    private static List<Character> createSymbolTable() {
        List<Character> symbolTable = new ArrayList<>();
        for (int i = 0; i < RADIX; i++) {
            symbolTable.add((char) i);
        }
        return symbolTable;
    }

    // apply move-to-front encoding, reading from standard input and writing to standard output
    public static void encode() {

        var symbolTable = createSymbolTable();

        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar();
            char index = (char) symbolTable.indexOf(ch);
            BinaryStdOut.write(index);

            symbolTable.remove(index);
            symbolTable.add(0, ch);
        }
        BinaryStdOut.close();
    }

    // apply move-to-front decoding, reading from standard input and writing to standard output
    public static void decode() {

        var symbolTable = createSymbolTable();
        while (!BinaryStdIn.isEmpty()) {
            char ch = BinaryStdIn.readChar();
            char index = symbolTable.get(ch);
            BinaryStdOut.write(index);

            symbolTable.remove(ch);
            symbolTable.add(0, index);
        }
        BinaryStdOut.close();
    }

    // if args[0] is "-", apply move-to-front encoding
    // if args[0] is "+", apply move-to-front decoding
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        }
        else if (args[0].equals("+")) {
            decode();
        }
    }

}