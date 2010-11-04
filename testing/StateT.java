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
	
	private boolean border, stench, glitter, breeze, visited, pit, wumpus;
	private int wumpusP = 0, pitP = 0, value;

	private boolean ok = (!pit || !wumpus || wumpusP <= 0 || pitP <= 0) ? true : false; 
	
	private boolean states[] = {border, breeze, stench, glitter, ok};
	
	Position position;
	
	String status = null;
	
	/**
	 * Constructor
	 */
	StateT(Position position, boolean visited) {
		this.position = position;
		this.visited = visited;
	}
	
	public void setStates(boolean list[]) { 
		states = list; 
		
		System.out.print("{");
		for(boolean b : states)
			System.out.print(b+", ");
		System.out.println("}");
		
	}
	
	public void setStatus() {
		StringBuilder sb = new StringBuilder();
		if(breeze) { sb.append("B"); }
		if(stench) { sb.append("S"); }
		if(glitter) { sb.append("G"); }
		if(pit) { sb.append("P("+pitP+")"); }
		if(wumpus) { sb.append("W("+wumpusP+")"); }
		if(border) { sb.append("X"); }
		if(sb.length() == 0) sb.append("0");
		status = sb.toString();
	}
	
	public String getStatus() { return status; }
	public boolean[] getStates() { return states; }
	
	public void setOk() {states[4] = true; pit = wumpus = false; setStatus(); }
	
	public boolean isOk() { return states[4]; } 
	
	public boolean isGlittery() { return states[3]; }
	
	public boolean isBorder() { return states[0]; }
	
	public boolean isBreezy() { return states[1]; }
	
	public boolean isSmelly() { return states[2]; }
	
	public boolean isPit() { return pit; }
	
	public boolean isWumpus() { return wumpus; }
	
	public int getPitPossibility() { return pitP; }
	
	public int getWumpusPossibility() { return wumpusP; }
	
	public void incrementPit() { pitP += 1; }
	
	public void incrementWumpus() { wumpusP += 1; }
	
	/**
	 * The state is the wumpus home
	 */
	public void setWumpus() { wumpus = true; ok = pit = false; setStatus(); }
	
	/**
	 * The state is a pit
	 */
	public void setPit() { pit = true; ok = wumpus = false; setStatus(); }
	
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
	 * State is visited
	 */
	public void setVisited() { visited = true; }
	
	/**
	 * State is out of range
	 */
	public void setBorder() { border = true; }
	
	@Override
	public String toString() {
		int x = position.getX(), y = position.getY();
		StringBuilder sb = new StringBuilder();
		sb.append("P["+x+","+y+"] status is ");
		if(visited) sb.append("("+value+")");
		if(breeze) {
			sb.append(" BREEZE ");
		}
		if(stench) {
			sb.append(" STENCH");
		}
		if(glitter) {
			sb.append(" GLITTER ");
		}
		if(pit) {
			sb.append(" PIT ");
		}
		if(wumpus) {
			sb.append(" WUMPUS ");
		}
		if(pitP > 0) sb.append(pitP); 
		if(wumpusP > 0) sb.append(wumpusP);
		return sb.toString();
	}
}