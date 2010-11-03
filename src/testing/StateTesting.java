import java.util.EnumSet;
import java.util.Set;

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
public class StateTesting {
	public final EnumSet<StatusTesting> status = EnumSet.range(StatusTesting.BREEZE, 
			StatusTesting.GLITTER);
	
	boolean nothing, stench, glitter, breeze, border, visited, ok,
		pit, wumpus;
	final boolean states[] = {breeze, stench, glitter};
	int wumpusP = 0, pitP = 0, time, value;
	
	Position position;
	
	/**
	 * Constructor
	 */
	StateTesting(Position position, boolean visited) {
		this.position = position;
		this.visited = visited;
	}
	
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
		for(StatusTesting st : status) 
			if(value == st.getStatus()) sb.append(st+"("+st.getStatus()+")");
		
		return sb.toString();
	}
}