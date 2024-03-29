package aufgabe2.algorithm.parallel.stolen;

/**
 * Created with IntelliJ IDEA.
 * User: Sven
 * Date: 21.11.12
 * Time: 22:42
 */
import aufgabe2.algorithm.parallel.QuickSortMultiThreaded;

import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveAction;

import static aufgabe2.algorithm.parallel.stolen.Quicksort.partition;

public class ForkJoinQuicksortTask extends RecursiveAction {
    private static final int SERIAL_THRESHOLD = 10;

    private final IntBuffer a;
    private final int left;
    private final int right;

    public ForkJoinQuicksortTask(IntBuffer a) {
        this(a, 0, a.limit() - 1);
    }

    private ForkJoinQuicksortTask(IntBuffer a, int left, int right) {
        this.a = a;
        this.left = left;
        this.right = right;
    }

    @Override
    protected void compute() {
        Thread.currentThread().setPriority(10);
        if (serialThresholdMet()) {
            QuickSortMultiThreaded.blockSort_insertion(a, left, right);
        } else {
            int pivotIndex = QuickSortMultiThreaded.quickSwap(a, left, right);
            ForkJoinTask t1 = null;


//            resultLeft = threadPool.submit(new quickSort(links, positionPivot - 1));
//            resultRight = threadPool.submit(new quickSort(positionPivot + 1, rechts));

            if (left < pivotIndex)
                t1 = new ForkJoinQuicksortTask(a, left, pivotIndex-1).fork();
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

