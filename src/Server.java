import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Timestamp;
import java.util.Date;

public class Server extends UnicastRemoteObject implements ServerIf, Runnable {
	
	private static final long serialVersionUID = 1L;
	private String IP;
	private String port;
	private String name;
	
//	public Server() throws RemoteException {}
	public Server(String IP, String port, String name) throws RemoteException {
		this.IP = IP;
		this.port = port;
		this.name = name;
	}

	public String read() throws RemoteException {
		Date date = new Date();
		return "News! " + new Timestamp(date.getTime());
		
	}

	@Override
	public void run() {
		System.setProperty("java.rmi.server.hostname", IP.concat(":" + port));
		try {
			Naming.rebind(name, this);
		} catch (RemoteException | MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
