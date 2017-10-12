package actionNodes;

import instructions.Instruction;
import instructions.InstructionPacket;
import instructions.InstructionType;
import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import knowledgeAccess.ActionElement;
import miniMain.ColorCoordinate;
import miniMain.PlayPacman;
import pacman.Pacman;
import processTree.ActionNode;
import processTree.CommandNode;
import processTree.ThingNode;
import subModel.Discrete2DSpatialModel;
import thingNodes.PacmanGame;
import words.Adverb;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Avoid extends ActionNode
{

    // Weights determining direction. Variable sideWeight is multiplied by the magnitude of the and used to make
    // instructions to go left/right.
    private double sideWeight = 0.01;
    private double momentumWeight = 0.1;

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
        if(PlayPacman.pause_play)
        {
            int x = 0;
            int y = x;
        }
        double[] vector = new double[getDirectObject().getParent().getAttribute("dimensions").split(",").length];
        for(double oneDVector : vector)
        {
            oneDVector = 0;
        }
        double urgency = 0;
        List<double[][]> probMaps = new ArrayList<>();
        for(Discrete2DSpatialModel model : models)
        {
            model.updateLocations();
            probMaps.add(model.generateProbabilityMap());
            double[] currentVector = model.getVector();
            for(int i = 0; i < vector.length; i++)
            {
                vector[i] += currentVector[i]; // The vector given will point toward rather than away
                urgency += Math.abs(currentVector[i]);
            }
        }
        setUrgencey(urgency);
        List<double[]> sideVectors = new ArrayList<>();
        double[] sideVector1 = new double[vector.length];
        double[] sideVector2 = new double[vector.length];
        int varsIndex = -1;
        double sideVectorLength = 0;
        double vectorLength = 0;
        for(int i = 0; i < vector.length; i++)
        {
            if(vector[i] != 0 && varsIndex < 0) // Mark the variable with a -1. We don't want it multiplied by zero
            {
                sideVector1[i] = -1;
                varsIndex = i;
            } else
            {
                sideVector1[i] = 1;
                sideVectorLength++;
            }
            vectorLength += vector[i] * vector[i];
        }
        if(varsIndex < 0)
        {
            return instructionPackets;
        }
        vectorLength = Math.sqrt(vectorLength);
        double unknownDimension = 0;
        for(int i = 0; i < vector.length; i++)
        {
            if(sideVector1[i] == 1)
            {
                unknownDimension -= vector[i];
            }
        }
        unknownDimension /= vector[varsIndex];
        sideVector1[varsIndex] = unknownDimension;
        sideVectorLength += unknownDimension * unknownDimension;
        sideVectorLength = Math.sqrt(sideVectorLength);
        for(int i = 0; i < sideVector1.length; i++)
        {
            sideVector1[i] *= vectorLength / sideVectorLength;
            sideVector2[i] = -1 * sideVector1[i];
        }
        sideVectors.add(sideVector1);
        sideVectors.add(sideVector2);
        for(double[] sideVector : sideVectors)
        {
            if(models.get(0).isAllowableMovement(sideVector))
            {
                List<String> params = new ArrayList<>();
                for(int i = 0; i < sideVector.length; i++)
                {
                    params.add(Double.toString(sideVector[i] * sideWeight));
                }
                instructionPackets.add(new InstructionPacket(new Instruction(InstructionType.MOVE, params), this));
            }
        }
        if(models.get(0).isAllowableMovement(vector))
        {
            List<String> params = new ArrayList<>();
            for(int i = 0; i < vector.length; i++)
            {
                params.add(Double.toString(vector[i]));
            }
            instructionPackets.add(new InstructionPacket(new Instruction(InstructionType.MOVE, params), this));
        }
        drawMaps(probMaps);
        return instructionPackets;
    }

    private void drawMaps(List<double[][]> probabilityMaps)
    {
        Pacman pMan = ((PacmanGame) getDirectObject().getParent()).getGame();

        double[][] finalProbabilityMap = probabilityMaps.get(0);
        for(int i = 1; i < probabilityMaps.size(); i++)
        {
            double[][] probabilityMap = probabilityMaps.get(i);
            for(int j = 0; j < finalProbabilityMap.length; j++)
            {
                for(int k = 0; k < finalProbabilityMap[0].length; k++)
                {
                    finalProbabilityMap[j][k] += probabilityMap[j][k];
                }
            }
        }
        double maxProbability = 0;
        for(int i = 0; i < finalProbabilityMap.length; i++)
        {
            for(int j = 0; j < finalProbabilityMap[0].length; j++)
            {
                if(finalProbabilityMap[i][j] > maxProbability)
                {
                    maxProbability = finalProbabilityMap[i][j];
                }
            }
        }
        ArrayList<ColorCoordinate> coloredCoordinates = new ArrayList<>();
        for(int i = 0; i < finalProbabilityMap.length; i++)
        {
            for(int j = 0; j < finalProbabilityMap[0].length; j++)
            {
                if(finalProbabilityMap[i][j] > 0)
                {
                    double probability = finalProbabilityMap[i][j] / maxProbability;
                    double blueVal = Math.max(1 - 2 * probability, 0);
                    double greenVal = 0;
                    if(probability < 0.5)
                    {
                        greenVal = 2 * probability;
                    } else
                    {
                        greenVal = 2 - 2 * probability;
                    }
                    double redVal = Math.max(2 * probability - 1, 0);
//                    double blueVal = 1 - probability;
//                    double greenVal = 0;
//                    if(probability < 0.5)
//                    {
//                        greenVal = 2 * probability;
//                    } else
//                    {
//                        greenVal = 2 - 2 * probability;
//                    }
//                    double redVal = probability;
                    Color probabilityColor = new Color((float) redVal,
                            (float) greenVal,
                            (float) blueVal);
                    coloredCoordinates.add(new ColorCoordinate(i, j, probabilityColor));
                }
            }
        }
        pMan.drawRunningTiles(coloredCoordinates);
    }

}
