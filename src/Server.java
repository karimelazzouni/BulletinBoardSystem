import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class Server {
	
	public static void main(String [] args) throws FileNotFoundException, AlreadyBoundException {
		int port = Integer.parseInt(args[0]);
		System.setProperty("java.rmi.server.hostname", args[1]);
		int rmiPort = Integer.parseInt(args[2]);
		String readersName = "server_readers";
		String writersName = "server_writers";
		
		try {
			News news = new News();
			
			PrintWriter readers = new PrintWriter(new FileOutputStream(new File(readersName)));
			BulletinBoardReaderImpl bbr = new BulletinBoardReaderImpl(news, readersName);
			readers.printf("%-10s %-10s %-10s %-10s\n", "sSeq", "oVal", "rID", "rNum");
			readers.close();
			
			PrintWriter writers = new PrintWriter(new FileOutputStream(new File(writersName)));
			BulletinBoardWriterImpl bbw = new BulletinBoardWriterImpl(news, writersName);
			writers.printf("%-10s %-10s %-10s\n", "sSeq", "oVal", "wID");
			writers.close();
			
			BulletinBoardReader stubReader = (BulletinBoardReader) UnicastRemoteObject.exportObject(bbr, port);
			BulletinBoardWriter stubWriter = (BulletinBoardWriter) UnicastRemoteObject.exportObject(bbw, port);
			Registry reg = LocateRegistry.getRegistry(rmiPort);
			reg.rebind("NewsBulletinBoardReader", stubReader);
			reg.rebind("NewsBulletinBoardWriter", stubWriter);
	
			System.out.println("Server is up and ready.");
		} catch (RemoteException re) {
			System.err.println("Remote Exception has occurred. Program will terminate.");
			re.printStackTrace();
		} finally {
		
		}
	}
}
