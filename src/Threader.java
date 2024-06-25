import java.lang.Runnable;

public class Threader implements Runnable {
    int[] arr;
    int ind;

    Threader(int[] arr, int ind) {
        this.arr = arr;
        this.ind = ind;
    }

    @Override
    public void run() {
        if (arr[ind - 2] == 0)
            return;
        for (int i = ind * ind; i <= arr.length + 2 && i > 0; i += ind) {
            arr[i - 2] = 0;
        }

    }

}
