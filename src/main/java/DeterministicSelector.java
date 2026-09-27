public class DeterministicSelector {
    private long comparisons;
    private int maxDepth;

    public int select(int[] array, int k) {
        comparisons = 0;
        maxDepth = 0;

        if (k < 0 || k >= array.length) {
            throw new IllegalArgumentException("Invalid index k");
        }

        return select(array, 0, array.length - 1, k, 1);
    }

    private int select(int[] array, int left, int right,
                       int k, int depth) {
        maxDepth = Math.max(maxDepth, depth);

        if (right - left + 1 <= 5) {
            insertionSort(array, left, right);
            return array[k];
        }

        int medianCount = 0;

        for (int start = left; start <= right; start += 5) {
            int end = Math.min(start + 4, right);
            insertionSort(array, start, end);

            int medianIndex = start + (end - start) / 2;
            swap(array, left + medianCount, medianIndex);
            medianCount++;
        }

        int medianPosition = left + medianCount / 2;

        int pivot = select(
                array,
                left,
                left + medianCount - 1,
                medianPosition,
                depth + 1
        );

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

        if (k < lt) {
            return select(array, left, lt - 1, k, depth + 1);
        }

        if (k > gt) {
            return select(array, gt + 1, right, k, depth + 1);
        }

        return pivot;
    }

    private void insertionSort(int[] array, int left, int right) {
        for (int i = left + 1; i <= right; i++) {
            int value = array[i];
            int j = i - 1;

            while (j >= left) {
                comparisons++;

                if (array[j] <= value) {
                    break;
                }

                array[j + 1] = array[j];
                j--;
            }

            array[j + 1] = value;
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