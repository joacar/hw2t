/**
 * Keeps the direction nice and secure.
 * 
 * @author joacar
 *
 */
public enum Direction { FORWARD_LEFT(0.5f), FORWARD(1), FORWARD_RIGHT(1.5f), RIGHT(2), BACK_RIGHT(2.5f), BACK(3), 
	BACK_LEFT(3.5f), LEFT(4);
	
	private final float heading;
	Direction(float heading) {
		this.heading = heading;
	}
	
	float getHeading() {
		return heading;
	}
}