import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.rmi.RemoteException;

public class BulletinBoardWriterImpl implements BulletinBoardWriter {

	private News news;
	private String logName;

	public BulletinBoardWriterImpl(News news, String logName) {
		this.news = news;
		this.logName = logName;
	}

	@Override
	public String write(String wid, String newsContent) throws RemoteException, FileNotFoundException {
		NewsStats newsStat = news.writeNews(wid, newsContent);
		PrintWriter log = new PrintWriter(new FileOutputStream(new File(logName), true));
		log.printf("%-10s %-10s %-10s\n", "" + newsStat.getsSeq(), newsStat.getoVal(), "" + newsStat.getId());
		log.close();
		return String.format("%-10s %-10s", "" + newsStat.getrSeq(), "" + newsStat.getsSeq());
	}
}
