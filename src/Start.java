import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;
import java.util.Scanner;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.ConnectionException;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

public class Start {
	
	private static String registryIP;
	private static String registryPort;
	private static String registryUsername;
	private static String registryPassword;
	private static String serverIP;
	private static int serverPort;
	private static String serverJarPath;
	private static String clientJarPath; 
	private static String serverUsername;
	private static String serverPassword;
	private static String clientUsername;
	private static String clientPassword;
	private static String objectName;
	private static int numReaders;
	private static String [] readersIPs;
	private static int numWriters;
	private static String [] writersIPs;
	private static int numAccesses;
	private static SSHClient rmiRegSSH;
	private static SSHClient serverSSH;
	private static final String SERVERNAME = "NewsBulletinServer";
	
	private static void loadProp() throws IOException {
		Properties prop = new Properties();
		InputStream input = new FileInputStream("system.properties");
		prop.load(input);
		registryIP = prop.getProperty("RW.registry");
		registryPort = prop.getProperty("RW.registry.port");
		registryUsername = prop.getProperty("RW.registry.username");
		registryPassword = prop.getProperty("RW.registry.password");
		serverIP = prop.getProperty("RW.server");
		serverPort = Integer.parseInt(prop.getProperty("RW.server.port"));
		serverUsername = prop.getProperty("RW.server.username");
		serverPassword = prop.getProperty("RW.server.password");
		clientUsername = prop.getProperty("RW.client.username");
		clientPassword = prop.getProperty("RW.client.password");
		serverJarPath = prop.getProperty("RW.server.jar");
		clientJarPath = prop.getProperty("RW.client.jar"); 
		objectName = prop.getProperty("RW.object.name");
		numReaders = Integer.parseInt(prop.getProperty("RW.numberOfReaders"));
		
		readersIPs = new String [numReaders];
		for (int i = 0; i < numReaders; i++) {
			String prop_str = "RW.reader" + new Integer(i).toString();
			readersIPs[i] = prop.getProperty(prop_str);
		}
		
		numWriters = Integer.parseInt(prop.getProperty("RW.numberOfWriters"));
		writersIPs = new String [numWriters];	
		for (int i = 0; i < numWriters; i++) {
			String prop_str = "RW.writer" + new Integer(i).toString();
			writersIPs[i] = prop.getProperty(prop_str);
		}
		numAccesses = Integer.parseInt(prop.getProperty("RW.numberOfAccesses"));
	}
	
	
	private static void printConfig() {
		System.out.println("Server IP: " + serverIP);
		System.out.println("Server Port: " + serverPort);
		System.out.println("Readers (" + numReaders + "): ");
		for (int i = 0; i < readersIPs.length; i++) {
			System.out.println("\t" + readersIPs[i]);
		}
		System.out.println("Writers (" + numWriters + "): ");
		for (int i = 0; i < writersIPs.length; i++) {
			System.out.println("\t" + writersIPs[i]);
		}
		System.out.println("Number Of Accesses: " + numAccesses);
	}
	
	private static String SSHExecuteReturn(Session s, String cmdLine) throws IOException{
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
		Session rmiSession = rmiRegSSH.startSession();
		rmiSession.exec("cd " + serverJarPath + " && rmiregistry " + registryPort + "&");
		rmiSession.close();
	}
	
	private static void closeRMIRegProcess() throws IOException {
		//TODO
		Session rmiSession = rmiRegSSH.startSession();
		rmiSession.exec("fuser -k " + registryPort + "/tcp");
		rmiSession.close();
		rmiRegSSH.disconnect();
		rmiRegSSH.close();
	}
	
	private static void startServerProcess() throws IOException {
		Session serverSession = serverSSH.startSession();
		serverSession.exec("cd " + serverJarPath +" && "+"java -jar Server.jar " + serverPort + " " + objectName +" "+ serverIP +" "+serverPort+ " &");
		System.out.println("after server");
		serverSession.close();
	}
	
	private static void closeServerProcess() throws IOException {
		//TODO
		Session serverSession = serverSSH.startSession();
		serverSession.exec("fuser -k " + serverPort + "/tcp");
		serverSession.close();
		serverSSH.disconnect();
		serverSSH.close();
	}
	
	private static SSHClient getSSHClient(String ip, String usr, String pass) throws IOException {
		SSHClient sshClient = new SSHClient();
		sshClient.loadKnownHosts();
		sshClient.addHostKeyVerifier(new PromiscuousVerifier());
		sshClient.connect(ip);
		sshClient.authPassword(usr, pass);
		return sshClient;
	}
	
	public static void main(String[] args) {
		
		try{
			// Load properties files
			loadProp();
			rmiRegSSH = getSSHClient(registryIP, registryUsername, registryPassword);
			startRMIRegProcess();
			
			serverSSH = getSSHClient(serverIP, serverUsername, serverPassword);
			startServerProcess();
			
			
			closeRMIRegProcess();
			closeServerProcess();
		} catch(FileNotFoundException fnfe) {
			System.err.println("system.properties file was not found. Program will terminate.");
			fnfe.printStackTrace();
		} catch (RemoteException re) {
			System.err.println("Remote Exception Occurred. Program will terminate.");
			re.printStackTrace();
		} catch(IOException ioe) {
			System.err.println("Cannot load system.properties file. Program will terminate.");
			ioe.printStackTrace();
		}

	}

}
