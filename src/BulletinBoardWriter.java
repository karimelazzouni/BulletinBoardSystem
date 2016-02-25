import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BulletinBoardWriter extends Remote {
	public String write(String news) throws RemoteException,FileNotFoundException;
}
