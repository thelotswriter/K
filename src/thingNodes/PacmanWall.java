package thingNodes;

import processTree.ThingNode;

public class PacmanWall extends ThingNode 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -9212764893147538230L;

	public PacmanWall(int locationX, int locationY, int width, int height)
	{
		setName("wall");
		addCategory("obstacle");
		StringBuilder locationBuilder = new StringBuilder();
		locationBuilder.append(locationX);
		locationBuilder.append(',');
		locationBuilder.append(locationY);
		setAttribute("location", locationBuilder.toString());
		StringBuilder dimensionBuilder = new StringBuilder();
		dimensionBuilder.append(width);
		dimensionBuilder.append(',');
		dimensionBuilder.append(height);
		setAttribute("dimensions", dimensionBuilder.toString());
	}
	
}
