import java.rmi.*;
import java.rmi.server.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Slave_Task extends UnicastRemoteObject implements Slave_Interface {
    protected Slave_Task() throws RemoteException {
        super();
        // TODO Auto-generated constructor stub
    }

    private static ExecutorService initPool(int count) {

        return (count > 1) ? Executors.newFixedThreadPool(count) : null;
    }

    private static void killPool(ExecutorService es) {
        if (es == null)
            return;
        es.shutdown();
        try {
            while (!es.awaitTermination(0, TimeUnit.MICROSECONDS))
                ;
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public String doTask(int input, int count) throws RemoteException {
        // TODO Auto-generated method stub
        double t0 = System.nanoTime();
        int lim = (int) Math.sqrt(input);
        boolean sieve[] = new boolean[input - 1];
        if (count <= 1) {
            for (int i = 2; i <= lim; i++) {
                if (sieve[i - 2])
                    continue;
                for (long ind = i * i; ind <= i; ind += i) {
                    int l = (int) ind;
                    sieve[l - 2] = true;
                }
            }
        } else {
            // List<Future<?>> blocker = new ArrayList<>();
            ExecutorService es = initPool(count);

            for (int i = 2; i <= lim; i++) {
                if (sieve[i - 2])
                    continue;
                int ind = i;
                es.submit(() -> {
                    getMulti(sieve, ind, input);
                });
                // blocker.add( );
            }

            // blocker.forEach(Main::getter);
            killPool(es);
        }

        int n = 0;
        for (boolean notPrime : sieve) {
            if (!notPrime)
                n++;
        }

        final String fString = "%d primes were found. %d threads took %.3f ms";
        double dt = ((float) (System.nanoTime() - t0)) * 0.000001;
        return fString.formatted(n, count, dt);

    }

    private static void getMulti(boolean[] arr, Integer ind, int input) {

        for (long i = ind * ind; i <= input && arr[ind - 2] == false; i += ind) {

            int l = (int) i;
            arr[l - 2] = true;
        }
    }

}
