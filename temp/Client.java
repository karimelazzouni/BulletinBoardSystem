import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Client {

	public static void main(String[] args) {
		try {
			ServerIf servif = (ServerIf)Naming.lookup("rmi://127.0.0.1/NewsBulletinServer");
			System.out.println(servif.read());
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			e.printStackTrace();
		}
	}
}
