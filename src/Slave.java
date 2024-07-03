import java.net.MalformedURLException;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;

public class Slave {
    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(2020);
            Slave_Task prime_stub = new Slave_Task();
            System.out.println("Slave fears");
            // TODO add an actual url
            Naming.rebind("//localhost:2020/slave", prime_stub);
        } catch (RemoteException | MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
