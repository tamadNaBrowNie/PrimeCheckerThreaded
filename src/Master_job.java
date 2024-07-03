import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Master_job extends UnicastRemoteObject implements Master_interface {

    protected Master_job() throws RemoteException {
        super();
    }

    @Override
    public String delegate(int i, int count) throws RemoteException {
        ExecutorService es = Executors.newFixedThreadPool(count);
        try {

            Slave_Task job = (Slave_Task) Naming.lookup("//localhost:2020/slave");
            Future<String> task = es.submit(new Callable<String>() {

                @Override
                public String call() throws Exception {
                    // TODO Auto-generated method stub
                    return job.doTask(i, count);
                }

            });
            es.shutdown();
            return task.get();
        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "FUCK";
    }

}
