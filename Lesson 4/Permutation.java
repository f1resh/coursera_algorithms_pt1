/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Permutation {
    public static void main(String[] args) {
        int k = Integer.parseInt(args[0]);
        RandomizedQueue<String> rq = new RandomizedQueue<String>();

        while (!StdIn.isEmpty()) {
            String temp = StdIn.readString();
            rq.enqueue(temp);
        }

        // for (int i = 0; i < k; ++i) {
        //    rq.enqueue(StdIn.readString());
        //}

        var it = rq.iterator();
        for (int i = 0; i < k; ++i) {
            StdOut.println(it.next());
        }

    }
}
