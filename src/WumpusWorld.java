/**
 * 
 * @author a0075840a and a0075885l
 *
 */
public class WumpusWorld {
	final State GRID[][] = new State[7][7];
	final int OK = 0, BREEZE = 1, STENCH, GLITTER, WUMPUS, PIT, STENCH_BREEZE, 
		STENCH_GLITTER, GLITTER_BREEZE, STENCH_GLITTER_BREEZE, OUT_OF_RANGE;
	
	private final static class State {
		boolean OK = false, STENCH, GLITTER, BREEZE;
		int WUMPUS = 0, PIT = 0;
	}
	
	WumpusWorld() {
		/*
		 *  Begin with calibrating the light sensors for the different colours
		 *  representing ok, breeze, smelly, etc
		 */
		int shadesValue[] = calibrateLigthSensor();
		
	}
	
	
}
