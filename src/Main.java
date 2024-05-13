import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.concurrent.Semaphore;
import java.time.Duration;
import java.time.Instant;

public class Main {
    private static final int LIMIT = 10000000;
    private static int input = LIMIT;
    private static int thread_count = 1;
    private static BufferedReader inp = new BufferedReader(
            new InputStreamReader(System.in));

    private static void read() {
        try {
            StringTokenizer st = new StringTokenizer(inp.readLine());
            Main.input = Integer.parseInt(st.nextToken());
            Main.thread_count = Main.thread_count << Integer.parseInt(st.nextToken());
        } catch (IOException e) {
            System.out.println("Error reading input");
        }
    }

    public static void main(String[] args) {
        System.out.println(
                "Enter Number to find primes for and the exponent for thread count (threads are 2^k where k is your input)");
        read();
        Instant start = Instant.now();
        Threader[] threads = new Threader[thread_count];
        Semaphore flag = new Semaphore(1);

        List<Integer> primes = new ArrayList<Integer>();
        int batch = (input >= thread_count) ? input / thread_count : 1;
        int mod = input % thread_count;
        if (input > thread_count) {
            thread_count = mod;
            mod = 0;
        }
        int j = 2;
        for (int i = 0; i < thread_count; i++) {
            System.out.println("threading");
            j += i * batch;
            int k = j + batch - 1;

            j += (mod > 0) ? batch : batch - 1;
            threads[i] = new Threader(j, k, primes, flag);
            threads[i].run();

        }
        for (int i = mod; i > 0; i--) {

            System.out.println("finishing");
            j++;
            threads[mod - i].setStart(j);
            threads[mod - i].setEnd(j);
            threads[mod - i].run();
        }
        System.out.printf("%d primes were found.\n", primes.size());
        Instant end = Instant.now();
        long t = Duration.between(start, end).toNanos();
        System.out.printf("%l threads took %l ns \n", thread_count, t);
    }

    public static void findPrimes(int start, int end, List<Integer> primes) {
        for (int current_num = start; current_num <= end; current_num++) {
            if (check_prime(current_num)) {
                primes.add(current_num);
                System.err.printf("%d is prime", current_num);
            }
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
        // why can't we use sieve? It is faster and easier to parellelize
        // This isn't even optimal trial division
        // easier to code this way though.
        int lim = (int) Math.sqrt(n);
        for (int i = 2; i <= lim; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}