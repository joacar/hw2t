package test;
import lejos.robotics.navigation.*;
import lejos.nxt.*;

public class Movement {
	static SimpleNavigator navigator = new SimpleNavigator(new TachoPilot(5.6f, 13.5f, Motor.C, Motor.B));
	
	public static void main(String args[]) {
		LCD.drawString("Testing movement", 0, 1);
		// Set starting position
		navigator.setPosition(-1, 0, 1);
		
		// Move forward about 20 cm
		navigator.travel(20f);
		
		// Get the new position
		float x = navigator.getX(), y = navigator.getY(), h = navigator.getHeading();
		LCD.drawString("["+x+","+y+"] Heading: "+h, 2, 3);
		
		// Turn 90 degrees left and move forward 20 cm
		navigator.rotate(90f);
		navigator.travel(20f);
		
		// Get the new position
		x = navigator.getX();
		y = navigator.getY();
		h = navigator.getHeading();
		LCD.drawString("["+x+","+y+"] Heading: "+h, 4, 5);
		
		// Turn 90 degrees right and move forward 20 cm
		navigator.rotate(-90f);
		navigator.travel(20f);
		
		// Get the new position
		x = navigator.getX();
		y = navigator.getY();
		h = navigator.getHeading();
		LCD.drawString("["+x+","+y+"] Heading: "+h, 6, 7);
		
		// Go forward and return immediately
		navigator.travel(20f, true);
		
		System.exit(0);
	}
}