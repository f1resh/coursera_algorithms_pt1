/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class KdTree {

    private class KdTreeNode {
        public Point2D point;
        public KdTreeNode left;
        public KdTreeNode right;
        public boolean isVertical;
        public RectHV rectangle;

        KdTreeNode(Point2D key, boolean vertical, RectHV rect) {
            point = key;
            isVertical = vertical;
            right = null;
            left = null;
            rectangle = rect;
        }

        public int compareTo(Point2D other) {
            if (isVertical) {
                return Point2D.X_ORDER.compare(point, other);
            }
            else {
                return Point2D.Y_ORDER.compare(point, other);
            }
        }
    }

    private KdTreeNode root;
    private int size;

    public KdTree() {
        root = null;
        size = 0;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public int size() {
        return size;
    }

    private RectHV createRectangle(boolean isVertical, boolean isRight, Point2D p,
                                   RectHV parentRect) {
        if (isVertical) {
            if (isRight) {
                return new RectHV(p.x(), parentRect.ymin(), parentRect.xmax(), parentRect.ymax());
            }
            else {
                return new RectHV(parentRect.xmin(), parentRect.ymin(), p.x(), parentRect.ymax());
            }
        }
        else {
            if (isRight) {
                return new RectHV(parentRect.xmin(), p.y(), parentRect.xmax(), parentRect.ymax());
            }
            else {
                return new RectHV(parentRect.xmin(), parentRect.ymin(), parentRect.xmax(), p.y());
            }
        }
    }

    public void insert(Point2D newPoint) {
        if (newPoint == null) throw new IllegalArgumentException("No point provided");
        if (isEmpty()) {
            root = new KdTreeNode(newPoint, true, new RectHV(0, 0, 1, 1));
            size = 1;
            return;
        }

        KdTreeNode current = root;

        while (true) {
            if (current.point.equals(newPoint)) return;
            if (current.compareTo(newPoint) < 0) {
                if (current.right == null) {

                    RectHV newNodeRectangle = createRectangle(current.isVertical, true,
                                                              current.point,
                                                              current.rectangle);

                    current.right = new KdTreeNode(newPoint, !current.isVertical, newNodeRectangle);
                    ++size;
                    return;
                }
                current = current.right;
            }
            else {
                if (current.left == null) {

                    RectHV newNodeRectangle = createRectangle(current.isVertical, false,
                                                              current.point,
                                                              current.rectangle);

                    current.left = new KdTreeNode(newPoint, !current.isVertical, newNodeRectangle);
                    ++size;
                    return;
                }
                current = current.left;
            }
        }
    }

    public boolean contains(Point2D p) {
        if (p == null) throw new IllegalArgumentException("No point provided");
        if (isEmpty()) return false;

        KdTreeNode current = root;
        while (current != null) {
            if (current.point.equals(p)) return true;
            if (current.compareTo(p) < 0) {
                current = current.right;
            }
            else {
                current = current.left;
            }
        }
        return false;
    }

    private void checkRectangle(KdTreeNode x, RectHV rect, ArrayList<Point2D> list) {
        if (x == null) return;
        if (rect.contains(x.point))
            list.add(x.point);

        if (x.left != null && rect.intersects(x.left.rectangle))
            checkRectangle(x.left, rect, list);

        if (x.right != null && rect.intersects(x.right.rectangle))
            checkRectangle(x.right, rect, list);
    }

    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) throw new IllegalArgumentException("No point provided");
        if (isEmpty()) return null;
        ArrayList<Point2D> result = new ArrayList<>();
        checkRectangle(root, rect, result);
        return result;
    }

    private Point2D checkPoint(KdTreeNode x, Point2D point, Point2D min) {

        if (x == null) return min;
        if (point.distanceSquaredTo(x.point) < point.distanceSquaredTo(min)) {
            min = x.point;
        }

        int compare = x.compareTo(point);

        // right/top side
        if (compare < 0) {
            min = checkPoint(x.right, point, min);
            if (x.left != null && min.distanceSquaredTo(point) > x.left.rectangle.distanceSquaredTo(
                    point)) {
                min = checkPoint(x.left, point, min);
            }
        }
        else {
            min = checkPoint(x.left, point, min);
            if (x.right != null
                    && min.distanceSquaredTo(point) > x.right.rectangle.distanceSquaredTo(point)) {
                min = checkPoint(x.right, point, min);
            }
        }

        return min;
    }

    public Point2D nearest(Point2D p) {
        if (p == null) throw new IllegalArgumentException("No point provided");
        if (isEmpty()) return null;

        return checkPoint(root, p, root.point);
    }

    private Iterable<KdTreeNode> allNodes() {
        Queue<KdTreeNode> q = new Queue<KdTreeNode>();
        inorder(root, q);
        return q;
    }

    private void inorder(KdTreeNode x, Queue<KdTreeNode> q) {
        if (x == null) return;
        q.enqueue(x);
        inorder(x.left, q);
        inorder(x.right, q);
    }

    public void draw() {
        if (isEmpty()) return;
        double xmin, xmax, ymin, ymax;
        for (KdTreeNode n : allNodes()) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.01);
            StdDraw.point(n.point.x(), n.point.y());
            if (n.isVertical) {
                StdDraw.setPenColor(StdDraw.RED);
                xmin = n.point.x();
                xmax = n.point.x();
                ymin = n.rectangle.ymin();
                ymax = n.rectangle.ymax();
            }
            else {
                StdDraw.setPenColor(StdDraw.BLUE);
                xmin = n.rectangle.xmin();
                xmax = n.rectangle.xmax();
                ymin = n.point.y();
                ymax = n.point.y();
            }
            StdDraw.setPenRadius();
            StdDraw.line(xmin, ymin, xmax, ymax);
        }
    }

    public static void main(String[] args) {
        KdTree tree = new KdTree();
        tree.insert(new Point2D(1.0, 0.5));
        tree.insert(new Point2D(0.5, 0.5));
        var list = tree.range(new RectHV(0, 0, 1, 1));
        for (Point2D p : list) {
            StdOut.println(p);
        }
    }
}
