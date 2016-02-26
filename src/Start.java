import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.util.Properties;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

public class Start {

	static class ClientData {
		int type;
		int id;
		String ip;
		String usr;
		String pass;

		public ClientData(int id, int type, String ip, String usr, String pass) {
			this.id = id;
			this.type = type;
			this.ip = ip;
			this.usr = usr;
			this.pass = pass;
		}
	}

	private static String registryPort;
	private static String serverIP;
	private static int serverPort;
	private static String serverUsername;
	private static String serverPassword;

	private static int numReaders;
	private static int numWriters;
	private static ClientData[] clientsData;

	private static int numAccesses;
	private static SSHClient serverSSH;
	private static SSHClient[] clientsSSH;
	private static Thread[] clientsEngines;

	private static void loadProp() throws IOException {
		Properties prop = new Properties();
		InputStream input = new FileInputStream("system.properties");
		prop.load(input);
		registryPort = prop.getProperty("RW.registry.port");
		serverIP = prop.getProperty("RW.server");
		serverPort = Integer.parseInt(prop.getProperty("RW.server.port"));
		serverUsername = prop.getProperty("RW.server.username");
		serverPassword = prop.getProperty("RW.server.password");

		numReaders = Integer.parseInt(prop.getProperty("RW.numberOfReaders"));
		numWriters = Integer.parseInt(prop.getProperty("RW.numberOfWriters"));
		clientsData = new ClientData[numReaders + numWriters];

		for (int i = 0; i < numReaders; i++) {
			String prop_str = ("RW.reader" + i);
			String username = prop_str.concat(".username");
			String password = prop_str.concat(".password");
			clientsData[i] = new ClientData(i, ClientEngine.READER, prop.getProperty(prop_str),
					prop.getProperty(username), prop.getProperty(password));
		}

		for (int i = numReaders; i < numWriters + numReaders; i++) {
			String prop_str = ("RW.writer" + (i-numReaders));
			String username = prop_str.concat(".username");
			String password = prop_str.concat(".password");
			clientsData[i] = new ClientData(i, ClientEngine.WRITER, prop.getProperty(prop_str),
					prop.getProperty(username), prop.getProperty(password));
		}
		numAccesses = Integer.parseInt(prop.getProperty("RW.numberOfAccesses"));
	}

	// @SuppressWarnings("unused")
	// private static void printConfig() {
	// System.out.println("Server IP: " + serverIP);
	// System.out.println("Server Port: " + serverPort);
	// System.out.println("Readers (" + numReaders + "): ");
	// for (int i = 0; i < readersIPs.length; i++) {
	// System.out.println("\t" + readersIPs[i]);
	// }
	// System.out.println("Writers (" + numWriters + "): ");
	// for (int i = 0; i < writersIPs.length; i++) {
	// System.out.println("\t" + writersIPs[i]);
	// }
	// System.out.println("Number Of Accesses: " + numAccesses);
	// }

	@SuppressWarnings("unused")
	private static String SSHExecuteReturn(Session s, String cmdLine) throws IOException {
		Command cmd = s.exec(cmdLine);
		BufferedReader br = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null)
			sb.append(line).append("\n");
		br.close();
		return sb.toString();
	}

	private static void startRMIRegProcess() throws IOException {
		Session rmiSession = serverSSH.startSession();
		rmiSession.exec("cd BServer && rmiregistry " + registryPort + "&");
		rmiSession.close();
	}

	private static void closeRMIRegProcess() throws IOException {
		// TODO
		Session rmiSession = serverSSH.startSession();
		rmiSession.exec("fuser -k " + registryPort + "/tcp &");
		rmiSession.close();
	}

	private static void startServerProcess() throws IOException {
		Session serverSession = serverSSH.startSession();
		serverSession.exec("cd BServer && " + "java -jar Server.jar " + serverPort + " " + serverIP + " " + registryPort + " &");
		System.out.println("after server");
		serverSession.close();
	}

	private static void closeServerProcess() throws IOException {
		// TODO
		Session serverSession = serverSSH.startSession();
		serverSession.exec("fuser -k " + serverPort + "/tcp &");
		serverSession.close();
	}

	private static SSHClient getSSHClient(String ip, String usr, String pass) throws IOException {
		SSHClient sshClient = new SSHClient();
		sshClient.loadKnownHosts();
		sshClient.addHostKeyVerifier(new PromiscuousVerifier());
		sshClient.connect(ip);
		sshClient.authPassword(usr, pass);
		return sshClient;
	}

	private static void startClients() throws IOException {
		startSSHClients();
		cleanLogs();
		clientsEngines = new Thread[clientsSSH.length];
		for (int i = 0; i < clientsSSH.length; i++) {
			ClientEngine engine = new ClientEngine(clientsData[i].id, clientsData[i].type, numAccesses, clientsSSH[i],
					serverIP, registryPort);
			clientsEngines[i] = new Thread(engine);
			clientsEngines[i].start();
		}
	}

	private static void startSSHClients() throws IOException {
		clientsSSH = new SSHClient[clientsData.length];
		for (int i = 0; i < clientsSSH.length; i++) {
			clientsSSH[i] = new SSHClient();
			clientsSSH[i].addHostKeyVerifier(new PromiscuousVerifier());
			clientsSSH[i].connect(clientsData[i].ip);
			clientsSSH[i].authPassword(clientsData[i].usr, clientsData[i].pass);
		}
	}

	private static void joinClients() throws InterruptedException {
		for (int i = 0; i < clientsEngines.length; i++) {
			clientsEngines[i].join();
		}
	}

	private static void cleanLogs() throws ConnectionException, TransportException {
		for (int i = 0; i < clientsSSH.length; i++) {
			Session rmSession = clientsSSH[i].startSession();
			rmSession.exec("cd BClient; rm log*;");
			rmSession.close();
		}
	}

	public static void main(String[] args) {

		try {
			// Load properties files
			loadProp();
			serverSSH = getSSHClient(serverIP, serverUsername, serverPassword);
			startRMIRegProcess();
			startServerProcess();

			startClients();

			joinClients();

			closeRMIRegProcess();
			closeServerProcess();
			serverSSH.disconnect();
			serverSSH.close();
		} catch (FileNotFoundException fnfe) {
			System.err.println("system.properties file was not found. Program will terminate.");
			fnfe.printStackTrace();
		} catch (RemoteException re) {
			System.err.println("Remote Exception Occurred. Program will terminate.");
			re.printStackTrace();
		} catch (IOException ioe) {
			System.err.println("Cannot load system.properties file. Program will terminate.");
			ioe.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
