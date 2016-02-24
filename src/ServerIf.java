

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerIf extends Remote {
	
	public String read() throws RemoteException;
}
