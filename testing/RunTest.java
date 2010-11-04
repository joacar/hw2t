import java.io.BufferedReader;
import java.io.IOError;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.Set;

public class RunTest {
	private AgentTesting agent;
	public TestWorld testWorld;
	private GenerateWumpusWorld gww;
	
	private String wumpusWorld[][];
	private String agentMatrix[][];
	
	private Hashtable<Position, Integer> testWorldTable;
	private Hashtable<Position, StateT> agentWorld;
	private Hashtable<Integer, String> table;
	
	private String inputFile = null;
	private final String PATH = "";
	
	private int[] start;
	
	public static void main(String args[]) {
		if(args.length != 1) {
			System.out.printf("Usage\tjava RunTest <input_file>\n");
			System.exit(0);
		}
		
		new RunTest(args[0]);
	}
	
	public RunTest(String inputFile) {		
		table = new Hashtable<Integer, String>();
		gww = new GenerateWumpusWorld(inputFile);
		
		wumpusWorld = gww.getWumpusWorld();
		
		inputFile = inputFile.replace(".txt", "_converted.txt");
		
		testWorld = new TestWorld(PATH+inputFile);
		testWorldTable = testWorld.getTestWorld();
		start = testWorld.getStartPosition();
		
		agent = new AgentTesting(testWorld);
		agentWorld = agent.getAgentWorld();
		
		initialize();

		/*BufferedReader br = null;
		String line = null;
		Position next = null, prev = null, cur = null;;
		try {
			 br = new BufferedReader(new InputStreamReader(System.in));
			 System.out.printf("\nCommand options is next or stop\n");
			 do {
				 System.out.printf("Move or stop, make your" +
				 		" choice: " );
				 line = br.readLine();
				 if(line.equals("next")) {
					 next = agent.decideMove(cur);
					 upDateAgentMatrix(cur);
					 upDateAgentLocation(cur, prev);
					 print(cur);
					 prev = cur;
					 cur = next;
				 }
				 if(line.equals("stop")) {
					 System.exit(0);
				 }
			 } while(true);
		} catch(IOError e) {
			System.err.println("Error "+e.getMessage());
		} catch(Exception e) {
			System.err.println("Error "+e.getMessage());
			e.printStackTrace();
		}*/
	}
	
	public void upDateAgentMatrix(Position position) {
		System.out.println("RunTest.upDateAgentMatrix()");
		StateT state = agent.getState(position);
		
		int x = position.getX()+start[0];
		int y = position.getY()+start[1];
		
		Set<Position> set = agentWorld.keySet();
		System.out.printf("Size of knowledge base is %d and explored states:\n", set.size());
		for(Position pos : set) {
			x = pos.getX()+start[0]; y = pos.getY()+start[1];
			state = agent.getState(pos);
			
			System.out.printf("\t[%d,%d] with &s\n",x,y,state.getStatus() );
			agentMatrix[x][y] = state.getStatus();
		}
	}
	
	public void upDateAgentLocation(Position position, Position previous) {
		if(previous != null) { 
			int xP = previous.getX()+start[0], yP = previous.getY()+start[1];
			agentMatrix[xP][yP] = "0";
		}
		
		int x = position.getX()+start[0], y = position.getY()+start[1];
		agentMatrix[x][y] = "R";
	}
	
	/**
	 * Sets up the transformations from strings
	 * to integers that should be performed
	 */
	private void initialize() {
		table.put(-1, "X");
		table.put(0, "0");
		table.put(1, "B");
		table.put(2, "S");
		table.put(3, "G");
		table.put(4, "BS");
		table.put(5, "BG");
		table.put(6, "SG");
		table.put(7, "BSG");
				
		agentMatrix = new String[wumpusWorld.length][wumpusWorld.length];
		for(int i = 0; i < wumpusWorld.length; i++) {
			for(int j = 0; j < wumpusWorld[0].length; j++) {
				if(start[0] == j && start[1] == i) {
					agentMatrix[j][i] = "R";
				} else {
					agentMatrix[j][i] = "0";
				}
			}
		}
	}
	
	public void print(Position position) {
		System.out.printf("Current position is %s\n", position);
		for(int i = 0; i < wumpusWorld.length; i++) {
			for(int j = 0; j < wumpusWorld[i].length; j++) {
				System.out.printf("%s ", wumpusWorld[j][i]);
			}
			System.out.printf("\t");
			for(int j = 0; j < wumpusWorld[i].length; j++) {
				System.out.printf("%s ", agentMatrix[j][i]);
			}
			System.out.printf("\n");
		}
	}
	
}
