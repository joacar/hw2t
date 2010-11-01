package robot;
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
	private enum Status {
		NOTHING, OUT_OF_RANGE, BREEZE, STENCH, GLITTER,	STENCH_BREEZE,
		GLITTER_STENCH, GLITTER_BREEZE, STENCH_GLITTER_BREEZE }
	private final int NUMBER_OF_SHADES = 9;
	private final int STATUS[] = new int[NUMBER_OF_SHADES];
	
	/**
	 * Basic constructor
	 */
	public CalibrateLightSensor(LightSensor ls) {
		/*
		 *  Begin with calibrating the light sensors for the different shades
		 *  between black and white representing ok, breeze, stench, etc
		 */
		for(Status s : Status.values()) {
			int i = s.ordinal();
			STATUS[i] = lightValue(ls, s.name(), i);
		}
	}
	
	/**
	 * Does what it does and did what it used to do
	 *  
	 * @param ls LightSensor ls
	 * @param text String text to show
	 * @return int value representing light
	 */
	private int lightValue(LightSensor ls, String text, int i) {	
		if(i == 0) {
			System.out.println("Place sensor above pure white...");
			Button.waitForPress();
			ls.calibrateHigh();
		} else if(i == 1) {
			System.out.println("Place sensor above pure black...");
			Button.waitForPress();
			ls.calibrateLow();
		} else {
			System.out.println("Place sensor above shade for "+text);
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
