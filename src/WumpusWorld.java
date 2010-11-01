import java.util.HashMap;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;

/**
 * 
 * @author a0075840a and a0075885l
 *
 */
public class WumpusWorld {
	private HashMap<Position, State> states;
	
	public int NOTHING, OUT_OF_RANGE,BREEZE, STENCH, GLITTER, STENCH_BREEZE,
	GLITTER_STENCH, GLITTER_BREEZE, STENCH_GLITTER_BREEZE;
	
	public int values[] = {NOTHING, OUT_OF_RANGE,BREEZE, STENCH, GLITTER, STENCH_BREEZE,
			GLITTER_STENCH, GLITTER_BREEZE, STENCH_GLITTER_BREEZE};
	
	// The limits of our world in the beginning of our exploration
	private final int X_LOWER_LIMIT = -1, Y_LOWER_LIMIT = -1;
	public int xLowerLimit = X_LOWER_LIMIT, xUpperLimit = Integer.MAX_VALUE,
	yLowerLimit = Y_LOWER_LIMIT, yUpperLimit = Integer.MAX_VALUE;

	WumpusWorld(int squareValues[]) {
		// Update the square value
		for(int i = 0; i < squareValues.length; i++)
			values[i] = squareValues[i];
	}
	
	/**
	 * Creates the newly discovered square and returns it
	 * 
	 * @param value ligth value of square
	 * @return the square in form of a State
	 */
	public State newState(Position position, int value) {
		// Black, go back and update world limits!
		if(value == OUT_OF_RANGE) {
			return null;
		}
		
		State state = new State(position, true);
		
		// Set variables that corresponds to the light value for the squares
		if(BREEZE == value) {
			state.breeze = true;
		} else if(STENCH == value) {
			state.stench = true;
		} else if(GLITTER == value) {
			state.glitter = true;
		} else if(STENCH_BREEZE == value) {
			state.stench = state.breeze = true;
		} else if(GLITTER_BREEZE == value) {
			state.glitter = state.breeze = true;
		} else if(GLITTER_STENCH == value) {
			state.glitter = state.stench = true; 
		} else if(STENCH_GLITTER_BREEZE == value) {
			state.stench = state.glitter = state.breeze = true;
		} else if(NOTHING == value) {		
			state.nothing = true;
		} else {
			// Black, go back and notice the position and update limits!
			return null;
		}
		
		states.put(position, state);
		state.lightValue = value;
		
		// Add neighbours
		for(int[] pos : Agent.VALID_MOVES) {
			int x = position.x + pos[0];
			int y = position.y + pos[1];
			
			Position newPosition = new Position(x, y);
			// Check that the state is not already added
			if(!states.containsKey(newPosition)) {
				states.put(newPosition, new State(newPosition, false));
			}
		}

		return state;
	}
	
	/**
	 * Checks if the new position has been detectecd
	 * as out of range. 
	 * 
	 * @param x new x coordinate
	 * @param y new y coordinate
	 * @return true iff in valid range
	 */
	public boolean wallCondition(int x, int y) {
		if(x > xLowerLimit && x < xUpperLimit
				&& y > yLowerLimit && y < yUpperLimit) return true;
		return false;
	}
	
	/**
	 * Returns the adjacent states
	 * 
	 * @param state Current state
	 * @return array of adjacent states
	 */
	public State[] getAdjacentStates(State state) {
		final int[][] adjacentLocations = Agent.VALID_MOVES; //{{-1,0}, {1,0}, {0,-1}, {0,1}};
		State[] adjacentStates = new State[8];
		Position position = state.position, newPosition;
		
		for(int i = 0; i < adjacentLocations.length; i++) {
			int x = position.x + adjacentLocations[i][0];
			int y = position.y + adjacentLocations[i][1];
			newPosition = new Position(x, y);
			if(wallCondition(x, y) && states.containsKey(newPosition))
				adjacentStates[i] = states.get(newPosition);
		}
		
		return adjacentStates; 
	}
	
	/**
	 * Returns the state matching the position
	 * @param position
	 * @return state if exists, null otherwise
	 */
	public State getState(Position position) {
		return (states.containsKey(position)) ? states.get(position) : null;
	}
}