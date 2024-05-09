import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Main {
    private static final int LIMIT = 10000000;
    private static int input = LIMIT;
    private static int threads = 1;
    private static BufferedReader br = new BufferedReader(
            new InputStreamReader(System.in));

    private static void read() throws IOException {

        StringTokenizer st = new StringTokenizer(br.readLine());
        Main.input = Integer.parseInt(st.nextToken());
        Main.threads = Integer.parseInt(st.nextToken());
    }

    public static void main(String[] args) {
        try {
            read();

        } catch (IOException e) {
            return;
        }
        final double MAX = Math.sqrt(input);
        List<Integer> primes = new ArrayList<Integer>();

        for (int current_num = 2; current_num <= MAX; current_num++) {
            if (check_prime(current_num)) {
                primes.add(current_num);
            }
        }

        System.out.printf("%d primes were found.\n", primes.size());
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