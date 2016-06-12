package newAutomata;

class Transition
{
	int from;
	int to;
	char onChar;
	int partitionNumber;
	
	public Transition(int from, int to, char onChar)
	{
		this.from = from;
		this.to = to;
		this.onChar = onChar;
		this.partitionNumber = 0;
	}
}