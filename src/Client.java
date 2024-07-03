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
                // String master = getString(buf_in, "master url"),
                // slave = getString(buf_in, "slave url");
                Master_interface job;
                scripted = getInput(buf_in, buf_so, "Automate? 0 for no, else yes") != 0;
                if (scripted) {
                    String where = getString(buf_in, "Write where?");
                    buf_in.close();
                    buf_so.close();
                    buf_so = new BufferedOutputStream(new FileOutputStream(where));
                    System.out.println("waiting for master");
                    job = (Master_interface) Naming.lookup("rmi://localhost:2021/master");
                    String str = getResults(job);
                    buf_so.write(str.getBytes());
                    buf_so.flush();
                    buf_so.close();

                    return;
                }
                int input = getInput(buf_in, buf_so);
                int thread_count = getPow(buf_in, buf_so);
                buf_in.close();
                double t0 = System.nanoTime(), dt;
                System.out.println("waiting for master");
                job = (Master_interface) Naming.lookup("rmi://localhost:2021/master");
                // String str = job.delegate(input, thread_count, slave);
                String str = job.delegate(input, thread_count);
                dt = System.nanoTime() - t0;
                str = str + String.format(" took %.3f ", dt * 0.000001);
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
                e.printStackTrace();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static String getResults(Master_interface job) throws RemoteException {
        final int LIMIT = 100000000;
        StringJoiner str = new StringJoiner("\n\n");
        int[] inputs = { 2, 3, 4, 523, 67800, LIMIT / 8, LIMIT / 4, LIMIT / 2, LIMIT - 8,
                LIMIT };
        double t0, tf, dt;
        for (int i : inputs) {
            for (int j = 0; j < 11; j++) {
                str.add("\nin " + i);
                int count = 1 << j;
                for (int k = 0; k < 5; k++) {
                    t0 = System.nanoTime();
                    // String log = job.delegate(i, count, slave);
                    String log = job.delegate(i, count);
                    tf = System.nanoTime();
                    dt = (System.nanoTime() - t0) * (0.000001);

                    str.add(String.format("%s took %.3f \n", log, dt));
                    t0 = tf;
                }
            }
        }
        return str.toString();
    }
}
