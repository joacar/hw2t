import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class TestWorld {
	private HashMap<Integer[],Integer> testworld;

	public TestWorld(String inputFile){
		testworld = new HashMap<Integer[],Integer>();
		
		String path = "../input/" + inputFile;
		Scanner s = null;
		try {
			s = new Scanner((new File(path)));
			Integer[] coords = new Integer[2];
			while(s.hasNext()){
				coords[0] = s.nextInt();
				coords[1] = s.nextInt();
				testworld.put(coords, s.nextInt());
			}
		} catch (FileNotFoundException e) {
			System.err.println("File not found: " + path);
			System.exit(1);
		}
	}
	
	public int getPercept(int x, int y){
		Integer[] key = {x,y};
		return testworld.get(key);
	}
	
	public static void main(String[] args){
		TestWorld tw = new TestWorld(args[0]);
		System.out.println("Percept @ (3,-2) " + tw.getPercept(3, -2));
	}
}
