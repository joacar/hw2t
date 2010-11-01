package robot;

import lejos.nxt.Motor;
import lejos.robotics.navigation.TachoPilot;

/**
 * This class takes care of the movement by done by
 * the actual robot. Move forward, move backward,
 * rotate and stuff like that are handled from here.
 * 
 * The instructions are sent from the agents conclusion
 * about what moves to do.
 * 
 * @author joacar
 *
 */
public class Move {
	// All distances are in cm
	private static final float WHEEL_DIAMETER = 0f;
	private static final float TRACK_WIDTH = 0f;	
	private static final float CC_SQUARE = 20f;
	private static final float DEVIATION = 0.5f;
	private TachoPilot pilot;
	
	public Move(Motor left, Motor right) {
		pilot = new TachoPilot(WHEEL_DIAMETER, TRACK_WIDTH, left, right);
	}
	
	/**
	 * Moves the robot forward or backward depending on the sign.
	 *  
	 * @param distance to move in cm
	 * 		negative backwards, positive forward
	 */
	public void travel(float distance) {
		pilot.travel(distance);
		
		float travelDistance = pilot.getTravelDistance();
		// If we moved to short or to long, correct it
		if(travelDistance < CC_SQUARE - DEVIATION) {
			pilot.travel(travelDistance - CC_SQUARE);
			return;
		} 
		if(pilot.getTravelDistance() > CC_SQUARE + DEVIATION) {
			pilot.travel(CC_SQUARE - travelDistance);
		}
	}
	
	/**
	 * Returns true if robot is moving, false otherwise
	 * 
	 * @return true if robot is moving, false otherwise
	 */
	public boolean isMoving() {
		return pilot.isMoving();
	}
}