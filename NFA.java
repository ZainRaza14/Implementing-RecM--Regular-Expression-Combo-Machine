package newAutomata;

import java.util.ArrayList;

class NFA
{
	String expression;
	State stateAlloc = null;
	ArrayList<Transition> transitionTable = new ArrayList<Transition>();  
	int head;
	int limit;
	int startState;
	int finalState;
	
	ArrayList<Character> inputSymbols = new ArrayList<Character>(); 
	
	public NFA(State stateAlloc, String expression)
	{
		this.stateAlloc = stateAlloc;
		this.head = this.stateAlloc.getStateNumber();
		this.expression = expression;
		this.limit = 0;
		this.startState = 0;
		this.finalState = 0;
	}

	public void callMaker()
	{
		finalState = makeNFA(head, expression);
	}

	public int makeNFA(int currState, String expression)
	{
		int expLen = expression.length() - 1;
		limit = expLen - 1;
		int toReturn = 0;
		char letter = expression.charAt(expLen);
		if(letter == '+')
		{
			int s1 = stateAlloc.getStateNumber();
			int s2 = stateAlloc.getStateNumber();
			int s5 = stateAlloc.getStateNumber();
			
			int s3, s4;
			transitionTable.add(new Transition(currState, s1, '^'));
			transitionTable.add(new Transition(currState, s2, '^'));
			
			if(expLen+1 > 0)
			{
				char fPrev = expression.charAt(expLen-1);
				if(fPrev != '|' && fPrev != '+' && fPrev != '*')
				{
					s3 = stateAlloc.getStateNumber();
					transitionTable.add(new Transition(s1, s3, fPrev));
					--limit;
				}
				else
					s3 = makeNFA(s1, expression.substring(0, expLen));
				transitionTable.add(new Transition(s3, s5, '^'));
				
				char sPrev = expression.charAt(limit);
				if(sPrev != '|' && sPrev != '+' && sPrev != '*')
				{
					s4 = stateAlloc.getStateNumber();
					transitionTable.add(new Transition(s2, s4, sPrev));
					--limit;
				}
				else
					s4 = makeNFA(s2, expression.substring(0, limit+1));
				transitionTable.add(new Transition(s4, s5, '^'));
				
				toReturn = s5;
			}
		}
		else if(letter == '*')
		{
			int s3;
			int s1 = stateAlloc.getStateNumber();
			transitionTable.add(new Transition(currState, s1, '^'));
			
			char fPrev = expression.charAt(expLen-1);
			if(fPrev != '|' && fPrev != '+' && fPrev != '*')
			{
				s3 = stateAlloc.getStateNumber();
				transitionTable.add(new Transition(s1, s3, fPrev));
				--limit;
			}
			else
				s3 = makeNFA(s1, expression.substring(0, expLen));
			
			int s2 = stateAlloc.getStateNumber();
			transitionTable.add(new Transition(s3, s2, '^'));
			transitionTable.add(new Transition(s3, s1, '^'));
			transitionTable.add(new Transition(currState, s2, '^'));
			toReturn = s2;
		}
		else if(letter == '|')
		{
			int s1 = 0;
			int s2 = stateAlloc.getStateNumber();
			int s3 = 0;
			
			if(expLen+1 > 0)
			{
				char fPrev = expression.charAt(expLen-1);
				if(fPrev != '|' && fPrev != '+' && fPrev != '*')
				{
					s1 = stateAlloc.getStateNumber();
					transitionTable.add(new Transition(s2, s1, fPrev));
					--limit;
				}
				else
					s1 = makeNFA(s2, expression.substring(0, expLen));
				
				char sPrev = expression.charAt(limit);  // expLen-1
				if(sPrev != '|' && sPrev != '+' && sPrev != '*')
				{
					s3 = stateAlloc.getStateNumber();
					transitionTable.add(new Transition(currState, s3, sPrev));
					--limit;
				}
				else
					s3 = makeNFA(currState, expression.substring(0, limit+1));
				
				transitionTable.add(new Transition(s3, s2, '^'));
			}
			toReturn = s1;
		}
		return toReturn;
	}

	public ArrayList<Transition> getTransitionTable()
	{
		return transitionTable;
	}

	public void displayTransitionTable()
	{
		for(Transition t : transitionTable){
			System.out.println(t.from + "   " + t.to + "   " + t.onChar);
		}
	}
	
	public int getFinalState()
	{
		return this.finalState;
	}

	public void setInputSymbols()
	{
		for(Transition t : transitionTable){
			if(t.onChar != '^' && !inputSymbols.contains(t.onChar))
				inputSymbols.add(t.onChar);
		}
	}

	public ArrayList<Character> getInputSymbols()
	{
		setInputSymbols();
		return this.inputSymbols;
	}
}