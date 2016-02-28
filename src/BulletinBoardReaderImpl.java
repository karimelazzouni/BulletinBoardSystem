import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.rmi.RemoteException;

public class BulletinBoardReaderImpl implements BulletinBoardReader {

	private News news;
	private String logName;

	public BulletinBoardReaderImpl(News news, String logName) {
		this.news = news;
		this.logName = logName;
	}

	@Override
	public String read(String rid) throws RemoteException, FileNotFoundException {
		NewsStats newsStats = news.readNews(rid);

		PrintWriter log = new PrintWriter(new FileOutputStream(new File(logName), true));
		log.printf("%-10s %-10s %-10s %-10s\n", "" + newsStats.getsSeq(), newsStats.getoVal(), newsStats.getId(),
				"" + newsStats.getrNum());
		log.close();
		return String.format("%-10s %-10s %-10s", "" + newsStats.getrSeq(), "" + newsStats.getsSeq(),
				newsStats.getoVal());
	}

}
