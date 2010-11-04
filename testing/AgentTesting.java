import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

/** The AI for the classic and legendary game Wumpus world!
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
public class AgentTesting {
	public static final boolean DEBUG = true;

	//private final String STATUS_STRING[] = Status.STRINGS;

	public static final String STATUS_STRING[] = {
			"BORDER", "NOTHING", "BREEZE", "STENCH", "GLITTER", 
			"STENCH_BREEZE", "GLITTER_BREEZE", "GLITTER_STENCH"
			, "STENCH_GLITTER_BREEZE" };

	// boolean states[] = {border, breeze, stench, glitter, ok};
	public static final boolean MASK_N[] = {false, false, false, false, true};
	public static final boolean MASK_O[] = {true, false, false, false, false};	
	public static final boolean MASK_B[] = {false, true, false, false, false};
	public static final boolean MASK_S[] = {false, false, true, false, false};
	public static final boolean MASK_G[] = {false, false, false, true, false};
	public static final boolean MASK_BS[] = {false, true, true, false, false};
	public static final boolean MASK_BG[] = {false, true, false, true, false};
	public static final boolean MASK_SG[] = {false, false, true, true, false};
	public static final boolean MASK_BSG[] = {false, true, true, true, false};

	private TestWorld testWorld;

	private Hashtable<Position, Integer> visited;
	private Hashtable<Position, Integer> adjacentToBase;
	private Hashtable<Position, Integer> unwantedStates;

	private Hashtable<Position, String> dirLookUp;
	private Hashtable<String, int[]> posLookUp;
	private Hashtable<Position, StateT> knowledgeBase;

	private KnowledgeBaseTest kb;
	private Position current, previous;

	private Random rand;

	private boolean fetchedGold = false;

	public static final int[][] ADJACENT = { 
		{1,0}, {-1,0}, {0,1}, {0,-1} };

	public static final int[][] DIAGONAL = {
		{1,-1}, {1,1}, {-1,-1}, {-1,1} };

	/**
	 * Entry point
	 *  
	 * @param args input test file
	 */
	public static void main(String args[]) {
		if(args.length != 1) {
			System.out.printf("Usage: \tjava AgentTesting <input_file>\n");
			System.exit(0);	
		}

		new AgentTesting(args[0]);
	}

	/**
	 * Constructor used for simulating tests on the 
	 * computer instead of using the actual robot. This
	 * is a good way to test that the logic works as
	 * it should
	 * 
	 * @param testWorld
	 */
	public AgentTesting(TestWorld testWorld) {
		this.testWorld = testWorld;

		initialize();

	}
	/**
	 * Constructor taking a input file
	 * 
	 * @param inputFile containg the world data
	 */
	private AgentTesting(String inputFile) {		
		// Load the test world with the input fule
		testWorld = new TestWorld(inputFile);

		initialize();

		Position current = new Position(0,0);

		int cnt = 0;
		Position base, oldBase;
		do {
			System.out.println("\ndecideMove called " + (++cnt) + " times\n\n");
			base = decideMove(current);
			previous = current;
			current = base;
		} while(!fetchedGold);

	}

	/**
	 * Sets up the vital parameters
	 */
	private void initialize() {
		// Load the knowledge base
		kb = new KnowledgeBaseTest(this);

		// Set up a tables
		dirLookUp = new Hashtable<Position, String>(10);
		posLookUp = new Hashtable<String, int[]>(10);
		setUpTables();

		adjacentToBase = new Hashtable<Position, Integer>();
		unwantedStates = new Hashtable<Position, Integer>();
		visited = new Hashtable<Position, Integer>();

		knowledgeBase = new Hashtable<Position, StateT>();

		rand = new Random();
	}

	/**
	 * 
	 * @param current
	 * @return
	 */
	private StateT percept(Position current) {
		// Make the new position for this state
		Position position = current.newPosition();
		
		// Get the value (percept)
		int value = testWorld.getPercept(position);
		
		StateT state = new StateT(position, true); 
		state.setValue(value);

		// Find the corresponding status to that value
		updateStates(state, value);

		knowledgeBase.put(position, state);
		visited.put(position, 0);
		
		if(DEBUG) {
			System.out.println("AgentTesting.percept()");
			System.out.printf("\tPerceiving %s\n", state.toString());
		}
		
		return state;
	}

	private void infer(StateT state) {
		if(DEBUG) {
			System.out.println("AgentTesting.infer()");
		}
		Position position = state.position;
		
		for(int[] pos : ADJACENT) {
			position = current.newPosition(pos[0], pos[1]);
			
			if(knowledgeBase.contains(position)) {
				state = knowledgeBase.get(position);
			} else {
				state = new StateT(position, false);
			}
			if(state.isOk()) {
				
				kb.setOk(position);
			} else {
				if(state.isBreezy()) {
					kb.setPitPossibility(position);
				}
				if(state.isSmelly()) {
					System.out.println("isSmelly() = "+state.isSmelly());
					kb.setWumpusPossibility(position);
				}
			}

			if(state.isGold()) {
				fetchedTheGold();
			}

			if(DEBUG) System.out.printf("\tInfered %s\n",state.toString());
			addState(position, state);
		}
	}

	private void updateStates(StateT state, int value) {
		switch(value) {
		case -1:
			state.setStates(MASK_O);
			break;
		case 0:
			state.setStates(MASK_N);
			break;
		case 1:
			state.setStates(MASK_B);
			break;
		case 2:
			state.setStates(MASK_S);
			break;
		case 3:
			state.setStates(MASK_G);
			break;
		case 4:
			state.setStates(MASK_BS);
			break;
		case 5:
			state.setStates(MASK_BG);
			break;
		case 6:
			state.setStates(MASK_SG);
			break;
		case 7:
			state.setStates(MASK_BSG);
			break;
		}
	}

	public Position decideMove(Position current) {
		System.out.println("==== AgentTesting.decideMove() ====");
		StateT state = percept(current);
		if(state.isBorder()) {
			unwantedStates.put(current, 0);
			return previous;
		}

		infer(state);
		Position lastKnownSafePosition = null;
		Position newPos = null;

		for(int[] pos : ADJACENT) {
			newPos = current.newPosition(pos[0], pos[1]);

			String dir = dirLookUp.get(newPos);
			newPos.setHeading(dir);

			if(state.isOk() && notExplored(newPos) && notBorder(newPos)) {
				// Move to new position
				state = percept(newPos);
				adjacentToBase.put(newPos, 0);

				infer(state);

				lastKnownSafePosition = newPos;
			}
		}

		ArrayList<int[]> diagonal = new ArrayList<int[]>();

		for(int[] pos : DIAGONAL) diagonal.add(pos);
		// Shuffle the diagonal moves
		int size = diagonal.size(), index;
		for(int i = 0; i < size; i++) {
			size = diagonal.size();
			index = rand.nextInt(size);

			int[] pos = diagonal.get(index);
			diagonal.remove(index);
			newPos = newPos.newPosition(pos[0], pos[1]);

			String dir = dirLookUp.get(newPos);
			newPos.setHeading(dir);

			if(state.isOk()  && notExplored(newPos) && notBorder(newPos)) {
				// Move to new position
				state = percept(newPos);
				adjacentToBase.put(newPos, 0);

				infer(state);

				lastKnownSafePosition = newPos;
			}
		}

		// We couldnt move from our new base. Go back to old base.
		if(lastKnownSafePosition == null) ;// return oldBase;

		return newPos;
	}

	/**
	 * Moves to the direction we are currently looking
	 */
	private void move(Position current) {
		int[] move = posLookUp.get(current.getHeading());
		current.change(move);
	}

	/**
	 * Go back to our previous location facing the
	 * same direction as when we entered the state
	 */
	private void reverse(Position current) {
		int[] undo = new int[2];
		/*for(Direction dir : Direction.values())
			if(dir.getHeading() == -current.getHeadingInt())
				undo = posLookUp.get(dir);*/

		System.out.printf("\tReverse [%d,%d]",current.getX(), current.getY());
		current.change(undo);
		System.out.printf(" back to [%d,%d]\n", current.getX(), current.getY());
	}

	/**
	 * Index the relative positions to their corresponding
	 * direction as a float value. Instead of calculating
	 * each direction trough the changes in the coordinates
	 * they can be accessed instantly with this table
	 */
	private void setUpTables() {
		/*
		 * <=== IMPORTANT ===>
		 * Must be sorted the same way as Direction enum
		 */
		int[][] validMoves = {
				{1,1}, {1,0}, {1,-1}, {0,-1}, {-1,-1}, {-1,0}, {-1,1}, {0,1} 
		};

		int i = 0;
		for(int[] pos : validMoves) {
			int x = pos[0], y = pos[1];
			Position position = new Position(x, y);

			dirLookUp.put(position, STATUS_STRING[i]);
			posLookUp.put(STATUS_STRING[i], pos);
		}
	}

	/**
	 * Just a functions that indicates that we have found and
	 * retrieved the gold
	 */
	private void fetchedTheGold() {
		System.out.println("I'm rich! Now it's time"+
		" to get the f*ck out of this world!");
		fetchedGold = true;
	}

	/**
	 * Add a state to the agent world
	 * @param position to that state
	 * @param state added
	 * 
	 * @return true iff already added,
	 * 		false otherwise
	 */
	public boolean addState(Position position, StateT state) {
		if(knowledgeBase.contains(position)) return false;

		knowledgeBase.put(position, state);
		return true;
	}

	/**
	 * Return the state corresponding to the position
	 * 
	 * @param position key to state
	 * @return state in our world
	 */
	public StateT getState(Position position) {
		return knowledgeBase.get(position);
	}

	/**
	 * Return the world as our agent knows ut
	 * 
	 * @return the world our agent perceived
	 */
	public Hashtable<Position, StateT> getAgentWorld() {
		return knowledgeBase;
	}

	/**
	 * Return true if position has been visited
	 * 
	 * @param position to be checked
	 * @return true if position has been visited,
	 * 		false otherwise
	 */
	public boolean isVisited(Position position) {
		return visited.contains(position);
	}

	/**
	 * Return true if position has not yet been explored
	 * 
	 * @param position to be explored
	 * @return true if position not yet explored,
	 * 		false otherwise
	 */
	public boolean notExplored(Position position) {
		return !isVisited(position);
	}

	/**
	 * Return true if position is not a border
	 * 
	 * @param position to be checked
	 * @return true if positions is not border,
	 * 		false otherwise
	 */
	public boolean notBorder(Position position) {
		return !unwantedStates.contains(position);
	}
}