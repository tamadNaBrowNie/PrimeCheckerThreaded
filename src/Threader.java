import java.lang.Runnable;

public class Threader implements Runnable {
    int start = 0;
    int end = 0;

    Threader(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        Main.check_prime(0);

    }

}
