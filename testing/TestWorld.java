import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class TestWorld {
	private HashMap<Position,Integer> testWorld;
	private final int[] START = new int[2];
	
	public static void main(String[] args){
		TestWorld tw = new TestWorld(args[0]);
		System.out.println("Percept @ (3,-2) " + tw.getPercept(new Position(3,-2)));
	}
	
	public TestWorld(String inputFile){
		testWorld = new HashMap<Position,Integer>();
		
		String path = "../input/" + inputFile;
		Scanner s = null;
		try {
			s = new Scanner((new File(path)));
			START[0] = s.nextInt(); START[1] = s.nextInt();
			while(s.hasNext()){
				int y = s.nextInt();
				int x = s.nextInt();
				Position pos = new Position(y,x);
				Integer i = new Integer(s.nextInt());
				testWorld.put(pos, i);
				//System.out.printf("[%d,%d] status %d\n",y,x,i);
			}
			//System.out.printf("============================\n");
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + path);
			System.exit(1);
		}
	}

	/**
	 * Returns the status of the position 
	 * 
	 * @param position Position key
	 * @return the status of the position
	 */
	public int getPercept(Position position) {
		return testWorld.get(position);
	}
	
	public StateT getState(Position position) {
		StateT state = new StateT(position, false);
		state.setValue(getPercept(position));
		return state;
	}
	
	public HashMap<Position, Integer> getTestWorld() {
		return testWorld;
	}
	
	/**
	 * Returns true if the position is in the world
	 * 
	 * @param position Position key
	 * @return true iff exists, false otherwise
	 */
	public boolean stateExists(Position position) {
		return testWorld.containsKey(position);
	}
	
	/**
	 * Returns start position
	 * 
	 * @return start position
	 */
	public int[] getStartPosition() {
		return START;
	}
}
