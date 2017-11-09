package thingNodes;

import pacman.Pacman;
import processTree.ProcessNode;
import processTree.ThingNode;
import thingNodes.CategoryNodes.GameNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PacmanGame extends GameNode
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -331355112898903506L;
	
	Pacman pMan;
	PacmanPlayer player;
	PacmanGhosts ghosts;

//	public PacmanGame()
//    {
//        setName("PacmanGame");
//        pMan = new Pacman();
//        setAttributes();
//        addElements();
//    }
//
//	public PacmanGame(Pacman pacman)
//	{
//		setName("PacmanGame");
//		pMan = pacman;
//		setAttributes();
//		addElements();
//	}

	public PacmanGame(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, elements, categories, attributes, confidence);
        setName("PacmanGame");
        pMan = new Pacman();
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
//		player = new PacmanPlayer(pMan.getPacmanX(), pMan.getPacmanY(), pMan.getTileSize(), pMan.getTileSize(),
//				pMan.getPacmanSpeed(), pMan.getPacmanDirection());
        player = new PacmanPlayer(this, null, null, getPlayerAttributes(), 1);
        StringBuilder dimensionsBuilder = new StringBuilder();
        dimensionsBuilder.append(pMan.getTileSize());
        dimensionsBuilder.append(',');
        dimensionsBuilder.append(pMan.getTileSize());
        player.setAttribute("dimensions", dimensionsBuilder.toString());
		addElement(player);
		PacmanWalls walls = new PacmanWalls(this, null, null, null, 1);
		int[] wallX = pMan.getWallX();
		int[] wallY = pMan.getWallY();
		for(int i = 0; i < wallX.length; i++)
		{
			PacmanWall wall = new PacmanWall(walls, null, null, null, 1);
			wall.setAttribute("dimensions", dimensionsBuilder.toString());
			StringBuilder wLocationBuilder = new StringBuilder();
			wLocationBuilder.append(wallX[i]);
			wLocationBuilder.append(",");
			wLocationBuilder.append(wallY[i]);
			wall.setAttribute("location", wLocationBuilder.toString());
			walls.addElement(wall);
		}
		addElement(walls);
		ghosts = new PacmanGhosts(this, null, null, null, 1);
        int[] ghostXs = pMan.getGhostXs();
		for(int i = 0; i < ghostXs.length; i++)
        {
            PacmanGhost ghost = new PacmanGhost(ghosts, null, null, null, 1);
            ghost.setAttribute("dimensions", dimensionsBuilder.toString());
            ghosts.addElement(ghost);
        }
        ghosts.setAttributes(getGhostAttributes());
		addElement(ghosts);
	}
	
	public void update()
	{
	    Map<String, String> pAttributes = getPlayerAttributes();
	    for(String key : pAttributes.keySet())
        {
            player.setAttribute(key, pAttributes.get(key));
        }
//		player.updateLocation(pMan.getPacmanX(), pMan.getPacmanY());
//		player.updateSpeed(pMan.getPacmanSpeed());
        Map<String, String[]> gAttributes = getGhostAttributes();
        for(String key : gAttributes.keySet())
        {
            ghosts.setAttribute(key, pAttributes.get(key));
        }
//		ghosts.updateLocations(pMan.getGhostXs(), pMan.getGhostYs());
//		ghosts.updateSpeeds(pMan.getGhostSpeeds());
	}

	public Pacman getGame()
	{
		return pMan;
	}

	private Map<String, String> getPlayerAttributes()
    {
        Map<String, String> pAttributes = new HashMap<>();
        StringBuilder locationBuilder = new StringBuilder();
        locationBuilder.append(pMan.getPacmanX());
        locationBuilder.append(",");
        locationBuilder.append(pMan.getPacmanY());
        pAttributes.put("location", locationBuilder.toString());
        StringBuilder speedBuilder = new StringBuilder();
        speedBuilder.append(pMan.getPacmanSpeed());
        pAttributes.put("speed", speedBuilder.toString());
        return pAttributes;
    }

    private Map<String, String[]> getGhostAttributes()
    {
        Map<String, String[]> gAttributes = new HashMap<>();
        int[] gLocationXs = pMan.getGhostXs();
        int[] gLocationYs = pMan.getGhostYs();
        String[] gLocations = new String[gLocationXs.length];
        for(int i = 0; i < gLocations.length; i++)
        {
            StringBuilder gLocationBuilder = new StringBuilder();
            gLocationBuilder.append(gLocationXs[i]);
            gLocationBuilder.append(",");
            gLocationBuilder.append(gLocationYs[i]);
            gLocations[i] = gLocationBuilder.toString();
        }
        gAttributes.put("location", gLocations);
        int[] gSpeeds = pMan.getGhostSpeeds();
        String[] gSpeedStrings = new String[gSpeeds.length];
        for(int i = 0; i < gSpeedStrings.length; i++)
        {
            gSpeedStrings[i] = Integer.toString(gSpeeds[i]);
        }
        gAttributes.put("speed", gSpeedStrings);
        return gAttributes;
    }
	
}
