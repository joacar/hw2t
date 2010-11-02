/**
 * Keeps the direction nice and secure and does
 * the calculations needed when traveling diagonally.
 *  * y|
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
 * @author joacar
 *
 */
public enum Direction { NORTH(1), NORTH_EAST(0.5f), WEST(2), NORTH_WEST(1.5f), SOUTH(3), SOUTH_WEST(2.5f),
	SOUTH_EAST(3.5f), EAST(4);
	
	private final float heading;
	Direction(float heading) {
		this.heading = heading;
	}
	
	float getHeading() {
		return heading;
	}
}