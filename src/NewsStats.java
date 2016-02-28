public class NewsStats {
	private int sSeq;
	private int rSeq;
	private String oVal;
	private String id;
	private int rNum;
	
	public NewsStats(int sSeq, int rSeq, String oVal, String id, int rNum) {
		this.sSeq = sSeq;
		this.rSeq = rSeq;
		this.oVal = oVal;
		this.id = id;
		this.rNum = rNum;
	}

	public int getsSeq() {
		return sSeq;
	}

	public int getrSeq() {
		return rSeq;
	}

	public String getoVal() {
		return oVal;
	}

	public String getId() {
		return id;
	}

	public int getrNum() {
		return rNum;
	}
}