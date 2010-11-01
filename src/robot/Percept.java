package robot;
import sun.management.Sensor;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorConstants;
import lejos.nxt.SensorPort;
import lejos.nxt.SensorPortListener;
import lejos.nxt.SoundSensor;

public class Percept implements SensorPortListener {
	public Percept() {}
	
	public static int percept(SensorPortListener sensor) {
		if(sensor instanceof LightSensor) {
			return ((LightSensor) sensor).readValue();
		} else {
			
		}
		return 0;
		
	}

	public void stateChanged(SensorPort arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
}
