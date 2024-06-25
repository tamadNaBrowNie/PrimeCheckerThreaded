import java.util.concurrent.Callable;

public class Net implements Callable<Boolean> {
    private final boolean[] sieve;
    private int ind;

    Net(boolean[] arr, int p) {
        this.sieve = arr;
        this.ind = p;
    }

    @Override
    public Boolean call() throws Exception {
        if (sieve[ind] == true) {
            for (int i = ind * ind; i <= sieve.length; i += ind)
                sieve[i] = false;
        }
        return sieve[ind];
    }

}
