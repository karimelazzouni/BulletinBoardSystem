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
		System.setProperty("java.rmi.server.hostname", "192.168.1.4");
		String rmiHost = args[1];
		int rmiPort = Integer.parseInt(args[2]);
		try {
			News news = new News();
			BulletinBoardReaderImpl bbr = new BulletinBoardReaderImpl(news);
			BulletinBoardWriterImpl bbw = new BulletinBoardWriterImpl(news);
			BulletinBoardReader stubReader = (BulletinBoardReader) UnicastRemoteObject.exportObject(bbr, port);
			BulletinBoardWriter stubWriter = (BulletinBoardWriter) UnicastRemoteObject.exportObject(bbw, port);
			Registry reg = LocateRegistry.getRegistry(rmiHost,rmiPort);
			reg.rebind("NewsBulletinBoardReader", stubReader);
			reg.rebind("NewsBulletinBoardWriter", stubWriter);
			PrintWriter readers = new PrintWriter(new FileOutputStream(
				    new File("server.txt")));
			readers.println("sSeq\t\toVal\t\trID\t\trNum");
			readers.close();
			
			System.out.println("Server is up and ready.");
		} catch (RemoteException re) {
			System.err.println("Remote Exception has occurred. Program will terminate.");
			re.printStackTrace();
		} finally {
			
		
		}
	}
}
