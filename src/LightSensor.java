
public class LightSensor extends Sensor {
	boolean setShadeHigh = false, setShadeLow = false;
	final LightSensor ls;
	
	LightSensor() {
		LightSensor lightSensor = new LightSensor(SensorPort.S3, true);
		ls = lightSensor;
		ls.setFloodlight(true);

	}
	
	/**
	 * Just calibrates the light sensor to its high
	 * and low. In this case just white and black.
	 * @param ls LightSensor to be calibrated
	 */
	private void calibrateLightSensor(LightSensor ls) {
		if(!setShadeHigh) {
			System.out.println("Place sensor above pure black...");
			Button.waitForPress();
			ls.calibrateLow();
			setShadeHigh = true;
		}
		if(!setShadeLow) {
			System.out.println("Place sensor above pure white...");
			Button.waitForPress();
			ls.calibrateHigh();
			setShadeLow = true;
		}
	}
	
	/**
	 * Returns the value assigned to each of the
	 * different shades shown between (in this case)
	 * white and black.
	 * @param ls LightSensor 
	 * @param text String 
	 * @return value of the light
	 */
	public int getLightValue(LightSensor ls, String text) {
		if(!setShadeHigh || !setShadeLow) {
			calibrateLightSensor(ls);
		}
		
		System.out.println("Place sensor above shade for "+text);
		Button.waitForPress();
		return ls.getLightValue();
	}
	
}
