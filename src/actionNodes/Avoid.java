package actionNodes;

import instructions.Instruction;
import instructions.InstructionPacket;
import instructions.InstructionType;
import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import knowledgeAccess.ActionElement;
import processTree.ActionNode;
import processTree.CommandNode;
import processTree.ProcessNode;
import processTree.ThingNode;
import processTree.subActionNodes.PlannableActionNode;
import processTree.toolNodes.Model;
import processTree.toolNodes.ModelPicker;
import subModel.Discrete2DSpatialModel;
import words.Adverb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Avoid extends PlannableActionNode
{

    // Weights determining direction. Variable sideWeight is multiplied by the magnitude of the and used to make
    // instructions to go left/right.
    private double sideWeight = 0.01;
    private double momentumWeight = 0.1;

    private List<Model> models;

    public Avoid(CommandNode root, ActionNode parent, ThingNode subject, ThingNode directObject, ThingNode indirectObject,
                 List<Adverb> adverbs, List<ActionElement> elements, double confidence, double priority, double urgency)
            throws NotAnActionNodeException, UnknownActionException, IOException, UnreadableActionNodeException
    {
        super(root, parent, subject, directObject, indirectObject, adverbs, elements, confidence, priority, urgency);
    }

    @Override
    public void initialize() throws NotAnActionNodeException, UnknownActionException, UnreadableActionNodeException, IOException {
        super.initialize();
        // Determine which sort of model should be used to avoid. Currently we only have one model to use, so we'll use that.
        // In the future, this may be expanded to use a search tree or some other method to select the best model
        models = new ArrayList<>();
        if(getDirectObject().isPlural())
        {
            for(ProcessNode singleObject : getDirectObject().getElements())
            {
                models.add(ModelPicker.getInstance().getModel((ThingNode) getDirectObject().getParent(), getSubject(),
                        (ThingNode) singleObject, getIndirectObject()));
            }
        } else
        {
            models.add(ModelPicker.getInstance().getModel((ThingNode) getDirectObject().getParent(), getSubject(), getDirectObject(), getIndirectObject()));
        }
    }

    @Override
    public List<InstructionPacket> planningRun()
    {
        List<InstructionPacket> instructionPackets = new ArrayList<>();
//        double[] vector = new double[((ThingNode) getDirectObject().getParent()).getAttribute("dimensions").split(",").length];
//        for(double oneDVector : vector)
//        {
//            oneDVector = 0;
//        }
//        double urgency = 0;
//        String[] subjectLocationStrings = getSubject().getAttribute("location").split(",");
//        int[] subjectLocation = new int[subjectLocationStrings.length];
//        for(int i = 0; i < subjectLocation.length; i++)
//        {
//            subjectLocation[i] = Integer.parseInt(subjectLocationStrings[i]);
//        }
//        for(Model model : models)
//        {
//            model.updateLocations();
//            double[] currentVector = model.getVector(subjectLocation);
//            for(int i = 0; i < vector.length; i++)
//            {
//                vector[i] += currentVector[i];
//                urgency += Math.abs(currentVector[i]);
//            }
//        }
//        setUrgencey(urgency);
//        List<double[]> sideVectors = new ArrayList<>();
//        double[] sideVector1 = new double[vector.length];
//        double[] sideVector2 = new double[vector.length];
//        int varsIndex = -1;
//        double sideVectorLength = 0;
//        double vectorLength = 0;
//        for(int i = 0; i < vector.length; i++)
//        {
//            if(vector[i] != 0 && varsIndex < 0) // Mark the variable with a -1. We don't want it multiplied by zero
//            {
//                sideVector1[i] = -1;
//                varsIndex = i;
//            } else
//            {
//                sideVector1[i] = 1;
//                sideVectorLength++;
//            }
//            vectorLength += vector[i] * vector[i];
//        }
//        // If the vector is a zero vector, return an empty list
//        if(varsIndex < 0)
//        {
//            return instructionPackets;
//        }
//        vectorLength = Math.sqrt(vectorLength);
//        double unknownDimension = 0;
//        for(int i = 0; i < vector.length; i++)
//        {
//            if(sideVector1[i] == 1)
//            {
//                unknownDimension -= vector[i];
//            }
//        }
//        unknownDimension /= vector[varsIndex];
//        sideVector1[varsIndex] = unknownDimension;
//        sideVectorLength += unknownDimension * unknownDimension;
//        sideVectorLength = Math.sqrt(sideVectorLength);
//        for(int i = 0; i < sideVector1.length; i++)
//        {
//            sideVector1[i] *= vectorLength / sideVectorLength;
//            sideVector2[i] = -1 * sideVector1[i];
//        }
//        sideVectors.add(sideVector1);
//        sideVectors.add(sideVector2);
//        for(double[] sideVector : sideVectors)
//        {
//            if(models.get(0).isAllowableMovement(subjectLocation, sideVector))
//            {
//                List<String> params = new ArrayList<>();
//                for(int i = 0; i < sideVector.length; i++)
//                {
//                    params.add(Double.toString(sideVector[i] * sideWeight));
//                }
//                instructionPackets.add(new InstructionPacket(new Instruction(InstructionType.MOVE, params), this));
//            }
//        }
//        if(models.get(0).isAllowableMovement(subjectLocation, vector))
//        {
//            List<String> params = new ArrayList<>();
//            for(int i = 0; i < vector.length; i++)
//            {
//                params.add(Double.toString(vector[i]));
//            }
//            instructionPackets.add(new InstructionPacket(new Instruction(InstructionType.MOVE, params), this));
//        }
        String[] subjectLocationStrings = getSubject().getAttribute("location").split(",");
        int[] subjectLocation = new int[subjectLocationStrings.length];
        for(int i = 0; i < subjectLocation.length; i++)
        {
            subjectLocation[i] = Integer.parseInt(subjectLocationStrings[i]);
        }
        double urgencyTotal = 0;
        for(Model model : models)
        {
            double cUrcency = model.getDistance(subjectLocation);
            if(cUrcency == 0)
            {
                urgencyTotal = Double.MAX_VALUE;
            } else
            {
                urgencyTotal += 1 / cUrcency;
            }
        }
        setUrgencey(urgencyTotal);
        return instructionPackets;
    }

}
