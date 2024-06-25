import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import java.util.concurrent.TimeUnit;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.IntStream;

import java.util.concurrent.CountDownLatch;
// import java.util.stream.Collectors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int LIMIT = 10000000;
    private static int input = LIMIT;
    private static BufferedReader buf_in = new BufferedReader(
            new InputStreamReader(System.in));
    private static OutputStream buf_so = new BufferedOutputStream(System.out);
    private static int thread_count = 1;
    private static final ReentrantLock LOCK = new ReentrantLock(true);

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
        Main.thread_count--;
    }

    public static void main(String[] args) throws InterruptedException {

        final String CYKA = "I/O SNAFU";
        ExecutorService pool = Executors.newFixedThreadPool(thread_count);
        try {
            read();
        } catch (IOException e) {
            System.out.println(CYKA);
            System.err.println(CYKA + " when getting input");
            return;
        }
        int sieve[] = new int[input + 1];
        Arrays.fill(sieve, 1);
        CountDownLatch ctr = new CountDownLatch(input - 1);
        Instant t0 = Instant.now();
        if (thread_count > 0)
            IntStream.rangeClosed(2, input).forEach(ind -> pool.execute(() -> {

                if (sieve[ind] == 1)
                    multiples(sieve, ind);
                ctr.countDown();

            }));
        else
            IntStream.rangeClosed(2, input).forEach(ind ->
            // pool.execute(() ->
            {

                if (sieve[ind] == 1)
                    multiples(sieve, ind);
                ctr.countDown();

            }
            // )
            );

        Instant tF = Instant.now();
        long dt = Duration.between(t0, tF).toMillis();
        String fString = "\n%d primes were found.\n%d threads took %d ms \n";
        ctr.await();
        int n = IntStream.of(sieve).sum() - 2;

        try {

            fString = fString.formatted(n, thread_count, dt);
            buf_so.write(fString.getBytes());

            buf_so.flush();
        } catch (IOException e) {
            System.out.println(CYKA);
            System.err.println(CYKA + " when displaying results");
        }
        pool.shutdown();
        try {
            while (!pool.awaitTermination(0, TimeUnit.MICROSECONDS))
                ;
        } catch (InterruptedException e) {
            System.err.println("Exec interrupted");
        }
    }

    private static void multiples(int[] sieve, int ind) {
        // System.out.println("ind " + ind);
        // if it overflows to negative, that means it is too big

        for (int i = ind * ind; i < sieve.length && i > 0; i += ind) {

            sieve[i] = 0;
        }
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