/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;


public class SAP {

    private class Pair {
        private final Set<Integer> first;
        private final Set<Integer> second;

        public Pair(Set<Integer> first, Set<Integer> second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;
            Pair pair = (Pair) other;
            return Objects.equals(first, pair.first) && Objects.equals(second, pair.second);
        }

        @Override
        public int hashCode() {
            return Objects.hash(first, second);
        }
    }

    // constructor takes a digraph (not necessarily a DAG)
    private final Digraph digraph;
    private final Map<Pair, Integer[]> cache;


    public SAP(Digraph G) {
        guardIsNotNull(G);
        digraph = new Digraph(G);
        cache = new HashMap<>();
    }

    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {
        checkInputVertex(v);
        checkInputVertex(w);
        if (v == w) return 0;

        return length(Set.of(v), Set.of(w));
    }

    // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
    public int ancestor(int v, int w) {
        checkInputVertex(v);
        checkInputVertex(w);
        if (v == w) return v;

        return ancestor(Set.of(v), Set.of(w));
    }

    // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        guardIsNotNull(v);
        guardIsNotNull(w);

        if (v == w) return 0;

        Set<Integer> setV = createSet(v);
        Set<Integer> setW = createSet(w);
        Pair pairs = new Pair(setV, setW);

        var cachedValue = cache.get(pairs);
        if (cachedValue != null) {
            return cachedValue[1];
        }

        int ancestor = ancestor(v, w);
        if (ancestor != -1) {
            return cache.get(pairs)[1];
        }
        else {
            return -1;
        }
    }

    // a common ancestor that participates in shortest ancestral path; -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        guardIsNotNull(v);
        guardIsNotNull(w);

        Pair pair = new Pair(createSet(v), createSet(w));
        var cachedValue = cache.get(pair);
        if (cachedValue != null) {
            return cachedValue[0];
        }

        int[] visited = new int[digraph.V()];
        int currentStep = 1;

        ArrayList<Integer> left = new ArrayList<>();
        for (Integer x : v) {
            guardIsNotNull(x);
            checkInputVertex(x);

            visited[x] = -currentStep;
            addAdjacentToList(x, left);
        }

        ArrayList<Integer> right = new ArrayList<>();
        for (Integer x : w) {
            guardIsNotNull(x);
            checkInputVertex(x);

            if (createSet(v).contains(x)) {
                cache.put(pair, new Integer[] { x, 0 });
                return x;
            }

            visited[x] = currentStep;
            addAdjacentToList(x, right);
        }

        int distance = Integer.MAX_VALUE;
        int candidate = -1;


        while (!left.isEmpty() || !right.isEmpty()) {
            currentStep++;
            if (!left.isEmpty()) {
                ArrayList<Integer> newList = new ArrayList<>();
                for (int vertex : left) {
                    if (visited[vertex] > 0) {
                        if (visited[vertex] == 1) {
                            cache.put(pair,
                                      new Integer[] { vertex, currentStep - 1 });
                            return vertex;
                        }
                        else {
                            if (currentStep - 1 + visited[vertex] - 1 < distance) {
                                candidate = vertex;
                                distance = currentStep - 1 + visited[vertex] - 1;
                            }
                            addAdjacentToList(vertex, newList);
                        }
                    }
                    else if (visited[vertex] == 0) {
                        visited[vertex] = -currentStep;
                        addAdjacentToList(vertex, newList);
                    }
                }
                left = newList;
            }

            if (!right.isEmpty()) {
                ArrayList<Integer> newList = new ArrayList<>();
                for (int vertex : right) {
                    if (visited[vertex] < 0) {
                        if (visited[vertex] == -1) {
                            cache.put(pair,
                                      new Integer[] { vertex, currentStep - 1 });
                            return vertex;
                        }
                        else {
                            if (currentStep - 1 - visited[vertex] - 1 < distance) {
                                distance = currentStep - 1 - visited[vertex] - 1;
                                candidate = vertex;
                            }
                            addAdjacentToList(vertex, newList);
                        }
                    }
                    else if (visited[vertex] == 0) {
                        visited[vertex] = currentStep;
                        addAdjacentToList(vertex, newList);
                    }
                }
                right = newList;
            }

            if (candidate != -1 && currentStep + 1 > distance) {
                break;
            }

        }
        if (candidate != -1) {
            cache.put(pair,
                      new Integer[] { candidate, distance });
            return candidate;
        }
        return -1;
    }

    private <T> void guardIsNotNull(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Argument is null");
        }
    }

    private void checkInputVertex(int v) {
        if (v < 0 || v >= digraph.V())
            throw new IllegalArgumentException("incorrect input vertices");
    }

    private void addAdjacentToList(int vertex, ArrayList<Integer> targetList) {
        for (int x : digraph.adj(vertex)) {
            targetList.add(x);
        }
    }

    private Set<Integer> createSet(Iterable<Integer> object) {
        Set<Integer> set = new HashSet<>();
        object.forEach(set::add);
        return set;
    }

    // do unit testing of this class
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            // ArrayList<Integer> v = new ArrayList<>(Arrays.asList(3, 4, 15, 17, 18));
            // ArrayList<Integer> w = new ArrayList<>(Arrays.asList(3, null, 15, 17, 18));
            // v.add(null);
            int length = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
