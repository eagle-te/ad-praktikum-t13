package aufgabe2_old.algorithm;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 21.11.12
 * Time: 22:42
 */
import java.util.Arrays;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import static aufgabe2_old.algorithm.Quicksort.partition;

public class ForkJoinQuicksortTask extends RecursiveAction {
    private static final int SERIAL_THRESHOLD = 0x1000;

    private final int[] a;
    private final int left;
    private final int right;

    public ForkJoinQuicksortTask(int[] a) {
        this(a, 0, a.length - 1);
    }

    private ForkJoinQuicksortTask(int[] a, int left, int right) {
        this.a = a;
        this.left = left;
        this.right = right;
    }

    @Override
    protected void compute() {
        Thread.currentThread().setPriority(10);
        if (serialThresholdMet()) {
            Arrays.sort(a, left, right + 1);
        } else {
            int pivotIndex = partition(a, left, right);
            ForkJoinTask t1 = null;

            if (left < pivotIndex)
                t1 = new ForkJoinQuicksortTask(a, left, pivotIndex).fork();
            if (pivotIndex + 1 < right)
                new ForkJoinQuicksortTask(a, pivotIndex + 1, right).invoke();

            if (t1 != null)
                t1.join();
        }
    }

    private boolean serialThresholdMet() {
        return right - left < SERIAL_THRESHOLD;
    }
}

