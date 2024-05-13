import java.lang.Runnable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class Threader implements Runnable {
    List<Integer> arr;

    public void setEnd(List<Integer> arr) {
        this.arr = arr;
    }

    Semaphore sig;
    List<Integer> master;

    Threader(List<Integer> arr, List<Integer> primes, Semaphore flag) {
        this.arr = arr;
        this.sig = flag;
        this.master = primes;
        System.out.println(arr);
    }

    @Override
    public void run() {

        List<Integer> primes = new ArrayList<Integer>();
        for (int current_num : this.arr) {
            if (Main.check_prime(current_num)) {
                primes.add(current_num);
            }
        }
        try {
            sig.acquire();
            this.master.addAll(primes);
        } catch (InterruptedException wait) {
            System.out.println("waiting");
        } finally {
            sig.release();
        }

    }

}
