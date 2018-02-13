package thingNodes;

import pacman.Pacman;
import processTree.ProcessNode;
import processTree.ThingNode;
import processTree.toolNodes.AttributeConverter;
import thingNodes.CategoryNodes.GameNode;

import java.awt.*;
import java.util.ArrayList;
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
	PacmanPellets pellets;

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
        pMan = null;
        setAttributes();
    }

	public void startGame()
    {
        if(pMan == null)
        {
            pMan = new Pacman();
        }
        pMan.play();
        addElements();
    }
	
	private void setAttributes()
	{
		setAttribute("goal", "avoid ghosts & approach pellets");
		setAttribute("dimensions", "360,360");
		setAttribute("grid", "24,24");
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
			wLocationBuilder.append(wallX[i] * pMan.getTileSize());
			wLocationBuilder.append(",");
			wLocationBuilder.append(wallY[i] * pMan.getTileSize());
			wall.setAttribute("location", wLocationBuilder.toString());
			walls.addElement(wall);
		}
		addElement(walls);
		pellets = new PacmanPellets(this, null, null, null, 1);
		List<Point> pelletLocations = pMan.getPelletLocationss();
		for(Point pelletLocation : pelletLocations)
        {
            PacmanPellet pellet = new PacmanPellet(pellets, null, null, null, 1);
            StringBuilder locationBuilder = new StringBuilder();
            locationBuilder.append(pelletLocation.x);
            locationBuilder.append(",");
            locationBuilder.append(pelletLocation.y);
            pellet.setAttribute("location", locationBuilder.toString());
            pellets.addElement(pellet);
        }
        addElement(pellets);
		ghosts = new PacmanGhosts(this, null, null, null, 1);
        int[] ghostXs = pMan.getGhostXs();
        ghosts.setAttribute("dimensions", dimensionsBuilder.toString());
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
        List<Point> currentPellets = pMan.getPelletLocationss();
	    List<ThingNode> eatenPellets = new ArrayList<>();
	    for(ThingNode pellet : pellets.getThingElements())
        {
            int[] pelletLocation = AttributeConverter.convertToIntArray(pellet.getAttribute("location"));
            boolean eaten = true;
            for(Point currentPellet : currentPellets)
            {
                if(pelletLocation[0] == currentPellet.x && pelletLocation[1] == currentPellet.y)
                {
                    eaten = false;
                    break;
                }
            }
            if(eaten)
            {
                eatenPellets.add(pellet);
            }
        }
        for(ThingNode eatenPellet : eatenPellets)
        {
            pellets.removeElement(eatenPellet);
        }
//		player.updateLocation(pMan.getPacmanX(), pMan.getPacmanY());
//		player.updateSpeed(pMan.getPacmanSpeed());
        ghosts.setAttributes(getGhostAttributes());
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
