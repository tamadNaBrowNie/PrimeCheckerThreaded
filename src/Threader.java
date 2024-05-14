import java.lang.Runnable;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Threader implements Runnable {
    List<Integer> arr;

    ReentrantLock lock;
    List<Integer> master;

    Threader(List<Integer> arr, List<Integer> primes, ReentrantLock lock) {
        this.arr = arr;
        this.lock = lock;
        this.master = primes;
    }

    private void check_prime(int n) {
        if (!Main.check_prime(n)) {
            return;
        }

        lock.lock();

        this.master.add(n);

        lock.unlock();

    }

    @Override
    public void run() {
        this.arr.forEach(i -> this.check_prime(i));

    }

}
