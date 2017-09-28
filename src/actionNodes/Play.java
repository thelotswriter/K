package actionNodes;

import instructions.InstructionPacket;
import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import knowledgeAccess.ActionElement;
import processTree.ActionNode;
import processTree.CommandNode;
import processTree.ThingNode;
import thingNodes.CategoryNodes.GameNode;
import words.Adverb;

import java.io.IOException;
import java.util.List;

public class Play extends ActionNode
{

    public Play(CommandNode root, ThingNode directObject, ThingNode indirectObject, List<Adverb> adverbs, List<ActionElement> elements,
                double confidence, double priority, double urgency) throws NotAnActionNodeException, UnknownActionException, IOException, UnreadableActionNodeException
    {
        super(root, directObject, indirectObject, adverbs, elements, confidence, priority, urgency);
    }

    @Override
    public void initialize()
    {
        ThingNode directObject = getDirectObject();
        if(directObject instanceof GameNode)
        {
            GameNode game = (GameNode) directObject;
            convertGoalsToNodes(game);
            game.startGame();
        }
    }

    private void convertGoalsToNodes(GameNode gameNode)
    {
        String[] goals = gameNode.getAttribute("goal").split(" & ");
        for(String goal : goals)
        {
            String[] goalArray = goal.split(" ");
            
        }
    }

    private ActionNode getAction(String[] goal)
    {
        return null;
    }

    @Override
    public List<InstructionPacket> run() {
        return null;
    }
}
