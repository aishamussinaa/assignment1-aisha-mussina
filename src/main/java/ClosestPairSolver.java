import java.util.Arrays;
import java.util.Comparator;

public class ClosestPairSolver {
    private static final Comparator<Point> BY_X =
            Comparator.comparingDouble((Point p) -> p.x)
                    .thenComparingDouble(p -> p.y);

    private static final Comparator<Point> BY_Y =
            Comparator.comparingDouble((Point p) -> p.y)
                    .thenComparingDouble(p -> p.x);

    private long distanceChecks;
    private int maxDepth;

    public double solve(Point[] points) {
        distanceChecks = 0;
        maxDepth = 0;

        if (points.length < 2) {
            return Double.POSITIVE_INFINITY;
        }

        Point[] sorted = points.clone();
        Arrays.sort(sorted, BY_X);

        Point[] buffer = new Point[points.length];
        Point[] strip = new Point[points.length];

        return closest(sorted, buffer, strip, 0, sorted.length - 1, 1);
    }

    private double closest(Point[] points, Point[] buffer, Point[] strip,
                           int left, int right, int depth) {
        maxDepth = Math.max(maxDepth, depth);

        if (right - left + 1 <= 3) {
            double best = Double.POSITIVE_INFINITY;

            for (int i = left; i <= right; i++) {
                for (int j = i + 1; j <= right; j++) {
                    best = Math.min(best, distance(points[i], points[j]));
                }
            }

            Arrays.sort(points, left, right + 1, BY_Y);
            return best;
        }

        int middle = left + (right - left) / 2;
        double middleX = points[middle].x;

        double leftDistance = closest(
                points, buffer, strip, left, middle, depth + 1
        );

        double rightDistance = closest(
                points, buffer, strip, middle + 1, right, depth + 1
        );

        double best = Math.min(leftDistance, rightDistance);

        mergeByY(points, buffer, left, middle, right);

        int stripSize = 0;

        for (int i = left; i <= right; i++) {
            if (Math.abs(points[i].x - middleX) < best) {
                strip[stripSize++] = points[i];
            }
        }

        for (int i = 0; i < stripSize; i++) {
            for (int j = i + 1;
                 j < stripSize && j <= i + 7;
                 j++) {

                if (strip[j].y - strip[i].y >= best) {
                    break;
                }

                best = Math.min(best, distance(strip[i], strip[j]));
            }
        }

        return best;
    }

    private void mergeByY(Point[] points, Point[] buffer,
                          int left, int middle, int right) {
        int i = left;
        int j = middle + 1;
        int k = left;

        while (i <= middle && j <= right) {
            if (BY_Y.compare(points[i], points[j]) <= 0) {
                buffer[k++] = points[i++];
            } else {
                buffer[k++] = points[j++];
            }
        }

        while (i <= middle) {
            buffer[k++] = points[i++];
        }

        while (j <= right) {
            buffer[k++] = points[j++];
        }

        for (int index = left; index <= right; index++) {
            points[index] = buffer[index];
        }
    }

    private double distance(Point a, Point b) {
        distanceChecks++;
        return Math.hypot(a.x - b.x, a.y - b.y);
    }

    public long getDistanceChecks() {
        return distanceChecks;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}