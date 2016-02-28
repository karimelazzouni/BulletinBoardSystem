public class ClientData {

	public static final int READER = 0;
	public static final int WRITER = 1;

	private int type;
	private int id;
	private String ip;
	private String usr;
	private String pass;

	public ClientData(int id, int type, String ip, String usr, String pass) {
		this.id = id;
		this.type = type;
		this.ip = ip;
		this.usr = usr;
		this.pass = pass;
	}

	public int getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public String getIp() {
		return ip;
	}

	public String getUsr() {
		return usr;
	}

	public String getPass() {
		return pass;
	}

	@Override
	public String toString() {
		return String.format("(id=%d, type=%s, ip=%s, username=%s)", id, type == READER ? "Reader" : "Writer", ip, usr);
	}
}