import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DeterministicSelectorTest {

    private void checkSelection(int[] input, int k) {
        int[] expected = input.clone();
        Arrays.sort(expected);

        DeterministicSelector selector = new DeterministicSelector();
        int actual = selector.select(input.clone(), k);

        assertEquals(expected[k], actual, "Wrong value for k = " + k);
    }

    @RepeatedTest(100)
    void randomArrays(RepetitionInfo repetitionInfo) {
        Random random = new Random(
                2026L + repetitionInfo.getCurrentRepetition()
        );

        int size = 1 + random.nextInt(2000);
        int[] array = new int[size];

        for (int i = 0; i < size; i++) {
            array[i] = random.nextInt();
        }

        int k = random.nextInt(size);
        checkSelection(array, k);
    }

    @Test
    void singleElement() {
        checkSelection(new int[]{42}, 0);
    }

    @Test
    void allPositionsInSmallArrays() {
        Random random = new Random(77);

        for (int size = 1; size <= 60; size++) {
            int[] array = new int[size];

            for (int i = 0; i < size; i++) {
                array[i] = random.nextInt(21) - 10;
            }

            for (int k = 0; k < size; k++) {
                checkSelection(array, k);
            }
        }
    }

    @Test
    void sortedArray() {
        int[] array = new int[1000];

        for (int i = 0; i < array.length; i++) {
            array[i] = i;
        }

        checkSelection(array, 0);
        checkSelection(array, 500);
        checkSelection(array, 999);
    }

    @Test
    void reverseSortedArray() {
        int[] array = new int[1000];

        for (int i = 0; i < array.length; i++) {
            array[i] = array.length - i;
        }

        checkSelection(array, 0);
        checkSelection(array, 500);
        checkSelection(array, 999);
    }

    @Test
    void duplicateHeavyArray() {
        Random random = new Random(123);
        int[] array = new int[1000];

        for (int i = 0; i < array.length; i++) {
            array[i] = random.nextInt(5);
        }

        checkSelection(array, 0);
        checkSelection(array, 500);
        checkSelection(array, 999);
    }

    @Test
    void allEqualElements() {
        int[] array = new int[1000];
        Arrays.fill(array, 7);

        checkSelection(array, 0);
        checkSelection(array, 500);
        checkSelection(array, 999);
    }

    @Test
    void extremeValues() {
        int[] array = {
                Integer.MIN_VALUE, Integer.MAX_VALUE,
                0, -1, 1, Integer.MIN_VALUE, Integer.MAX_VALUE
        };

        for (int k = 0; k < array.length; k++) {
            checkSelection(array, k);
        }
    }

    @Test
    void emptyArrayRejected() {
        DeterministicSelector selector = new DeterministicSelector();

        assertThrows(IllegalArgumentException.class,
                () -> selector.select(new int[0], 0));
    }

    @Test
    void invalidIndexRejected() {
        DeterministicSelector selector = new DeterministicSelector();
        int[] array = {3, 1, 2};

        assertThrows(IllegalArgumentException.class,
                () -> selector.select(array, -1));

        assertThrows(IllegalArgumentException.class,
                () -> selector.select(array, 3));
    }
}