import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

public class Main {
    private static final int LIMIT = 10000000;
    private static int input = LIMIT;
    private static BufferedReader buf_in = new BufferedReader(
            new InputStreamReader(System.in));
    private static OutputStream buf_so = new BufferedOutputStream(System.out);
    private static int thread_count = 1;
    private static final Semaphore FLAG = new Semaphore(1);

    private static int getInput(String msg) throws IOException {

        try {
            buf_so.write(msg.getBytes());
            buf_so.flush();
            return Integer.parseInt(buf_in.readLine());
        } catch (NumberFormatException f) {

            System.out.println("Input not a number");

        }
        return getInput(msg);
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

    public static void main(String[] args) {
        boolean o1 = false; // optimisation flag. prevents creating and running unneeded threads.
        final String CYKA = "I/O SNAFU";
        final String fString = "\n%d primes were found.\n%d threads took %d ms \n";
        
        List<Integer> primes = new ArrayList<Integer>();
        try {
            read();
        } catch (IOException e) {
            System.out.println(CYKA);
            System.err.println(CYKA + " when getting input");
            return;
        }

        Instant t0 = Instant.now();
        Threader[] threads = new Threader[thread_count];
        final List<Integer> IN = IntStream.rangeClosed(2, input).boxed().collect(Collectors.toList());

        int size = IN.size();
        int batch = 1, mod = 0;
        if (size > thread_count) {
            batch = size / thread_count;
            mod = size % thread_count;
        } else {
            thread_count = size;
        }

        for (int i = 0, start = 0, end = start + batch; i < thread_count; i++) {
            if (mod > 0) {
                mod--;
                end++;
            }
            if (end > size)
                end = size;
            threads[i] = new Threader(IN.subList(start, end), primes, Main.FLAG);
            start = (end == size) ? size - 1 : end;
            end = start + batch;
            threads[i].run();
        }
        if (!o1)
            for (int i = thread_count; i < threads.length; i++) {
                new Threader(new ArrayList<Integer>(), primes, Main.FLAG).run();
            }

        Instant tF = Instant.now();
        long dt = Duration.between(t0, tF).toMillis();
        String res = String.format(fString,primes.size(), threads.length, dt);
        try {
            for (int i : primes) {
                buf_so.write((i + ", ").getBytes());
            }
            buf_so.write(res.getBytes());

            buf_so.flush();
        } catch (IOException e) {
            System.out.println(CYKA);
            System.err.println(CYKA + " when displaying results");
        }
    }
    private static int sieve(ThreadPoolExecutor pool) {
        int lim = (int) Math.sqrt(input);
        int arr[] = new int[input - 1];
        Arrays.fill(arr, 1);
        Consumer<Integer> consumer = ind->{
              for (
                int i = ind * ind; 
                i <= input &&
                i > 0;
                i += ind
                ) 
                arr[i - 2] = 0;
    };
        for (int i = 2; i <= lim; i++) {
            if(arr[i-2] == 1)
                continue; 
            if (thread_count <= 1) {
                consumer.accept(i);
                continue;
            }
            int ind = i;
            pool.submit(() -> {
                consumer.accept(ind);
            });

        }
        return IntStream.of(arr).sum();
    }

    /*
     * This function checks if an integer n is prime.
     * 
     * Parameters:
     * n : int - integer to check
     * 
     * Returns true if n is prime, and false otherwise.
     */
    public static boolean check_prime(int n) {
        /*
         * why can't we use sieve? It is faster and easier to parellelize
         * This isn't even optimal trial division easier to code this way though.
         */

        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}