package newAutomata;

import java.util.ArrayList;
import java.util.Hashtable;

class minDFA
{
	ArrayList<Character> inputSymbols;
	ArrayList<Integer> DFAFinalStates;
	ArrayList<Transition> DFATransitionTable;
	ArrayList<ArrayList<Transition>> partition;
	ArrayList<Transition> minDFATransitionTable;
	ArrayList<Integer> minDFAFinalStates;
	
	public minDFA(ArrayList<Transition> DFATransitionTable, ArrayList<Integer> DFAFinalStates, ArrayList<Character> inputSymbols)
	{
		this.inputSymbols = inputSymbols;
		this.DFAFinalStates = DFAFinalStates;
		this.DFATransitionTable = DFATransitionTable;
		partition = new ArrayList<ArrayList<Transition>>();
		minDFATransitionTable = new ArrayList<Transition>(); 
		minDFAFinalStates = new ArrayList<Integer>(); 
	}

	public void makeMinDFA()
	{
		ArrayList<Transition> finalStates = new ArrayList<Transition>();
		ArrayList<Transition> nonFinalStates = new ArrayList<Transition>();
		Hashtable<Character, Integer> standard = new Hashtable<Character, Integer>();
		
		//for(int j=0; j<DFAFinalStates.size(); ++j)
		//{
			for(int i=0; i<DFATransitionTable.size(); ++i)
			{
				if(DFAFinalStates.contains(DFATransitionTable.get(i).from))
				{
					finalStates.add(DFATransitionTable.get(i));
				}
				else if(!nonFinalStates.contains(DFATransitionTable.get(i)))
					nonFinalStates.add(DFATransitionTable.get(i));
			}
		//}
		
		partition.add(nonFinalStates);
		partition.add(finalStates);
		
		Hashtable<Character, Integer> currStandard = new Hashtable<Character, Integer>();
		for(int i=0; i<partition.size(); ++i)
		{
			doMarking();
			
			ArrayList<Transition> cP = partition.get(i);
			standard = findStandard(cP);
			
			for(int j=0; j<cP.size(); ++j)
			{
				Transition cT = cP.get(j);
				currStandard = findStandardOf(cP, cT);
				
				if(!standard.equals(currStandard))
				{
					findPartitionToAdd(cT, currStandard, i);
					cP = partition.get(i);
					j=0;
				}
			}
		}
		partitionToTransitionTable();
	}

	public ArrayList<Integer> getFinalStates()
	{
		for(int i=0; i<partition.size(); ++i)
		{
			ArrayList<Transition> cP = partition.get(i);
			for(int j=0; j<cP.size(); ++j)
			{
				Transition cT = cP.get(j);
				if(DFAFinalStates.contains(cT.from))
				{
					if(!minDFAFinalStates.contains(i))
						minDFAFinalStates.add(i);
				}
			}
		}
		return minDFAFinalStates;
	}

	public void displayFinalStates()
	{
		for(int i=0; i<minDFAFinalStates.size(); ++i)
		{
			System.out.println(minDFAFinalStates.get(i));
		}
	}

	public void partitionToTransitionTable()
	{
		Hashtable<Character, Integer> standard = new Hashtable<Character, Integer>();
		for(int i=0; i<partition.size(); ++i)
		{
			ArrayList<Transition> cP = partition.get(i);
			standard = findStandard(cP);

			for(int j=0; j<inputSymbols.size(); ++j)
			{
				if(standard.containsKey(inputSymbols.get(j)))
					minDFATransitionTable.add(new Transition(i, standard.get(inputSymbols.get(j)), inputSymbols.get(j)));
			}
		}
	}

	public ArrayList<Transition> getMinDFATransitionTable()
	{
		return minDFATransitionTable;
	}

	public void displayMinDFATransitionTable()
	{
		System.out.println("--Minimized DFA--");
		for(int i=0; i<minDFATransitionTable.size(); ++i)
		{
			Transition t = minDFATransitionTable.get(i);
			System.out.println(t.from + "   " + t.to + "   " + t.onChar);
		}
	}

	public void findPartitionToAdd(Transition cT, Hashtable<Character, Integer> currStandard, int partitionNumber)
	{
		boolean exists = false;
		Hashtable<Character, Integer> standard = new Hashtable<Character, Integer>();
		int i=0;
		for(i=0; i<partition.size(); ++i)
		{
			ArrayList<Transition> cP = partition.get(i);
			standard = findStandard(cP);
			if(standard.equals(currStandard))
			{
				exists = true;
				break;
			}
		}
		
		ArrayList<Transition> nP = new ArrayList<Transition>();
		ArrayList<Transition> tP = new ArrayList<Transition>();
		if(exists == true)
		{
			nP = partition.get(i);
		}
		
		tP = partition.get(partitionNumber);
		for(int j=0; j<tP.size(); ++j)
			if(tP.get(j).from == cT.from)
			{
				nP.add(tP.get(j));
			}
		
		for(int j=0; j<tP.size(); ++j)
			if(tP.get(j).from == cT.from)
			{
				partition.get(partitionNumber).remove(j);
				j=0;
			}
		
		if(exists == false)
			partition.add(nP);
	}

	public Hashtable<Character, Integer> findStandardOf(ArrayList<Transition> cP, Transition cT)
	{
		Hashtable<Character, Integer> standard = new Hashtable<Character, Integer>();
		int toFindOf = cT.from;
		for(int i=0; i<cP.size(); ++i)
		{
			if(cP.get(i).from == toFindOf)
				standard.put(cP.get(i).onChar, cP.get(i).partitionNumber);
		}
		return standard;
	}

	public Hashtable<Character, Integer> findStandard(ArrayList<Transition> cP)
	{
		Hashtable<Character, Integer> standard = new Hashtable<Character, Integer>();
		int toFindOf = cP.get(0).from;
		for(int i=0; i<cP.size(); ++i)
		{
			if(cP.get(i).from == toFindOf)
				standard.put(cP.get(i).onChar, cP.get(i).partitionNumber);
		}
		return standard;
	}

	public int findPartiton(Transition cT)
	{
		int partitionNumber = -1;
		boolean breakIt = false;
		for(int i=0; i<partition.size(); ++i)
		{
			ArrayList<Transition> thisPartition = partition.get(i);
			for(int j=0; j<thisPartition.size(); ++j)
			{
				int thisFrom = thisPartition.get(j).from;
				if(cT.to == thisFrom)
				{
					partitionNumber = i;
					breakIt = true;
					break;
				}
			}
			if(breakIt)
				break;
		}
		return partitionNumber;
	}

	public void doMarking()
	{
		for(int i=0; i<partition.size(); ++i)
		{
			ArrayList<Transition> cP = partition.get(i);
			for(int j=0; j<cP.size(); ++j)
			{
				Transition cT = cP.get(j);
				cT.partitionNumber = findPartiton(cT);
			}
		}
	}
}
