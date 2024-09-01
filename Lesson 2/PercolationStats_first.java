import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;


public class PercolationStats {

    private static final double CONF_CONST = 1.96;

    private int numberOfTests;
    private int gridSize;

    private double[] results;
    private int[] order;

    // perform independent trials on an n-by-n grid
    public PercolationStats(int n, int trials) {
        if (n <= 0 || trials <= 0) throw new IllegalArgumentException("Wrong arguments");
        gridSize = n;
        numberOfTests = trials;
        results = new double[numberOfTests];
        runTests();
    }

    private void randomizeShuffle() {
        // randomize order of opening sites
        order = new int[gridSize * gridSize];
        for (int i = 0; i < gridSize * gridSize; ++i) {
            order[i] = i + 1;
        }
        StdRandom.shuffle(order);
    }

    private void runTests() {
        // processing percolation
        for (int i = 0; i < numberOfTests; ++i) {
            Percolation perc = new Percolation(gridSize);
            randomizeShuffle();

            int k = 0;
            while (!perc.percolates()) {
                int row = (order[k] - 1) / gridSize + 1;
                int col = (order[k] - 1) % gridSize + 1;
                perc.open(row, col);
                k++;
            }
            results[i] = (double) perc.numberOfOpenSites() / (gridSize * gridSize);
        }
    }

    // sample mean of percolation threshold
    public double mean() {
        return StdStats.mean(results);
    }

    // sample standard deviation of percolation threshold
    public double stddev() {
        return StdStats.stddev(results);
    }

    // low endpoint of 95% confidence interval
    public double confidenceLo() {
        return mean() - CONF_CONST * stddev() / Math.sqrt(numberOfTests);
    }

    // high endpoint of 95% confidence interval
    public double confidenceHi() {
        return mean() + CONF_CONST * stddev() / Math.sqrt(numberOfTests);
    }

    // test client (see below)
    public static void main(String[] args) {
        // Stopwatch stopWatch = new Stopwatch();
        PercolationStats pStats = new PercolationStats(Integer.parseInt(args[0]),
                                                       Integer.parseInt(args[1]));
        // double elapsedTime = stopWatch.elapsedTime();
        StdOut.printf("mean\t\t\t\t\t= %.18f\n", pStats.mean());
        StdOut.printf("stddev\t\t\t\t\t= %.18f\n", pStats.stddev());
        StdOut.printf("95%% confidence interval\t= [%.18f,%.18f]\n", pStats.confidenceLo(),
                      pStats.confidenceHi());
        // StdOut.printf("Elapsed time in seconds: %.5f", elapsedTime);
    }
}
