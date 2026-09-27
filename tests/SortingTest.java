import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class SortingTest {

    private void checkBothSorts(int[] input) {
        int[] expected = input.clone();
        Arrays.sort(expected);

        int[] mergeArray = input.clone();
        MergeSorter mergeSorter = new MergeSorter();
        mergeSorter.sort(mergeArray);

        assertArrayEquals(expected, mergeArray, "MergeSort failed");

        int[] quickArray = input.clone();
        QuickSorter quickSorter = new QuickSorter(42);
        quickSorter.sort(quickArray);

        assertArrayEquals(expected, quickArray, "QuickSort failed");
    }

    @Test
    void emptyArray() {
        checkBothSorts(new int[0]);
    }

    @Test
    void singleElement() {
        checkBothSorts(new int[]{7});
    }

    @Test
    void sortedArray() {
        int[] array = new int[1000];

        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }

        checkBothSorts(array);
    }

    @Test
    void reverseSortedArray() {
        int[] array = new int[1000];

        for (int i = 0; i < array.length; i++) {
            array[i] = array.length - i;
        }

        checkBothSorts(array);
    }

    @Test
    void duplicateHeavyArray() {
        Random random = new Random(123);
        int[] array = new int[1000];

        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(5);
        }

        checkBothSorts(array);
    }

    @Test
    void allEqualElements() {
        int[] array = new int[1000];
        Arrays.fill(array, 8);

        checkBothSorts(array);
    }

    @Test
    void negativeAndExtremeValues() {
        checkBothSorts(new int[]{
                Integer.MAX_VALUE, -10, 0, Integer.MIN_VALUE,
                5, -1, Integer.MAX_VALUE, Integer.MIN_VALUE
        });
    }

    @Test
    void smallArraySizes() {
        Random random = new Random(17);

        for (int size = 2; size <= 33; size++) {
            int[] array = new int[size];

            for (int i = 0; i < size; i++) {
                array[i] = random.nextInt(101) - 50;
            }

            checkBothSorts(array);
        }
    }

    @Test
    void randomArrays() {
        Random random = new Random(2026);

        for (int test = 0; test < 100; test++) {
            int size = 1 + random.nextInt(2000);
            int[] array = new int[size];

            for (int i = 0; i < size; i++) {
                array[i] = random.nextInt();
            }

            checkBothSorts(array);
        }
    }
}