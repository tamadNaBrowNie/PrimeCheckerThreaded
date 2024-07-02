import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.StringJoiner;
import java.io.FileOutputStream;
import java.rmi.*;

public class Client {
    private static int getInput(BufferedReader in, OutputStream out, String msg) throws IOException {

        try {
            out.write(msg.getBytes());
            out.flush();
            int ans = Integer.parseInt(in.readLine());

            return ans;
        } catch (NumberFormatException f) {

            System.out.println("Input not a number");

        }
        // buf_in.readLine();
        return getInput(in, out, msg);
    }

    private static String getString(BufferedReader in, String msg) throws IOException {

        System.out.println(msg);
        return in.readLine();

    }

    private static int getInput(BufferedReader buf_in, OutputStream buf_so) throws IOException {
        int in = 0;
        final int LIMIT = 100000000;
        do {
            in = getInput(buf_in, buf_so, "\nEnter number to search: ");

            if (in > LIMIT)
                buf_so.write("Input too large. enter again\n".getBytes());

            else if (in < 2)
                buf_so.write("Input too small. enter again\n".getBytes());
        } while (in < 2 || in > LIMIT);
        return in;
    }

    private static int getPow(BufferedReader buf_in, OutputStream buf_so) throws IOException {
        int pow = -1;
        while (pow < 0 || pow > 10) {

            pow = getInput(buf_in, buf_so, "\nCore counts is 2^n where 0 <= n < 11 and n is your input: ");
            if (pow < 0 || pow > 10)
                buf_so.write("Invalid power".getBytes());
        }
        return 1 << pow;
    }

    private static final String CYKA = "I/O SNAFU";

    public static void main(String[] args) {

        try (BufferedReader buf_in = new BufferedReader(
                new InputStreamReader(System.in))) {
            OutputStream buf_so = new BufferedOutputStream(System.out, 1 << 16);
            boolean scripted = false;

            try {
                Master_job job = (Master_job) Naming.lookup(null);
                scripted = getInput(buf_in, buf_so, "Automate? 0 for no, else yes") != 0;
                if (scripted) {
                    String where = getString(buf_in, "Write where?");
                    buf_in.close();
                    buf_so.close();
                    buf_so = new BufferedOutputStream(new FileOutputStream(where));
                    String str = getResults(job);
                    buf_so.write(str.getBytes());
                    buf_so.flush();
                    buf_so.close();

                    return;
                }
                int input = getInput(buf_in, buf_so);
                int thread_count = getPow(buf_in, buf_so);
                buf_in.close();
                String str = job.delegate(input, thread_count);
                try {
                    buf_so.write(str.getBytes());

                } catch (IOException e) {
                    System.out.println(CYKA);
                    System.err.println(CYKA + " when getting input");
                    e.printStackTrace();
                }
                buf_so.flush();
                buf_so.close();

            } catch (IOException | NotBoundException e) {
                System.out.println(CYKA);
                System.err.println(CYKA + " when getting input");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static String getResults(Master_job job) throws RemoteException {
        final int LIMIT = 100000000;
        StringJoiner str = new StringJoiner("\n\n");
        int[] inputs = { 2, 3, 4, 523, 67800, LIMIT / 8, LIMIT / 4, LIMIT / 2, LIMIT - 8,
                LIMIT };
        for (int i : inputs) {
            for (int j = 0; j < 11; j++) {
                str.add("\nin " + i);
                int count = 1 << j;
                for (int k = 0; k < 5; k++) {
                    str.add(job.delegate(i, count));
                }
            }
        }
        return str.toString();
    }
}