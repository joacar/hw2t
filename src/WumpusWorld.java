import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;

/**
 * 
 * @author a0075840a and a0075885l
 *
 */
public class WumpusWorld {
	final State GRID[][] = new State[7][7];

	
	private HashMap<Agent.Position, State> exploredStates;
	private ArrayList<HashSet<State>> adjacentList;
	
	final int BREEZE, STENCH, GLITTER, STENCH_BREEZE, OK, 
	GLITTER_STENCH, GLITTER_BREEZE, STENCH_GLITTER_BREEZE;

	private final static class State {
		boolean ok, stench, glitter, breeze;
		boolean visited = false;
		int wumpus = 0, pit = 0;
		final boolean states[] = {ok, stench, glitter, breeze};

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("Current state is ");
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

	private int lightValue(LightSensor ls, String text) {	
		System.out.println("Place sensor above shade for "+text);
		Button.waitForPress();
		return ls.getLightValue();
	}


}
