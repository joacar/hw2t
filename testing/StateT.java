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
	
	private class Status {
		private int status = 16;
		
		Status() {}
		
		public int status() { return status; }
		
		public void and(byte flag) { status = status & flag; }
		
		public void or(byte flag) { status = status | flag;	}
		
		public boolean contains(byte flag) { return ((status | flag) > 0) ? true : false;}
	
		public void setOk() { status = 16; }
		
		public boolean isOk() { return ((status & 4) > 0) ? true : false; }
		
		public boolean isGold() { return ((status & 8) == 1) ? true : false; }
	}
	
	boolean border, stench, glitter, breeze, visited, ok,
		pit, wumpus;
	private boolean states[] = {border, breeze, stench, glitter, ok};
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
	
	public void setStates(boolean list[]) { states = list; }
	
	public boolean[] getStates() { return states; }
	
	public void setOk() {states[4] = true; }
	
	public boolean isOk() { return states[4]; } 
	
	public boolean isGold() { return states[3]; }
	
	/** ===== SHIT ======= **/
	public boolean contains(byte flag) { return status.contains(flag); }
	
	public int getStatus() { return status.status; }
	
	public void or(byte flag) {status.or(flag); }
	
	public void and(byte flag) {status.and(flag); }
	
	
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
	public void setWumpus() { wumpus = true; ok = visited = pit = false; }
	
	/**
	 * The state is a pit
	 */
	public void setPit() { pit = true; ok = visited = wumpus = false; }
	
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