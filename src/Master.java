import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;

public class Master {
    public static void main(String[] args) {

        // TODO: THREAD DIS
        try {
            LocateRegistry.createRegistry(2021);

            System.out.println("Master waits");
            Master_job cmd = new Master_job();
            Naming.rebind("rmi://localhost:2021/master", cmd);
        } catch (RemoteException | MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
