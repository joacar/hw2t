/**
 * Keeps track of what direction the robot
 * is currently positioned.

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
	
	/**
	 * Constructor taking two Directions as input
	 * 
	 * @param d1 First Direction
	 * @param d2 Second Direction
	 */
	Heading(Direction d1, Direction d2) {
		this.heading = d1.getDirection(d1, d2);
	}
	
	/**
	 * Return the directions as a float
	 * 
	 * @return the directions as a float
	 */
	public float getHeading() {
		return heading;
	}
}