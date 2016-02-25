import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class News {
	
	private String news;
	public final ReentrantReadWriteLock contentLock = new ReentrantReadWriteLock(true);
	public final AtomicInteger toBeServed = new AtomicInteger(1);
	public final AtomicInteger beingServed = new AtomicInteger(1);
	
	public News(String news){
		this.news = news;
	}
	
	public News(){
		news = "-1";
	}

	public String getNews() {
		return news;
	}

	public synchronized void setNews(String news) {
		this.news = news;
	}
}
