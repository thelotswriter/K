package subModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import instructions.Instruction;
import processTree.ThingNode;

public abstract class SubModel 
{
	
	private ThingNode theThing;
	private ThingNode theWorld;
	private ThingNode theObject;
	
	String goal;
	private Map<String, String> capabilities;
	private Map<String, String> objectCapabilities;
	
	public SubModel(ThingNode thing, ThingNode world)
	{
		theThing = thing;
		theWorld = world;
		goal = theThing.getAttribute("goal");
		String[] goalWords = goal.split(" ");
		theObject = null;
		switch(goalWords.length)
		{
			case 0:
			{
				break;
			} case 2:
			{
				theObject = theWorld.getThing(goalWords[1]);
			}
		}
		loadCapabilities();
		if(theObject != null)
		{
			loadObjectCapabilities();
		}
	}
	
	/**
	 * Generates a sequence of actions. Each action in a collection is to be executed simultaneously, while each collection is 
	 * executed sequentially
	 * @return A list of collections of actions, meant to achieve some goal
	 */
	public abstract List<Collection<Instruction>> generateActionSequence();
	
	public ThingNode getThing()
	{
		return theThing;
	}
	
	public ThingNode getWorld()
	{
		return theWorld;
	}
	
	public Map<String, String> getCapabilities()
	{
		return capabilities;
	}
	
	public ThingNode getObject()
	{
		return theObject;
	}
	
	private void loadCapabilities()
	{
		capabilities = new HashMap<>();
		if(theThing.hasAttribute("speed"))
		{
			capabilities.put("speed", theThing.getAttribute("speed"));
		}
		if(theThing.hasAttribute("move"))
		{
			capabilities.put("move", theThing.getAttribute("move"));
		}
	}
	
	private void loadObjectCapabilities()
	{
		objectCapabilities = new HashMap<>();
		if(theObject.hasAttribute("speed"))
		{
			objectCapabilities.put("speed", theThing.getAttribute("speed"));
		}
		if(theObject.hasAttribute("move"))
		{
			objectCapabilities.put("move", theThing.getAttribute("move"));
		}
	}
	
}
