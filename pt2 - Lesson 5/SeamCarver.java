/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;

public class SeamCarver {

    private Picture picture;
    private double[][] energies;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        guardIsNotNull(picture);
        this.picture = new Picture(picture);
        calculateEnergies();

    }

    private void calculateEnergies() {
        this.energies = new double[picture.width()][picture.height()];
        for (int i = 0; i < width(); ++i) {
            for (int j = 0; j < height(); ++j) {
                calculateSquareEnergy(i, j);
            }
        }
    }

    private void transpose() {
        int width = picture.width();
        int height = picture.height();
        Picture transposed = new Picture(height, width);  // Swap dimensions
        double[][] newEnergies = new double[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color color = picture.get(col, row);
                newEnergies[row][col] = energies[col][row];
                transposed.set(row, col, color);  // Swap row and col
            }
        }
        picture = transposed;
        energies = newEnergies;
    }

    // current picture
    public Picture picture() {
        return new Picture(picture);
    }

    // width of current picture
    public int width() {
        return picture.width();
    }

    // height of current picture
    public int height() {
        return picture.height();
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        if (x < 0 || y < 0 || x >= width() || y >= height())
            throw new IllegalArgumentException("Arguments are not in range");

        return energies[x][y];
    }

    private void calculateSquareEnergy(int x, int y) {
        if (x == 0 || y == 0 || x == width() - 1 || y == height() - 1) {
            energies[x][y] = 1000;
            return;
        }

        energies[x][y] = Math.sqrt(
                gradientDiference(x - 1, y, x + 1, y)
                        + gradientDiference(x, y - 1, x, y + 1)
        );
    }

    private int gradientDiference(int x1, int y1, int x2, int y2) {
        int argb1 = picture.getRGB(x1, y1);

        int r1 = (argb1 >> 16) & 0xFF;
        int g1 = (argb1 >> 8) & 0xFF;
        int b1 = (argb1 >> 0) & 0xFF;

        int argb2 = picture.getRGB(x2, y2);

        int r2 = (argb2 >> 16) & 0xFF;
        int g2 = (argb2 >> 8) & 0xFF;
        int b2 = (argb2 >> 0) & 0xFF;

        return (r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2);
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        transpose();
        int[] seam = getVerticalSeam();
        transpose();
        return seam;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        return getVerticalSeam();
    }

    private int[] getVerticalSeam() {


        int[][] edgeTo = new int[width()][height()];
        double[][] distTo = new double[width()][height()];

        // first row is special
        for (int col = 0; col < width(); ++col) {
            edgeTo[col][0] = -1;
            distTo[col][0] = 1000;
        }

        // populating edgeTo and distTo arrays
        for (int row = 1; row < height(); ++row) {
            for (int col = 0; col < width(); ++col) {
                double value = energies[col][row];
                int xminE = minDistV(col, row, distTo);
                int yminE = row - 1;
                distTo[col][row] = value + distTo[xminE][yminE];
                edgeTo[col][row] = xminE;
            }
        }

        // finding min dist value in lastrow
        int seamEndingX = 0;
        double min = 1000 * height();
        for (int col = 0; col < width(); ++col) {
            if (distTo[col][height() - 1] < min) {
                seamEndingX = col;
                min = distTo[col][height() - 1];
            }
        }

        // populating seam
        int[] seam = new int[height()];
        for (int row = height() - 1; row > 0; --row) {
            seam[row] = seamEndingX;
            seamEndingX = edgeTo[seamEndingX][row];
        }
        seam[0] = seamEndingX;

        return seam;
    }

    private int minDistV(int x, int y, double[][] distTo) {

        double min = distTo[x][y - 1];
        int minX = x;

        if (x > 0) {
            double left = distTo[x - 1][y - 1];
            if (left < min) {
                min = left;
                minX = x - 1;
            }
        }
        if (x < width() - 1) {
            double right = distTo[x + 1][y - 1];
            if (right < min) {
                minX = x + 1;
            }
        }
        return minX;
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        guardIsNotNull(seam);
        transpose();
        removeVerticalSeam(seam);
        transpose();
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        guardIsNotNull(seam);

        if (seam.length != height() || width() <= 1)
            throw new IllegalArgumentException(
                    "Argument is not valid: seam length is not equal to height");
        checkSeam(seam, width());

        int newWidth = width() - 1;
        int newHeight = height();
        Picture newPicture = new Picture(newWidth, newHeight);
        // int[][] newArgbs = new int[newWidth][newHeight];

        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++) {
                if (col < seam[row]) {
                    Color color = picture.get(col, row);
                    newPicture.set(col, row, color);
                }
                else if (col > seam[row]) {
                    Color color = picture.get(col, row);
                    newPicture.set(col - 1, row, color);
                }
            }
        }
        picture = newPicture;
        calculateEnergies();
    }

    private void checkSeam(int[] seam, int border) {
        for (int i = 0; i < seam.length; ++i) {
            if (seam[i] < 0 || seam[i] >= border)
                throw new IllegalArgumentException("Incorrect seam: out of boundaries");
            if (i < seam.length - 1 && Math.abs((seam[i] - seam[i + 1])) > 1)
                throw new IllegalArgumentException("Incorrect seam: difference is more than 1");
        }
    }

    private <T> void guardIsNotNull(T obj) {
        if (obj == null) {
            throw new IllegalArgumentException("Argument is null");
        }
    }


    //  unit testing (optional)
    public static void main(String[] args) {
        Picture inputImg = new Picture("out2.png");
        SeamCarver sc = new SeamCarver(inputImg);
        int[] seam = { 2, 3, 2, 2, 3, 2 };
        sc.removeVerticalSeam(seam);
        StdOut.println(sc.width()); //   ==> 6

        String marker = " ";
        for (int row = 0; row < sc.height(); ++row) {
            for (int col = 0; col < sc.width(); ++col) {
                StdOut.printf("%7.2f%s ", sc.energy(col, row), marker);
            }
            StdOut.print("\n");
        }
        // int removeColumns = 30;
        // int removeRows = 40;
        //
        // StdOut.printf("image is %d columns by %d rows\n", inputImg.width(), inputImg.height());
        // SeamCarver sc = new SeamCarver(inputImg);
        //
        // Stopwatch sw = new Stopwatch();
        //
        // for (int i = 0; i < removeRows; i++) {
        //     int[] horizontalSeam = sc.findHorizontalSeam();
        //     sc.removeHorizontalSeam(horizontalSeam);
        // }
        //
        // for (int i = 0; i < removeColumns; i++) {
        //     int[] verticalSeam = sc.findVerticalSeam();
        //     sc.removeVerticalSeam(verticalSeam);
        // }
        // Picture outputImg = sc.picture();
        //
        // StdOut.printf("new image size is %d columns by %d rows\n", sc.width(), sc.height());
        //
        // StdOut.println("Resizing time: " + sw.elapsedTime() + " seconds.");
        // inputImg.show();
        // outputImg.show();
    }

}
