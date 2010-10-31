import java.util.HashSet;
import java.util.LinkedList;

import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.SoundSensor;

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
	
	private LinkedList<Position> queue;
	private LinkedList<Position> path;
	private HashSet<Position> visitedStates;
	private HashSet<Position> dangerousStates;
	
	private boolean wumpusAlive = true, fetchedGold = false;
	//private final boolean array[] = new boolean[4];

	public static final int[][] VALID_MOVES = {
		{-1,-1}, {1,1}, {1,-1}, {-1,1},	// diagonal moves
		{-1,0}, {1,0},					// horizontal moves
		{0,-1}, {0,1}, 					// vertical moves
	};
	
	/**
	 * Entry point. No args needed at moment.
	 *  
	 * @param args
	 */
	public static void main(String args[]) {
		new Agent();
	}
	
	/**
	 * Basic constructor
	 */
	private Agent() {
		light = new LightSensor(SensorPort.S3, true);
		sound = new SoundSensor(SensorPort.S2);

		wumpusWorld = new WumpusWorld(light);
		
		Position prevPosition = null, curPosition = null;
		curPosition = new Position(-1,0);
		/**
		 * Here i declare, or states, that the direction 'north' is the
		 * direction with increasing values along the x-axis. East is
		 * the direction with the increasing values along the y-axis.
		 * The direction north-east follows by a diagonal move with
		 * increasing values along both x and y-axis.
		 */
		// {North, West, South, East}
		boolean array[] = {true,false,false,false};
		curPosition.setDirection(array);
		
		queue.add(curPosition);
		
		// Start our journey from start location (-1,0)
		do {
			curPosition = queue.poll();
			int result = explore(curPosition, prevPosition);
			/*
			 * If the current state is not a valid one (explore
			 * returns -1 if thats the case, 1 otherwise), i.e
			 * outside of the world, then we want to go back
			 * to that one and stille keep the previous state
			 * for the state _before_ we went out of range. 
			 * Therefore keeping track of the path and adding
			 * states to the path only when we are sure that
			 * the next state is a valid one.
			 */
			if(result != -1) {
				prevPosition = curPosition;
				path.addFirst(curPosition);
			} else {
				prevPosition = path.peek();
			}
		} while(!queue.isEmpty());
		
		// The gold is collected! It is time for the great escape!
		do {
			// getTheFuckOutOfThisPlace()
		} while(true);
	}

	private int explore(Position curPosition, Position prevPosition) {
		// Step 1. Percept
		State state = percept(curPosition);

		/*
		 *  If we went outside the board, the state returned
		 *  will be null. Update the world limits relative to
		 *  our position accordingly so we wont make this
		 *  misstake again, go back to where we came from
		 *  and figure out the next move.
		 */
		if(state == null) {
			updateLimits(curPosition, prevPosition);
			/*
			 *  Go back to where we came from by rotating 180 degrees
			 *  and then move forward the correct distance.
			 */
			MovementControl.goBack();
			queue.addFirst(prevPosition);
			
			return -1;
		}

		// Step 2. Logically infer the status of adjacent squares 
		infer(state);

		// Step 3. Move out from information gathered above
		makeMove(state);
		
		return 0;
		
	}
	
	private void makeMove(State state) {
		State[] adjacentStates = wumpusWorld.getAdjacentStates(state);
		boolean okStates[] = new boolean[adjacentStates.length];
		boolean goBack = true;
		
		for(int i = 0; i < adjacentStates.length; i++) {
			State s = adjacentStates[0];
			/*
			 * If the square contains neither the pit nor the
			 * wumpus it is safe to move there. 
			 */
			if(s.pit <= 0 && s.wumpus <= 0) {
				if(!visitedStates.contains(s.position)) {
					okStates[i] = true;
					queue.add(s.position);
					goBack = false;
				}
			}
		}
		
		if(goBack) {
			MovementControl.goBack();
		} else {
			
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
			wumpusWorld.xUpperLimit = curPosition.x;
		} else {
			wumpusWorld.yUpperLimit = curPosition.y;
		}
	}
	
	private float heading(Position curPosition, Position prevPosition) {
		int diffX = curPosition.x - prevPosition.x;
		int diffY = curPosition.y - prevPosition.y;
		
		// We have moved diagonally towards increasing values of x- and y-axis
		if(diffX > 0 && diffY > 0) {	
			
		} 
		// We have moved diagonally towards decreasing values of x and increasing of y
		else if(diffX < 0 && diffY > 0) {
			
		}
		// We have moved diagonally towards decreasing values of x and y
		else if(diffX < 0 && diffY < 0) {
			
		}
		// We have moved diagonally towards increasing values of x and decreasing of y
		else if(diffX > 0 && diffY < 0) {
			
		}
		// We have moved along increasing values of x
		else if(diffX > 0 && diffY == 0) {
			
		}
		// We have moved along decreasing values of x
		else if(diffX < 0 && diffY == 0) {
			
		}
		// We have moved along decreasing values of y
		else if(diffX == 0 && diffY < 0) {
			
		}
		// We have moved along increasing values of y
		else if(diffX == 0 && diffY > 0) {
			
		} else {
			// Will never happen
		}
		
		return 0f;
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
		State state = wumpusWorld.newState(position, lightValue);
		visitedStates.add(state.position);
		
		return state;
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