import java.util.HashMap;

/**
 * Contains the information we are able to 
 * infer from the given rules and logical
 * relations between for example the pit
 * and that the adjacent squares are breezy
 * 
 * @author joacar
 *
 */
public class KnowledgeBaseTest {	
	public KnowledgeBaseTest() {}
	
	/**
	 * Checks if two squares are adjacent or not. 
	 * A square [i,j] us adjacent to [x,y] if, for
	 * all x,y,i,j, it holds that |x-i| == 1
	 * or |y-j| == 1
	 * 
	 * @param pos1 Position
	 * @param pos2 Position
	 * @return true iff adjacent, false otherwise
	 */
	public boolean adjacent(Position pos1, Position pos2) { 
		int x = pos1.getX(), y = pos1.getY(), i = pos2.getX(), j = pos2.getY();

		if((Math.abs(x-i) ^ Math.abs(y-j)) == 1) return true;
	
		return false;
	}
	
	/**
	 * If the state we are currently in is neither
	 * breezy nor smelly (it is nothing), then we 
	 * can conclude that the adjacent squares are 
	 * free from the either wumpus or the pit or
	 * both
	 * 
	 * @param state State currently in
	 */
	public void setOk(Position position, HashMap<Position, StateTesting> world) {		
		System.out.println("KnowledgeBaseTest.setOk()");
		System.out.println("Current position = "+position);
		for(int[] pos : Agent.VALID_MOVES) {
			Position newPosition = position.newPosition(pos[0], pos[1]);
			System.out.println(newPosition);
			StateTesting state = new StateTesting(newPosition, false);
			
			if(adjacent(position, newPosition) && newPosition != position) {
				world.put(newPosition, state);
				state.ok = true;
			}
		}
	}

	/**
	 * If the state we currently are in is breezy,
	 * then we can conclude that any of its adjacent
	 * squares can be a pit as long as that dont 
	 * violates a nother state condition.
	 * 
	 * If B[x,y] that implies that Pit[i,j] & Adjacent([x,y], [i,j])
	 * 
	 * We cant for sure conclude that there is a pit, we now that
	 * there _might_ be a pit there and hence pit "possibility" 
	 * increases with one.
	 * 
	 * @param position current
	 */
	public void setPitPossibility(Position position) {
		for(int[] pos : Agent.VALID_MOVES) {
			Position newPosition = position.newPosition(pos[0], pos[1]);
			StateTesting state = new StateTesting(newPosition, false);
			
			if(!state.ok && adjacent(position, state.position)) {
				state.pitP += 1;			// Increase the chance that there is a pit
				//setPit(s.position);	// Is there a pit there?
			}
		}
	}

	/**
	 * If the state we currently are in is smelly,
	 * then we can conclude that any of its adjacent
	 * squares can contain the wumpus as long as that 
	 * dont violates another state..
	 * 
	 * If S[x,y] that implies that Wumpus[i,j] & Adjacent([x,y], [i,j])
	 * 
	 * We cant for sure conclude that it is the wumpus home, we now that
	 * it _might_  be and hence Wumpus "possibility" increases with one.
	 * 
	 * @param position current
	 */
	public void setWumpusPossibility(Position position) {
		for(int[] pos : Agent.VALID_MOVES) {
			Position newPosition = position.newPosition(pos[0], pos[1]);
			StateTesting state = new StateTesting(newPosition, false);
			
			if(!state.ok && adjacent(position, state.position)) {
				state.pitP += 1;		// Increase the chance that there is a pit
				//setWumpus(s.position);	// Is it the home of the Wumpus?
			}
		}
	}
	
	/**
	 * Figures out if a square is certaintly a pit and sets the
	 * state flag to true
	 * 
	 * @param position current
	 */
	public boolean setPit(Position position) {
		int count = 0;	// Counts number of adjacent states
		
		for(int[] pos : Agent.VALID_MOVES) {
			Position newPosition = position.newPosition(pos[0], pos[1]);
			StateTesting state = new StateTesting(newPosition, false);
			
			if(state.visited && adjacent(position, state.position) && state.pitP > 0) {
				count += 1;
			}	
		}
		
		if(count > 1) return true;
		return false;
	}
	
	/**
	 * Figures out if a square is certaintly containing the
	 * wumpus and sets the state flag to true
	 * 
	 * @param position current
	 */
	public boolean setWumpus(Position position) {
		int count = 0;	// Counts number of adjacent states
		
		for(int[] pos : Agent.VALID_MOVES) {
			Position newPosition = position.newPosition(pos[0], pos[1]);
			StateTesting state = new StateTesting(newPosition, false);
			
			if(state.visited && adjacent(position, state.position) && state.wumpusP > 0) {
				count += 1;
			}	
		}
		
		if(count > 1) return true;
		return false;
	}
}