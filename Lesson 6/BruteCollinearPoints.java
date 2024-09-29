/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {

    private LineSegment[] segments;


    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        validateArgument(points);
        ArrayList<LineSegment> segmList = new ArrayList<>();

        for (int i = 0; i < points.length; ++i) {
            for (int j = i + 1; j < points.length; ++j) {
                double slopeFirst = points[i].slopeTo(points[j]);
                for (int k = j + 1; k < points.length; ++k) {
                    double slopeSecond = points[i].slopeTo(points[k]);
                    if (slopeFirst == slopeSecond) {
                        for (int m = k + 1; m < points.length; ++m) {
                            double slopeThird = points[i].slopeTo(points[m]);
                            if (slopeFirst == slopeThird) {
                                Point[] fullSegment = new Point[4];
                                fullSegment[0] = points[i];
                                fullSegment[1] = points[j];
                                fullSegment[2] = points[k];
                                fullSegment[3] = points[m];
                                Arrays.sort(fullSegment);
                                segmList.add(new LineSegment(fullSegment[0], fullSegment[3]));
                            }
                        }
                    }
                }
            }
        }
        segments = segmList.toArray(new LineSegment[segmList.size()]);
    }

    private void validateArgument(Point[] points) {
        if (points == null)
            throw new java.lang.IllegalArgumentException("Points array is null or empty");
        for (int i = 0; i < points.length; ++i) {
            if (points[i] == null)
                throw new java.lang.IllegalArgumentException("One of the points is null");
            for (int j = i + 1; j < points.length; ++j) {
                if (points[j] == null)
                    throw new java.lang.IllegalArgumentException("One of the points is null");
                if (points[i].compareTo(points[j]) == 0)
                    throw new java.lang.IllegalArgumentException("Some points are repeated");
            }
        }
    }

    // the number of line segments
    public int numberOfSegments() {
        return segments.length;
    }

    // the line segments
    public LineSegment[] segments() {
        return Arrays.copyOf(segments, numberOfSegments());
    }

    public static void main(String[] args) {

        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
