import java.io.BufferedReader;
import java.io.IOError;
import java.io.IOException;
import java.io.InputStreamReader;
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

	private Hashtable<Position, Integer> exploredStates;
	public static Hashtable<Position, StateT> knowledgeBase;

	private Hashtable<Position, Integer> adjacentToBase;
	private Hashtable<Position, Integer> unwantedStates;

	private Hashtable<Position, String> dirLookUp;
	private Hashtable<String, int[]> posLookUp;


	private KnowledgeBaseTest kb;
	public Position current = null, previous = null;
	public StateT currentState = null;
	private Random rand;

	private boolean foundTheGold = false;

	public static final int[][] ADJACENT = { 
		{1,0}, {-1,0}, {0,1}, {0,-1} };

	public static final int[][] DIAGONAL = {
		{1,-1}, {1,1}, {-1,-1}, {-1,1} };

	// For testing
	private BufferedReader br;
	private RunTest runTest;
	private int moveNumber = 0;
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
		runTest = new RunTest(inputFile);
		testWorld = runTest.testWorld; //new TestWorld(inputFile);
		runTest.agentWorld = knowledgeBase;
		
		initialize();

		explore();

	}

	public void control() {
		String line = null;
		try {
			br = new BufferedReader(new InputStreamReader(System.in));
			System.out.printf("Move or stop, make your" +
			" choice: " );
			line = br.readLine();
			if(line.equals("next")) {
				runTest.upDateAgentMatrix(currentState);
				runTest.upDateAgentLocation(current, previous);
				runTest.print(current, moveNumber);
			}
			if(line.equals("stop")) {
				System.exit(0);
			}
		} catch(IOError e) {
			System.err.println("Error "+e.getMessage());
		} catch(Exception e) {
			System.err.println("Error "+e.getMessage());
			e.printStackTrace();
		}
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
		exploredStates = new Hashtable<Position, Integer>();

		knowledgeBase = new Hashtable<Position, StateT>();

		rand = new Random();
	}

	private void explore() {
		Position origin = new Position(0,0), base, newPosition,
		lastKnownSafePosition = null;
		
		current = origin;
		
		StateT state = percept(origin);
		currentState = state;
		knowledgeBase.put(origin, state);
		inference(state); //infer(state);
		explored(origin);
		base = origin;
				
		int movesMade = 0;
		do {
			for(int[] pos : AgentTesting.ADJACENT) {
				int newX = base.getX() + pos[0];
				int newY = base.getY() + pos[1];

				previous = current;
				current = newPosition = new Position(newX, newY);
				if(move(newPosition, movesMade)) {
					lastKnownSafePosition = newPosition;
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

				int newX = base.getX() + pos[0];
				int newY = base.getY() + pos[1];

				previous = current;
				current = newPosition = new Position(newX, newY);

				if(move(newPosition, movesMade)) {
					lastKnownSafePosition = newPosition;
				}
			}

			if(movesMade == 0) {	//lastKnowSafePosition == null
				//escape()
			}
			base = lastKnownSafePosition;
		}while(true);
	}

	private boolean move(Position position, int movesMade) {
		StateT state = null;
		moveNumber += 1;
		if(knowledgeBase.contains(position)) {
			// Hmmz TODO
			state = knowledgeBase.get(position);
		} else {			
			if(isSafe(position) && !explored(position)) {
				//moveTo(position)
				movesMade += 1;
				state = percept(position);
				currentState = state;
				knowledgeBase.put(position, state);
				explored(position);

				inference(state);

				if(state.isGlittery()) {
					foundTheGold = true;
					//escape()
				}

				/*if(!state.isBorder()) {
					//nextBase = ;
				}*/
				if(state.isBorder()) return false;
				//moveTo(base.x, base.y)
			}
		}
		return true;
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
		StateT adjacent = null;
		for(int[] pos : ADJACENT) {
			position = position.newPosition(pos[0], pos[1]);

			if(knowledgeBase.contains(position)) {
				adjacent = knowledgeBase.get(position);
			} else {
				adjacent = new StateT(position, false);
			}

			if(state.isOk()) {
				//kb.setOk(position);
				adjacent.setOk();
			} else {
				if(state.isBreezy()) {
					adjacent.setPit();
					adjacent.incrementPit();
					//kb.setPitPossibility(position);
				}
				if(state.isSmelly()) {
					kb.setWumpusPossibility(adjacent);
					adjacent.setWumpus();
					adjacent.incrementWumpus();
				}
			}

			if(DEBUG) System.out.printf("\tInfered @ %s\n",adjacent.toString());
			addState(position, adjacent);
		}
	}

	public void inference(StateT state) {
		if(DEBUG) {
			System.out.println("AgentTesting.inference()");
		}

		if(state.isOk()) {
			kb.setOk(state);
		} else {
			if(state.isBreezy()) {
				kb.setPitPossibility(state);
			}
			if(state.isSmelly()) {
				kb.setWumpusPossibility(state);
			}
		}

		if(DEBUG) System.out.printf("\tInfered @ %s\n",state.toString());
		control();
	}

	private boolean isSafe(Position position) {
		StateT state = knowledgeBase.get(position);

		if(state.isBorder()) {
			return false;
		}

		if(state.isOk() || (state.getPitPossibility() <= 0 && state.getWumpusPossibility() <= 0)) {
			return true;	
		}

		return false;
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

		//infer(state);
		inference(state);
		Position lastKnownSafePosition = null;
		Position newPos = null;

		for(int[] pos : ADJACENT) {
			newPos = current.newPosition(pos[0], pos[1]);

			String dir = dirLookUp.get(newPos);
			newPos.setHeading(dir);

			if(state.isOk() && explored(newPos) && notBorder(newPos)) {
				// Move to new position
				state = percept(newPos);
				adjacentToBase.put(newPos, 0);

				inference(state);//infer(state);

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

			if(state.isOk()  && explored(newPos) && notBorder(newPos)) {
				// Move to new position
				state = percept(newPos);
				adjacentToBase.put(newPos, 0);

				inference(state);//infer(state);

				lastKnownSafePosition = newPos;
			}
		}

		// We couldnt move from our new base. Go back to old base.
		if(lastKnownSafePosition == null) ;// return oldBase;

		return newPos;
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
		foundTheGold = true;
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
		if(knowledgeBase.contains(position)) return knowledgeBase.get(position);

		return null;
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
	 * Return true if position has been explored
	 * 
	 * @param position to be explored
	 * @return true if position explored,
	 * 		false otherwise
	 */
	public boolean explored(Position position) {
		if(exploredStates.contains(position)) {
			return true;
		} else {
			exploredStates.put(position, 0);
			return false;
		}
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