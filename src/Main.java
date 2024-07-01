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
import java.util.List;
import java.util.StringJoiner;
import java.io.FileOutputStream;

// THIS IS THE SLAVE PROCESS
public class Main {
    private static int getInput(BufferedReader in, OutputStream out, String msg) throws IOException {

        try {
            out.write(msg.getBytes());
            out.flush();
            int ans = Integer.parseInt(in.readLine());

            return ans;
        } catch (NumberFormatException f) {

            System.out.println("Input not a number");

        }
        // buf_in.readLine();
        return getInput(in, out, msg);
    }

    private static String getString(BufferedReader in, String msg) throws IOException {

        System.out.println(msg);
        return in.readLine();

    }

    private static int getInput(BufferedReader buf_in, OutputStream buf_so) throws IOException {
        int in = 0;
        final int LIMIT = 100000000;
        do {
            in = getInput(buf_in, buf_so, "\nEnter number to search: ");

            if (in > LIMIT)
                buf_so.write("Input too large. enter again\n".getBytes());

            else if (in < 2)
                buf_so.write("Input too small. enter again\n".getBytes());
        } while (in < 2 || in > LIMIT);
        return in;
    }

    private static int getPow(BufferedReader buf_in, OutputStream buf_so) throws IOException {
        int pow = -1;
        while (pow < 0 || pow > 10) {

            pow = getInput(buf_in, buf_so, "\nCore counts is 2^n where 0 <= n < 11 and n is your input: ");
            if (pow < 0 || pow > 10)
                buf_so.write("Invalid power".getBytes());
        }
        return 1 << pow;
    }

    private static final String CYKA = "I/O SNAFU";

    public static void main(String[] args) {

        try (BufferedReader buf_in = new BufferedReader(
                new InputStreamReader(System.in))) {
            OutputStream buf_so = new BufferedOutputStream(System.out, 1 << 16);
            boolean scripted = false;

            try {
                scripted = getInput(buf_in, buf_so, "Automate? 0 for no, else yes") != 0;
                if (scripted) {
                    String where = getString(buf_in, "Write where?");
                    buf_in.close();
                    buf_so.close();
                    buf_so = new BufferedOutputStream(new FileOutputStream(where));
                    // buf_so.write(getResults().getBytes());
                    String str = getResults();
                    buf_so.write(str.getBytes());
                    buf_so.flush();
                    buf_so.close();

                    return;
                }
                int input = getInput(buf_in, buf_so);
                int thread_count = getPow(buf_in, buf_so);
                buf_in.close();
                String str = doTask(input, thread_count);
                try {
                    buf_so.write(str.getBytes());

                } catch (IOException e) {
                    System.out.println(CYKA);
                    System.err.println(CYKA + " when getting input");
                    e.printStackTrace();
                }
                buf_so.flush();
                buf_so.close();

            } catch (IOException e) {
                System.out.println(CYKA);
                System.err.println(CYKA + " when getting input");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static String getResults() {
        final int LIMIT = 100000000;
        StringJoiner str = new StringJoiner("\n\n");
        int[] inputs = { 2, 3, 4, 523, 67800, LIMIT / 8, LIMIT / 4, LIMIT / 2, LIMIT - 8,
                LIMIT };
        for (int i : inputs) {
            for (int j = 0; j < 11; j++) {
                str.add("\nin " + i);
                int count = 1 << j;
                for (int k = 0; k < 5; k++) {
                    str.add(doTask(i, count));
                }
            }
        }
        return str.toString();
    }

    // private static String getResults() throws IOException {
    // final int LIMIT = 100000000;
    // String str = "";
    // int[] inputs = { 2, 3, 4, 523, 67800, LIMIT / 8, LIMIT / 4, LIMIT / 2, LIMIT
    // - 8,
    // LIMIT };
    // for (int i : inputs) {
    // for (int j = 0; j < 11; j++) {
    // str.concat("\n in" + i);
    // int count = 1 << j;
    // for (int k = 0; k < 5; k++) {
    // str.concat(doTask(i, count));
    // }
    // }
    // }

    // return str;
    // }
    //
    private static ExecutorService initPool(int count) {

        return (count > 1) ? Executors.newFixedThreadPool(count) : null;
    }

    private static void killPool(ExecutorService es) {
        if (es == null)
            return;
        es.shutdown();
        try {
            while (!es.awaitTermination(0, TimeUnit.MICROSECONDS))
                ;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static void getter(Future<?> f) {
        try {
            f.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    };

    private static String doTask(int input, int thread_count) {
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
            // List<Future<?>> blocker = new ArrayList<>();
            ExecutorService es = initPool(thread_count);

            for (int i = 2; i <= lim; i++) {
                if (sieve[i - 2])
                    continue;
                int ind = i;
                es.submit(() -> {
                    getMulti(sieve, ind, input);
                });
                // blocker.add( );
            }

            // blocker.forEach(Main::getter);
            killPool(es);
        }

        // int n = IntStream.of(sieve).sum();
        int n = 0;
        for (boolean notPrime : sieve) {
            if (!notPrime)
                n++;
        }

        final String fString = "%d primes were found. %d threads took %.3f ms";
        double dt = ((float) (System.nanoTime() - t0)) * 0.000001;
        return fString.formatted(n, thread_count, dt);

    }

    private static void getMulti(boolean[] arr, Integer ind, int input) {

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