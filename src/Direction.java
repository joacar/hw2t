import java.util.Hashtable
;
public class Direction {
	public static final Hashtable<String, Integer> map = new Hashtable<String, Integer>(8);
	public static final String FORWARD = "FORWARD";			//1
	public static final String BACK = "BACK"; 				// -1
	public static final String RIGHT = "RIGHT"; 			// 2
	public static final String LEFT = "LEFT"; 				// -2
	public static final String F_RIGHT = "FORWARD_RIGHT";	// 3
	public static final String B_LEFT = "BACK_LEFT";		// -3
	public static final String F_LEFT = "FORWARD_LEFT"; 	// 4
	public static final String B_RIGHT = "BACK_RIGHT";		// -4
	
	
	public static String[] ALL = {
		"FORWARD", "BACK", "RIGHT", "LEFT", "FORWARD_RIGHT",
		"BACK_LEFT", "FORWARD_LEFT", "BACK_RIGHT" };
	
	public static void fillMap() {
		int i = 1;
		for(String s : ALL) {
			if((i % 2) == 0) {
				i = -1*i;
			} else {
				
			}
			map.put(s, i);
		}
	}
	
	public static void main(String args[]) {
		fillMap();
		for(String s : ALL)
			System.out.println(s+" "+map.get(s));
	}
}
