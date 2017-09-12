package thingNodes;

import processTree.ThingNode;
import processTree.ThingsNode;

import java.util.List;

public class PacmanGhosts extends ThingsNode
{

    public PacmanGhosts()
    {
        super();
        setName("ghosts");
        addCategory("enemy");
        addCategory("monster");
    }

    public void updateLocations(int[] ghostXs, int[] ghostYs)
    {
        List<ThingNode> ghosts = getElements();
        for(int i = 0; i < ghosts.size(); i++)
        {
            ((PacmanGhost) ghosts.get(i)).updateLocation(ghostXs[i], ghostYs[i]);
        }
    }

    public void updateSpeeds(int[] ghostSpeeds)
    {
        List<ThingNode> ghosts = getElements();
        for(int i = 0; i < ghosts.size(); i++)
        {
            ((PacmanGhost) ghosts.get(i)).updateSpeed(ghostSpeeds[i]);
        }
    }

}
