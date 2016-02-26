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
		int regPort = Integer.parseInt(args[2]);
		try {
			File file = new File("log" + id);
			PrintWriter out = null;
			if(!file.exists()) {
				out = new PrintWriter(new FileOutputStream(file));
				out.printf("Client type: Writer\n");
				out.printf("Client name: %s\n", id);
				out.printf("%-10s %-10s\n", "rSeq", "sSeq");
				out.flush();
			} else {
				out = new PrintWriter(new FileOutputStream(file, true));
			}

			Registry reg = LocateRegistry.getRegistry(serverIP, regPort);
			BulletinBoardWriter stubWriter = (BulletinBoardWriter) reg.lookup("NewsBulletinBoardWriter");
			
			// write News
			out.println(stubWriter.write(id, id));
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
