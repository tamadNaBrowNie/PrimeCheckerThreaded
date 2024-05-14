import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.IntStream;
import java.util.stream.Collectors;

public class Main {
    private static final int LIMIT = 10000000;
    private static int input = LIMIT;
    // private static int thread_count = 1;
    private static BufferedReader io = new BufferedReader(
            new InputStreamReader(System.in));
    private static int thread_count = 1;

    private static void read() throws IOException {
        int pow = -1;
        do {
            System.out.println("Enter number to search");
            Main.input = Integer.parseInt(io.readLine());
            if (Main.input > LIMIT)
                System.out.println("Input too large. enter again");

            if (Main.input < 2)
                System.out.println("Input too small. enter again");
        } while (Main.input < 2 || Main.input > LIMIT);
        while (pow < 0 || pow > 10) {
            System.out.println("Core counts are powers of 2. Enter exponent for core count.");
            pow = Integer.parseInt(io.readLine());
            if (pow < 0 || pow > 10) {
                System.out.println("Invalid power, enter again");
            }
        }

        Main.thread_count = 1 << pow;

    }

    public static void main(String[] args) {
        boolean o1 = false;
        try {
            read();
        } catch (IOException e) {
            System.out.println("Error reading input");
        }

        Instant start = Instant.now();
        Threader[] threads = new Threader[thread_count];
        List<Integer> in = IntStream.rangeClosed(2, input).boxed().collect(Collectors.toList());
        // IntStream stm = IntStream.rangeClosed(2, input);
        Semaphore flag = new Semaphore(1);

        List<Integer> primes = new ArrayList<Integer>();
        int siz = in.size();
        int batch = (input - 1 >= thread_count) ? siz / thread_count : 1;
        int mod = (siz <= thread_count) ? siz % thread_count : 0;
        if (siz < thread_count) {
            thread_count = siz;
        }
        int j = 0, k = j + batch;
        for (int i = 0; i < thread_count; i++) {
            System.out.println("thread " + i);

            if (mod > 0) {
                mod--;
                k++;
            }
            if (k > siz)
                k = siz;
            threads[i] = new Threader(in.subList(j, k), primes, flag);
            j = (k == siz) ? siz - 1 : k;
            k = j + batch;
            threads[i].run();

        }
        if (!o1)
            for (int i = thread_count; i < threads.length; i++) {
                threads[i] = new Threader(new ArrayList<Integer>(), primes, flag);
            }
        Instant end = Instant.now();
        long t = Duration.between(start, end).toMillis();
        System.out.printf("%d primes were found.\n", primes.size());
        System.out.println(primes);

        System.out.printf("%d threads took %d ns \n", threads.length, t);
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

        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}