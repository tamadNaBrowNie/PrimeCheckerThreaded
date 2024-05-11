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
        read();
        Instant start = Instant.now();
        Runnable[] threads = new Runnable[thread_count];
        Semaphore flag = new Semaphore(1);

        List<Integer> primes = new ArrayList<Integer>();
        int r = input / thread_count;
        for (int i = 0; i < thread_count; i++) {
            int j = i * r;
            threads[i] = new Runnable() {
                int start = j;
                int end = j + r - 1;
                Semaphore sig = flag;

                // List<Integer> tmp = new ArrayList<Integer>();
                @Override
                public void run() {
                    for (int current_num = this.start; current_num <= this.end; current_num++) {
                        if (check_prime(current_num)) {
                            primes.add(current_num);
                        }
                    }
                    try {
                        sig.acquire();
                        primes.addAll(primes);
                    } catch (InterruptedException wait) {

                    } finally {
                        sig.release();
                    }
                }

            };
            // new Threader(j, j + r - 1, primes, flag);

            // extracted(primes);

            // TODO: Buffer output after we thread(kek, thread output also with time.)
        }
        ;
        System.out.printf("%d primes were found.\n", primes.size());
        Instant end = Instant.now();
        long t = Duration.between(start, end).toNanos();
        System.out.printf("%l threads took %l ns \n", thread_count, t);
    }

    public static void findPrimes(int start, int end, List<Integer> primes) {
        for (int current_num = start; current_num <= end; current_num++) {
            if (check_prime(current_num)) {
                primes.add(current_num);
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