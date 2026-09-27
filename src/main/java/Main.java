public class Main {
    public static void main(String[] args) {
        Point[] points = {
                new Point(2, 3),
                new Point(12, 30),
                new Point(40, 50),
                new Point(5, 1),
                new Point(12, 10),
                new Point(3, 4)
        };

        ClosestPairSolver solver = new ClosestPairSolver();

        long start = System.nanoTime();
        double result = solver.solve(points);
        long elapsed = System.nanoTime() - start;

        double expected = Math.sqrt(2);

        System.out.println("Closest distance: " + result);
        System.out.println("Expected distance: " + expected);
        System.out.println("Correct: " +
                (Math.abs(result - expected) < 1e-9));
        System.out.println("Time (ns): " + elapsed);
        System.out.println("Max recursion depth: " + solver.getMaxDepth());
        System.out.println("Distance checks: " + solver.getDistanceChecks());
    }
}