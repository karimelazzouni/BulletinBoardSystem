import java.io.FileNotFoundException;
import java.rmi.RemoteException;

public class BulletinBoardWriterImpl implements BulletinBoardWriter {
	
	private News news;
	
	public BulletinBoardWriterImpl(News news){
		this.news = news;
	}
	
	@Override
	public String write(String newsContent) throws RemoteException, FileNotFoundException {
		int rSeq = news.toBeServed.getAndIncrement();
		news.contentLock.writeLock().lock();
		int sSeq = news.beingServed.getAndIncrement();
		this.news.setNews(newsContent);
		news.contentLock.writeLock().unlock();
		String logTuple = String.format("%-10s %-10s", rSeq, sSeq);
		return logTuple;
	}
}
