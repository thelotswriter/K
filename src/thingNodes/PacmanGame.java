package thingNodes;

import pacman.Pacman;
import processTree.ThingNode;
import thingNodes.CategoryNodes.GameNode;

public class PacmanGame extends GameNode
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -331355112898903506L;
	
	Pacman pMan;
	PacmanPlayer player;
	PacmanGhosts ghosts;

	public PacmanGame()
    {
        setName("PacmanGame");
        pMan = new Pacman();
        setAttributes();
        addElements();
    }
	
	public PacmanGame(Pacman pacman)
	{
		setName("PacmanGame");
		pMan = pacman;
		setAttributes();
		addElements();
	}

	public void startGame()
    {
        pMan.play();
    }
	
	private void setAttributes()
	{
		setAttribute("goal", "avoid ghosts");
		setAttribute("dimensions", "360,360");
		StringBuilder tileBuilder = new StringBuilder();
		tileBuilder.append(pMan.getTileSize());
		tileBuilder.append(',');
		tileBuilder.append(pMan.getTileSize());
		setAttribute("grid", tileBuilder.toString());
	}
	
	private void addElements()
	{
		player = new PacmanPlayer(pMan.getPacmanX(), pMan.getPacmanY(), pMan.getTileSize(), pMan.getTileSize(),
				pMan.getPacmanSpeed(), pMan.getPacmanDirection());
		addElement(player);
		PacmanWalls walls = new PacmanWalls(this);
		int[] wallX = pMan.getWallX();
		int[] wallY = pMan.getWallY();
		for(int i = 0; i < wallX.length; i++)
		{
			PacmanWall wall = new PacmanWall(wallX[i] * pMan.getTileSize(), wallY[i] * pMan.getTileSize(), 
					pMan.getTileSize(), pMan.getTileSize());
			walls.addElement(wall);
		}
		addElement(walls);
		ghosts = new PacmanGhosts(this);
        StringBuilder ghostDimensionsBuilder = new StringBuilder();
        ghostDimensionsBuilder.append(pMan.getTileSize());
        ghostDimensionsBuilder.append(',');
        ghostDimensionsBuilder.append(pMan.getTileSize());
        int[] ghostXs = pMan.getGhostXs();
        int[] ghostYs = pMan.getGhostYs();
		for(int i = 0; i < ghostXs.length; i++)
        {
            PacmanGhost ghost = new PacmanGhost(ghosts, pMan.getGhostX(i), pMan.getGhostY(i), pMan.getTileSize(), pMan.getTileSize(),
                    pMan.getPacmanSpeed());
            StringBuilder ghostLocationBuilder = new StringBuilder();
            ghostLocationBuilder.append(pMan.getGhostX(i));
            ghostLocationBuilder.append(',');
            ghostLocationBuilder.append(pMan.getGhostY(i));
            ghost.setAttribute("location", ghostLocationBuilder.toString());

            ghost.setAttribute("dimensions", ghostDimensionsBuilder.toString());
            ghosts.addElement(ghost);
        }
		addElement(ghosts);
	}
	
	public void update()
	{
		player.updateLocation(pMan.getPacmanX(), pMan.getPacmanY());
		player.updateSpeed(pMan.getPacmanSpeed());
		ghosts.updateLocations(pMan.getGhostXs(), pMan.getGhostYs());
		ghosts.updateSpeeds(pMan.getGhostSpeeds());
	}

	public Pacman getGame()
	{
		return pMan;
	}
	
}
