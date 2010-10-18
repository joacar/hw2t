import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;

/**
 * 
 * @author a0075840a and a0075885l
 *
 */
public class WumpusWorld {
	private HashMap<Agent.Position, State> exploredStates;
	private ArrayList<HashSet<State>> adjacentList;

	private final int BREEZE, STENCH, GLITTER, STENCH_BREEZE, OK, 
	GLITTER_STENCH, GLITTER_BREEZE, STENCH_GLITTER_BREEZE;

	public class State {
		boolean ok, stench, glitter, breeze;
		final boolean states[] = {ok, stench, glitter, breeze};
		int wumpus = 0, pit = 0, time;

		/**
		 * Constructor
		 * 
		 * @param time the square was found
		 * 		(discrete counter)
		 */
		State(int time) {
			this.time = time;
		}

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
					if(ok) sb.append("OK");
					return sb.toString();
				}
			}

			return sb.toString();
		}
	}

	WumpusWorld(LightSensor ls) {
		/*
		 *  Begin with calibrating the light sensors for the different colours
		 *  representing ok, breeze, stench, etc
		 */
		int tempLightValue = 0;

		System.out.println("Place sensor above pure black...");
		Button.waitForPress();
		tempLightValue = ls.getLightValue();
		OK = tempLightValue;

		System.out.println("Place sensor above pure white...");
		Button.waitForPress();
		ls.calibrateHigh();

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

		adjacentList = new ArrayList<HashSet<State>>();
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
	public State newState(int lightValue, int time) {
		HashSet<State> adjacentVertices = new HashSet<State>();

		State state = new State(time);

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
		} else if(OK == lightValue) {		
			state.ok = true;
		} else {
			// else what ?
		}
		
		adjacentVertices.add(state);
		
		adjacentList.add(time, adjacentVertices);

		return state;
	}
	
	/**
	 * Returns an iterator 
	 * 
	 * @param state
	 * @return
	 */
	public Iterator<State> getAdjacentStates(State state) {
		int time = state.time;
		HashSet<State> adjacentVertices = adjacentList.get(time);
		
		return adjacentVertices.iterator();
	}


}
