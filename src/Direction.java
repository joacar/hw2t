/**
 * Keeps the direction nice and secure and does
 * the calculations needed when traveling diagonally.
 * 
 * @author joacar
 *
 */
public enum Direction { NORTH, WEST, SOUTH, EAST;
	float getDirection(Direction d1, Direction d2) {
		double f = d1.ordinal(), d = 0;
		if(d2 != null) {
			switch(d1) {
				case NORTH:
					d = (d2.ordinal() == WEST.ordinal()) ? -0.5 : 0.5; 
				case SOUTH:
					d = (d2.ordinal() == WEST.ordinal()) ? 0.5 : -0.5; 
				case EAST:
					d = (d2.ordinal() == SOUTH.ordinal()) ? -0.5 : -3.5; 
				case WEST:
					d = (d2.ordinal() == SOUTH.ordinal()) ? 0.5 : -0.5; 
			}
		}
		return (float) (f + d);
	}
}