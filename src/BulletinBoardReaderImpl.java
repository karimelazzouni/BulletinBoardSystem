import java.io.FileNotFoundException;
import java.rmi.RemoteException;

public class BulletinBoardReaderImpl implements BulletinBoardReader  {
	
	private News news;
	
	public BulletinBoardReaderImpl(News news){
		this.news = news;
	}
	
	@Override
	public String read() throws RemoteException, FileNotFoundException {
		int rSeq = news.toBeServed.getAndIncrement();
		news.contentLock.readLock().lock();
		int sSeq = news.beingServed.getAndIncrement();
		String newsContent = news.getNews();
		news.contentLock.readLock().unlock();
		String logTuple = String.format("%-10s %-10s %-10", rSeq, sSeq, newsContent);
		return logTuple;
	}
	
}
