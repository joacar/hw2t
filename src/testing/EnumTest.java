import java.util.EnumSet;


public class EnumTest {
	enum Status {BORDER, NOTHING, BREEZE, STENCH, GLITTER};
	static EnumSet<Status> status = EnumSet.allOf(Status.class);
	
	private final static EnumSet<Status> MASK_BSG = EnumSet.range(Status.BREEZE,Status.GLITTER);
	private final static EnumSet<Status> MASK_B = EnumSet.of(Status.BREEZE);
	private final static EnumSet<Status> MASK_N = EnumSet.complementOf(status);
	
	public static void main(String args[]) {
		System.out.println(status.complementOf(MASK_B));
		System.out.println("MASK_BSG : "+status.retainAll(MASK_BSG));
		System.out.println(status);
		for(Status s : status)
			status.remove(s);
		System.out.println(status);
	}
}
