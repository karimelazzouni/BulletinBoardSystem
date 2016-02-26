import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class News {

	private String news;

	private final ReentrantReadWriteLock contentLock = new ReentrantReadWriteLock(true);
	private final AtomicInteger toBeServed = new AtomicInteger(1);
	private final AtomicInteger beingServed = new AtomicInteger(1);
	private final AtomicInteger readersCount = new AtomicInteger(1);

	public News(String news) {
		this.news = news;
	}

	public News() {
		news = "-1";
	}

	public NewsStats readNews(String rid) {
		int rSeq = toBeServed.getAndIncrement();
		contentLock.readLock().lock();
		int rNum = readersCount.getAndIncrement();
		int sSeq = beingServed.getAndIncrement();
		NewsStats ns = new NewsStats(sSeq, rSeq, news, rid, rNum);
		readersCount.decrementAndGet();
		contentLock.readLock().unlock();
		return ns;
	}

	public NewsStats writeNews(String wid, String news) {
		int rSeq = toBeServed.getAndIncrement();
		contentLock.writeLock().lock();
		int sSeq = beingServed.getAndIncrement();
		this.news = news;
		NewsStats ns = new NewsStats(sSeq, rSeq, news, wid, -1);
		contentLock.writeLock().unlock();
		return ns;
	}
}
