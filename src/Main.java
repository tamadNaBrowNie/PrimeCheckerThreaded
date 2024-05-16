import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

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
    private static final ReentrantLock LOCK = new ReentrantLock();

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
        final String CYKA = "I/O SNAFU";
        boolean scripted = false;
        try {
            scripted = getInput("Automate?") != 0;
            if (!scripted) {
                read();
                doTask();
                return;
            }
            getResults();

        } catch (IOException e) {
            System.out.println(CYKA);
            System.err.println(CYKA + " when getting input");
        }

    }

    private static void getResults() throws IOException {
        int[] inputs = { 2, 512, 1024, 67800, 10000000 };
        for (int i : inputs) {
            for (int j = 0; j < 11; j++) {
                for (int k = 0; k < 5; k++) {
                    Main.input = i;
                    Main.thread_count = 1 << j;
                    doTask();
                }
            }
        }
    }

    private static void doTask() throws IOException {
        List<Integer> primes = new ArrayList<Integer>();

        Instant t0 = Instant.now();
        List<Threader> threads = new ArrayList<Threader>();
        final List<Integer> IN = IntStream.rangeClosed(2, input).boxed().collect(Collectors.toList());

        int size = IN.size();
        int batch = 1, mod = 0;
        if (size > thread_count) {
            batch = size / thread_count;
            mod = size % thread_count;
        }
        int start = 0, end = start + batch;
        for (int i = 0; i < ((size > thread_count) ? thread_count : size); i++) {
            if (mod > 0) {
                mod--;
                end++;
            }
            if (end > size)
                end = size;
            threads.add(new Threader(IN.subList(start, end), primes, Main.LOCK));
            start = (end == size) ? size - 1 : end;
            end = start + batch;
        }
        for (; threads.size() < thread_count;)
            threads.add(new Threader(new ArrayList<Integer>(), primes, Main.LOCK));

        threads.forEach(t -> t.start());

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
            }
        }
        Instant tF = Instant.now();
        long dt = Duration.between(t0, tF).toMillis();
        String fString = "\n%d primes were found.\n%d threads took %d ms \n";

        LOCK.lock();
        primes.sort(null);
        // for (int i : primes) buf_so.write((i + ", ").getBytes());

        LOCK.unlock();
        fString = fString.formatted(primes.size(), threads.size(), dt);

        buf_so.write(fString.getBytes());

        buf_so.flush();

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