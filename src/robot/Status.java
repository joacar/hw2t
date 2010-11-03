package robot;

public class Status {
	public static final int NOTHING = 0;
	public static final int BORDER = 1;
	public static final int BREEZE = 2;
	public static final int STENCH = 3; 
	public static final int GLITTER = 4; 
	public static final int STENCH_BREEZE = 5;
	public static final int	GLITTER_STENCH = 6; 
	public static final int GLITTER_BREEZE = 7; 
	public static final int STENCH_GLITTER_BREEZE  = 8;
	
	public static final int[] ALL = 
		{
			NOTHING,BORDER,BREEZE,STENCH,GLITTER,STENCH_BREEZE,GLITTER_STENCH,
			GLITTER_BREEZE,STENCH_GLITTER_BREEZE
		};
	
	public static final String[] STRINGS = 
	{
		"NOTHING","BORDER","BREEZE","STENCH,GLITTER","STENCH_BREEZE","GLITTER_STENCH",
		"GLITTER_BREEZE","STENCH_GLITTER_BREEZE"
	};
}
