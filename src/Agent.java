import java.util.HashSet;
import java.util.Queue;

import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.SoundSensor;
import lejos.robotics.Movement;

/**
 * The AI for the classic and legendary game Wumpus world!
 * 
 * The AI starts with percieving to see if anything new about
 * the world can be noticed. If so, it takes that new
 * information under considerations and via logic reasoning
 * and a knowledge base decides what the most appropriate
 * action to that percept and logic conclusion is. 
 * 
 * The sequence described above is looped over and over
 * until it reaches it final state. That is the gold is
 * collected and the robot is back to its starting location.
 * 
 * @author a0075840a and a0075885l
 *
 */
public class Agent {
	private WumpusWorld wumpusWorld;
	private LightSensor light;
	private SoundSensor sound;
	private Movement movement;
	private Motor motor;
	
	private Queue<Position> queue;
	private HashSet<Position> visitedStates;
	
	private boolean wumpusAlive = true, fetchedGold = false;

	public static final int[][] VALID_MOVES = {
		{-1,-1}, {1,1}, {1,-1}, {-1,1},	// diagonal moves
		{-1,0}, {1,0},					// horizontal moves
		{0,-1}, {0,1}, 					// vertical moves
	};

	public static void main(String args[]) {
		new Agent();
	}

	Agent() {
		light = new LightSensor(SensorPort.S3, true);
		sound = new SoundSensor(SensorPort.S2);

		wumpusWorld = new WumpusWorld(light);
		
		queue.add(new Position(-1,0));
		Position prevPosition = null, curPosition = null;
		
		// Start our journey from start location (-1,0)
		do {
			curPosition = queue.poll();
			explore(queue.poll(), prevPosition);
			prevPosition = curPosition;
		} while(!queue.isEmpty());
		
		// The gold is collected! It is time for the great escape!
		do {
			// getTheFuckOutOfThisPlace()
		} while(true);
	}

	private void explore(Position curPosition, Position prevPosition) {
		// Step 1. Percept
		State state = percept(curPosition);

		/*
		 *  If we went outside the board, the state returned
		 *  will be null. Update the world limits relative to
		 *  our position accordingly so we wont make this
		 *  misstake again and choose next move available
		 *  from the queue
		 */
		if(state == null) {
			updateLimits(curPosition, prevPosition);
			// go back to where we came from
			return;
		}

		// Step 2. Logically infer what to do next 
		infer(state);

		// Step 3. Move out from information gathered above
		makeMove(state);
		
	}
	
	private void makeMove(State state) {
		State[] adjacentStates = wumpusWorld.getAdjacentStates(state);
		
		for(State s : adjacentStates) {
			// The square is safe
			if(s.pit == -1 && s.wumpus == -1) {
				if(!visitedStates.contains(s.position)) {
					queue.add(s.position);
					visitedStates.add(s.position);
				}
			}
		}
		
		

	}

	/**
	 * From the current state, using our knowledge base, we
	 * are able to draw some conclusions about the adjacent
	 * squares and make new statements about the unknown
	 * world surrounding our pour agent.
	 * 
	 * @param state State currently in
	 */
	private void infer(State state) {
		if(state.nothing) {	// !state.breeze && !state.stench
			setOk(state);
		} else {
			int statusInt = 0;

			for(int i = 1; i < 4; i++)  
				if(state.states[i])	
					statusInt += i;

			if(statusInt > 3) statusInt += 1;

			switch(statusInt) {
			case 1:		// breezy
				setPitPossibility(state);
			case 2:		// smelly
				setWumpusPossibility(state);
			case 3: 	// glittery
				fetchedGold = true;
			case 4: 	// breezy and smelly
				setPitPossibility(state);
				setWumpusPossibility(state);
			case 5:		// breezy and glittery
				setPitPossibility(state);
				fetchedGold = true;
			case 6:		// smelly and glittery
				setWumpusPossibility(state);
				fetchedGold = true;
			case 7:		// breezy, smelly and glittery
				setPitPossibility(state);
				setWumpusPossibility(state);
				fetchedGold = true;
			}

			if(fetchedGold) {
				fetchedTheGold();
			}
		}
	}

	/**
	 * If we move outside the world, we would
	 * like the new at the boundaries. Here
	 * we find them and update are world accordingly
	 * so that we can wonder around without straying
	 * out of course... Well, at least at one side of
	 * the world.
	 * 
	 *
	 * ATTENTION! Might have some trouble if we go out
	 * of range in a diagonal move.. ATTENTION!
	 * 
	 * @param curPosition current position
	 * @param prevPosition previous position
	 */
	private void updateLimits(Position curPosition, Position prevPosition) {
		int diff = prevPosition.x - curPosition.x;
		if(diff != 0) {
			if(diff > 0) {	// Movement "rightwards"
				wumpusWorld.xLowerLimit = prevPosition.x;
				return;
			} else {		// Movement "leftwards"
				wumpusWorld.xUpperLimit = prevPosition.x;
				return;
			}
		} else {
			diff = prevPosition.y - curPosition.y;
			if(diff > 0) {	// Movement "upwards"
				wumpusWorld.yLowerLimit = prevPosition.y;
				return;
			} else {		// Movement "downwards"
				wumpusWorld.yUpperLimit = prevPosition.y;
				return;
			}
		}
	}

	/**
	 * Just a functions that indicates that we have found and
	 * retrieved the gold
	 */
	private void fetchedTheGold() {
		System.out.println("I'm rich! Now it's time"+
		" to get the f*ck out of this world!");
		Sound.beep();
	}

	/**
	 * Perceives data about the real world trough a ligth sensor
	 * 
	 * @return State newly discovered
	 */
	private State percept(Position position) {
		// Read in the value of the current square
		int lightValue = light.readValue();

		// Create a state of newly discovered square 
		return wumpusWorld.newState(position, lightValue);
	}

	/**
	 * Checks if two squares are adjacent or not
	 * 
	 * @param s1 State s1
	 * @param s2 State s2
	 * @return true iff adjacent
	 */
	private boolean adjacent(State s1, State s2) { 
		/*
		 * A square [i,j] is adjacent to [x,y] if, (fA x,y,i,j) |x-i| or |y-j|
		 * Formally: (fA i,j,x,y) Adjacent([i,j], [x,y]) <=> (|x-i| or |y-j|) 
		 */
		int x = s1.position.x, y = s1.position.y, i = s2.position.x, j = s2.position.y;

		if(x+1 == i && y == j ||  x-1 == i && y == j 
				|| x == i && y+1 == j || x == i && y-1 == j) return true;

		return false;
	}

	/**
	 * If the state we are currently in is neither
	 * breezy nor smelly, then we can conclude that
	 * the adjacent squares are free from the wumpus
	 * or the pit
	 * 
	 * @param state State currently in
	 */
	private void setOk(State state) {
		State[] adjacentStates = wumpusWorld.getAdjacentStates(state);

		for(State s : adjacentStates) {
			s.pit = -1;
			s.wumpus = -1;
		}
	}

	/**
	 * If the state we currently are in is breezy,
	 * then we can conclude that any of its adjacent
	 * squares can be a pit
	 * 
	 * @param state State currently in
	 */
	private void setPitPossibility(State state) {
		/*
		 * A square [x,y] is breezy if adjacent to a pit ( and [x,y] might
		 * be a pit of some adjacent squares are breezy. )
		 * Formally: (fA s) Breezy(s) (<=)=> (tE r) Adjacent(r,s) & Pit(r)
		 */
		State[] adjacentStates = wumpusWorld.getAdjacentStates(state);

		for(State s : adjacentStates) {
			// If square is not ok and they are adjacent (double check..), it might be a pit
			if(!s.nothing && adjacent(state, s)) {
				s.pit += 1;		// Increase the chance that there is a pit
			}
		}		
	}

	/** If the state we currently are in is smelly,
	 * then we can conclude that any of its adjacent
	 * squares can be the wumpus
	 * 
	 * @param state State currently in
	 */
	private void setWumpusPossibility(State state) {
		/*
		 * A square [x,y] is smelly if adjacent to the wumpus ( and [x,y] might
		 * be the wumpus of some adjacent squares are smelly. )
		 * Formally: (fA s) Breezy(s) (<=)=> (tE r) Adjacent(r,s) & Pit(r)
		 */
		State[] adjacentStates = wumpusWorld.getAdjacentStates(state);

		for(State s : adjacentStates) {
			// If square is not ok and they are adjacent (double check..), it might be the wumpus
			if(!s.nothing && adjacent(state, s)) {
				s.wumpus += 1;		// Increase the chance that there is a wumpus in that square
			}
		}		
	}

	/**
	 * Just a stupid welcome message
	 * 
	 * @throws InterruptedException
	 */
	private void welcomeMsg() throws InterruptedException {
		System.out.println("What took you so long?!\n"+
		"Been waiting forever for you to bring me back to life");		
		System.out.println("Configuring system.");
		Thread.sleep(10);
		System.out.print(".");
		Thread.sleep(10);
		System.out.print(".");
		Thread.sleep(10);
		System.out.print(".");
		System.out.println("System updated and ready");
	}
}