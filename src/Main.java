import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final int LIMIT = 10000000;
    private static int input = LIMIT;
    private static BufferedReader buf_in = new BufferedReader(
            new InputStreamReader(System.in));
    private static OutputStream buf_so = new BufferedOutputStream(System.out);
    private static int thread_count = 1;

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
        // Main.thread_count--;
    }

    public static void main(String[] args) throws InterruptedException {

        final String CYKA = "I/O SNAFU";

        final String fString = "\n%d primes were found.\n%d threads took %.3f ms \n";
        double t0, tF;
        try {
            read();
        } catch (IOException e) {
            System.out.println(CYKA);
            System.err.println(CYKA + " when getting input");
            return;
        }
        ExecutorService pool = Executors.newFixedThreadPool(thread_count);
        int sieve[] = new int[input - 1];
        Arrays.fill(sieve, 1);

        t0 = System.currentTimeMillis();

        for (int i = 2; i * i < input && i >= 0; i++) {
            if (thread_count <= 1) {
                ifPrime(sieve, i);
                continue;
            }
            int ind = i;
            pool.submit(() -> {
                ifPrime(sieve, ind);
            });

        }
        if (thread_count > 1) {
            pool.shutdown();
            try {
                while (!pool.awaitTermination(0, TimeUnit.MICROSECONDS))
                    ;
            } catch (InterruptedException e) {
                System.err.println("Exec interrupted");
            }
        }
        int n = IntStream.of(sieve).sum();

        tF = System.currentTimeMillis();

        try {

            String result = fString.formatted(n, thread_count, tF - t0);
            buf_so.write(result.getBytes());

            buf_so.flush();
        } catch (IOException e) {
            System.out.println(CYKA);
            System.err.println(CYKA + " when displaying results");
        }

    }

    private static void ifPrime(int[] sieve, int ind) {
        if (sieve[ind - 2] == 0)
            return;
        for (int i = ind * ind; i <= input && i > 0; i += ind) {
            sieve[i - 2] = 0;
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

        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}