import java.util.BitSet;
import java.util.EnumSet;

/**
 * Class for representing every square that is 
 * present in the game environment. Every square 
 * either is ok, stenchy, glittery or breezy or
 * a combination of the last three. The square might
 * contain the Wumpus or a pit.
 * 
 *  Wumpus and pit represents by an integer value ranging
 *  from 0 to 4, depending with how big a certainty a
 *  square contains the Wumpus or a Pit. A value of 4 
 *  indictes that it, with 100 percent certainty, is a
 *  Wumpus or pit in that square. Dependning on whether
 *  corner or wall position, 2 or 3 is likely to contain
 *  either of them.
 *  
 * @author a0075840a and a0075885l
 *
 */
public class StateT {
	//EnumSet<StatusT> status; //= EnumSet.of(StatusT.BORDER, StatusT.GLITTER);
	private class Status {
		private final boolean STATUS[] = new boolean[5];
		private final int LENGTH = STATUS.length;
		
		Status() {}
		
		public void set(byte list[]) {
			for(int i = 0; i < list.length; i++)
				if(list[i] == 1) STATUS[i] = true;
		}
		
		public void and(byte list[]) {
			for(int i = 0; i < list.length; i++)
				if(list[i] == 1 && STATUS[i]) {
					STATUS[i] = true;
				} else {
					STATUS[i] = false;
				}
		}
		
		public void or(byte list[]) {
			for(int i = 0; i < list.length; i++)
				if(list[i] == 1 || STATUS[i]) {
					STATUS[i] = true;
				} else {
					STATUS[i] = false;
				}
		}
		
		public boolean contains(byte list[]) { 
			for(int i = 0; i < list.length; i++) {
				if(!STATUS[i]) return false;
			}
			return true;
		}
	
		public void setOk(byte list[]) {
			for(int i = 2; i < LENGTH; i++) {
				STATUS[i] = true;
			}
		}
		
		public boolean isOk() {
			for(int i = 0; i < LENGTH; i++) {
				if(i < 2 && STATUS[i]) {
					return false;
				} else {
					if(!STATUS[i]) return false;
				}
			}
			return true;
		}
	}
	
	boolean nothing, stench, glitter, breeze, border, visited, ok,
		pit, wumpus;
	final boolean states[] = {nothing, border, breeze, stench, glitter};
	int wumpusP = 0, pitP = 0, time, value;
	Status status;
	Position position;
	
	/**
	 * Constructor
	 */
	StateT(Position position, boolean visited) {
		this.position = position;
		this.visited = visited;
		status = new Status();
	}
	
	public void setStatus(byte flag[]) { status.set(flag); }
	
	public void or(byte flag[]) {status.or(flag); }
	
	public void and(byte flag[]) {status.and(flag); }
	
	public void setOk(byte flag[]) {status.setOk(flag); }
	
	public boolean isOk() { return status.isOk(); } 
	
	public boolean contains(byte flag[]) { return status.contains(flag); }
	
	
	/**
	 * The state is ok
	 */
	public void setNothing() { nothing = ok = visited = true; }
	
	/**
	 * The state is a pit
	 */
	public void setPit() { pit = true; nothing = ok = visited = wumpus = false; }
	
	/**
	 * Set the status value
	 * @param value status
	 */
	public void setValue(int value) { this.value = value; }
	
	/**
	 * Returns the status value
	 * @return status value
	 */
	public int getValue() { return value; }
	
	/**
	 * The state is the wumpus home
	 */
	public void setWumpus() { wumpus = true; nothing = ok = visited = pit = false; }
	
	/**
	 * State is visited
	 */
	public void setVisited() { visited = true; }
	
	/**
	 * State is out of range
	 */
	public void setBorder() { border = true; }
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("P["+position.getX()+","+position.getY()+"] status is ");
		//for(StatusT st : status) 
		//	if(value == st.getStatus()) sb.append(st+"("+st.getStatus()+")");
		
		return sb.toString();
	}
}