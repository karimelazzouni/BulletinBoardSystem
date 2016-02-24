import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.Date;

public class Server extends UnicastRemoteObject implements ServerIf {
	

	private static final long serialVersionUID = 1L;

	public Server() throws RemoteException {}

	public String read() throws RemoteException {
		Date date = new Date();
		return "News! " + new Timestamp(date.getTime());
		
	}
	
	public static void main(String[] args) {
		System.setProperty("java.rmi.server.hostname", "localhost");
    	//System.setProperty("java.rmi.activation.port", "55555");
    	
    	try {
			Server s = new Server();
			Naming.rebind("NewsBulletinServer", s);
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
    	
	}
}
