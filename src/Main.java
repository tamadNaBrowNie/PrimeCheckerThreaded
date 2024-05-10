import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.time.Duration;
import java.time.Instant;

public class Main {
    private static final int LIMIT = 10000000;
    private static int input = LIMIT;
    private static int threads = 1;
    private static BufferedReader inp = new BufferedReader(
            new InputStreamReader(System.in));

    private static void read() {
        try {
            StringTokenizer st = new StringTokenizer(inp.readLine());
            Main.input = Integer.parseInt(st.nextToken());
            Main.threads = Main.threads << Integer.parseInt(st.nextToken());
        } catch (IOException e) {
            System.out.println("Error reading input");
        }
    }

    public static void main(String[] args) {
        read();
        Instant start = Instant.now();

        List<Integer> primes = new ArrayList<Integer>();

        for (int current_num = 2; current_num <= Main.input; current_num++) {
            if (check_prime(current_num)) {
                primes.add(current_num);
            }
        }

        System.out.printf("%d primes were found.\n", primes.size());
        Instant end = Instant.now();
        long t = Duration.between(start, end).toNanos();
        System.out.printf("%l threads took %l ns \n", threads, t);
        // TODO: Buffer output after we thread(kek, thread output also with time.)
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
        for (int i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }
}