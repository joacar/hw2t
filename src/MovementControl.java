import lejos.nxt.Motor;
import lejos.robotics.navigation.SimpleNavigator;
import lejos.robotics.navigation.TachoPilot;

public class MovementControl {
	final static float WHEEL_DIAMETER = 5.6f;	// wheel diameter in cm
	final static float TRACK_WIDTH = 7.4f;		// distance between center of wheels
	final static double CIRCUMFERENCE = Math.PI * WHEEL_DIAMETER;	// Circumference of wheel
	final static float D_45 = 45f, D_90 = 90f;
	final static float SQUARE_SIZE = 20f;		// 20 centimeters is the square cc distance
	// Initilizes the Pilot class which gives access to good steering methods
	final static TachoPilot pilot = new TachoPilot(WHEEL_DIAMETER, TRACK_WIDTH, Motor.B, Motor.C);
	// Simple navigator (surprice?) to navigate
	final static SimpleNavigator navigator = new SimpleNavigator(pilot);
	
	public MovementControl() {}
	
	public static void move() {
		navigator.travel(SQUARE_SIZE);
	}
	
	public static void goBack() {
		navigator.rotate(2*D_90);
		move();
	}
	
	public static void setPosition(float x, float y, float direction) {
		navigator.setPosition(x, y, direction);
	}
	
	public static void rotate(float direction) {
		if(direction == 0.5) {
			navigator.rotate(-D_45);
		} else if(direction == 4) {
			navigator.rotate(2*D_90);
		} else if(direction == 1.5) {
			navigator.rotate(D_45);
		} else if(direction == 2) {
			navigator.rotate(D_90);
		} else if(direction == 4) {
			navigator.rotate(-D_90);
		} else if(direction == 2.5) {
			navigator.rotate(D_90+D_45);
		} else if(direction == 3.5) {
			navigator.rotate(-D_90-D_45);
		} else {
		}
	}
	
	public static void rotateTo(float direction) {
		navigator.rotate(direction);
	}
}
