import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.time.Duration;
import java.time.Instant;

public class Main {
    private static final int LIMIT = 10000000;
    private static int input = LIMIT;
    // private static int thread_count = 1;
    private static BufferedReader inp = new BufferedReader(
            new InputStreamReader(System.in));

    // private static void read() {
    // try {
    // // StringTokenizer st = new StringTokenizer();
    // Main.input = Integer.parseInt(inp.readLine());
    // int pow = Integer.parseInt(inp.readLine());
    // // pow = 1 << pow;
    // System.out.println(Main.thread_count);
    // Main.thread_count = 1 << pow;
    // System.out.println(Main.thread_count);
    // } catch (IOException e) {
    // System.out.println("Error reading input");
    // }
    // }

    public static void main(String[] args) {
        System.out.println(
                "Enter Number to find primes for and the exponent for thread count (threads are 2^k where k is your input)");
        int thread_count = 1;
        // read();
        try {
            // StringTokenizer st = new StringTokenizer();
            Main.input = Integer.parseInt(inp.readLine());
            int pow = Integer.parseInt(inp.readLine());
            // pow = 1 << pow;
            // System.out.println(Main.thread_count);
            thread_count = 1 << pow;
            System.out.println(thread_count);
        } catch (IOException e) {
            System.out.println("Error reading input");
        }
        Instant start = Instant.now();
        Threader[] threads = new Threader[thread_count];
        Semaphore flag = new Semaphore(1);

        List<Integer> primes = new ArrayList<Integer>();
        int batch = (input >= thread_count) ? input / thread_count : 1;
        int mod = input % thread_count;
        System.out.println(input > thread_count);
        if (input < thread_count) {
            thread_count = mod;
            mod = 0;
            System.out.println(input);
            System.out.println(thread_count);

        }
        int j = 2;
        for (int i = 0; i < thread_count; i++) {
            System.out.println("threading");
            j += i * batch;
            int k = j + batch - 1;
            if (k > input) {
                k = input;
            }
            System.out.println("k is");
            System.out.println(j);
            // j += (mod > 0) ? batch : batch - 1;
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
        System.out.println(primes);
        Instant end = Instant.now();
        long t = Duration.between(start, end).toMillis();
        System.out.printf("%d threads took %d ns \n", thread_count, t);
    }

    // public static void findPrimes(int start, int end, List<Integer> primes) {

    // }

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