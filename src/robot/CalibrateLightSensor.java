package robot;
import java.util.EnumMap;
import java.util.Map;

import lejos.nxt.Button;
import lejos.nxt.LightSensor;

/**
 * Calibrates the light sensor to the different shades/colours
 * that are beeing used to recognize what the status of the
 * square is.
 * 
 * @author joacar
 *
 */
public class CalibrateLightSensor {
	public enum Status {
		NOTHING, BORDER, BREEZE, STENCH, GLITTER, STENCH_BREEZE,
		GLITTER_STENCH, GLITTER_BREEZE, STENCH_GLITTER_BREEZE }
	private final int NUMBER_OF_SHADES = Status.values().length;
	private final int STATUS[] = new int[NUMBER_OF_SHADES];
	
	/**
	 * Basic constructor
	 */
	public CalibrateLightSensor(LightSensor ls) {
		/*
		 *  Begin with calibrating the light sensors for the different shades
		 *  between black and white representing ok, breeze, stench, etc
		 */
		for(Status s : Status.values())
			STATUS[s.ordinal()] = lightValue(ls, s);
	}
	
	/**
	 * Does what it does and did what it used to do
	 *  
	 * @param ls LightSensor ls
	 * @param status Status enum
	 * @return int value representing light
	 */
	private int lightValue(LightSensor ls, Status status) {	
		switch(status) {
		case NOTHING:
			System.out.println("Place sensor above pure white...");
			Button.waitForPress();
			ls.calibrateHigh();
		case BORDER:
			System.out.println("Place sensor above pure black...");
			Button.waitForPress();
			ls.calibrateLow();
		default:
			System.out.println("Place sensor above shade for "+status);
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