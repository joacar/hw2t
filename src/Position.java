
/**
 * Just a simple holder for x and y coordinates, facing parameter
 * and timer.
 * 
 * @author a0075840a and a0075885l
 * 
 */
public class Position {
	int x, y;
	private boolean north = false, south = false, east = false, west = false;
	private final boolean FACING[] = {north, west, south, east};
	public float heading = 0;
	
	Position(int x, int y) { 
		this.x = x; this.y = y; 
	}
	
	/**
	 * Set the direction of where we currently at. If we
	 * want to express north east, it is represented as
	 * nort = true and east = true.
	 * 
	 * @param array containg the directions, in
	 * 		order: north, west, south, east
	 */
	public void setDirection(boolean array[]) {
		for(int i = 0; i < FACING.length; i++)
			FACING[i] = array[i];
	}
	
	public float getDirection() {
		float simpleDirection = -0.5f;
		float direction = 0;
		
		for(float i = 1; i <= FACING.length; i++) {
			if(FACING[(int) i-1]) {
				simpleDirection += 0.5f;
				direction = i;
			}
		}
		
		return direction - simpleDirection;
	}
}