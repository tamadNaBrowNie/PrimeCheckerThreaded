import java.lang.Runnable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.Objects;

public class Threader implements Runnable {
    int start = 0;
    int end = 0;

    public Threader() {
    }

    public Threader(int start, int end, List<Integer> primes, Semaphore sig, List<Integer> master) {
        this.start = start;
        this.end = end;
        this.primes = primes;
        this.sig = sig;
        this.master = master;
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return this.end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public List<Integer> getPrimes() {
        return this.primes;
    }

    public void setPrimes(List<Integer> primes) {
        this.primes = primes;
    }

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
        System.out.println("Running thread");
        System.err.println(this.end);

        for (int current_num = this.start; current_num <= this.end; current_num++) {
            System.err.printf("%d iter\n", current_num);
            if (Main.check_prime(current_num)) {
                primes.add(current_num);
                System.err.printf("%d is prime\n", current_num);
            }
        }
        try {
            sig.acquire();
            this.master.addAll(primes);
        } catch (InterruptedException wait) {

        } finally {
            sig.release();
        }

    }

}
