package subModel;

import java.util.ArrayList;
import java.util.List;

import instructions.Instruction;
import instructions.InstructionType;
import processTree.ProcessNode;
import processTree.ThingNode;
import processTree.toolNodes.Model;
import structures.ID2DDFS;
import structures.MoveType2D;

public class Discrete2DSpatialModel extends Model
{
    private final int N_DIMENSIONS = 2;
    private final int MAX_SEARCH = 10;
    private final double STEP_COEFFICIENT = 10;

    private ThingNode object;
    private int[] tileDimensions;
    private int[] worldDimensions;
    private int[] thingDimensions;
    private int[] objectDimensions;
    private int[] thingLocation;
    private int[] objectLocation;
    private MoveType2D[] moveCapabilities;
    private MoveType2D[] objectMoveCapabilities;
    private boolean[][] allowedSpaces;
    private double[][] workingMap;
    private double[][] finalMap;
    private int steps;

    /**
     * Creates a model made for a two-dimensional, discrete world
     * @param thing The thing being modeled
     * @param world The world the model exists in
     */
    public Discrete2DSpatialModel(ThingNode thing, ThingNode world)
    {
        super(thing, world);
        object = null;
        String[] goal = thing.getAttribute("goal").split(" ");
        if(goal.length > 1)
        {
            object = world.getThing(goal[1]);
            if(object == null)
            {
                if(goal[1].equalsIgnoreCase("ahead") && goal[1].equalsIgnoreCase("right")
                        && goal[1].equalsIgnoreCase("left") && goal.length > 3)
                {
                    object = world.getThing(goal[3]);
                } else if(goal[1].equalsIgnoreCase("behind") &&  goal.length > 2)
                {
                    object = world.getThing(goal[2]);
                }
            }
        }
        initializeMaps();
        checkMovementCapabilities();
        setTilesToSearch();
        updateLocations();
    }

    /**
     * Loads the maps (allowedSpaces, workingMap, finalMap) with their initial values
     */
    private void initializeMaps()
    {
        tileDimensions = new int[N_DIMENSIONS];
        worldDimensions = new int[N_DIMENSIONS];
        thingDimensions = new int[N_DIMENSIONS];
        objectDimensions = new int[N_DIMENSIONS];
        thingLocation = new int[N_DIMENSIONS];
        objectLocation = new int[N_DIMENSIONS];
        if(object != null)
        {
            for(int i = 0; i < N_DIMENSIONS; i++)
            {
                tileDimensions[i] = Integer.parseInt(getWorld().getAttribute("grid").split(",")[i]);
                worldDimensions[i] = Integer.parseInt(getWorld().getAttribute("dimensions").split(",")[i]);
                thingDimensions[i] = Integer.parseInt(getThing().getAttribute("dimensions").split(",")[i]);
                objectDimensions[i] = Integer.parseInt(object.getAttribute("dimensions").split(",")[i]);
            }
        } else
        {
            for(int i = 0; i < N_DIMENSIONS; i++)
            {
                tileDimensions[i] = Integer.parseInt(getWorld().getAttribute("grid").split(",")[i]);
                worldDimensions[i] = Integer.parseInt(getWorld().getAttribute("dimensions").split(",")[i]);
                thingDimensions[i] = Integer.parseInt(getThing().getAttribute("dimensions").split(",")[i]);
                objectDimensions[i] = tileDimensions[i];
            }
        }

        allowedSpaces = new boolean[worldDimensions[0] / tileDimensions[0]][worldDimensions[1] / tileDimensions[1]];
        workingMap = new double[worldDimensions[0] / tileDimensions[0]][worldDimensions[1] / tileDimensions[1]];
        finalMap = new double[worldDimensions[0] / tileDimensions[0]][worldDimensions[1] / tileDimensions[1]];
        for(int i = 0; i < worldDimensions[0] / tileDimensions[0]; i++)
        {
            for(int j = 0; j < worldDimensions[1] / tileDimensions[1]; j++)
            {
                allowedSpaces[i][j] = true;
            }
        }
        setUnallowedSpaces();
    }

    /**
     * Determines which spaces are blocked and blocks them
     */
    private void setUnallowedSpaces()
    {
        for(ProcessNode pNode : getWorld().getElements())
        {
            ThingNode node = (ThingNode) pNode;
            if(node.getCategories().contains("obstacle"))
            {
                if(node.isPlural())
                {
                    for(ProcessNode singlePNode : node.getElements())
                    {
                        ThingNode singleNode = (ThingNode) singlePNode;
                        blockObstacle(singleNode);
                    }
                } else
                {
                    blockObstacle(node);
                }
            }
        }
    }

    /**
     * Makes sure an obstacle is not an allowed location for a given obstacle
     * @param obstacle An obstacle which things are not allowed to pass
     */
    private void blockObstacle(ThingNode obstacle)
    {
        int x = Integer.parseInt(obstacle.getAttribute("location").split(",")[0]);
        int y = Integer.parseInt(obstacle.getAttribute("location").split(",")[1]);
        int width = Integer.parseInt(obstacle.getAttribute("dimensions").split(",")[0]);
        int height = Integer.parseInt(obstacle.getAttribute("dimensions").split(",")[1]);
        for(int i = x; i < x + width; i++)
        {
            for(int j = y; j < y + height; j++)
            {
                allowedSpaces[i / tileDimensions[0]][j/ tileDimensions[1]] = false;
            }
        }
    }

    /**
     * Loads the movement capabilities of the thing
     */
    private void checkMovementCapabilities()
    {
        moveCapabilities = new MoveType2D[N_DIMENSIONS];
        objectMoveCapabilities = new MoveType2D[N_DIMENSIONS];
        String moveString = getThing().getAttribute("move");
        if(moveString != null)
        {
            String[] moveStrings = moveString.split(",");
            for(int i = 0; i < N_DIMENSIONS; i++)
            {
                moveCapabilities[i] = MoveType2D.stringToMoveType(moveStrings[i]);
            }
        }
        for(int i = 0; i < N_DIMENSIONS; i++)
        {
            objectMoveCapabilities[i] = MoveType2D.NEITHER;
        }
        if(object != null)
        {
            String objectMoveString = object.getAttribute("move");
            if(moveString != null)
            {
                String[] moveStrings = objectMoveString.split(",");
                for(int i = 0; i < N_DIMENSIONS; i++)
                {
                    moveCapabilities[i] = MoveType2D.stringToMoveType(moveStrings[i]);
                }
            }
        }
    }

    /**
     * Sets how many tiles to search based on the speed of the thing and object
     */
    private void setTilesToSearch()
    {
        double thingSpeed = 0;
        double objectSpeed = 0;
        if(getThing().hasAttribute("speed"))
        {
            thingSpeed = Double.parseDouble(getThing().getAttribute("speed"));
        }
        ThingNode object = getWorld().getThing(getThing().getAttribute("goal").split(" ")[1]);
        if(object != null && object.hasAttribute("speed"))
        {
            objectSpeed = Double.parseDouble(object.getAttribute("speed"));
        }
        if(thingSpeed == 0 || objectSpeed == 0)
        {
            steps = MAX_SEARCH;
        } else
        {
            steps = Math.min(MAX_SEARCH, (int) (STEP_COEFFICIENT * thingSpeed / objectSpeed));
        }
    }

    public void updateLocations()
    {
        for(int i = 0; i < N_DIMENSIONS; i++)
        {
            thingLocation[i] = Integer.parseInt(getThing().getAttribute("location").split(",")[i]);
        }
        if(object != null)
        {
            String[] goal = getThing().getAttribute("goal").split(" ");
            int[] extra = new int[N_DIMENSIONS];
            for(int i = 0; i < N_DIMENSIONS; i++)
            {
                extra[i] = 0;
            }
            if(goal[1].equalsIgnoreCase("ahead"))
            {

            } else if(goal[1].equalsIgnoreCase("behind"))
            {

            }
            for(int i = 0; i < N_DIMENSIONS; i++)
            {
                objectLocation[i] = Integer.parseInt(object.getAttribute("location").split(",")[i]) + extra[i];
            }
        } else
        {

        }
        finalMap = null;
    }

    /**
     * Generates a 2D array of values representing the likelihood that the thing being modeled will be in a given space,
     * weighted by how soon they will be in the specified location
     * @return A 2D array representing the likelihood that the thing being modeled will be in a given location, weighted
     * by how soon they will be in the specified location
     */
    private void generateProbabilityMap()
    {
        if(getThing().hasAttribute("intelligence") && getThing().getAttribute("intelligence").equalsIgnoreCase("random"))
        {
            generateRandomPath();
        } else
        {
            List<int[]> startTiles = new ArrayList<>();
            List<int[]> goalTiles = new ArrayList<>();
            int[] startTile0 = new int[N_DIMENSIONS];
            int[] goalTile0 = new int[N_DIMENSIONS];
            startTile0[0] = thingLocation[0] / tileDimensions[0];
            startTile0[1] = thingLocation[1] / tileDimensions[1];
            startTiles.add(startTile0);
            goalTile0[0] = objectLocation[0] / tileDimensions[0];
            goalTile0[1] = objectLocation[1] / tileDimensions[1];
            goalTiles.add(goalTile0);
            boolean[][] visitedSpaces = new boolean[allowedSpaces[0].length][allowedSpaces[1].length];
            // If a space is not allowed we can say we have already "visited it" to simplify the search
            for(int i = 0; i < visitedSpaces[0].length; i++)
            {
                for(int j = 0; j < visitedSpaces[1].length; j++)
                {
                    visitedSpaces[i][j] = !allowedSpaces[i][j];
                }
            }
            ID2DDFS searcher = new ID2DDFS(steps, startTiles, goalTiles, visitedSpaces, moveCapabilities);
            finalMap = searcher.findPaths();
        }
    }

    private void generateRandomPath()
    {

    }

    public double[] getVector(int[] location)
    {
        double[] vector = new double[N_DIMENSIONS];
        for(int i = 0; i < N_DIMENSIONS; i++)
        {
            vector[i] = 0;
        }
        if(finalMap == null)
        {
            generateProbabilityMap();
        }
        for(int i = 0; i < finalMap.length; i++)
        {
            for(int j = 0; j < finalMap[0].length; j++)
            {
                int xDiff = location[0] / tileDimensions[0] - i;
                int yDiff = location[1] / tileDimensions[1] - j;
                int dist = xDiff + yDiff;
                if(dist != 0)
                {
                    vector[0] += finalMap[i][j] * Math.abs(Math.cos(Math.atan2(yDiff, xDiff))) / Math.pow(dist, 2);
                    vector[1] += finalMap[i][j] * Math.abs(Math.sin(Math.atan2(yDiff, xDiff))) / Math.pow(dist, 2);
                }
            }
        }
        return vector;
    }

    public boolean isAllowableMovement(int[] location, double[] direction)
    {
        int[] tileLocation = new int[location.length];
        // Check if the location is between tiles. If so, set the location be the tile in the direction headed
        for(int i = 0; i < N_DIMENSIONS; i++)
        {
            if(location[i] % tileDimensions[i] != 0)
            {
                if(direction[i] < 0)
                {
                    tileLocation[i] = 1 + location[i] / tileDimensions[i];
                } else
                {
                    tileLocation[i] = location[i] / tileDimensions[i];
                }
            } else
            {
                tileLocation[i] = location[i] / tileDimensions[i];
            }
        }
        // Since the space is discrete, a mixed direction is not allowed. Therefore, we only care about the highest weighted direction
        int[] discreteDirection = new int[N_DIMENSIONS];
        if(Math.abs(direction[0]) > Math.abs(direction[1]))
        {
            if(direction[0] > 0)
            {
                discreteDirection[0] = 1;
            } else
            {
                discreteDirection[0] = -1;
            }
            discreteDirection[1] = 0;
        } else
        {
            discreteDirection[0] = 0;
            if(direction[1] > 0)
            {
                discreteDirection[1] = 1;
            } else
            {
                discreteDirection[1] = -1;
            }
        }
        int[] newLocation = new int[N_DIMENSIONS];
        for(int i = 0; i < N_DIMENSIONS; i++)
        {
            newLocation[i] = tileLocation[i] + discreteDirection[i];
        }
        if(newLocation[0] < 0 || newLocation[0] >= allowedSpaces.length || newLocation[1] < 0 || newLocation[1] >= allowedSpaces[0].length)
        {
            return false;
        }
        return allowedSpaces[newLocation[0]][newLocation[1]];
    }

    public boolean isSameDirection(double[] direction1, double[] direction2)
    {
        int[] discreteDirection1 = new int[N_DIMENSIONS];
        int[] discreteDirection2 = new int[N_DIMENSIONS];
        if(Math.abs(direction1[0]) > Math.abs(direction1[1]))
        {
            discreteDirection1[0] = (int) (direction1[0] / Math.abs(direction1[0]));
            discreteDirection1[1] = 0;
        } else
        {
            discreteDirection1[0] = 0;
            discreteDirection1[1] = (int) (direction1[1] / Math.abs(direction1[1]));
        }
        if(Math.abs(direction2[0]) > Math.abs(direction2[1]))
        {
            discreteDirection2[0] = (int) (direction2[0] / Math.abs(direction2[0]));
            discreteDirection2[1] = 0;
        } else
        {
            discreteDirection2[0] = 0;
            discreteDirection2[1] = (int) (direction2[1] / Math.abs(direction2[1]));
        }
        return (discreteDirection1[0] == discreteDirection2[0] && discreteDirection1[1] == discreteDirection2[1]);
    }

    @Override
    public List<ThingNode> getFutureWorldStates(Instruction action)
    {
        // First, get the player's new location based on the given instruction--be sure to check how far the player moves
        // Move the thing based on the thing's distance traveled times the object's speed divided by the thing's speed
        // At every junction, split the object into multiple copies (one for each allowed movement)
        double[] moveAction = new double[N_DIMENSIONS];
        moveAction[0] = Double.parseDouble(action.getParameters().get(0));
        moveAction[1] = Double.parseDouble(action.getParameters().get(1));
        int[] newObjectLocation = new int[N_DIMENSIONS];
        newObjectLocation[0] = objectLocation[0];
        newObjectLocation[1] = objectLocation[1];
        if(moveAction[0] != 0 && moveAction[1] != 0)
        {
            if(Math.abs(moveAction[0]) > Math.abs(moveAction[1]))
            {
                moveAction[1] = 0;
            } else
            {
                moveAction[0] = 0;
            }
        }
        int playerDist = 0;
        if(objectLocation[0] % tileDimensions[0] != 0 || objectLocation[1] % tileDimensions[1] != 0)
        {
            if(object.hasAttribute("direction"))
            {
                if(objectLocation[0] % tileDimensions[0] != 0)
                {
                    if(Double.parseDouble(object.getAttribute("direction").split(",")[0]) > 0)
                    {
                        if(moveAction[0] < 0)
                        {
                            playerDist = newObjectLocation[0] - (newObjectLocation[0] / tileDimensions[0]) * tileDimensions[0];
                            newObjectLocation[0] -= playerDist;
                        } else
                        {
                            playerDist = ((newObjectLocation[0] / tileDimensions[0]) + 1) * tileDimensions[0] - newObjectLocation[0];
                            newObjectLocation[0] += playerDist;
                        }
                    } else
                    {
                        if(moveAction[0] > 0)
                        {
                            playerDist = ((newObjectLocation[0] / tileDimensions[0]) + 1) * tileDimensions[0] - newObjectLocation[0];
                            newObjectLocation[0] += playerDist;
                        } else
                        {
                            playerDist = newObjectLocation[0] - (newObjectLocation[0] / tileDimensions[0]) * tileDimensions[0];
                            newObjectLocation[0] -= playerDist;
                        }
                    }
                } else
                {
                    if(Double.parseDouble(object.getAttribute("direction").split(",")[1]) > 0)
                    {
                        if(moveAction[1] < 0)
                        {
                            playerDist = newObjectLocation[0] - (newObjectLocation[1] / tileDimensions[1]) * tileDimensions[1];
                            newObjectLocation[1] -= playerDist;
                        } else
                        {
                            playerDist = ((newObjectLocation[1] / tileDimensions[1]) + 1) * tileDimensions[1] - newObjectLocation[1];
                            newObjectLocation[1] += playerDist;
                        }
                    } else
                    {
                        if(moveAction[1] > 0)
                        {
                            playerDist = ((newObjectLocation[1] / tileDimensions[1]) + 1) * tileDimensions[1] - newObjectLocation[1];
                            newObjectLocation[1] += playerDist;
                        } else
                        {
                            playerDist = newObjectLocation[1] - (newObjectLocation[1] / tileDimensions[1]) * tileDimensions[1];
                            newObjectLocation[1] -= playerDist;
                        }
                    }
                }
            } else
            {
                if(objectLocation[0] % tileDimensions[0] != 0 && moveAction[0] != 0)
                {
                    if(moveAction[0] > 0)
                    {
                        playerDist = ((newObjectLocation[0] / tileDimensions[0]) + 1) * tileDimensions[0] - newObjectLocation[0];
                        newObjectLocation[0] += playerDist;
                    } else
                    {
                        playerDist = newObjectLocation[0] - (newObjectLocation[0] / tileDimensions[0]) * tileDimensions[0];
                        newObjectLocation[0] -= playerDist;
                    }
                } else if(objectLocation[1] % tileDimensions[1] != 0 && moveAction[1] != 0)
                {
                    if(moveAction[1] > 0)
                    {
                        playerDist = ((newObjectLocation[1] / tileDimensions[1]) + 1) * tileDimensions[1] - newObjectLocation[1];
                        newObjectLocation[1] += playerDist;
                    } else
                    {
                        playerDist = newObjectLocation[1] - (newObjectLocation[1] / tileDimensions[1]) * tileDimensions[1];
                        newObjectLocation[1] -= playerDist;
                    }
                } else
                {
                    if(objectLocation[0] % tileDimensions[0] != 0)
                    {
                        int positiveDist = ((newObjectLocation[0] / tileDimensions[0]) + 1) * tileDimensions[0] - newObjectLocation[0];
                        int negativeDist = newObjectLocation[0] - (newObjectLocation[0] / tileDimensions[0]) * tileDimensions[0];
                        int[] newObjectLocation2 = new int[N_DIMENSIONS];
                        newObjectLocation2[0] = newObjectLocation[0] - negativeDist;
                        newObjectLocation2[1] = newObjectLocation[1];
                        objectLocation[0] += positiveDist;
                        return twoPlayerPositionFutureWorld(newObjectLocation, newObjectLocation2, positiveDist, negativeDist);
                    } else
                    {
                        int positiveDist = ((newObjectLocation[1] / tileDimensions[1]) + 1) * tileDimensions[1] - newObjectLocation[1];
                        int negativeDist = newObjectLocation[1] - (newObjectLocation[1] / tileDimensions[1]) * tileDimensions[1];
                        int[] newObjectLocation2 = new int[N_DIMENSIONS];
                        newObjectLocation2[0] = newObjectLocation[0];
                        newObjectLocation2[1] = newObjectLocation[1] - negativeDist;
                        objectLocation[1] += positiveDist;
                        return twoPlayerPositionFutureWorld(newObjectLocation, newObjectLocation2, positiveDist, negativeDist);
                    }
                }
            }
        } else
        {
            double[] moveDirections = new double[N_DIMENSIONS];
            for(int i = 0; i < moveDirections.length; i++)
            {
                moveDirections[i] = Double.parseDouble(action.getParameters().get(i));
            }
            if(isAllowableMovement(objectLocation, moveDirections))
            {
                int[] newLocation = new int[N_DIMENSIONS];
                if(Math.abs(moveDirections[0]) > Math.abs(moveDirections[1]))
                {
                    newLocation[1] = objectLocation[1];
                    if(moveDirections[0] > 0)
                    {
                        newLocation[0] = objectLocation[0] + tileDimensions[0];
                    } else
                    {
                        newLocation[0] = objectLocation[0] - tileDimensions[0];
                    }
                    playerDist = tileDimensions[0];
                } else
                {
                    newLocation[0] = objectLocation[0];
                    if(moveDirections[1] > 0)
                    {
                        newLocation[1] = objectLocation[1] + tileDimensions[1];
                    } else
                    {
                        newLocation[1] = objectLocation[1] - tileDimensions[1];
                    }
                    playerDist = tileDimensions[1];
                }
            }
        }
        int thingDist = 0;
        if(getThing().hasAttribute("speed") && object.hasAttribute("speed"))
        {
            thingDist = playerDist * ((int) (Double.parseDouble(getThing().getAttribute("speed"))
                    / Double.parseDouble(object.getAttribute("speed"))));
        } else
        {
            thingDist = playerDist;
        }

        return null;
    }

    private List<ThingNode> twoPlayerPositionFutureWorld(int[] playerLoc1, int[] playerLoc2, int playerDist1, int playerDist2)
    {
        return null;
    }

    private List<ThingNode> possibleThingLocations(int distanceTraveled)
    {
        return null;
    }

    @Override
    public List<Instruction> getPossibleActions()
    {
        List<Instruction> possibleActions = new ArrayList<>();
        String[] possibleMoves = object.getAttribute("move").split(",");
        if(possibleMoves[0] == "both" || possibleMoves[0] == "forward")
        {
            List<String> params = new ArrayList<>();
            params.add("1");
            params.add("0");
            possibleActions.add(new Instruction(InstructionType.MOVE, params));
        }
        if(possibleMoves[0] == "both" || possibleMoves[0] == "backward")
        {
            List<String> params = new ArrayList<>();
            params.add("-1");
            params.add("0");
            possibleActions.add(new Instruction(InstructionType.MOVE, params));
        }
        if(possibleMoves[1] == "both" || possibleMoves[1] == "forward")
        {
            List<String> params = new ArrayList<>();
            params.add("0");
            params.add("1");
            possibleActions.add(new Instruction(InstructionType.MOVE, params));
        }
        if(possibleMoves[1] == "both" || possibleMoves[1] == "backward")
        {
            List<String> params = new ArrayList<>();
            params.add("0");
            params.add("-1");
            possibleActions.add(new Instruction(InstructionType.MOVE, params));
        }
        return possibleActions;
    }

}
