package processTree;

import java.awt.AWTException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import instructions.Instruction;
import instructions.InstructionAggregator;
import instructions.InstructionDoer;
import instructions.InstructionInterpreter;
import instructions.InstructionPacket;
import instructions.InstructionType;
import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnknownThingException;
import kaiExceptions.UnknownWordsException;
import kaiExceptions.UnreadableActionNodeException;
import knowledgeAccess.ActionNodeLoader;
import knowledgeAccess.KnowledgeAccessor;
import knowledgeAccess.KnowledgePacket;
import words.Adverb;
import words.Noun;
import words.Verb;
import words.Word;
import words.WordType;

/**
 * A node for commands (either an action or a compound node)
 * @author BLJames
 *
 */
public class CommandNode extends ProcessNode implements Runnable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4564775281509616055L;
	
	private List<Word> words;
	
	private List<ThingNode> thingTrees;
	private ActionNode actionTree;
	
	private List<String> goals;
	
	private InstructionInterpreter instructionInterpreter;
	
	/**
	 * Creates a new process tree based on the given commands. Currently assumes a verb and a direct object
	 * @param command The command to be executed
	 * @throws UnknownWordsException Thrown if any of the needed words aren't known so KAI can learn them
	 * @throws NotAnActionNodeException Thrown if there is a problem reading the action node file
	 * @throws UnreadableActionNodeException Thrown if there is a problem reading the action node file
	 * @throws IOException Thrown if there is an error accessing a file relating to the command
	 * @throws FileNotFoundException Thrown if there is an error accessing a file relating to the command
	 */
	public CommandNode(List<Word> command) throws UnknownWordsException, UnreadableActionNodeException, NotAnActionNodeException, FileNotFoundException, IOException
	{
		boolean understood = true;
		List<Word> unrecognizedWords = new ArrayList<>();
		words = new ArrayList<Word>();
		words.addAll(command);
		
		ThingNode directObject = null;
		ThingNode indirectObject = null;
		List<Adverb> adverbs = new ArrayList<>();
		// Construct thing trees first so the thing nodes can be passed to action nodes as needed. Simultaneously collect adverbs
		for(Word word : words)
		{
			if(word.getType() == WordType.NOUN || word.getType() == WordType.SINGULAR_NOUN || word.getType() == WordType.PLURAL_NOUN
					|| word.getType() == WordType.PROPER_NOUN)
			{
				try
				{
					addThingTree(word);
				} catch(UnknownThingException e)
				{
					unrecognizedWords.add(new Noun(e.getMessage()));
					understood = false;
				}
			} else if(word.getType() == WordType.DIRECT_OBJECT)
			{
				try
				{
					directObject = addThingTree(word);
				} catch(UnknownThingException e)
				{
					unrecognizedWords.add(new Noun(e.getMessage()));
					understood = false;
				}
			} else if(word.getType() == WordType.INDIRECT_OBJECT)
			{
				try
				{
					indirectObject = addThingTree(word);
				} catch(UnknownThingException e)
				{
					unrecognizedWords.add(new Noun(e.getMessage()));
					understood = false;
				}
			} else if(word.getType() == WordType.ADVERB)
			{
				adverbs.add((Adverb) word);
			}
		}
		try
		{
			for(Word word : words)
			{
				if(word.getType() == WordType.VERB)
				{
					addActionTree(word, directObject, indirectObject, adverbs);
					break;
				}
			}
		} catch(UnknownActionException e)
		{
			unrecognizedWords.add(new Verb(e.getMessage()));
			understood = false;
		}
		if(!understood)
		{
			throw new UnknownWordsException(unrecognizedWords);
		}
	}
	
	private ThingNode addThingTree(Word word) throws FileNotFoundException, UnknownThingException, IOException
	{
		KnowledgePacket thingKnowledge = KnowledgeAccessor.getInstance().getNounKnowledge(word.toString());
		ThingNode newThing = new ThingNode(word, null, thingKnowledge.getCategories(), thingKnowledge.getAttributes(), 
				thingKnowledge.getModels(), thingKnowledge.getPrimaryModelName(), thingKnowledge.getInstructionInterpreter(), thingKnowledge.getConfidence(), 
				thingKnowledge.getElements()); 
		thingTrees.add(newThing);
		if(newThing.hasAttribute("Goals"))
		{
			String[] goals = newThing.getAttribute("Goals").split(",");
			for(String goal : goals)
			{
				this.goals.add(goal);
			}
		} else if(newThing.hasAttribute("goals"))
		{
			String[] goals = newThing.getAttribute("goals").split(",");
			for(String goal : goals)
			{
				this.goals.add(goal);
			}
		} else if(newThing.hasAttribute("Goal"))
		{
			String[] goals = newThing.getAttribute("Goal").split(",");
			for(String goal : goals)
			{
				this.goals.add(goal);
			}
		} else if(newThing.hasAttribute("goal"))
		{
			String[] goals = newThing.getAttribute("goal").split(",");
			for(String goal : goals)
			{
				this.goals.add(goal);
			}
		}
		return newThing;
	}
	
	private void addActionTree(Word word, ThingNode directObject, ThingNode indirectObject, List<Adverb> adverbs) throws UnknownActionException, UnreadableActionNodeException, 
	NotAnActionNodeException, FileNotFoundException, IOException
	{
		actionTree = ActionNodeLoader.getInstance().loadNode(word.toString(), this, directObject, indirectObject, adverbs);
	}
	
	public NodeType getType()
	{
		return NodeType.COMMAND_NODE;
	}

	@Override
	public void run() 
	{
		List<InstructionPacket> thingsToDo = new ArrayList<>();
		thingsToDo.add(new InstructionPacket(new Instruction(InstructionType.START, null), null));
		while(act(thingsToDo))
		{
			List<InstructionPacket> instructions = actionTree.run();
			thingsToDo = aggregate(instructions);
		}
	}
	
	private boolean act(List<InstructionPacket> thingsToDo)
	{
		if(thingsToDo.isEmpty())
		{
			return false;
		} else
		{
			for(InstructionPacket instructionPacket : thingsToDo)
			{
				Instruction instruction = instructionPacket.getInstruction();
				try 
				{
					InstructionDoer.getInstance().act(instructionInterpreter.interpret(instruction));
					if(!goals.isEmpty())
					{
						//TODO Code learning from goals
					}
				} catch (AWTException e) 
				{
					System.err.println("Failed to run instruction " + instruction.toString());
					System.err.println(e.getMessage());
				} catch (IOException e) {
					System.err.println("Couldn't open a file");
					System.err.println(e.getMessage());
				}
			}
			return true;
		}
	}
	
	private List<InstructionPacket> aggregate(List<InstructionPacket> possibleInstructions)
	{
		//TODO Code aggregation (currently working on)
		// Find instructions with matching types and combine
		List<List<InstructionPacket>> matchedPackets = new ArrayList<>();
		for(InstructionPacket freePacket : possibleInstructions)
		{
			boolean added = false;
			for(List<InstructionPacket> packetGroup : matchedPackets)
			{
				if(freePacket.getInstruction().getType() == packetGroup.get(0).getInstruction().getType())
				{
					packetGroup.add(freePacket);
					added = true;
					break;
				}
			}
			if(!added)
			{
				List<InstructionPacket> newGroup = new ArrayList<>();
				newGroup.add(freePacket);
				matchedPackets.add(newGroup);
			}
		}
		List<Instruction> aggregatedInstructions = new ArrayList<>();
		for(List<InstructionPacket> match : matchedPackets)
		{
			aggregatedInstructions.add(InstructionAggregator.getInstance().aggregateSameInstructionType(match));
		}
		// Of the remaining instructions, determine if any get interpreted to the same type of action, and for any which do so determine which to select
		// Store nodes involved so they can be graded
		return null;
	}
	
	/**
	 * Gets the named thing used in running the process tree
	 * @param name The name of the thing being searched for
	 * @return The thing node named, or null if it can't be found
	 */
	public ThingNode getThing(String name)
	{
		for(ThingNode thingNode : thingTrees)
		{
			ThingNode result = thingNode.getThing(name);
			if(result != null)
			{
				return result;
			}
		}
		return null;
	}
	
	// Old code
	{
//	private ThingNode[] things;
//	private ActionNode[] actions;
//	
//	@Expose
//	private String[] thingNames;
//	@Expose
//	private String[] actionNames;
//	
//	/**
//	 * Default command node constructor
//	 */
//	public CommandNode()
//	{
//	}
//	
//	private CommandNode(String command)
//	{
//		setName(command);
//	}
//	
//	/**
//	 * Creates a new command node. Currently, assumes two word commands of form VERB NOUN  
//	 * @param command The command to be built
//	 * @throws UnknownCommandException Thrown if the command cannot be even partially interpreted
//	 * @throws UnknownActionException Thrown if the action involved in the command cannot be interpreted
//	 * @throws UnknownThingException Thrown if the thing involved in the command cannot be interpreted
//	 */
//	public CommandNode(String[] command) throws UnknownCommandException, UnknownActionException, UnknownThingException
//	{
//		StringBuilder nameBuilder = new StringBuilder();
//		
//		nameBuilder.append(command[0]);
//		for(int i = 1; i < command.length; i++)
//		{
//			nameBuilder.append(" ");
//			nameBuilder.append(command[i]);
//		}
//		setName(nameBuilder.toString());
//		
//		things = new ThingNode[1];
//		actions = new ActionNode[1];
//		
//		boolean goodThing = true;
//		try 
//		{
//			things[0] = NodeRetriever.getInstance().getThingNode(command[0]);
//		} catch (UnknownThingException e) 
//		{
//			goodThing = false;
//		}
//		try 
//		{
//			actions[0] = NodeRetriever.getInstance().getActionNode(command[1]);
//		} catch (UnknownActionException e) 
//		{
//			if(!goodThing)
//			{
//				StringBuilder builder = new StringBuilder();
//				builder.append(command[0]);
//				builder.append(" ");
//				builder.append(command[1]);
//				throw new UnknownCommandException(builder.toString());
//			} else
//			{
//				throw e;
//			}
//		}
//		if(!goodThing)
//		{
//			throw new UnknownThingException(command[0]);
//		}
//	}
//	
//	/**
//	 * Creates a dummy command node for use in testing
//	 * @param command The command that the dummy command node will use
//	 * @return A dummy command node
//	 */
//	public static CommandNode generateTestCommandNode(String[] command)
//	{
//		StringBuilder builder = new StringBuilder();
//		builder.append(command[0]);
//		for(int i = 1; i < command.length; i++)
//		{
//			builder.append(" ");
//			builder.append(command[i]);
//		}
//		return new CommandNode(builder.toString());
//	}
//	
//	/**
//	 * Initializes command nodes loaded from memory (not on the fly)
//	 * @param thing The thing involved with the command
//	 * @throws UnknownThingException 
//	 * @throws UnknownActionException 
//	 */
//	public void initialize(ThingNode thing) throws UnknownThingException, UnknownActionException
//	{
//		things = new ThingNode[thingNames.length];
//		actions = new ActionNode[actionNames.length];
//		for(int i = 0; i < things.length; i++)
//		{
//			things[i] = thing.getThing(thingNames[i]);
//		}
//		for(int i = 0; i < actions.length; i++)
//		{
//			actions[i] = NodeRetriever.getInstance().getActionNode(actionNames[i]);
//		}
//	}
//	
//	/**
//	 * Runs the root command node
//	 */
//	public void run()
//	{
//		// Run the action node, passing the thing node(s) as (a) parameter(s)
//		while(true)
//		{
//			ArrayList<Suggestion> currentSuggestions = new ArrayList<>();
//			boolean finish = true;
//			// Collect the current suggestions
//			for(ActionNode actionNode : actions)
//			{
//				for(Suggestion suggestion : actionNode.getSuggestions())
//				{
//					// If the instruction is to finish, don't add the suggestion. If all suggestions are FINISH we will quit running
//					if(suggestion.getInstruction().getType() != InstructionType.FINISH)
//					{
//						currentSuggestions.add(suggestion);
//						finish = false;
//					}
//				}
//			}
//			if(finish)
//			{
//				break;
//			} else
//			{
//				// Determine which instructions to run
//				ArrayList<Instruction> instructions = new ArrayList<>();
//				Set<ActionNode> actingNodes = new HashSet<>();
//				while(!currentSuggestions.isEmpty())
//				{
//					// Remove the first suggestion from the list
//					Suggestion suggestion = currentSuggestions.remove(0);
//					// Determine which other suggestions match the type (and can be combined) 
//					Set<Suggestion> matchingSuggestions = new HashSet<>();
//					Set<Suggestion> competingSuggestions = new HashSet<>();
//					for(Suggestion otherSuggestion : currentSuggestions)
//					{
//						if(suggestion.getInstruction().getType() == otherSuggestion.getInstruction().getType())
//						{
//							matchingSuggestions.add(otherSuggestion);
//						} else if(suggestion.getInstruction().conflictsWith(otherSuggestion.getInstruction()))
//						{
//							competingSuggestions.add(otherSuggestion);
//						}
//					}
//					// Remove any suggestions which matched with or competed against the suggestion
//					currentSuggestions.removeAll(matchingSuggestions);
//					currentSuggestions.removeAll(competingSuggestions);
//					// Combine and compare with competing suggestions
//					ArrayList<WeightedInstruction> matches = new ArrayList<>();
//					for(Suggestion matchingSuggestion : matchingSuggestions)
//					{
//						double matchingWeight = matchingSuggestion.getPriority() * matchingSuggestion.getUrgency() * matchingSuggestion.getSuggestor().getScore();
//						matches.add(new WeightedInstruction(matchingWeight, matchingSuggestion.getSuggestor(), matchingSuggestion.getInstruction()));
//					}
//					double weight = suggestion.getPriority() * suggestion.getUrgency() * suggestion.getSuggestor().getScore();
//					ReferencedInstruction result = suggestion.getInstruction().combineWeightedInstructions(weight, suggestion.getSuggestor(), matches);
//					ArrayList<ReferencedInstruction> competingResults = combineCompetition(competingSuggestions);
//					result = result.getInstruction().compareReferencedInstructions(result.getWeight(), result.getNodes(), competingResults);
//					instructions.add(result.getInstruction());
//					actingNodes.addAll(result.getNodes());
//				}
//				InstructionDoer.getInstance().doWhatItsTold(instructions);
//			}
//		}
//	}
//	
//	public Suggestion[] getSuggestions()
//	{
//		ArrayList<Suggestion> currentSuggestions = new ArrayList<>();
//		// Collect the current suggestions
//		for(ActionNode actionNode : actions)
//		{
//			for(Suggestion suggestion : actionNode.getSuggestions())
//			{
//				// If the instruction is to finish, don't add the suggestion. If all suggestions are FINISH we will quit running
//				if(suggestion.getInstruction().getType() != InstructionType.FINISH)
//				{
//					currentSuggestions.add(suggestion);
//				}
//			}
//		}
//		Suggestion[] suggestions = new Suggestion[currentSuggestions.size()];
//		return currentSuggestions.toArray(suggestions);
//	}
//	
//	/**
//	 * Combines suggestions and transforms them into a list of Referenced Instructions
//	 * @param suggestions Suggestions which may match
//	 * @return A list of referenced instructions, possibly combining some suggestions
//	 */
//	private ArrayList<ReferencedInstruction> combineCompetition(Set<Suggestion> suggestions)
//	{
//		ArrayList<ReferencedInstruction> results = new ArrayList<>();
//		ArrayList<ArrayList<Suggestion>> matchedSuggestions = new ArrayList<>();
//		for(Suggestion suggestion : suggestions)
//		{
//			boolean added = false;
//			for(ArrayList<Suggestion> matches : matchedSuggestions)
//			{
//				if(matches.get(0).getInstruction().getType() == suggestion.getInstruction().getType())
//				{
//					matches.add(suggestion);
//					added = true;
//					break;
//				}
//			}
//			if(!added)
//			{
//				ArrayList<Suggestion> newMatch = new ArrayList<>();
//				newMatch.add(suggestion);
//				matchedSuggestions.add(newMatch);
//			}
//		}
//		for(ArrayList<Suggestion> matches : matchedSuggestions)
//		{
//			Suggestion firstMatch = matches.get(0);
//			double weight = firstMatch.getPriority() * firstMatch.getUrgency() * firstMatch.getSuggestor().getScore();
//			ArrayList<WeightedInstruction> weightedInstructions = new ArrayList<>();
//			for(int i = 1; i < matches.size(); i++)
//			{
//				double otherWeight = matches.get(i).getPriority() * matches.get(i).getUrgency() * matches.get(i).getSuggestor().getScore();
//				weightedInstructions.add(new WeightedInstruction(otherWeight, matches.get(i).getSuggestor() , matches.get(i).getInstruction()));
//			}
//			results.add(firstMatch.getInstruction().combineWeightedInstructions(weight, firstMatch.getSuggestor(), weightedInstructions));
//		}
//		return results;
//	}
//	
//	/**
//	 * Gives the highest level thing node
//	 * @return The highest level thing node
//	 */
//	public ThingNode getThing()
//	{
//		return things[0];
//	}
//	
//	/**
//	 * Returns the requested thing, looking based on the string
//	 * @param thing The name of the string being searched for
//	 * @return The thing requested, or null if it's not in the thing tree
//	 */
//	public ThingNode getThing(String thing)
//	{
//		ThingNode theThing = null;
//		for(ThingNode thingNode : things)
//		{
//			if(thingNode.getName().equalsIgnoreCase(thing))
//			{
//				return thingNode;
//			}
//		}
//		for(ThingNode thingNode : things)
//		{
//			theThing = thingNode.getThing(thing);
//			if(theThing != null)
//			{
//				break;
//			}
//		}
//		return theThing;
//	}
//	
//	/**
//	 * Gives the highest level action node
//	 * @return The highest level action node
//	 */
//	ActionNode getAction()
//	{
//		return actions[0];
//	}
//	
////	/**
////	 * Returns the requested object, looking based on the provided string
////	 * @param action The name of the action being searched for
////	 * @return The action node requested, or null if it's not in the action tree
////	 */
////	ActionNode getAction(String action)
////	{
////		ActionNode theAction = null;
////		for(ActionNode actionNode : actions)
////		{
////			if(actionNode.getName().equalsIgnoreCase(action))
////			{
////				return actionNode;
////			}
////		}
////		for(ActionNode actionNode : actions)
////		{
////			theAction = actionNode.getAction(action);
////			if(theAction != null)
////			{
////				break;
////			}
////		}
////		return theAction;
////	}
//	
//	/**
//	 * Returns the requested process node from the command node's trees
//	 * @param what The name of the node being sought
//	 */
//	public ProcessNode get(String what)
//	{
//		ProcessNode theNode = null;
//		// First, check if the highest-level things and actions match the provided name
//		for(ThingNode thingNode : things)
//		{
//			if(thingNode.getName().equalsIgnoreCase(what))
//			{
//				return thingNode;
//			}
//		}
//		for(ActionNode actionNode : actions)
//		{
//			if(actionNode.getName().equalsIgnoreCase(what))
//			{
//				return actionNode;
//			}
//		}
//		// Then search the thing trees...
//		for(ThingNode thingNode : things)
//		{
//			theNode = thingNode.getThing(what);
//			if(theNode != null)
//			{
//				break;
//			}
//		}
//		// Finally, search the action trees
//		if(theNode == null)
//		{
//			for(ActionNode actionNode : actions)
//			{
//				theNode = actionNode.get(what);
//				if(theNode != null)
//				{
//					break;
//				}
//			}
//		}
//		return theNode;
//	}
	}
		
	

}
