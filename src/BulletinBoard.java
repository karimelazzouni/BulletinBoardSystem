import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BulletinBoard extends Remote {
	
	public String read() throws RemoteException,FileNotFoundException;
	
	public String write() throws RemoteException,FileNotFoundException;
}
