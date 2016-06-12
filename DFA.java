package newAutomata;

import java.util.ArrayList;
import java.util.Hashtable;

class DFA
{
	int dfaHead;
	int nfaFinalState;
	ArrayList<Integer> dfaFinalStates;
	ArrayList<Transition> NFATransitionTable;
	ArrayList<Transition> DFATransitionTable;
	ArrayList<Character> inputSymbols;
	Hashtable<Integer, ArrayList<Integer>> references = new Hashtable<Integer, ArrayList<Integer>>();
	
	public DFA(ArrayList<Transition> transitionTable, int nfaHead, int nfaFinalState)
	{
		this.dfaHead = nfaHead;
		this.nfaFinalState = nfaFinalState;
		this.dfaFinalStates = new ArrayList<Integer>();
		NFATransitionTable = new ArrayList<Transition>();
		NFATransitionTable = transitionTable;
		DFATransitionTable = new ArrayList<Transition>();
		inputSymbols = new ArrayList<Character>();
	}

	public ArrayList<Character> getInputSymbols()
	{
		return this.inputSymbols;
	}

	public ArrayList<Integer> lambdaClosure(ArrayList<Integer> fromStates)
	{
		ArrayList<Integer> toStates = new ArrayList<Integer>();
		toStates.addAll(fromStates);
		
		int state = 0; 
		for(int i=0; i<toStates.size(); ++i)
		{
			state = toStates.get(i);
			for(Transition t : NFATransitionTable)
			{
				if(state == t.from && t.onChar == '^')
				{
					if(!toStates.contains(t.to))
						toStates.add(t.to);
				}
			}
		}
		return toStates;
	}

	public ArrayList<Integer> move(ArrayList<Integer> fromStates, char onChar)
	{
		ArrayList<Integer> toStates = new ArrayList<Integer>();
		if(onChar == '^' && !toStates.contains(fromStates.get(0)))
			toStates.add(fromStates.get(0));
		for(Integer state : fromStates)
		{
			for(Transition t : NFATransitionTable)
			{
				if(state == t.from && onChar == t.onChar)
				{
					if(!toStates.contains(t.to))
						toStates.add(t.to);
				}
			}
		}
		return toStates;
	}

	public void makeDFA()
	{
		int count=0;
		ArrayList<Character> inputSymbols = new ArrayList<Character>();
		ArrayList<Integer> startFrom = new ArrayList<Integer>();
		startFrom.add(dfaHead);
		
		for(Transition t : NFATransitionTable)
		{
			if(!inputSymbols.contains(t.onChar) && t.onChar != '^')
				inputSymbols.add(t.onChar);
		}
		
		int ref = 0;
		references.put(count, lambdaClosure(startFrom));
		++count;
		for(int i=0; i<references.size(); ++i)
		{
			for(Character symbol: inputSymbols)
			{
				ArrayList<Integer> toState = null;
				toState = lambdaClosure(move(references.get(i), symbol));
				if(toState.size() != 0)
				{
					if(!references.containsValue(toState))
					{
						ref = count;
						references.put(count, toState);
						count++;
					}
					else
					{
						for(int j=0; j<references.size(); ++j)
						{
							if(references.get(j).equals(toState))
							{
								ref = j;
								break;
							}
						}
					}
				}
				else
					ref = -1;
				DFATransitionTable.add(new Transition(i, ref, symbol));
			}
		}
	}

	public ArrayList<Integer> getFinalStates()
	{
		for(int i=0; i<references.size(); ++i)
		{	
			if(references.get(i).contains(nfaFinalState))
			{
				dfaFinalStates.add(i);
			}
		}
		return dfaFinalStates;
	}	

	public ArrayList<Transition> getTransitionTable()
	{
		return DFATransitionTable;
	}

	public void displayTransitionTable()
	{
		System.out.println("--DFA--");
		for(Transition t : DFATransitionTable){
			//System.out.println(references.get(t.from)+ "   " + t.from + "   " + t.to + "   " + t.onChar);
			System.out.println(t.from + "   " + t.to + "   " + t.onChar);
		}
	}

	public void displayFinalStates()
	{
		System.out.println("--DFA Final States--");
		for(int i=0; i<references.size(); ++i)
		{	
			if(references.get(i).contains(nfaFinalState))
			{
				System.out.print(i + " ");
			}
		}
	}

	public boolean checkOn(String string)
	{
		int from;
		from = dfaHead;
		boolean acceptable = true;
		char []inputString = string.toCharArray();
		for(Character c : inputString)
		{
			for(Transition t : DFATransitionTable)
			{
				if(t.from == from && t.onChar == c)
				{
					from = t.to;
					if(dfaFinalStates.contains(from))
						acceptable = true;
					else
						acceptable = false;
					break;
				}
				else
					acceptable = false;
			}
		}
		return acceptable;
	}	

	public Hashtable<Integer, Character> toCFG()
	{
		char start = 'S';
		char startFrom = 'A'; 
		Hashtable<Integer, Character> CFGreferences = new Hashtable<Integer, Character>();
		for(Transition t : DFATransitionTable)
		{
			if(t.from == 0 && !CFGreferences.containsKey(t.from)) 
				CFGreferences.put(t.from, start);
			else 
				if(!CFGreferences.containsKey(t.from))
				{
					CFGreferences.put(t.from, startFrom);
					++startFrom;
				}
		}
		return CFGreferences;
	}
}