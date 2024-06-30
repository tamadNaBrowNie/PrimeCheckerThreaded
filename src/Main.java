import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.io.FileOutputStream;

public class Main {
    private static final int LIMIT = 10000000;
    private static int input = LIMIT;
    private static BufferedReader buf_in = new BufferedReader(
            new InputStreamReader(System.in));
    private static OutputStream buf_so = new BufferedOutputStream(System.out, 1 << 16);
    private static int thread_count = 1;

    private static int getInput(String msg) throws IOException {

        try {
            buf_so.write(msg.getBytes());
            buf_so.flush();
            int ans = Integer.parseInt(buf_in.readLine());

            return ans;
        } catch (NumberFormatException f) {

            System.out.println("Input not a number");

        }
        // buf_in.readLine();
        return getInput(msg);
    }

    private static String getString(String msg) throws IOException {

        buf_so.write(msg.getBytes());
        buf_so.flush();
        return buf_in.readLine();

    }

    private static void read() throws IOException {
        int pow = -1;
        do {
            Main.input = getInput("\nEnter number to search: ");

            if (Main.input > LIMIT)
                buf_so.write("Input too large. enter again\n".getBytes());

            else if (Main.input < 2)
                buf_so.write("Input too small. enter again\n".getBytes());
        } while (Main.input < 2 || Main.input > LIMIT);

        while (pow < 0 || pow > 10) {

            pow = getInput("Core counts is 2^n where 0 <= n < 11 and n is your input: ");
            if (pow < 0 || pow > 10)
                buf_so.write("Invalid power".getBytes());
        }
        Main.thread_count = 1 << pow;

    }

    private static final String fString = "\n%d primes were found. %d threads took %.3f ms \n";
    private static final String CYKA = "I/O SNAFU";

    public static void main(String[] args) {

        boolean scripted = false;

        try {
            scripted = getInput("Automate? 0 for no, else yes") != 0;
            if (scripted) {
                String where = getString("Write where?");
                buf_in.close();
                buf_so.close();
                buf_so = new BufferedOutputStream(new FileOutputStream(where));
                getResults();
                buf_so.flush();
                buf_so.close();

                return;
            }
            read();
            buf_in.close();
            doTask();
            buf_so.flush();
            buf_so.close();

        } catch (IOException e) {
            System.out.println(CYKA);
            System.err.println(CYKA + " when getting input");
        }

    }

    private static void getResults() throws IOException {
        int[] inputs = { 2, 3, 4, 523, 67800, LIMIT / 8, LIMIT / 4, LIMIT / 2, LIMIT - 8,
                LIMIT };
        for (int i : inputs) {
            Main.input = i;
            for (int j = 0; j < 11; j++) {
                buf_so.write(("\n in" + input).getBytes());
                Main.thread_count = 1 << j;
                for (int k = 0; k < 5; k++) {
                    doTask();
                }

            }
        }
    }

    private static ExecutorService initPool() {

        return (Main.thread_count > 1) ? Executors.newFixedThreadPool(thread_count) : null;
    }

    private static void killPool(ExecutorService es) {
        if (es != null)
            es.shutdownNow();
    }

    private static void getter(Future<?> f) {
        try {
            f.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    };

    private static void doTask() {
        /*
         * List<Integer> primes = new ArrayList<Integer>();
         * 
         * final List<Integer> IN = IntStream.rangeClosed(2,
         * input).boxed().collect(Collectors.toList());
         * 
         * Threader[] threads = new Threader[thread_count];
         * 
         * for (int i = 0; i < thread_count; i++) {
         * threads[i] = new Threader(new ArrayList<Integer>(), primes, LOCK);
         * }
         * 
         * int ind = 0;
         * for (int i : IN) {
         * if (ind >= thread_count)
         * ind = 0;
         * threads[ind].add(i);
         * ind++;
         * }
         * 
         * for (Thread t : threads) {
         * t.start();
         * }
         * try {
         * for (Thread t : threads)
         * t.join();
         * } catch (InterruptedException e) {
         * }
         * LOCK.lock();
         * primes.sort(null);
         * 
         * LOCK.unlock();
         */

        double t0 = System.nanoTime();
        int lim = (int) Math.sqrt(input);
        boolean sieve[] = new boolean[input - 1];
        if (thread_count <= 1) {
            for (int i = 2; i <= lim; i++) {
                if (sieve[i - 2])
                    continue;
                for (long ind = i * i; ind <= input; ind += i) {
                    int l = (int) ind;
                    sieve[l - 2] = true;
                }
            }
        } else {
            List<Future<?>> blocker = new ArrayList<>();
            ExecutorService es = initPool();

            for (int i = 2; i <= lim; i++) {
                if (sieve[i - 2])
                    continue;
                int ind = i;

                blocker.add(
                        es.submit(() -> {
                            getMulti(sieve, ind);
                        }));
            }

            blocker.forEach(Main::getter);
            killPool(es);
        }

        // int n = IntStream.of(sieve).sum();
        int n = 0;
        for (boolean notPrime : sieve) {
            if (!notPrime)
                n++;
        }
        double dt = ((float) (System.nanoTime() - t0)) * 0.000001;
        String str = fString.formatted(n, thread_count, dt);
        try {
            buf_so.write(str.getBytes());

        } catch (IOException e) {
            System.out.println(CYKA);
            System.err.println(CYKA + " when getting input");
            e.printStackTrace();
        }

    }

    private static void getMulti(boolean[] arr, Integer ind) {

        for (long i = ind * ind; i <= input && arr[ind - 2] == false; i += ind) {

            int l = (int) i;
            arr[l - 2] = true;
        }
    }

    public static boolean check_prime(int n) {

        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}