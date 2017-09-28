package thingNodes;

import processTree.ThingNode;

public class PacmanPlayer extends ThingNode 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -844915845220596749L;

	public PacmanPlayer(int locationX, int locationY, int width, int height, int speed, int[] direction)
	{
		setName("Player");
		StringBuilder locationBuilder = new StringBuilder();
		locationBuilder.append(locationX);
		locationBuilder.append(',');
		locationBuilder.append(locationY);
		setAttribute("location", locationBuilder.toString());
		StringBuilder dimensionBuilder = new StringBuilder();
		dimensionBuilder.append(width);
		dimensionBuilder.append(',');
		dimensionBuilder.append(height);
		StringBuilder directionBuilder = new StringBuilder();
		directionBuilder.append(direction[0]);
		directionBuilder.append(',');
		directionBuilder.append(direction[1]);
		setAttribute("dimensions", dimensionBuilder.toString());
		setAttribute("move", "both,both");
		setAttribute("direction",directionBuilder.toString());
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
