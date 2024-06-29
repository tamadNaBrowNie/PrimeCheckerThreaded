
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

public class Threader extends Thread {
    List<Integer> arr;

    ReentrantLock lock;
    List<Integer> master;

    Threader(List<Integer> arr, List<Integer> primes, ReentrantLock lock) {
        this.arr = arr;
        this.lock = lock;
        this.master = primes;
    }

    public void add(int n) {
        boolean flag = false;
        for (int i = 2; i * i <= n && !flag; i++) {
            if (n % i == 0) {
                flag = true;
                break;
            }
        }
        if (flag) {
            return;
        }

        lock.lock();

        this.master.add(n);

        lock.unlock();
    }

    @Override
    public void run() {
        this.arr.forEach(this::add);

    }

}
