import java.lang.Runnable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Threader implements Runnable {
    int start = 0;
    int end = 0;

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    Semaphore sig;
    List<Integer> master;

    Threader(int start, int end, List<Integer> primes, Semaphore flag) {
        this.start = start;
        this.end = end;
        this.sig = flag;
        this.master = primes;
    }

    @Override
    public void run() {
        System.out.println("Running thread");
        System.err.println(this.end);

        List<Integer> primes;
        primes = new ArrayList<Integer>();
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
