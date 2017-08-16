package processTree;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import instructions.Action;
import instructions.Instruction;
import subModel.DiscreteSpatialModel;
import subModel.SubModel;

public class Model implements Serializable
{
	
	private SubModel subModel;
	
	private ThingNode theThing;
	private ThingNode theWorld;
//	private ThingNode theObject;
//	
//	String goal;
//	private Map<String, String> capabilities;
	private boolean maximize;
	private ModelType type;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5360519631521367354L;
	
	//TODO Code model
	
	/**
	 * Creates a new model based on the given thing and world
	 * @param thingToModel The thing being modeled
	 * @param worldToModel The environment in which the thing exists
	 */
	public Model(ThingNode thingToModel, ThingNode worldToModel)
	{
		theThing = thingToModel;
		theWorld = worldToModel;
		String goal = theThing.getAttribute("goal");
		String[] goalWords = goal.split(" ");
//		theObject = null;
		setSubModel(goalWords[0]);
//		switch(goalWords.length)
//		{
//			case 1:
//			{
//				
//			} default:
//			{
//				
//			}
//		}
//		loadCapabilities();
	}

	private void setSubModel(String command)
	{
//		boolean maximize;
//		if(command.equalsIgnoreCase("touch") || command.equalsIgnoreCase("approach") || command.equalsIgnoreCase("minimize"))
//		{
//			maximize = false;
//		} else
//		{
//			maximize = true;
//		}
//		type = ModelType.DISCRETE_SPATIAL;
		subModel = new DiscreteSpatialModel(theThing, theWorld);
	}
	
	public List<Collection<Instruction>> generateActionSequence()
	{
		return subModel.generateActionSequence();
//		if(maximize)
//		{
//			return generateMaximizingActionSequence();
//		} else
//		{
//			return generateMinimizingActionSequence();
//		}
	}
	
//	private List<Collection<Action>> generateMaximizingActionSequence()
//	{
//		switch(type)
//		{
//			case DISCRETE_SPATIAL:
//			{
//				return generateMaximizingDiscreteSpatialActionSequence();
//			} default:
//			{
//				return null;
//			}
//		}
//	}
//	
//	private List<Collection<Action>> generateMinimizingActionSequence()
//	{
//		switch(type)
//		{
//			case DISCRETE_SPATIAL:
//			{
//				return generateMinimizingDiscreteSpatialActionSequence();
//			} default:
//			{
//				return null;
//			}
//		}
//	}
//	
//	private List<Collection<Action>> generateMaximizingDiscreteSpatialActionSequence()
//	{
//		return null;
//	}
//	
//	private List<Collection<Action>> generateMinimizingDiscreteSpatialActionSequence()
//	{
//		return null;
//	}
	
//	private void loadCapabilities()
//	{
//		capabilities = new HashMap<>();
//		if(theThing.hasAttribute("speed"))
//		{
//			capabilities.put("speed", theThing.getAttribute("speed"));
//		}
//		if(theThing.hasAttribute("move"))
//		{
//			capabilities.put("move", theThing.getAttribute("move"));
//		}
//	}
	
	private enum ModelType
	{
		
		DISCRETE_SPATIAL,
		CONTINUOUS_SPATIAL;
		
	}

}
