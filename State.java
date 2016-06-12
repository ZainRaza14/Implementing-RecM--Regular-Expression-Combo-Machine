package newAutomata;

class State
{
	static int stateCount;
	
	public State()
	{
		State.stateCount = 0;
	}
	
	public int getStateNumber()
	{
		return stateCount++;
	}
}