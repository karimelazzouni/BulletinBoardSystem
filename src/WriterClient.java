import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class WriterClient {

	public static void main(String[] args) {
		String serverIP = args[0];
		String id = args[1];

		try {
			PrintWriter out = new PrintWriter(new FileOutputStream(new File("log" + id), true));
			Registry reg = LocateRegistry.getRegistry(serverIP);
			BulletinBoardWriter stubWriter = (BulletinBoardWriter) reg.lookup("NewsBulletinBoardWriter");
			out.printf("Client type: Writer\n");
			out.printf("Client name: %i\n" + id);
			out.printf("%-10s %-10s", "rSeq", "sSeq");
			
			// write News
			out.println(stubWriter.write(id));
			
			out.close();
		} catch (RemoteException re) {
			System.err.println("Remote Exception has occurred. Program will terminate.");
			re.printStackTrace();
		} catch (NotBoundException nbe) {
			System.err.println("Remote BulletinBoard object is not bound. Program will terminate.");
			nbe.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
