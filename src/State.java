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
public class State {
	boolean nothing, stench, glitter, breeze, border, visited, ok,
		pit, wumpus;
	final boolean states[] = {breeze, stench, glitter};
	int wumpusP = 0, pitP = 0, time, value;
	
	Position position;
	
	/**
	 * Constructor
	 */
	State(Position position, boolean visited) {
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
		sb.append("Current state "+time+"is ");
		for(int i = 8; i >= 1; i++) {
			switch(i) {
			case 1:
				if(stench) sb.append("STENCH");
			case 2:
				if(glitter) sb.append("GLITTER");
			case 3:
				if(breeze) sb.append("BREEZE");
			case 4:
				if(stench && glitter) sb.append("STENCH + GLITTER");
			case 5:
				if(stench && breeze) sb.append("STENCH + BREEZE");
			case 6:
				if(breeze && glitter) sb.append("BREEZE + GLITTER");
			case 7:
				if(stench && glitter && breeze) sb.append("STENCH + BREEZE + GLITTER");
			case 8:
				if(nothing) sb.append("OK");
				return sb.toString();
			}
		}
		return sb.toString();
	}
}