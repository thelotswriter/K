package thingNodes;

import processTree.ProcessNode;
import processTree.ThingNode;
import processTree.ThingsNode;

import java.util.List;
import java.util.Map;

public class PacmanWalls extends ThingsNode 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4787457181386142963L;

	public PacmanWalls(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
	{
		super(parent, elements, categories, attributes, confidence);
		setName("walls");
		addCategory("obstacle");
	}
	
}
