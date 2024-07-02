
import java.rmi.*;

public interface Master_interface extends Remote {
    public String delegate(int i, int count) throws RemoteException;
}