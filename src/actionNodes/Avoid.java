package actionNodes;

import instructions.InstructionPacket;
import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import knowledgeAccess.ActionElement;
import processTree.ActionNode;
import processTree.CommandNode;
import processTree.ThingNode;
import subModel.Discrete2DSpatialModel;
import words.Adverb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Avoid extends ActionNode
{

    // Weights determining direction. Variable sideWeight is multiplied by the magnitude of the and used to make
    // instructions to go left/right.
    // Variable reverseWeight is multiplied in the direction opposite the subject's current movement to discourage
    // backtracking (should be < 1)
    private double sideWeight = 0.25;
    private double reverseWeight = 0.8;

    private List<Discrete2DSpatialModel> models;
    private ThingNode subject;

    public Avoid(CommandNode root, ThingNode subject, ThingNode directObject, ThingNode indirectObject, List<Adverb> adverbs, List<ActionElement> elements,
                 double confidence, double priority, double urgency) throws NotAnActionNodeException, UnknownActionException, IOException, UnreadableActionNodeException
    {
        super(root, directObject, indirectObject, adverbs, elements, confidence, priority, urgency);
        this.subject = subject;
    }

    @Override
    public void initialize()
    {
        // Determine which sort of model should be used to avoid. Currently we only have one model to use, so we'll use that.
        // In the future, this may be expanded to use a search tree or some other method to select the best model
        models = new ArrayList<>();
        if(getDirectObject().isPlural())
        {
            for(ThingNode singleObject : getDirectObject().getElements())
            {
                models.add(new Discrete2DSpatialModel(singleObject, getDirectObject().getParent()));
            }
        } else
        {
            models.add(new Discrete2DSpatialModel(getDirectObject(), getDirectObject().getParent()));
        }
    }

    @Override
    public List<InstructionPacket> run()
    {
        List<InstructionPacket> instructionPackets = new ArrayList<>();
        double[] vector = new double[getDirectObject().getAttribute("dimensions").split(",").length];
        for(double oneDVector : vector)
        {
            oneDVector = 0;
        }
        double urgency = 0;
        for(Discrete2DSpatialModel model : models)
        {
            model.generateProbabilityMap();
            double[] currentVector = model.getVector();
            for(int i = 0; i < vector.length; i++)
            {
                vector[i] += currentVector[i];
                urgency += Math.abs(currentVector[i]);
            }
        }
        setUrgencey(urgency);
        List<double[]> sideVectors = new ArrayList<>();
        double[] sideVector1 = new double[vector.length];
        double[] sideVector2 = new double[vector.length];
        int varsRemaining = 0;
        for(int i = 0; i < vector.length; i++)
        {
            if(vector[i] == 0)
            {
                sideVector1[i] = 1;
            } else
            {
                sideVector1[i] = 0;
                varsRemaining++;
            }
        }
        return instructionPackets;
    }
}
