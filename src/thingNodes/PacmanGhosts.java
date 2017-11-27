package thingNodes;

import processTree.ProcessNode;
import processTree.ThingNode;
import processTree.ThingsNode;

import java.util.List;
import java.util.Map;

public class PacmanGhosts extends ThingsNode
{

    public PacmanGhosts(ProcessNode parent, List<ThingNode> elements, List<String> categories, Map<String, String> attributes, double confidence)
    {
        super(parent, elements, categories, attributes, confidence);
        setName("ghosts");
        addCategory("enemy");
        addCategory("monster");
        setAttribute("goal", "approach Player");
    }

//    public void updateLocations(int[] ghostXs, int[] ghostYs)
//    {
//        List<ThingNode> ghosts = getElements();
//        for(int i = 0; i < ghosts.size(); i++)
//        {
//            ((PacmanGhost) ghosts.get(i)).updateLocation(ghostXs[i], ghostYs[i]);
//        }
//    }

//    public void updateSpeeds(int[] ghostSpeeds)
//    {
//        List<ThingNode> ghosts = getElements();
//        for(int i = 0; i < ghosts.size(); i++)
//        {
//            ((PacmanGhost) ghosts.get(i)).updateSpeed(ghostSpeeds[i]);
//        }
//    }

}
