/**
 * Position is made to make our life easier
 * keeping track of the current position using
 * a carthesian coordinate system as well as
 * keeping track of what direction we are
 * currently looking at. 
 * 
 * The coordinates are not absolute, they are
 * relative to our starting location and the 
 * compass directions are such that North is
 * along the positive x-axis and east is 
 * along the positive y-axis.
 * 
 * Each compass direction as been assigned a float
 * value additional to the enum declaration. For
 * further info, look at the Heading or Direction
 * class.
 * 
 * @author a0075840a and a0075885l
 * 
 */
public class Position {
	public int x, y;				// x,y coordinates
	private Heading heading = null;	// Heading class
	
	/**
	 * Basic constructor taking two parameters
	 * 
	 * @param x coordinate
	 * @param y coordinate
	 */
	Position(int x, int y) { 
		this.x = x; this.y = y; 
	}
	
	/**
	 * Set the current heading
	 * 
	 * @param heading Heading 
	 */
	public void setHeading(Heading heading) {
		this.heading = heading;
	}
	
	/**
	 * Get the heading we are facing as a string
	 * 
	 * @return heading as a String
	 */
	public String getHeadingString() {
		return heading.getHeadingString();
	}
	
	/**
	 * Get the heading we are facing as a float
	 * 
	 * @return heading as a float
	 */
	public float getHeadingFloat() {
		return heading.getHeading();
	}
	
	/**
	 * toString
	 */
	@Override
	public String toString() {
		return "Currently at ["+x+","+y+"] looking "+getHeadingString();
	}
}