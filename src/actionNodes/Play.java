package actionNodes;

import instructions.Action;
import instructions.InstructionPacket;
import instructions.InstructionType;
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
import java.util.ArrayList;
import java.util.List;

public class Play extends ActionNode
{

    public Play(CommandNode root, ThingNode directObject, ThingNode indirectObject, List<Adverb> adverbs, List<ActionElement> elements,
                double confidence, double priority, double urgency) throws NotAnActionNodeException, UnknownActionException, IOException, UnreadableActionNodeException
    {
        super(root, directObject, indirectObject, adverbs, elements, confidence, priority, urgency);
    }

    @Override
    public void initialize() throws NotAnActionNodeException, UnknownActionException, UnreadableActionNodeException, IOException
    {
        ThingNode directObject = getDirectObject();
        if(directObject instanceof GameNode)
        {
            GameNode game = (GameNode) directObject;
            convertGoalsToNodes(game);
            for(ActionNode element : getElements())
            {
                element.initialize();
            }
            game.startGame();
        }
    }

    private void convertGoalsToNodes(GameNode gameNode) throws NotAnActionNodeException, UnknownActionException, IOException, UnreadableActionNodeException {
        String[] goals = gameNode.getAttribute("goal").split(" & ");
        for(String goal : goals)
        {
            String[] goalArray = goal.split(" ");
            ActionNode goalNode = getAction(goalArray);
            addElement(goalNode);
        }
    }

    private ActionNode getAction(String[] goal) throws NotAnActionNodeException, UnknownActionException, UnreadableActionNodeException, IOException {
        if(goal[0].equalsIgnoreCase("avoid"))
        {
            return new Avoid(getRoot(), getDirectObject().getThing("Player"), getDirectObject().getThing(goal[1]), getIndirectObject(),
                    getAdverbs(), null,1, 1, 1);
        }
        return null;
    }

    @Override
    public List<InstructionPacket> run()
    {
        List<InstructionPacket> instructionPackets = new ArrayList<>();
        List<ActionNode> elements = getElements();
        for(ActionNode element : elements)
        {
            List<InstructionPacket> elementPackets = element.run();
            if(elementPackets != null)
            {
                instructionPackets.addAll(elementPackets);
            }
        }
        return instructionPackets;
    }
}
