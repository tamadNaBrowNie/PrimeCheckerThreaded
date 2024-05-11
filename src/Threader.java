import java.lang.Runnable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Threader implements Runnable {
    int start = 0;
    int end = 0;
    List<Integer> primes;
    Semaphore sig;
    List<Integer> master;

    Threader(int start, int end, List<Integer> primes, Semaphore flag) {
        this.start = start;
        this.end = end;
        this.sig = flag;
        this.master = primes;
        this.primes = new ArrayList<Integer>();
    }

    @Override
    public void run() {
        Main.check_prime(0);
        Main.findPrimes(this.start, this.end, this.primes);
        try {
            sig.acquire();
            this.master.addAll(primes);
        } catch (InterruptedException wait) {

        } finally {
            sig.release();
        }

    }

}
