package thingNodes;

import processTree.ProcessNode;
import processTree.ThingNode;

import java.util.List;
import java.util.Map;

public class PacmanWall extends ThingNode 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9212764893147538230L;

	public PacmanWall(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
	{
		super(parent, elements, categories, attributes, confidence);
		setName("wall");
		addCategory("obstacle");
//		StringBuilder locationBuilder = new StringBuilder();
//		locationBuilder.append(locationX);
//		locationBuilder.append(',');
//		locationBuilder.append(locationY);
//		setAttribute("location", locationBuilder.toString());
//		StringBuilder dimensionBuilder = new StringBuilder();
//		dimensionBuilder.append(width);
//		dimensionBuilder.append(',');
//		dimensionBuilder.append(height);
//		setAttribute("dimensions", dimensionBuilder.toString());
	}
	
}
