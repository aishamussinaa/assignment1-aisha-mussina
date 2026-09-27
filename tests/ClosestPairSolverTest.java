import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClosestPairSolverTest {

    private double bruteForce(Point[] points) {
        double best = Double.POSITIVE_INFINITY;

        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double distance = Math.hypot(
                        points[i].x - points[j].x,
                        points[i].y - points[j].y
                );
                best = Math.min(best, distance);
            }
        }

        return best;
    }

    private void checkResult(Point[] points) {
        double expected = bruteForce(points);
        double actual = new ClosestPairSolver().solve(points);

        assertEquals(expected, actual, 1e-9);
    }

    private Point[] randomPoints(int size, long seed) {
        Random random = new Random(seed);
        Point[] points = new Point[size];

        for (int i = 0; i < size; i++) {
            points[i] = new Point(
                    random.nextDouble() * 2000 - 1000,
                    random.nextDouble() * 2000 - 1000
            );
        }

        return points;
    }

    @Test
    void emptyArray() {
        checkResult(new Point[0]);
    }

    @Test
    void singlePoint() {
        checkResult(new Point[]{
                new Point(5, 10)
        });
    }

    @Test
    void twoPoints() {
        Point[] points = {
                new Point(0, 0),
                new Point(3, 4)
        };

        assertEquals(5.0, new ClosestPairSolver().solve(points), 1e-9);
    }

    @Test
    void knownExample() {
        checkResult(new Point[]{
                new Point(2, 3),
                new Point(12, 30),
                new Point(40, 50),
                new Point(5, 1),
                new Point(12, 10),
                new Point(3, 4)
        });
    }

    @Test
    void closestPointsAcrossTheSplit() {
        checkResult(new Point[]{
                new Point(-10, 20),
                new Point(-5, 10),
                new Point(-0.1, 0),
                new Point(0.1, 0),
                new Point(5, 10),
                new Point(10, 20)
        });
    }

    @Test
    void duplicatePoints() {
        checkResult(new Point[]{
                new Point(-10, 2),
                new Point(-5, 8),
                new Point(0, 0),
                new Point(0, 0),
                new Point(5, 8),
                new Point(10, 2)
        });
    }

    @Test
    void allPointsEqual() {
        Point[] points = new Point[100];

        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(4, 7);
        }

        checkResult(points);
    }

    @Test
    void verticalLine() {
        Point[] points = new Point[100];

        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(5, i * 3);
        }

        checkResult(points);
    }

    @Test
    void horizontalLineInReverseOrder() {
        Point[] points = new Point[100];

        for (int i = 0; i < points.length; i++) {
            points[i] = new Point((100 - i) * 2, -5);
        }

        checkResult(points);
    }

    @Test
    void randomDatasets() {
        Random random = new Random(2026);

        for (int test = 0; test < 100; test++) {
            int size = 2 + random.nextInt(299);
            checkResult(randomPoints(size, random.nextLong()));
        }
    }

    @Test
    void twoThousandPoints() {
        checkResult(randomPoints(2000, 42));
    }

    @Test
    void inputOrderIsPreserved() {
        Point[] points = randomPoints(100, 17);
        Point[] original = points.clone();

        new ClosestPairSolver().solve(points);

        assertArrayEquals(original, points);
    }

    @Test
    void largeDataset() {
        Point[] points = new Point[100_000];

        for (int i = 0; i < points.length; i++) {
            points[i] = new Point(i * 3.0, 0);
        }

        double actual = new ClosestPairSolver().solve(points);

        assertEquals(3.0, actual, 1e-9);
    }
}