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
	AgentTesting agent;
	
	public KnowledgeBaseTest(AgentTesting agent) {
		this.agent = agent; 
	}
	
	/**
	 * Checks if two squares are adjacent or not. 
	 * A square [i,j] is adjacent to [x,y] if, for
	 * all x,y,i,j, it holds that |x-i| == 1
	 * xor |y-j| == 1. The xor states the they are
	 * "purely" adjacent.
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
	public void setOk(StateT state) {
		Position position = state.position;
		
		for(int[] pos : AgentTesting.ADJACENT) {
			Position newPosition = position.newPosition(pos[0], pos[1]);
			StateT adjacent = new StateT(newPosition, false);
			
			if(!agent.explored(newPosition)) {
				adjacent.setOk();
				agent.addState(newPosition, adjacent);
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
	 * @param state current
	 */
	public void setPitPossibility(StateT state) {
		Position position = state.position, newPosition;
		StateT adjacent = null;
		
		for(int[] pos : AgentTesting.ADJACENT) {
			newPosition = position.newPosition(pos[0], pos[1]);
			
			/*
			 * If the adjacent state is not in our knowledge
			 * base, create a new state for the position
			 */
			adjacent = agent.getState(newPosition);
			if(adjacent == null ) adjacent = new StateT(newPosition, false);
			
			if(!agent.explored(newPosition)) {
				adjacent.incrementPit();
				adjacent.setPit();
				agent.addState(newPosition, adjacent);
				print(adjacent);
				
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
	 * @param state TODO
	 */
	public void setWumpusPossibility(StateT state) {
		Position position = state.position, newPosition;
		StateT adjacent = null;
		
		for(int[] pos : AgentTesting.ADJACENT) {
			newPosition = position.newPosition(pos[0], pos[1]);
			adjacent = new StateT(newPosition, false);
			
			if(!agent.explored(newPosition)) {
				adjacent.incrementWumpus();
				adjacent.setWumpus();
				agent.addState(newPosition, adjacent);
				print(adjacent);
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
		
		for(int[] pos : AgentTesting.ADJACENT) {
			Position newPosition = position.newPosition(pos[0], pos[1]);
			StateT state = new StateT(newPosition, false);
			
			if(agent.explored(newPosition) && adjacent(position, state.position) && state.getPitPossibility() > 0) {
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
		
		for(int[] pos : AgentTesting.ADJACENT) {
			Position newPosition = position.newPosition(pos[0], pos[1]);
			StateT state = new StateT(newPosition, false);
			
			if(agent.explored(newPosition) && adjacent(position, state.position) && state.getWumpusPossibility() > 0) {
				count += 1;
			}	
		}
		
		if(count > 1) return true;
		return false;
	}
	
	private void print(StateT state) {
		if(AgentTesting.DEBUG) System.out.printf("\tInfered @ %s\n",state.toString());
	}
}