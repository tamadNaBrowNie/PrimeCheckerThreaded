import java.rmi.*;

public interface Slave_Interface extends Remote {
    public String doTask(int i, int count) throws RemoteException;
}