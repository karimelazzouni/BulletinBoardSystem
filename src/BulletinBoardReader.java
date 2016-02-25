import java.io.FileNotFoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BulletinBoardReader extends Remote {
	public String read() throws RemoteException,FileNotFoundException;
}
