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
    private static OutputStream log = new BufferedOutputStream(System.out);
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
                buf_so.close();
                buf_in.close();
                return;
            }
            buf_in.close();
            getResults();
            buf_so.close();

        } catch (IOException e) {
            System.out.println(CYKA);
            System.err.println(CYKA + " when getting input");
        } finally {

        }

    }

    private static void getResults() throws IOException {
        int[] inputs = { 2, 512, 1024, 67800, 10000000 };
        for (int i : inputs) {
            for (int j = 0; j < 11; j++) {
                buf_so.write(("\n in" + input).getBytes());
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

        final List<Integer> IN = IntStream.rangeClosed(2, input).boxed().collect(Collectors.toList());

        Threader[] threads = new Threader[thread_count];

        for (int i = 0; i < thread_count; i++) {
            threads[i] = new Threader(new ArrayList<Integer>(), primes, LOCK);
        }

        int ind = 0;
        for (int i : IN) {
            if (ind >= thread_count)
                ind = 0;
            threads[ind].add(i);
            ind++;
        }

        for (Thread t : threads) {
            t.start();
        }
        try {
            for (Thread t : threads)
                t.join();
        } catch (InterruptedException e) {
        }
        Instant tF = Instant.now();
        long dt = Duration.between(t0, tF).toMillis();
        String fString = "\n%d primes were found.\n%d threads took %d ms \n";

        LOCK.lock();
        primes.sort(null);
        // for (int i : primes)
        // buf_so.write((i + ", ").getBytes());
        fString = fString.formatted(primes.size(), thread_count, dt);
        LOCK.unlock();

        buf_so.write(fString.getBytes());

        buf_so.flush();

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