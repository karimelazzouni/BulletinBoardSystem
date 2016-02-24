import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Properties;

public class Start {
	
	private static String serverIP;
	private static int serverPort;
	private static int numReaders;
	private static String [] readersIPs;
	private static int numWriters;
	private static String [] writersIPs;
	private static int numAccesses;
	private static final String SERVERNAME = "NewsBulletinServer";
	
	private static void load_prop() throws IOException {
		Properties prop = new Properties();
		InputStream input = new FileInputStream("system.properties");
		prop.load(input);
		
		serverIP = prop.getProperty("RW.server");
		serverPort = Integer.parseInt(prop.getProperty("RW.server.port"));
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
	
	@SuppressWarnings("unused")
	private static void print_config() {
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
	
	public static void main(String[] args) {
		
		try{
			// Load properties files
			load_prop();
			
			// Start Server Thread
			Server s = new Server(serverIP, Integer.toString(serverPort), SERVERNAME);
			Thread t = new Thread(s);
			t.run();
			
//			// Start one Client
//			Client c = new Client(serverIP, Integer.toString(serverPort), SERVERNAME);
//			c.initiateClient();
			
			
		} catch(FileNotFoundException fnfe) {
			System.err.println("systems.properties file was not found. Program will terminate.");
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
