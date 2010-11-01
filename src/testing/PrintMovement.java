import java.util.HashMap;
/**
 * A form of old school GUI. Prints information
 * about where the robot has moved, what direction
 * it currently is facing and its progress in the
 * world. The world updates when the agent perceives
 * new information and logically infers how the other
 * squares might/should look like.
 * 
 * @author joacar
 *
 */
public class PrintMovement {
	private String wumpusWorld[][];
	private int x, y, rows, cols;
	private HashMap<Position, Position> coordinateMap;
	
	private final int[][] VALID_MOVES = {
		{-1,0}, {1,0},					// horizontal moves
		{0,-1}, {0,1}, 					// vertical moves
		{-1,-1}, {1,1}, {1,-1}, {-1,1},	// diagonal moves
		
	};
	
	/**
	 * Constructor
	 *  
	 * @param wumpusWorld WumpusWorld test world
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param rows number of rows in world
	 * @param cols number of cols in world
	 */
	public PrintMovement(String wumpusWorld[][], int x, int y, int rows, int cols) {
		coordinateMap = new HashMap<Position, Position>();
		this.wumpusWorld = wumpusWorld;
		this.x = x;
		this.y = y;
		this.rows = rows;
		this.cols = cols;
		
		setUpCoordinateList();
	}
	
	/**
	 * A method to convert the relative coordinates
	 * to absolute so that we wont get the wonderful
	 * ArrayIndexOutOfBound exception all the time
	 */
	private void setUpCoordinateList() {
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++) 
				coordinateMap.put(new Position((i-x), (j-y)), new Position(i, j));
	}
	
	/**
	 * Updates and prints the world according what the 
	 * agent perceives and can infer by logic resolution. 
	 * It prints the current position, previous position 
	 * and what direction we are heading. 
	 * 
	 * @param cur Position current
	 * @param prev Position previous
	 * @param state State current
	 */
	public void print(Position cur, Position prev, State state) {
		Position aCur, aPrev;
		
		System.out.printf("Moved from [%d,%d] to [%d,%d] facing %f"
				,prev.x, prev.x, cur.x, cur.y, cur.heading);
		
		aCur = coordinateMap.get(cur);
		aPrev = coordinateMap.get(prev);
		
		wumpusWorld[aPrev.x][aPrev.y] = "0";
		wumpusWorld[aCur.x][aCur.y] = "R";
		
		for(int[] pos : VALID_MOVES) {
			int x = aCur.x + pos[0];
			int y = aCur.y + pos[1];
			
			// Would be fine to se that flags are set as they should
			wumpusWorld[x][y] = ""; 
		}
		
		print();
		
	}
	
	/**
	 * Help method to print the board
	 */
	private void print() {
		for(int i = 0; i < rows; i++) {
			for(int j = 0; j < cols; j++) { 
				System.out.printf("%s ",wumpusWorld[i][j]);
			}
			System.out.printf("\n");
		}
	}
}
