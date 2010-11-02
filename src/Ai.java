
public interface Ai {
	void run();
	
	State[] getAdjacentStates(Position position);
	
	State getState(Position position);
}
