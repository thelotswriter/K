package thingNodes;

import pacman.Pacman;
import processTree.ThingNode;

public class PacmanGame extends ThingNode 
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -331355112898903506L;
	
	Pacman pMan;
	PacmanPlayer player;
	PacmanGhost ghost;
	
	public PacmanGame(Pacman pacman)
	{
		setName("PacmanGame");
		pMan = pacman;
		setAttributes();
		addElements();
	}
	
	private void setAttributes()
	{
		setAttribute("dimensions", "360,360");
		StringBuilder tileBuilder = new StringBuilder();
		tileBuilder.append(pMan.getTileSize());
		tileBuilder.append(',');
		tileBuilder.append(pMan.getTileSize());
		setAttribute("grid", tileBuilder.toString());
	}
	
	private void addElements()
	{
		player = new PacmanPlayer(pMan.getPacmanX(), pMan.getPacmanY(), pMan.getTileSize(), pMan.getTileSize(), pMan.getPacmanSpeed());
		addElement(player);
		PacmanWalls walls = new PacmanWalls();
		int[] wallX = pMan.getWallX();
		int[] wallY = pMan.getWallY();
		for(int i = 0; i < wallX.length; i++)
		{
			PacmanWall wall = new PacmanWall(wallX[i] * pMan.getTileSize(), wallY[i] * pMan.getTileSize(), 
					pMan.getTileSize(), pMan.getTileSize());
			walls.addElement(wall);
		}
		addElement(walls);
		ghost = new PacmanGhost(pMan.getGhostX(0), pMan.getGhostY(0), pMan.getTileSize(), pMan.getTileSize(),
				pMan.getPacmanSpeed());
		StringBuilder ghostLocationBuilder = new StringBuilder();
		ghostLocationBuilder.append(pMan.getGhostX(0));
		ghostLocationBuilder.append(',');
		ghostLocationBuilder.append(pMan.getGhostY(0));
		ghost.setAttribute("location", ghostLocationBuilder.toString());
		StringBuilder ghostDimensionsBuilder = new StringBuilder();
		ghostDimensionsBuilder.append(pMan.getTileSize());
		ghostDimensionsBuilder.append(',');
		ghostDimensionsBuilder.append(pMan.getTileSize());
		ghost.setAttribute("dimensions", ghostDimensionsBuilder.toString());
		addElement(ghost);
	}
	
	public void update()
	{
		player.updateLocation(pMan.getPacmanX(), pMan.getPacmanY());
		player.updateSpeed(pMan.getPacmanSpeed());
		ghost.updateLocation(pMan.getGhostX(0), pMan.getGhostY(0));
		ghost.updateSpeed(pMan.getGhostSpeed());
	}
	
}
