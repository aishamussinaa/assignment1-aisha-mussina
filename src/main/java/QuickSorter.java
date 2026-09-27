import java.util.Random;

public class QuickSorter {
    private final Random random;
    private long comparisons;
    private int maxDepth;

    public QuickSorter() {
        random = new Random();
    }

    public QuickSorter(long seed) {
        random = new Random(seed);
    }

    public void sort(int[] array) {
        comparisons = 0;
        maxDepth = 0;

        if (array.length < 2) {
            return;
        }

        quickSort(array, 0, array.length - 1, 1);
    }

    private void quickSort(int[] array, int left, int right, int depth) {
        maxDepth = Math.max(maxDepth, depth);

        while (left < right) {
            int pivotIndex = left + random.nextInt(right - left + 1);
            int pivot = array[pivotIndex];

            int lt = left;
            int i = left;
            int gt = right;

            while (i <= gt) {
                comparisons++;

                if (array[i] < pivot) {
                    swap(array, lt, i);
                    lt++;
                    i++;
                } else {
                    comparisons++;

                    if (array[i] > pivot) {
                        swap(array, i, gt);
                        gt--;
                    } else {
                        i++;
                    }
                }
            }

            int leftSize = lt - left;
            int rightSize = right - gt;

            if (leftSize < rightSize) {
                if (leftSize > 1) {
                    quickSort(array, left, lt - 1, depth + 1);
                }

                left = gt + 1;
            } else {
                if (rightSize > 1) {
                    quickSort(array, gt + 1, right, depth + 1);
                }

                right = lt - 1;
            }
        }
    }

    private void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public long getComparisons() {
        return comparisons;
    }

    public int getMaxDepth() {
        return maxDepth;
    }
}