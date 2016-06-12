package newAutomata;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Scanner;
import java.util.Stack;

public class Main 
{
	private static final Map<Character, Integer> precedenceMap;
	static {
		Map<Character, Integer> map = new HashMap<Character, Integer>();
		map.put('*', 3);
		map.put('|', 2);
		map.put('+', 1);
		precedenceMap = Collections.unmodifiableMap(map);
	};
	
	private static int getPrecedence(Character c) 
	{
		int precedence = precedenceMap.get(c);
		return precedence;
	}	
	
	public static String toPostfix(String expression)
	{
		Stack<Character> stack = new Stack<Character>();
		String output = new String();
		char []reverse = new StringBuilder(expression).toString().toCharArray();
		char currentChar;
		
		for(int i=0; i<reverse.length; ++i)
		{
			currentChar = reverse[i];
			if(currentChar == '|' || currentChar == '+' || currentChar == '*')
			{
				while(true)
				{
					if(stack.size() > 0)
					{
						char peekedChar = stack.peek();
						if(peekedChar == '|' || peekedChar == '+' || peekedChar == '*')
						{
							int peekedCharPrecedence = getPrecedence(peekedChar);
							int currentCharPrecedence = getPrecedence(currentChar);
							if(peekedCharPrecedence >= currentCharPrecedence)
							{
								output += stack.pop();
							}
							else
							{
								stack.push(currentChar);
								break;
							}
						}
						else
						{
							stack.push(currentChar);
							break;
						}
					}
					else
					{
						stack.push(currentChar);
						break;
					}
				}
			}
			else if(currentChar == '(')
				stack.push(currentChar);
			else if(currentChar == ')')
			{
				while(stack.size() > 0 && stack.peek() != '(')
					output += stack.pop();
				stack.pop();
			}
			else
				output += currentChar;
		}
		while(stack.size() > 0)
			output += stack.pop();
		return output;
	}
	
	public static void writeCFGToFile(Hashtable<Integer, Character> CFGreferences, ArrayList<Transition> DFATransitionTable, ArrayList<Integer> DFAFinalStates)
	{
		Hashtable<Integer, Character> CFGRecord = new Hashtable<Integer, Character>();
		try
		{
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(new File("CFG.txt")));
			
			String line = "";
			for(Transition t: DFATransitionTable)
			{
				if(t.to != -1)
				{
					line = CFGreferences.get(t.from) + "  ->  " + t.onChar + "" + CFGreferences.get(t.to);
					bufWriter.write(line, 0, line.length());
					bufWriter.write("\r\n");
				}
			}
			for(Transition t: DFATransitionTable)
			{
				if(DFAFinalStates.contains(t.from) && !CFGRecord.containsKey(t.from))
				{
					line = CFGreferences.get(t.from) + "  ->  " + "^";
					bufWriter.write(line, 0, line.length());
					bufWriter.write("\r\n");
					CFGRecord.put(t.from, '^');
				}
			}
			
			System.out.println("Done Writing!");
			bufWriter.close();
		}
		catch(Exception e)
		{
			System.out.println("Error in testing for acceptence of Strings");
		}
	}
	
	public static void checkAndWriteToFile(String readFrom, DFA dfa)
	{
		try
		{
			BufferedReader bufReader = new BufferedReader(new FileReader(new File(readFrom)));
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(new File("output.txt")));
			
			String line = "";
			while((line = bufReader.readLine()) != null)
			{
				if(dfa.checkOn(line))
				{	
					bufWriter.write(line, 0, line.length());
					bufWriter.write("\r\n");
				}
			}
			bufReader.close();
			bufWriter.close();
		}
		catch(Exception e)
		{
			System.out.println("Error in testing for acceptence of Strings");
		}
	}
	
	public static void writeToFile(ArrayList<Character> Alphabet, ArrayList<Integer> FinalStates, ArrayList<Transition> TransitionTable, String fileName)
	{
		try
		{
			BufferedWriter bufWriter = new BufferedWriter(new FileWriter(new File(fileName)));
			
			String line = "";
			line = "Start State : 0";
			bufWriter.write(line, 0, line.length());
			bufWriter.write("\r\n");
			
			line = "Final States : ";
			for(Integer i : FinalStates)
				line += i + ", ";
			bufWriter.write(line, 0, line.length());
			bufWriter.write("\r\n");
			
			line = "Alphabet : ";
			for(Character c : Alphabet)
				line += c + ", ";
			bufWriter.write(line, 0, line.length());
			bufWriter.write("\r\n");
			
			line = "Transition Table : ";
			bufWriter.write(line, 0, line.length());
			bufWriter.write("\r\n\r\n");
			
			line = "from   to   on";
			bufWriter.write(line, 0, line.length());
			bufWriter.write("\r\n");
			for(Transition t : TransitionTable)
			{	
				line = t.from + "      " + t.to + "      " + t.onChar;
				bufWriter.write(line, 0, line.length());
				bufWriter.write("\r\n");
			}
			
			bufWriter.close();
		}
		catch(Exception e)
		{
			System.out.println("Error in testing for acceptence of Strings");
		}
	}
	
	public static void main(String[] args) 
	{
		Scanner input = new Scanner(System.in);
		ArrayList<Transition> NFATransitionTable = new ArrayList<Transition>();
		ArrayList<Transition> DFATransitionTable = new ArrayList<Transition>();
		ArrayList<Transition> MinDFATransitionTable = new ArrayList<Transition>();
		
		ArrayList<Character> inputSymbols = new ArrayList<Character>();
		Hashtable<Integer, Character> CFGreferences = new Hashtable<Integer, Character>();
		
		int NFAFinalState = 0;
		ArrayList<Integer>NFAFinalStates = new ArrayList<Integer>();
		ArrayList<Integer> DFAFinalStates = new ArrayList<Integer>();
		ArrayList<Integer> MinDFAFinalStates = new ArrayList<Integer>();
		
		System.out.println("Enter Regular Expression :");
		String expression = input.nextLine();
		String postfix = toPostfix(expression);
		System.out.println("Postfix: " + postfix);
		
		State head = new State();
		
		NFA nfa = new NFA(head, postfix);
		nfa.callMaker();
		NFATransitionTable = nfa.getTransitionTable();
		NFAFinalState = nfa.getFinalState();
		inputSymbols = nfa.getInputSymbols();
		NFAFinalStates.add(NFAFinalState);
		
		writeToFile(inputSymbols, NFAFinalStates, NFATransitionTable, "NFA.txt");
		
		DFA dfa = new DFA(NFATransitionTable, 0, NFAFinalState);
		dfa.makeDFA();
		DFATransitionTable = dfa.getTransitionTable();
		DFAFinalStates = dfa.getFinalStates();
		
		writeToFile(inputSymbols, DFAFinalStates, DFATransitionTable, "DFA.txt");
		
		minDFA minDfa = new minDFA(DFATransitionTable, DFAFinalStates, inputSymbols);
		minDfa.makeMinDFA();
		MinDFAFinalStates = minDfa.getFinalStates();
		MinDFATransitionTable = minDfa.getMinDFATransitionTable();

		writeToFile(inputSymbols, MinDFAFinalStates, MinDFATransitionTable, "MinDFA.txt");
		
		checkAndWriteToFile(args[0], dfa);
		
		CFGreferences = dfa.toCFG();
		writeCFGToFile(CFGreferences, DFATransitionTable, DFAFinalStates);
		
		input.close();
	}
}
