import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.Date;

public class BulletinBoardImpl implements BulletinBoard { 
	
	@Override
	public String read() throws RemoteException, FileNotFoundException {
		Date date = new Date();
		PrintWriter readers = new PrintWriter(new FileOutputStream(
			    new File("readers.txt"), 
			    true /* append = true */));
		readers.println("sSeq\t\toVal\t\trID\t\trNum");
		readers.close();
		return "Read News! " + new Timestamp(date.getTime());
		
	}

	@Override
	public String write() throws RemoteException, FileNotFoundException {
		Date date = new Date();
		PrintWriter writers = new PrintWriter(new FileOutputStream(
			    new File("writers.txt"), 
			    true /* append = true */));
		writers.println("sSeq\t\toVal\t\twID");
		writers.close();
		return "Write News! " + new Timestamp(date.getTime());
	}
}
