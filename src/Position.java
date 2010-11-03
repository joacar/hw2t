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
	private int x, y;				// x,y coordinates
	private float heading;			// heading as a float
	private Direction direction;	// direction looking
	
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
	 * Constructor taking three parameters
	 * 
	 * @param x coordinate
	 * @param y coordinate
	 * @param direction Direction
	 */
	Position(int x, int y, Direction heading) {
		this.x = x; this.y = y;
		setHeading(heading);
		this.direction = heading;
	}
	
	/**
	 * Set the current heading
	 * 
	 * @param heading Heading 
	 */
	public void setHeading(Direction heading) {
		this.heading = heading.getHeading();
		this.direction = heading;
	}
	
	/**
	 * Get the heading we are facing as a enum
	 * of type Direction
	 * 
	 * @return heading as a enum
	 */
	public Direction getHeading() { return direction; }
	
	/**
	 * Get the heading we are facing as a string
	 * 
	 * @return heading as a String
	 */
	public String getHeadingString() { return direction.name(); }
	
	/**
	 * Get the heading we are facing as a float
	 * 
	 * @return heading as a float
	 */
	public float getHeadingInt() { return heading; }
	
	/**
	 * Returns x coordinate
	 * 
	 * @return x coordinate
	 */
	public int getX() {	return x; }
	
	/**
	 * Returns y coordinate
	 * 
	 * @return y coordinate
	 */
	public int getY() { return y; }
	
	/**
	 * Adds a position to this
	 * 
	 * @param position to be added
	 */
	public void change(int[] position ) {
		x = x + position[0];
		y = y + position[1];
	}
	public void reverseHeading() {
		for(Direction st : Direction.values()) 
			if(heading == -st.getHeading()) direction = st;
	}
	
	public Position newPosition() { return new Position(this.x, this.y, direction);}
	
	public Position newPosition(int x, int y) { 
		return new Position(this.x+x, this.y+y, direction); }

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + x;
		result = prime * result + y;
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Position other = (Position) obj;
		if (x != other.x)
			return false;
		if (y != other.y)
			return false;
		return true;
	}

	/**
	 * toString
	 */
	@Override
	public String toString() {
		return "Currently at ["+x+","+y+"] looking "+direction+"("+heading+")";
	}
	
}