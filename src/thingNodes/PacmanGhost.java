package thingNodes;

import processTree.ProcessNode;
import processTree.ThingNode;

import java.util.List;
import java.util.Map;

public class PacmanGhost extends ThingNode 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6346522976848669458L;
	
	public PacmanGhost(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
	{
		super(parent, elements, categories, attributes, confidence);
		setName("ghost");
		addCategory("enemy");
		addCategory("monster");
		setAttribute("goal", "approach Player");
		setAttribute("move", "both,both");
		setAttribute("behavior","intelligent");
		StringBuilder locationBuilder = new StringBuilder();
//		locationBuilder.append(locationX);
//		locationBuilder.append(',');
//		locationBuilder.append(locationY);
//		setAttribute("location", locationBuilder.toString());
//		StringBuilder dimensionBuilder = new StringBuilder();
//		dimensionBuilder.append(width);
//		dimensionBuilder.append(',');
//		dimensionBuilder.append(height);
//		setAttribute("dimensions", dimensionBuilder.toString());
//		setAttribute("move", "both,both");
//		StringBuilder speedBuilder = new StringBuilder();
//		speedBuilder.append(speed);
//		setAttribute("speed", speedBuilder.toString());
	}
	
//	public void updateLocation(int x, int y)
//	{
//		StringBuilder locationBuilder = new StringBuilder();
//		locationBuilder.append(x);
//		locationBuilder.append(',');
//		locationBuilder.append(y);
//		setAttribute("location", locationBuilder.toString());
//	}

//	public void updateSpeed(int speed)
//	{
//        StringBuilder speedBuilder = new StringBuilder();
//        speedBuilder.append(speed);
//        setAttribute("speed", speedBuilder.toString());
//	}
	
}
