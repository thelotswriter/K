package thingNodes;

import processTree.ThingNode;

public class PacmanGhost extends ThingNode 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6346522976848669458L;
	
	public PacmanGhost(int locationX, int locationY, int width, int height, int speed)
	{
		setName("ghost");
		addCategory("enemy");
		addCategory("monster");
		setAttribute("goal", "approach Player");
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
		setAttribute("move", "both,both");
		StringBuilder speedBuilder = new StringBuilder();
		speedBuilder.append(speed);
		setAttribute("speed", speedBuilder.toString());
	}
	
	public void updateLocation(int x, int y)
	{
		StringBuilder locationBuilder = new StringBuilder();
		locationBuilder.append(x);
		locationBuilder.append(',');
		locationBuilder.append(y);
		setAttribute("location", locationBuilder.toString());
	}

	public void updateSpeed(int speed)
	{
        StringBuilder speedBuilder = new StringBuilder();
        speedBuilder.append(speed);
        setAttribute("speed", speedBuilder.toString());
	}
	
}
