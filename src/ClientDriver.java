import java.io.IOException;
import java.util.Random;

public class ClientDriver implements Runnable {

	private ClientData data;
	private int lifetime;
	private String registryPort;
	private String serverIP;
	private Random rand;

	public ClientDriver(ClientData data, int lifetime, String serverIP, String registryPort) throws IOException {
		this.data = data;
		this.lifetime = lifetime;
		this.serverIP = serverIP;
		this.registryPort = registryPort;
		this.rand = new Random(System.currentTimeMillis());
	}

		private void execRemoteCmdSSHWait(String usrName, String password, String host, String cmd)
			throws IOException, InterruptedException {
				System.out.println("Executing '" + cmd + "' on " + usrName + "@" + host + " ...");
		String[] startRMICMD = { "/usr/bin/expect", "-c",
		String.format("set timeout 60; spawn ssh %s@%s \"%s\"; expect \"yes/no\" { send \"yes\\r\"; expect \"*?assword\" { send \"%s\\r\"; } } \"*?assword\" { send \"%s\\r\"; }; interact;", usrName, host, cmd.replaceAll("\\s+", "\\ "), password, password) };
		Runtime.getRuntime().exec(startRMICMD).waitFor();
		System.out.println("Done executing '" + cmd + "' on " + usrName + "@" + host + ".");	
	}

	@Override
	public void run() {
		String cmd = "cd BClient; java -jar "
				+ (data.getType() == ClientData.READER ? "ReaderClient.jar" : "WriterClient.jar") + " " + serverIP + " "
				+ data.getId() + " " + registryPort + ";";

		try {
			for (int i = 0; i < lifetime; i++) {
				Thread.sleep(rand.nextInt(2000) + 1);
				execRemoteCmdSSHWait(data.getUsr(), data.getPass(), data.getIp(), cmd);
				Thread.sleep(rand.nextInt(10000) + 1);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
