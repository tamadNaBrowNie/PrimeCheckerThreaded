import java.lang.Runnable;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Threader implements Runnable {
    int num;

    ReentrantLock lock;
    List<Integer> master;

    Threader(int arr, List<Integer> primes, ReentrantLock lock) {
        this.num = arr;
        this.lock = lock;
        this.master = primes;
    }

    @Override
    public void run() {
        if (Main.check_prime(this.num)) {

            lock.lock();

            this.master.add(this.num);

            lock.unlock();
        }

    }

}
