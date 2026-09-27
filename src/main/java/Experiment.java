import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Random;

public class Experiment {

    private static final int[] SIZES = {
            100, 1000, 5000, 10000, 50000, 100000
    };

    private static final String[] TYPES = {
            "random", "sorted", "reverse", "duplicates"
    };

    private static final int WARMUP_RUNS = 5;
    private static final int MEASURED_RUNS = 7;
    private static final long SEED = 2026;

    public static void run() throws IOException {
        Path directory = Path.of("results");
        Files.createDirectories(directory);

        String environment =
                "Java: " + System.getProperty("java.version") + "\n"
                        + "VM: " + System.getProperty("java.vm.name") + "\n"
                        + "OS: " + System.getProperty("os.name") + "\n"
                        + "Architecture: " + System.getProperty("os.arch") + "\n"
                        + "Available processors: "
                        + Runtime.getRuntime().availableProcessors() + "\n"
                        + "Seed: " + SEED + "\n"
                        + "Warmup runs per case: " + WARMUP_RUNS + "\n"
                        + "Measured runs per case: " + MEASURED_RUNS + "\n";

        Files.writeString(directory.resolve("environment.txt"), environment);
        System.out.println(environment);

        Path output = directory.resolve("results.csv");

        try (BufferedWriter writer = Files.newBufferedWriter(output)) {
            writer.write(
                    "algorithm,input_type,n,median_ns,min_ns,max_ns,"
                            + "max_depth,operations,metric"
            );
            writer.newLine();

            System.out.printf(
                    "%-14s %-11s %8s %12s %7s %12s%n",
                    "Algorithm", "Input", "n",
                    "Median ms", "Depth", "Operations"
            );

            for (int size : SIZES) {
                for (String type : TYPES) {
                    int[] input = createArray(size, type);
                    int[] expected = input.clone();
                    Arrays.sort(expected);

                    for (String algorithm : new String[]{
                            "MergeSort", "QuickSort", "Select"
                    }) {
                        runArrayExperiment(
                                writer, algorithm, type, input, expected
                        );
                    }

                    Point[] points = createPoints(size, type);
                    runPointExperiment(writer, type, points);
                }
            }
        }

        System.out.println();
        System.out.println("Experiments finished.");
        System.out.println("Results: " + output.toAbsolutePath());
    }

    private static int[] createArray(int size, String type) {
        Random random = new Random(SEED + size);
        int[] array = new int[size];

        for (int i = 0; i < size; i++) {
            array[i] = type.equals("duplicates")
                    ? random.nextInt(10)
                    : random.nextInt();
        }

        if (type.equals("sorted") || type.equals("reverse")) {
            Arrays.sort(array);
        }

        if (type.equals("reverse")) {
            for (int i = 0; i < size / 2; i++) {
                int temporary = array[i];
                array[i] = array[size - 1 - i];
                array[size - 1 - i] = temporary;
            }
        }

        return array;
    }

    private static Point[] createPoints(int size, String type) {
        Random random = new Random(SEED + size);
        Point[] points = new Point[size];

        for (int i = 0; i < size; i++) {
            if (type.equals("duplicates")) {
                points[i] = new Point(
                        random.nextInt(10),
                        random.nextInt(10)
                );
            } else {
                points[i] = new Point(
                        random.nextDouble() * 10000,
                        random.nextDouble() * 10000
                );
            }
        }

        if (type.equals("sorted") || type.equals("reverse")) {
            Arrays.sort(
                    points,
                    Comparator.comparingDouble((Point p) -> p.x)
                            .thenComparingDouble(p -> p.y)
            );
        }

        if (type.equals("reverse")) {
            for (int i = 0; i < size / 2; i++) {
                Point temporary = points[i];
                points[i] = points[size - 1 - i];
                points[size - 1 - i] = temporary;
            }
        }

        return points;
    }

    private static void runArrayExperiment(
            BufferedWriter writer,
            String algorithm,
            String type,
            int[] input,
            int[] expected
    ) throws IOException {

        long[] times = new long[MEASURED_RUNS];
        long operations = 0;
        int maxDepth = 0;

        for (int run = -WARMUP_RUNS; run < MEASURED_RUNS; run++) {
            int[] array = input.clone();

            long elapsed;
            long currentOperations;
            int currentDepth;

            if (algorithm.equals("MergeSort")) {
                MergeSorter sorter = new MergeSorter();

                long start = System.nanoTime();
                sorter.sort(array);
                elapsed = System.nanoTime() - start;

                currentOperations = sorter.getComparisons();
                currentDepth = sorter.getMaxDepth();

                if (!Arrays.equals(expected, array)) {
                    throw new IllegalStateException("MergeSort failed");
                }

            } else if (algorithm.equals("QuickSort")) {
                QuickSorter sorter = new QuickSorter(SEED);

                long start = System.nanoTime();
                sorter.sort(array);
                elapsed = System.nanoTime() - start;

                currentOperations = sorter.getComparisons();
                currentDepth = sorter.getMaxDepth();

                if (!Arrays.equals(expected, array)) {
                    throw new IllegalStateException("QuickSort failed");
                }

            } else {
                DeterministicSelector selector =
                        new DeterministicSelector();
                int k = array.length / 2;

                long start = System.nanoTime();
                int result = selector.select(array, k);
                elapsed = System.nanoTime() - start;

                currentOperations = selector.getComparisons();
                currentDepth = selector.getMaxDepth();

                if (result != expected[k]) {
                    throw new IllegalStateException("Select failed");
                }
            }

            if (run >= 0) {
                times[run] = elapsed;
                operations = currentOperations;
                maxDepth = Math.max(maxDepth, currentDepth);
            }
        }

        saveResult(
                writer, algorithm, type, input.length,
                times, maxDepth, operations, "comparisons"
        );
    }

    private static void runPointExperiment(
            BufferedWriter writer,
            String type,
            Point[] points
    ) throws IOException {

        long[] times = new long[MEASURED_RUNS];
        long operations = 0;
        int maxDepth = 0;

        boolean checkWithBruteForce = points.length <= 2000;
        double expected = checkWithBruteForce
                ? bruteForce(points)
                : Double.NaN;

        for (int run = -WARMUP_RUNS; run < MEASURED_RUNS; run++) {
            ClosestPairSolver solver = new ClosestPairSolver();

            long start = System.nanoTime();
            double result = solver.solve(points);
            long elapsed = System.nanoTime() - start;

            if (!Double.isFinite(result) || result < 0) {
                throw new IllegalStateException("Invalid closest distance");
            }

            if (checkWithBruteForce
                    && Math.abs(result - expected) > 1e-9) {
                throw new IllegalStateException("Closest Pair failed");
            }

            if (run >= 0) {
                times[run] = elapsed;
                operations = solver.getDistanceChecks();
                maxDepth = Math.max(maxDepth, solver.getMaxDepth());
            }
        }

        saveResult(
                writer, "ClosestPair", type, points.length,
                times, maxDepth, operations, "distance_checks"
        );
    }

    private static double bruteForce(Point[] points) {
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

    private static void saveResult(
            BufferedWriter writer,
            String algorithm,
            String type,
            int size,
            long[] times,
            int maxDepth,
            long operations,
            String metric
    ) throws IOException {

        Arrays.sort(times);

        long median = times[times.length / 2];
        long minimum = times[0];
        long maximum = times[times.length - 1];

        writer.write(
                algorithm + "," + type + "," + size + ","
                        + median + "," + minimum + "," + maximum + ","
                        + maxDepth + "," + operations + "," + metric
        );
        writer.newLine();

        System.out.printf(
                Locale.US,
                "%-14s %-11s %8d %12.4f %7d %12d%n",
                algorithm, type, size,
                median / 1_000_000.0, maxDepth, operations
        );
    }
}