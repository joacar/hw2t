import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class TestWorld {
	private HashMap<Position,Integer> testworld;

	public TestWorld(String inputFile){
		testworld = new HashMap<Position,Integer>();
		
		String path = "../input/" + inputFile;
		Scanner s = null;
		try {
			s = new Scanner((new File(path)));
			while(s.hasNext()){
				Position pos = new Position(s.nextInt(),s.nextInt());
				testworld.put(pos,new Integer(s.nextInt()));
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + path);
			System.exit(1);
		}
	}
	
	public int getPercept(int x, int y){
		return testworld.get(new Position(x,y));
	}
	
	public static void main(String[] args){
		TestWorld tw = new TestWorld(args[0]);
		System.out.println("Percept @ (3,-2) " + tw.getPercept(3,-2));
	}
}
