package robot;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;

/**
 * Calibrates the light sensor to the different shades/colours
 * that are being used to recognize what the status of the
 * square is.
 * 
 * @author joacar
 *
 */
public class CalibrateLightSensor {
	private final int NUMBER_OF_SHADES = Status.ALL.length;
	private final int STATUS[] = new int[NUMBER_OF_SHADES];
	
	/**
	 * Basic constructor
	 */
	public CalibrateLightSensor(LightSensor ls) {
		/*
		 *  Begin with calibrating the light sensors for the different shades
		 *  between black and white representing ok, breeze, stench, etc
		 */
		for(int statusCode : Status.ALL)
			STATUS[statusCode] = lightValue(ls, statusCode);
	}
	
	/**
	 * Does what it does and did what it used to do
	 *  
	 * @param ls LightSensor ls
	 * @param status Status enum
	 * @return int value representing light
	 */
	private int lightValue(LightSensor ls, int statusCode) {	
		switch(statusCode) {
		case Status.NOTHING:
			System.out.println("Place sensor above pure white...");
			Button.waitForPress();
			ls.calibrateHigh();
		case Status.BORDER:
			System.out.println("Place sensor above pure black...");
			Button.waitForPress();
			ls.calibrateLow();
		default:
			System.out.println("Place sensor above shade for "+Status.STRINGS[statusCode]);
			Button.waitForPress();
		}
		return ls.getLightValue();
	}
	
	/**
	 * Return the light values
	 * 
	 * @return array - the light values
	 */
	public int[] getLightValues() {
		return STATUS;
	}
}