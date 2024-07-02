import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.server.*;

public class Master_job extends UnicastRemoteObject implements Master_interface {

    protected Master_job() throws RemoteException {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String delegate(int i, int count) throws RemoteException {
        // TODO Auto-generated method stub
        try {
            Slave_Task job = (Slave_Task) Naming.lookup(null);
            return job.doTask(i, count);
        } catch (MalformedURLException | RemoteException | NotBoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "FUCK";
    }

}
