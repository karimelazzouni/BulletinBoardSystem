import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

	private String serverIP;
	private String serverPort;
	private String serverName;
	public Client(String serverIP, String serverPort, String serverName) {
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		this.serverName = serverName;
	}
	public void initiateClient() {
		try {
			String lookupQuery ="rmi://" + serverIP + ":" + serverPort + "/" + serverName;
			ServerIf servif = (ServerIf)Naming.lookup(lookupQuery);
			System.out.println(servif.read());
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
}
