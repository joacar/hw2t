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

	public int BREEZE, STENCH, GLITTER, STENCH_BREEZE, NOTHING, OUT_OF_RANGE,
	GLITTER_STENCH, GLITTER_BREEZE, STENCH_GLITTER_BREEZE;
	// The limits of our world in the beginning of our exploration
	public int xLowerLimit = Integer.MIN_VALUE, xUpperLimit = Integer.MAX_VALUE,
	yLowerLimit = Integer.MIN_VALUE, yUpperLimit = Integer.MAX_VALUE;

	WumpusWorld(LightSensor ls) {
		/*
		 *  Begin with calibrating the light sensors for the different shades
		 *  between black and white representing ok, breeze, stench, etc
		 */
		int tempLightValue = 0;

		System.out.println("Place sensor above pure black...");
		Button.waitForPress();
		ls.calibrateLow();
		tempLightValue = ls.getLightValue();
		OUT_OF_RANGE = tempLightValue;

		System.out.println("Place sensor above pure white...");
		Button.waitForPress();
		ls.calibrateHigh();
		tempLightValue = ls.getLightValue();
		NOTHING = tempLightValue;

		tempLightValue = lightValue(ls, "GLITTER");
		GLITTER = tempLightValue;

		tempLightValue = lightValue(ls, "BREEZE");
		BREEZE = tempLightValue;

		tempLightValue = lightValue(ls, "STENCH");
		STENCH = tempLightValue;

		tempLightValue = lightValue(ls, "STENCH + BREEZE");
		STENCH_BREEZE = tempLightValue;

		tempLightValue = lightValue(ls, "STENCH + GLITTER");
		GLITTER_STENCH = tempLightValue;

		tempLightValue = lightValue(ls, "GLITTER + BREEZE");
		GLITTER_BREEZE = tempLightValue;

		tempLightValue = lightValue(ls, "STENCH + GLITTER + BREEZE");
		STENCH_GLITTER_BREEZE = tempLightValue;
	}

	/**
	 * Did not want to put any more code duplication 
	 * in constructor than already there
	 * 
	 * @param ls LightSensor ls
	 * @param text String text to show
	 * @return int value representing light
	 */
	private int lightValue(LightSensor ls, String text) {	
		System.out.println("Place sensor above shade for "+text);
		Button.waitForPress();
		return ls.getLightValue();
	}

	/**
	 * Creates the newly discovered square and returns it
	 * 
	 * @param lightValue ligth value of square
	 * @return the square in form of a State
	 */
	public State newState(Position position, int lightValue) {
		// Black, go back and update world limits!
		if(lightValue == OUT_OF_RANGE) {
			return null;
		}
		
		State state = new State();
		state.position = position;		// Position of this square
		
		// This must be here as it is implemented now
		// Set variables that corresponds to the light value 
		if(BREEZE == lightValue) {
			state.breeze = true;
		} else if(STENCH == lightValue) {
			state.stench = true;
		} else if(GLITTER == lightValue) {
			state.glitter = true;
		} else if(STENCH_BREEZE == lightValue) {
			state.stench = state.breeze = true;
		} else if(GLITTER_BREEZE == lightValue) {
			state.glitter = state.breeze = true;
		} else if(GLITTER_STENCH == lightValue) {
			state.glitter = state.stench = true; 
		} else if(STENCH_GLITTER_BREEZE == lightValue) {
			state.stench = state.glitter = state.breeze = true;
		} else if(NOTHING == lightValue) {		
			state.nothing = true;
		} else {
			// Black, go back and notice the position and update limits!
			return null;
		}
		
		states.put(position, state);
		state.status = lightValue;
		
		// Add neighbours
		for(int[] pos : Agent.VALID_MOVES) {
			int x = position.x + pos[0];
			int y = position.y + pos[1];
			
			Position newPosition = new Position(x, y);
			// Check wall condition
			if(wallCondition(x, y) && !states.containsKey(newPosition)) {
				states.put(newPosition, new State());
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
		final int[][] adjacentLocations = {{-1,0}, {1,0}, {0,-1}, {0,1}};
		State[] adjacentStates = new State[3];
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
}