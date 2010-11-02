import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class TestWorld {
	private HashMap<Position,Integer> testWorld;
	
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
			while(s.hasNext()){
				Position pos = new Position(s.nextInt(),s.nextInt());
				testWorld.put(pos, new Integer(s.nextInt()));
			}
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
	
	public State getState(Position position) {
		State state = new State(position, false);
		state.setValue(getPercept(position));
		return state;
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
}
