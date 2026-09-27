import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[] array = {
                9, 3, 7, 1, 8, 2, 6, 5, 4, 0,
                15, 12, 18, 11, 14, 13, 17, 16, 3, -2
        };

        int[] expected = array.clone();
        Arrays.sort(expected);

        System.out.println("Before: " + Arrays.toString(array));

        MergeSorter sorter = new MergeSorter();

        long start = System.nanoTime();
        sorter.sort(array);
        long elapsed = System.nanoTime() - start;

        System.out.println("After: " + Arrays.toString(array));
        System.out.println("Correct: " + Arrays.equals(array, expected));
        System.out.println("Time (ns): " + elapsed);
        System.out.println("Max recursion depth: " + sorter.getMaxDepth());
        System.out.println("Comparisons: " + sorter.getComparisons());
    }
}