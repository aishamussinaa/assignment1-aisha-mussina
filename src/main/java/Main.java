import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        int[] array = {
                9, 3, 7, 1, 8, 2, 6, 5, 4, 0,
                15, 12, 18, 11, 14, 13, 17, 16, 3, -2
        };

        int k = 7;

        int[] expected = array.clone();
        Arrays.sort(expected);

        System.out.println("Array: " + Arrays.toString(array));
        System.out.println("Index k: " + k);

        DeterministicSelector selector = new DeterministicSelector();

        long start = System.nanoTime();
        int result = selector.select(array, k);
        long elapsed = System.nanoTime() - start;

        System.out.println("Selected value: " + result);
        System.out.println("Expected value: " + expected[k]);
        System.out.println("Correct: " + (result == expected[k]));
        System.out.println("Time (ns): " + elapsed);
        System.out.println("Max recursion depth: " + selector.getMaxDepth());
        System.out.println("Comparisons: " + selector.getComparisons());
    }
}