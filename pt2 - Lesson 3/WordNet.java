/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Topological;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class WordNet {

    //

    private final HashMap<String, List<Integer>> ids;
    private final HashMap<Integer, String> synsetDict;
    private final SAP sap;

    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {
        guardIsNotNull(synsets);
        guardIsNotNull(hypernyms);
        int vertexNumber = 0;

        ids = new HashMap<String, List<Integer>>();
        synsetDict = new HashMap<Integer, String>();
        In in = new In(synsets);
        while (in.hasNextLine()) {
            vertexNumber++;
            String[] parts = in.readLine().split(",");
            int id = Integer.parseInt(parts[0]);
            String synset = parts[1];
            for (String word : synset.split(" ")) {
                if (!ids.containsKey(word)) {
                    ids.put(word, new LinkedList<Integer>());
                }
                ids.get(word).add(id);
            }
            synsetDict.put(id, synset);
        }
        in.close();

        Digraph digraph = new Digraph(vertexNumber);

        in = new In(hypernyms);
        while (in.hasNextLine()) {
            String[] parts = in.readLine().split(",");
            int from = Integer.parseInt(parts[0]);
            for (int i = 1; i < parts.length; i++) {
                int to = Integer.parseInt(parts[i]);
                digraph.addEdge(from, to);
            }

        }
        in.close();

        DirectedCycle dCycle = new DirectedCycle(digraph);
        Topological top = new Topological(digraph);
        if (dCycle.hasCycle()) throw new IllegalArgumentException("Digraph has cycle");
        if (!top.hasOrder()) throw new IllegalArgumentException("Digraph has cycle");
        validate(digraph);
        sap = new SAP(digraph);
    }

    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return ids.keySet();
    }

    // is the word a WordNet noun?
    public boolean isNoun(String word) {
        guardIsNotNull(word);
        return ids.containsKey(word);
    }

    // distance between nounA and nounB (defined below)
    public int distance(String nounA, String nounB) {
        guardIsNotNull(nounA);
        guardIsNotNull(nounB);
        if (!isNoun(nounA) || !isNoun(nounB)) {
            throw new IllegalArgumentException("Word is missing from WordNet");
        }
        if (nounA.equals(nounB)) return 0;

        List<Integer> vertexA = ids.get(nounA);
        List<Integer> vertexB = ids.get(nounB);
        return sap.length(vertexA, vertexB);

    }

    // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
    // in a shortest ancestral path (defined below)
    public String sap(String nounA, String nounB) {
        guardIsNotNull(nounA);
        guardIsNotNull(nounB);
        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException("Word is missing from WordNet");

        if (nounA.equals(nounB)) {
            var b = ids.get(nounA);
            return synsetDict.get(b.get(0));
        }

        List<Integer> vertexA = ids.get(nounA);
        List<Integer> vertexB = ids.get(nounB);
        int result = sap.ancestor(vertexA, vertexB);
        return synsetDict.get(result);
    }

    private <T> void guardIsNotNull(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Argument is null");
        }
    }

    private void validate(Digraph G) {
        int numberOfRoot = 0;
        for (int i = 0; i < G.V(); i++)
            if (G.outdegree(i) == 0)
                numberOfRoot++;
        if (numberOfRoot != 1)
            throw new IllegalArgumentException("The input does not correspond to a rooted DAG.");
    }

    // do unit testing of this class
    public static void main(String[] args) {
        WordNet wn = new WordNet(args[0], args[1]);
        StdOut.println(
                wn.distance("chondrin",
                            "scleroprotein albuminoid"));
        StdOut.println(
                wn.sap("globin hematohiston haematohiston", "gamma_globulin human_gamma_globulin"));
    }
}
