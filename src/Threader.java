import java.lang.Runnable;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Threader implements Runnable {
    List<Integer> arr;

    Semaphore sig;
    List<Integer> master;

    Threader(List<Integer> arr, List<Integer> primes, Semaphore flag) {
        this.arr = arr;
        this.sig = flag;
        this.master = primes;
    }

    private void check_prime(int n) {
        if (!Main.check_prime(n))
            return;

        try {
            sig.acquire();
            this.master.add(n);
        } catch (InterruptedException wait) {
            System.out.println("waiting");
        }
        sig.release();
    }

    @Override
    public void run() {
        this.arr.forEach(i -> this.check_prime(i));
    }

}
