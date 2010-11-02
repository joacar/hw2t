/**
 * Keeps track of what direction the robot
 * is currently positioned.
 *
 * y|
 *  |	  E 
 *  | S <-|-> N
 *  |     W
 *  |
 * ------------x  
 * 0|
 * 
 * North = 1, West = 2, South = 3, East = 4;
 * NE = 0.5, NW = 1.5, SW = 2.5, SE = 3.5
 * 
 * The logic is implemented in enum Direction
 * 
 * @author joacar
 *
 */
public class Heading {
	private float heading = -1;
	private String direction = null;
	/**
	 * Basic constructor
	 * 
	 * @param dir Direction dir
	 */
	public Heading(Direction dir) {
		this.heading = dir.getHeading();
		this.direction = setHeadingString();
	}
	
	/**
	 * Return the directions as a float
	 * 
	 * @return the directions as a float
	 */
	public float getHeading() {
		return heading;
	}
	
	/**
	 * Changes the heading
	 * 
	 * @param dir Direction dir
	 */
	public void changeHeading(Direction dir) {
		this.heading = dir.getHeading();
	}
	
	/**
	 * Returns the directions as a string
	 * 
	 * @return heading String heading
	 */
	private String setHeadingString() {
		String heading = null;
		for(Direction dir : Direction.values()) 
			if(dir.getHeading() == this.heading) heading = dir.name();
		return heading;
	}
	
	/**
	 * Returns the directions as a string
	 * 
	 * @return heading String heading
	 */
	public String getHeadingString() {
		return direction;
	}
}