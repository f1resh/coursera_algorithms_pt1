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

public class FastCollinearPoints {
    private LineSegment[] segments;


    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        validateArgument(points);
        ArrayList<LineSegment> segmList = new ArrayList<>();

        Point[] pointsCopy = Arrays.copyOf(points, points.length);
        Arrays.sort(pointsCopy, Point::compareTo);


        for (int i = 0; i < pointsCopy.length; ++i) {
            Point current = points[i];
            Point[] slopeSorted = Arrays.copyOf(pointsCopy, pointsCopy.length);
            Arrays.sort(slopeSorted, current.slopeOrder());
            int segmSize = 1;
            int segmStart = 0;
            for (int j = 1; j < slopeSorted.length; ++j) {

                if (current.slopeTo(slopeSorted[j]) == current.slopeTo(slopeSorted[j - 1])
                ) {
                    segmSize++;
                }
                else {
                    if (segmSize >= 3) {
                        if (current.compareTo(slopeSorted[segmStart]) < 0) {
                            segmList.add(
                                    new LineSegment(current,
                                                    slopeSorted[segmStart + segmSize - 1]));
                        }

                    }
                    segmSize = 1;
                    segmStart = j;
                }

                if (j == slopeSorted.length - 1 && segmSize >= 3) {

                    if (current.compareTo(slopeSorted[segmStart]) < 0) {
                        segmList.add(
                                new LineSegment(current, slopeSorted[segmStart + segmSize - 1]));
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
