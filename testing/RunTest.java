import java.io.BufferedReader;
import java.io.IOError;
import java.io.InputStreamReader;
import java.util.Hashtable;


public class RunTest {
	private AgentTesting agent;
	private TestWorld testWorld;
	
	private String wumpusWorld[][];
	private String agentMatrix[][];
	
	private Hashtable<Position, Integer> testWorldTable;
	private Hashtable<Integer, String> table;
	
	private String inputFile = null;
	private final String PATH = "../input/";
	
	public static void main(String args[]) {
		if(args.length != 1) {
			System.out.printf("Usage\tjava RunTest <input_file>\n");
			System.exit(0);
		}
		
		new RunTest(args[0]);
	}
	
	public RunTest(String inputFile) {
		table = new Hashtable<Integer, String>();
		wumpusWorld = new GenerateWumpusWorld(PATH+inputFile).getWumpusWorld();
		
		inputFile = inputFile.replace(".txt", "_converted.txt");
		
		testWorld = new TestWorld(PATH+inputFile);
		testWorldTable = testWorld.getTestWorld();
		
		agent = new AgentTesting(testWorld);
		
		BufferedReader br = null;
		String line = null;
		try {
			 br = new BufferedReader(new InputStreamReader(System.in));
			 line = br.readLine();
			 do {
				 // decideMove
			 } while(line.equals("next"));
		} catch(IOError e) {
			System.err.println("Error "+e.getMessage());
		} catch(Exception e) {
			System.err.println("Error "+e.getMessage());
		}
	}
	
	/**
	 * Sets up the transformations from strings
	 * to integers that should be performed
	 */
	private void setUpTable() {
		table.put(-1, "X");
		table.put(0, "0");
		table.put(1, "B");
		table.put(2, "S");
		table.put(3, "G");
		table.put(4, "BS");
		table.put(5, "BG");
		table.put(6, "SG");
		table.put(7, "BSG");
	}
	
}
