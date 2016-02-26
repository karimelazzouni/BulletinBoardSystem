import java.io.IOException;
import java.util.Random;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;

public class ClientEngine implements Runnable {

	public static final int READER = 0;
	public static final int WRITER = 1;

	private int id;
	private int type;
	private int lifetime;
	private String registryPort;
	private String serverIP;
	private SSHClient sshClient;
	private Random rand;

	public ClientEngine(int id, int type, int lifetime, SSHClient sshClient, String serverIP, String registryPort) throws IOException {
		this.id = id;
		this.type = type;
		this.lifetime = lifetime;
		this.sshClient = sshClient;
		this.serverIP = serverIP;
		this.registryPort=registryPort;
		this.rand = new Random(System.currentTimeMillis());
	}

	@Override
	public void run() {
		try {
			for (int i = 0; i < lifetime; i++) {
				Thread.sleep(rand.nextInt(2000) + 1);
				Session s = sshClient.startSession();
				if (type == READER) {
					s.exec("cd BClient; java -jar ReaderClient.jar " + serverIP + " " + id + " " + registryPort + ";");
				} else if (type == WRITER) {
					s.exec("cd BClient; java -jar WriterClient.jar " + serverIP + " " + id + " " + registryPort + ";");
				}
				s.close();
				Thread.sleep(rand.nextInt(10000) + 1);
			}
			sshClient.disconnect();
			sshClient.close();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ConnectionException e) {
			e.printStackTrace();
		} catch (TransportException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
