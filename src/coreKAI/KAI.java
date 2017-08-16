package coreKAI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownWordsException;
import kaiExceptions.UnreadableActionNodeException;
import languageProcessing.StringToWordConverter;
import processTree.CommandNode;
import words.Verb;
import words.Word;

/**
 * This is KAI! She's our GAI!
 * @author BLJames
 *
 */
public class KAI 
{
	
	private static final Scanner keys = new Scanner(System.in);
	
	public static void main(String[] args)
	{
		System.out.println("What can I do for you today? ");
		String command = keys.nextLine();
		while(keepGoing(command))
		{
			CommandNode commandNode;
			try 
			{
				commandNode = new CommandNode(StringToWordConverter.getInstance().convert(command));
				new Thread(commandNode).start();
			} catch (UnknownWordsException e) 
			{
				learnWords(e.getUnknownWords());
			} catch (UnreadableActionNodeException e) 
			{
				List<Word> problemWords = new ArrayList<>();
				problemWords.add(new Verb(e.toString()));
				learnWords(problemWords);
			} catch (NotAnActionNodeException e) 
			{
				List<Word> problemWords = new ArrayList<>();
				problemWords.add(new Verb(e.toString()));
				learnWords(problemWords);
			} catch (FileNotFoundException e) 
			{
				System.err.println("I'm sorry, there was a problem accessing one of my files!");
			} catch (IOException e) 
			{
				System.err.println("I'm sorry, there was a problem accessing one of my files!");
			}
			command = keys.nextLine();
		}
		System.out.println("Goodbye!");
		
//		String command = keys.nextLine();
//		String[] words = command.split(" ");
//		if(words.length == 2)
//		{
//			try {
//				CommandNode commandRoot = new CommandNode(words);
//				commandRoot.run();
//			} catch (UnknownCommandException e) 
//			{
//				System.err.println("I don't know how to " + e.getMessage() + ".");
//			} catch (UnknownActionException e) {
//				System.err.println("I don't know how to " + e.getMessage()  + ".");
//			} catch (UnknownThingException e) {
//				System.err.println("I don't what " + e.getMessage() + " is.");
//			}
//		} else
//		{
//			System.out.println("I'm sorry. I'm not programmed to understand commands that aren't exactly two words currently.");
//		}
	}
	
	private static boolean keepGoing(String command)
	{
		String[] stopCommands = {"stop", "end", "finish"};
		for(String stopCommand : stopCommands)
		{
			if(command.equalsIgnoreCase(stopCommand))
			{
				return false;
			}
		}
		return true;
	}
	
	private static void learnWords(List<Word> wordsToLearn)
	{
		//TODO Code learning words by asking questions
	}
	
}
