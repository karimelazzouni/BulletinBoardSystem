import java.io.FileNotFoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

	private String serverIP;
	private String serverName;
	public Client(String serverIP, String serverName) {
		this.serverIP = serverIP;
		this.serverName = serverName;
	}
	public void initiateClient() throws FileNotFoundException {
		try {
			Registry reg = LocateRegistry.getRegistry(serverIP);
			BulletinBoard stub = (BulletinBoard) reg.lookup(serverName);
			System.out.println(stub.read());
		} catch (RemoteException re) {
			System.err.println("Remote Exception has occurred. Program will terminate.");
			re.printStackTrace();
		} catch (NotBoundException nbe) {
			System.err.println("Remote BulletinBoard object is not bound. Program will terminate.");
			nbe.printStackTrace();
		} 
	}
}
