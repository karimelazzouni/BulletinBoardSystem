import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Random;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Start {

	private static String serverIP;
	private static int serverPort;
	private static String registryPort;
	private static String serverUsername;
	private static String serverPassword;

	private static int numReaders;
	private static int numWriters;
	private static ClientData[] clientsData;
	private static int numAccesses;

	private static Thread[] clientsEngines;

	private static void loadProp() throws IOException {
		System.out.println("Loading properties file: system.properties ...");

		Properties prop = new Properties();
		InputStream input = new FileInputStream("system.properties");
		prop.load(input);
		serverIP = prop.getProperty("RW.server");
		serverPort = Integer.parseInt(prop.getProperty("RW.server.port"));
		registryPort = prop.getProperty("RW.registry.port");
		serverUsername = prop.getProperty("RW.server.username");
		serverPassword = prop.getProperty("RW.server.password");

		numReaders = Integer.parseInt(prop.getProperty("RW.numberOfReaders"));
		numWriters = Integer.parseInt(prop.getProperty("RW.numberOfWriters"));
		clientsData = new ClientData[numReaders + numWriters];

		for (int i = 0; i < numReaders; i++) {
			String prop_str = ("RW.reader" + i);
			String username = prop_str.concat(".username");
			String password = prop_str.concat(".password");
			clientsData[i] = new ClientData(i, ClientData.READER, prop.getProperty(prop_str),
					prop.getProperty(username), prop.getProperty(password));
		}

		for (int i = numReaders; i < numWriters + numReaders; i++) {
			String prop_str = ("RW.writer" + (i - numReaders));
			String username = prop_str.concat(".username");
			String password = prop_str.concat(".password");
			clientsData[i] = new ClientData(i, ClientData.WRITER, prop.getProperty(prop_str),
					prop.getProperty(username), prop.getProperty(password));
		}
		numAccesses = Integer.parseInt(prop.getProperty("RW.numberOfAccesses"));

		System.out.println("Done loading properties file.");
	}

	private static void execRemoteCmdSSH(String usrName, String password, String host, String cmd) throws IOException {
		System.out.println("Executing '" + cmd + "' on " + usrName + "@" + host + " ...");
		String[] startRMICMD = { "/usr/bin/expect", "-c",
		String.format("set timeout 60; spawn ssh %s@%s \"%s\"; expect \"yes/no\" { send \"yes\\r\"; expect \"*?assword\" { send \"%s\\r\"; } } \"*?assword\" { send \"%s\\r\"; }; interact;", usrName, host, cmd.replaceAll("\\s+", "\\ "), password, password) };
		Runtime.getRuntime().exec(startRMICMD);
		System.out.println("Done executing '" + cmd + "' on " + usrName + "@" + host + ".");
	}

	private static void execRemoteCmdSSHWait(String usrName, String password, String host, String cmd)
			throws IOException, InterruptedException {
		System.out.println("Executing '" + cmd + "' on " + usrName + "@" + host + " ...");
		String[] startRMICMD = { "/usr/bin/expect", "-c",
		String.format("set timeout 60; spawn ssh %s@%s \"%s\"; expect \"yes/no\" { send \"yes\\r\"; expect \"*?assword\" { send \"%s\\r\"; } } \"*?assword\" { send \"%s\\r\"; }; interact;", usrName, host, cmd.replaceAll("\\s+", "\\ "), password, password) };
		Runtime.getRuntime().exec(startRMICMD).waitFor();
		System.out.println("Done executing '" + cmd + "' on " + usrName + "@" + host + ".");	
	}

	private static void startServerProcess() throws IOException {
		System.out.println("Starting Bulletin Board Server on " + serverIP + ":" + serverPort + " ...");
		execRemoteCmdSSH(serverUsername, serverPassword, serverIP,
				"cd BServer; " + "java -jar Server.jar " + serverPort + " " + serverIP + " " + registryPort + " &");
		System.out.println("Done starting Bulletin Board Server process on " + serverIP + ":" + serverPort + ".");
	}

	private static void closeServerProcess() throws IOException, InterruptedException {
		System.out.println("Closing Bulletin Board Server on " + serverIP + ":" + registryPort + " ...");
		execRemoteCmdSSHWait(serverUsername, serverPassword, serverIP, "fuser -k " + serverPort + "/tcp;");
		System.out.println("Done closing Bulletin Board Server on " + serverIP + ":" + registryPort + ".");
	}

	private static void startClients() throws IOException, InterruptedException {
		System.out.println("Starting clients ...");
		clientsEngines = new Thread[clientsData.length];
		for (int i = 0; i < clientsData.length; i++) {
			ClientDriver engine = new ClientDriver(clientsData[i], numAccesses, serverIP, registryPort);
			clientsEngines[i] = new Thread(engine);
			clientsEngines[i].start();
			System.out.println("Started client with: " + clientsData[i].toString() + ".");
		}
		System.out.println("Done starting clients.");
	}

	private static boolean allTrue(boolean[] arr) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == false) {
				return false;
			}
		}
		return true;
	}

	private static void joinClients() throws InterruptedException {
		System.out.println("Waiting for all clients to terminate gracefully ...");
		boolean[] pad = new boolean[clientsData.length];
		Random r = new Random(System.currentTimeMillis());
		while (!allTrue(pad)) {
			int idx = r.nextInt(clientsData.length);
			if (pad[idx])
				continue;
			clientsEngines[idx].join();
			pad[idx] = true;
			System.out.println("Client with: " + clientsData[idx].toString() + " terminated gracefully.");
		}
		System.out.println("Done waiting for all clients to terminate gracefully.");
	}

	private static void cleanLogs() throws IOException, InterruptedException {
		execRemoteCmdSSHWait(serverUsername, serverPassword, serverIP, "cd BServer; rm log*;");
		for (ClientData client : clientsData) {
			execRemoteCmdSSHWait(client.getUsr(), client.getPass(), client.getIp(), "cd BClient; rm log*;");
		}
	}

	public static void main(String[] args) {
		try {
			System.out.println("Starting Simulation ...");
			loadProp();
			cleanLogs();
			startServerProcess();
			startClients();
			joinClients();
			closeServerProcess();
			System.out.println("Simulation is done ...");
			System.out.println("Terminating gracefully ...");
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
