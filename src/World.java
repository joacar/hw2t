import java.util.HashMap;


public interface World {
	/*
	 *  Each world has a hash map representing the world. The 
	 *  position is used as a key for each value state and this
	 *  ensures that each state is mapped to one unique position
	 */
	HashMap<Position, State> world = new HashMap<Position, State>();
	
	/**
	 * Returns the state corresponding to the position
	 * 
	 * @param position Position key
	 * @return the state corresponding to the position
	 */
	State getState(Position position);
	
	/**
	 * Returns true if the position is in the world
	 * 
	 * @param position Position key
	 * @return true iff exists, false otherwise
	 */
	public boolean stateExists(Position position);
}
