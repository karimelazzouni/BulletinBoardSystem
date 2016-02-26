import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BulletinBoardWriter extends Remote {
	public String write(String wid, String news) throws RemoteException,FileNotFoundException;
}
