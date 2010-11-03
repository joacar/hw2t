/**
 * Keeps the direction nice and secure.
 * 
 * @author joacar
 *
 */
public enum Direction { 
	FORWARD_LEFT(4), FORWARD(1), FORWARD_RIGHT(3), RIGHT(2),
	BACK_RIGHT(-4), BACK(-1),  BACK_LEFT(-3), LEFT(-2);
	private final int heading;
	
	Direction(int heading) {
		this.heading = heading;
	}
	
	int getHeading() {
		return heading;
	}
}