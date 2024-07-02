import java.rmi.RemoteException;
import java.net.MalformedURLException;
import java.rmi.*;

public class Master {
    public static void main(String[] args) {
        try {
            Master_job cmd = new Master_job();
            Naming.rebind(null, cmd);
        } catch (RemoteException | MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
