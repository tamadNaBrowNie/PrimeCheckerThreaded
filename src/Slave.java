import java.net.MalformedURLException;
import java.rmi.*;

public class Slave {
    public static void main(String[] args) {
        try {
            Slave_Task prime_stub = new Slave_Task();
            // TODO add an actual url
            Naming.rebind(null, prime_stub);
        } catch (RemoteException | MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
