public enum StatusTesting {
		BORDER(-1), NOTHING(0), BREEZE(1), STENCH(2), GLITTER(3), STENCH_BREEZE(4),
		GLITTER_BREEZE(5), GLITTER_STENCH(6), STENCH_GLITTER_BREEZE(7);
		
		private final int status;
		StatusTesting(int status) { this.status = status; } 
		int getStatus() { return status; }		
	}