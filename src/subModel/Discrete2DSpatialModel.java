package subModel;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

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

    private Constructor worldConstructor;
    private Constructor thingConstructor;
    private Constructor objectConstructor;

    /**
     * Creates a model made for a two-dimensional, discrete world
     * @param thing The thing being modeled
     * @param world The world the model exists in
     */
    public Discrete2DSpatialModel(ThingNode thing, ThingNode world)
    {
        super(thing, world);
        object = null;
        try
        {
            worldConstructor = world.getClass().getDeclaredConstructor(ProcessNode.class, List.class, List.class,
                    Map.class, double.class);
            thingConstructor = thing.getClass().getDeclaredConstructor(ProcessNode.class, List.class, List.class,
                    Map.class, double.class);
        } catch (NoSuchMethodException e) {}
        objectConstructor = null;
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
            if(object != null)
            {
                try
                {
                    objectConstructor = object.getClass().getDeclaredConstructor(ProcessNode.class, List.class, List.class,
                            Map.class, double.class);
                } catch (NoSuchMethodException e) {}
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
        int[] newPlayerLocation = new int[N_DIMENSIONS];
        newPlayerLocation[0] = objectLocation[0];
        newPlayerLocation[1] = objectLocation[1];
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
                            playerDist = newPlayerLocation[0] - (newPlayerLocation[0] / tileDimensions[0]) * tileDimensions[0];
                            newPlayerLocation[0] -= playerDist;
                        } else
                        {
                            playerDist = ((newPlayerLocation[0] / tileDimensions[0]) + 1) * tileDimensions[0] - newPlayerLocation[0];
                            newPlayerLocation[0] += playerDist;
                        }
                    } else
                    {
                        if(moveAction[0] > 0)
                        {
                            playerDist = ((newPlayerLocation[0] / tileDimensions[0]) + 1) * tileDimensions[0] - newPlayerLocation[0];
                            newPlayerLocation[0] += playerDist;
                        } else
                        {
                            playerDist = newPlayerLocation[0] - (newPlayerLocation[0] / tileDimensions[0]) * tileDimensions[0];
                            newPlayerLocation[0] -= playerDist;
                        }
                    }
                } else
                {
                    if(Double.parseDouble(object.getAttribute("direction").split(",")[1]) > 0)
                    {
                        if(moveAction[1] < 0)
                        {
                            playerDist = newPlayerLocation[0] - (newPlayerLocation[1] / tileDimensions[1]) * tileDimensions[1];
                            newPlayerLocation[1] -= playerDist;
                        } else
                        {
                            playerDist = ((newPlayerLocation[1] / tileDimensions[1]) + 1) * tileDimensions[1] - newPlayerLocation[1];
                            newPlayerLocation[1] += playerDist;
                        }
                    } else
                    {
                        if(moveAction[1] > 0)
                        {
                            playerDist = ((newPlayerLocation[1] / tileDimensions[1]) + 1) * tileDimensions[1] - newPlayerLocation[1];
                            newPlayerLocation[1] += playerDist;
                        } else
                        {
                            playerDist = newPlayerLocation[1] - (newPlayerLocation[1] / tileDimensions[1]) * tileDimensions[1];
                            newPlayerLocation[1] -= playerDist;
                        }
                    }
                }
            } else
            {
                if(objectLocation[0] % tileDimensions[0] != 0 && moveAction[0] != 0)
                {
                    if(moveAction[0] > 0)
                    {
                        playerDist = ((newPlayerLocation[0] / tileDimensions[0]) + 1) * tileDimensions[0] - newPlayerLocation[0];
                        newPlayerLocation[0] += playerDist;
                    } else
                    {
                        playerDist = newPlayerLocation[0] - (newPlayerLocation[0] / tileDimensions[0]) * tileDimensions[0];
                        newPlayerLocation[0] -= playerDist;
                    }
                } else if(objectLocation[1] % tileDimensions[1] != 0 && moveAction[1] != 0)
                {
                    if(moveAction[1] > 0)
                    {
                        playerDist = ((newPlayerLocation[1] / tileDimensions[1]) + 1) * tileDimensions[1] - newPlayerLocation[1];
                        newPlayerLocation[1] += playerDist;
                    } else
                    {
                        playerDist = newPlayerLocation[1] - (newPlayerLocation[1] / tileDimensions[1]) * tileDimensions[1];
                        newPlayerLocation[1] -= playerDist;
                    }
                } else
                {
                    if(objectLocation[0] % tileDimensions[0] != 0)
                    {
                        int positiveDist = ((newPlayerLocation[0] / tileDimensions[0]) + 1) * tileDimensions[0] - newPlayerLocation[0];
                        int negativeDist = newPlayerLocation[0] - (newPlayerLocation[0] / tileDimensions[0]) * tileDimensions[0];
                        int[] newPlayerLocation2 = new int[N_DIMENSIONS];
                        newPlayerLocation2[0] = newPlayerLocation[0] - negativeDist;
                        newPlayerLocation2[1] = newPlayerLocation[1];
                        objectLocation[0] += positiveDist;
                        return twoPlayerPositionFutureWorld(newPlayerLocation, newPlayerLocation2, positiveDist, negativeDist);
                    } else
                    {
                        int positiveDist = ((newPlayerLocation[1] / tileDimensions[1]) + 1) * tileDimensions[1] - newPlayerLocation[1];
                        int negativeDist = newPlayerLocation[1] - (newPlayerLocation[1] / tileDimensions[1]) * tileDimensions[1];
                        int[] newPlayerLocation2 = new int[N_DIMENSIONS];
                        newPlayerLocation2[0] = newPlayerLocation[0];
                        newPlayerLocation2[1] = newPlayerLocation[1] - negativeDist;
                        objectLocation[1] += positiveDist;
                        return twoPlayerPositionFutureWorld(newPlayerLocation, newPlayerLocation2, positiveDist, negativeDist);
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
//                int[] newLocation = new int[N_DIMENSIONS];
                if(Math.abs(moveDirections[0]) > Math.abs(moveDirections[1]))
                {
                    newPlayerLocation[1] = objectLocation[1];
                    if(moveDirections[0] > 0)
                    {
                        newPlayerLocation[0] = objectLocation[0] + tileDimensions[0];
                    } else
                    {
                        newPlayerLocation[0] = objectLocation[0] - tileDimensions[0];
                    }
                    playerDist = tileDimensions[0];
                } else
                {
                    newPlayerLocation[0] = objectLocation[0];
                    if(moveDirections[1] > 0)
                    {
                        newPlayerLocation[1] = objectLocation[1] + tileDimensions[1];
                    } else
                    {
                        newPlayerLocation[1] = objectLocation[1] - tileDimensions[1];
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
        List<ThingNode> futureThings = calculatePossibleFutures(thingDist, getThing());
        List<ThingNode> futureWorlds = new ArrayList<>();
        try
        {
            StringBuilder pLocationBuilder = new StringBuilder();
            pLocationBuilder.append(newPlayerLocation[0]);
            pLocationBuilder.append(",");
            pLocationBuilder.append(newPlayerLocation[1]);
            int[] playerDirection = new int[N_DIMENSIONS];
            if(objectLocation[0] - newPlayerLocation[0] == 0)
            {
                playerDirection[0] = 0;
                if(objectLocation[1] - newPlayerLocation[1] > 0)
                {
                    playerDirection[1] = 1;
                } else
                {
                    playerDirection[1] = -1;
                }
            } else
            {
                if(objectLocation[0] - newPlayerLocation[0] > 0)
                {
                    playerDirection[0] = 1;
                } else
                {
                    playerDirection[0] = -1;
                }
                playerDirection[1] = 0;
            }
            StringBuilder pDirectionBuilder = new StringBuilder();
            pDirectionBuilder.append(playerDirection[0]);
            pDirectionBuilder.append(",");
            pDirectionBuilder.append(playerDirection[1]);
            for(ThingNode futureThing : futureThings)
            {
                ThingNode futureWorld = (ThingNode) worldConstructor.newInstance(null, null, getWorld().getCategories(),
                        getWorld().getAttributes(), getWorld().getConfidence());
                futureWorld.setName(getWorld().getName());
                ThingNode futurePlayer = (ThingNode) objectConstructor.newInstance(futureWorld, null, object.getCategories(),
                        object.getAttributes(), object.getConfidence());
                futurePlayer.setName(object.getName());
                futureThing.setParent(futureWorld);
                futurePlayer.setAttribute("location", pLocationBuilder.toString());
                futurePlayer.setAttribute("direction", pDirectionBuilder.toString());
                futureWorld.addElement(futurePlayer);
                futureWorld.addElement(futureThing);
                futureWorlds.add(futureWorld);
            }
        } catch (InstantiationException e) {}
        catch (IllegalAccessException e) {}
        catch (InvocationTargetException e) {}
        return futureWorlds;
    }

    private List<ThingNode> twoPlayerPositionFutureWorld(int[] playerLoc1, int[] playerLoc2, int playerDist1, int playerDist2)
    {
        int thingDist1 = 0;
        int thingDist2 = 0;
        if(getThing().hasAttribute("speed") && object.hasAttribute("speed"))
        {
            thingDist1 = playerDist1 * ((int) (Double.parseDouble(getThing().getAttribute("speed"))
                    / Double.parseDouble(object.getAttribute("speed"))));
            thingDist2 = playerDist2 * ((int) (Double.parseDouble(getThing().getAttribute("speed"))
                    / Double.parseDouble(object.getAttribute("speed"))));
        } else
        {
            thingDist1 = playerDist1;
            thingDist2 = playerDist2;
        }
        List<ThingNode> futureThings1 = calculatePossibleFutures(thingDist1, getThing());
        List<ThingNode> futureThings2 = calculatePossibleFutures(thingDist2, getThing());
        StringBuilder p1LocationBuilder = new StringBuilder();
        p1LocationBuilder.append(playerLoc1[0]);
        p1LocationBuilder.append(",");
        p1LocationBuilder.append(playerLoc1[1]);
        List<ThingNode> possibleFutureWorlds = new ArrayList<>();
        try
        {
            int[] player1Direction = new int[N_DIMENSIONS];
            if(objectLocation[0] - playerLoc1[0] == 0)
            {
                player1Direction[0] = 0;
                if(objectLocation[1] - playerLoc1[1] > 0)
                {
                    player1Direction[1] = 1;
                } else
                {
                    player1Direction[1] = -1;
                }
            } else
            {
                if(objectLocation[0] - playerLoc1[0] > 0)
                {
                    player1Direction[0] = 1;
                } else
                {
                    player1Direction[0] = -1;
                }
                player1Direction[1] = 0;
            }
            StringBuilder p1DirectionBuilder = new StringBuilder();
            p1DirectionBuilder.append(player1Direction[0]);
            p1DirectionBuilder.append(",");
            p1DirectionBuilder.append(player1Direction[1]);
            for(ThingNode futureThing : futureThings1)
            {
                ThingNode futureWorld = (ThingNode) worldConstructor.newInstance(null, null, getWorld().getCategories(),
                        getWorld().getAttributes(), getWorld().getConfidence());
                futureWorld.setName(getWorld().getName());
                ThingNode futurePlayer = (ThingNode) objectConstructor.newInstance(futureWorld, null, object.getCategories(),
                        object.getAttributes(), object.getConfidence());
                futurePlayer.setName(object.getName());
                futureThing.setParent(futureWorld);
                futurePlayer.setAttribute("location", p1LocationBuilder.toString());
                futureWorld.addElement(futurePlayer);
                futureWorld.addElement(futureThing);
                possibleFutureWorlds.add(futureWorld);
            }
            StringBuilder p2LocationBuilder = new StringBuilder();
            p2LocationBuilder.append(playerLoc2[0]);
            p2LocationBuilder.append(",");
            p2LocationBuilder.append(playerLoc2[1]);
            int[] player2Direction = new int[N_DIMENSIONS];
            if(objectLocation[0] - playerLoc2[0] == 0)
            {
                player2Direction[0] = 0;
                if(objectLocation[1] - playerLoc2[1] > 0)
                {
                    player2Direction[1] = 1;
                } else
                {
                    player2Direction[1] = -1;
                }
            } else
            {
                if(objectLocation[0] - playerLoc2[0] > 0)
                {
                    player2Direction[0] = 1;
                } else
                {
                    player2Direction[0] = -1;
                }
                player2Direction[1] = 0;
            }
            StringBuilder p2DirectionBuilder = new StringBuilder();
            p2DirectionBuilder.append(player2Direction[0]);
            p2DirectionBuilder.append(",");
            p2DirectionBuilder.append(player2Direction[1]);
            for(ThingNode futureThing : futureThings2)
            {
                ThingNode futureWorld = (ThingNode) worldConstructor.newInstance(null, null, getWorld().getCategories(),
                        getWorld().getAttributes(), getWorld().getConfidence());
                ThingNode futurePlayer = (ThingNode) objectConstructor.newInstance(futureWorld, null, object.getCategories(),
                        object.getAttributes(), object.getConfidence());
                futureThing.setParent(futureWorld);
                futurePlayer.setAttribute("location", p2LocationBuilder.toString());
                futureWorld.addElement(futurePlayer);
                futureWorld.addElement(futureThing);
                possibleFutureWorlds.add(futureWorld);
            }
        } catch (IllegalAccessException e) {}
        catch (InstantiationException e) {}
        catch (InvocationTargetException e) {}
//        for(ThingNode futureThing : futureThings2)
//        {
//
//        }
        return possibleFutureWorlds;
    }

    /**
     * Calculates possible future states given the distance traveled
     * @param distance The distance the given thing will travel
     * @param thingToFuture Thing to predict its future
     * @return A list of future things
     */
    private List<ThingNode> calculatePossibleFutures(int distance, ThingNode thingToFuture)
    {
        List<ThingNode> futureThings = new ArrayList<>();
        try
        {
            Constructor futureConstructor = thingToFuture.getClass().getDeclaredConstructor(ProcessNode.class, List.class,
                    List.class, Map.class, double.class);
            int[] originalLocation = new int[N_DIMENSIONS];
            originalLocation[0] = Integer.parseInt(thingToFuture.getAttribute("location").split(",")[0]);
            originalLocation[1] = Integer.parseInt(thingToFuture.getAttribute("location").split(",")[1]);
            Queue<int[]> newLocations = new LinkedList<>();
            Queue<Integer> distancesToTravel = new LinkedList<>();
            newLocations.add(originalLocation);
            distancesToTravel.add(distance);
            while(!newLocations.isEmpty())
            {
                int[] currentLocation = newLocations.remove();
                int currentDistance = distancesToTravel.remove();
                if(currentLocation[0] % tileDimensions[0] != 0) // Between tiles
                {
                    int distLeft = currentLocation[0] - (currentLocation[0] / tileDimensions[0]) * tileDimensions[0];
                    int distRight = ((currentLocation[0] / tileDimensions[0]) + 1) * tileDimensions[0] - currentLocation[0];
                    if(distLeft >= currentDistance)
                    {
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[0] = currentLocation[0] - currentDistance;
                        newLocation[1] = currentLocation[1];
                        try {
                            ThingNode futureState = (ThingNode) futureConstructor.newInstance(null, null, thingToFuture.getCategories(),
                                    thingToFuture.getAttributes(), thingToFuture.getConfidence());
                            futureState.setName(thingToFuture.getName());
                            StringBuilder locationBuilder = new StringBuilder();
                            locationBuilder.append(newLocation[0]);
                            locationBuilder.append(",");
                            locationBuilder.append(newLocation[1]);
                            futureState.setAttribute("location", locationBuilder.toString());
                            futureState.setAttribute("direction", "-1,0");
                            futureThings.add(futureState);
                        } catch (InstantiationException e) {}
                        catch (IllegalAccessException e) {}
                        catch (InvocationTargetException e) {}
                    } else
                    {
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[0] = currentLocation[0] - distLeft;
                        newLocation[1] = currentLocation[1];
                        newLocations.add(newLocation);
                        distancesToTravel.add(currentDistance - distLeft);
                    }
                    if(distRight >= currentDistance)
                    {
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[0] = currentLocation[0] + currentDistance;
                        newLocation[1] = currentLocation[1];
                        try {
                            ThingNode futureState = (ThingNode) futureConstructor.newInstance(null, null, thingToFuture.getCategories(),
                                    thingToFuture.getAttributes(), thingToFuture.getConfidence());
                            futureState.setName(thingToFuture.getName());
                            StringBuilder locationBuilder = new StringBuilder();
                            locationBuilder.append(newLocation[0]);
                            locationBuilder.append(",");
                            locationBuilder.append(newLocation[1]);
                            futureState.setAttribute("location", locationBuilder.toString());
                            futureState.setAttribute("direction", "1,0");
                            futureThings.add(futureState);
                        } catch (InstantiationException e) {}
                        catch (IllegalAccessException e) {}
                        catch (InvocationTargetException e) {}
                    } else
                    {
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[0] = currentLocation[0] + distRight;
                        newLocation[1] = currentLocation[1];
                        newLocations.add(newLocation);
                        distancesToTravel.add(currentDistance - distRight);
                    }
                } else if(currentLocation[1] % tileDimensions[1] != 0) // Between tiles
                {
                    int distUp = currentLocation[1] - (currentLocation[1] / tileDimensions[1]) * tileDimensions[1];
                    int distDown = ((currentLocation[1] / tileDimensions[1]) + 1) * tileDimensions[1] - currentLocation[1];
                    if(distUp >= currentDistance)
                    {
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[0] = currentLocation[0];
                        newLocation[1] = currentLocation[1] - currentDistance;
                        try {
                            ThingNode futureState = (ThingNode) futureConstructor.newInstance(null, null, thingToFuture.getCategories(),
                                    thingToFuture.getAttributes(), thingToFuture.getConfidence());
                            futureState.setName(thingToFuture.getName());
                            StringBuilder locationBuilder = new StringBuilder();
                            locationBuilder.append(newLocation[0]);
                            locationBuilder.append(",");
                            locationBuilder.append(newLocation[1]);
                            futureState.setAttribute("location", locationBuilder.toString());
                            futureState.setAttribute("direction", "0,-1");
                            futureThings.add(futureState);
                        } catch (InstantiationException e) {}
                        catch (IllegalAccessException e) {}
                        catch (InvocationTargetException e) {}
                    } else
                    {
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[0] = currentLocation[0];
                        newLocation[1] = currentLocation[1] - distUp;
                        newLocations.add(newLocation);
                        distancesToTravel.add(currentDistance - distUp);
                    }
                    if(distDown >= currentDistance)
                    {
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[0] = currentLocation[0];
                        newLocation[1] = currentLocation[1] + currentDistance;
                        try {
                            ThingNode futureState = (ThingNode) futureConstructor.newInstance(null, null, thingToFuture.getCategories(),
                                    thingToFuture.getAttributes(), thingToFuture.getConfidence());
                            futureState.setName(thingToFuture.getName());
                            StringBuilder locationBuilder = new StringBuilder();
                            locationBuilder.append(newLocation[0]);
                            locationBuilder.append(",");
                            locationBuilder.append(newLocation[1]);
                            futureState.setAttribute("location", locationBuilder.toString());
                            futureThings.add(futureState);
                            futureState.setAttribute("direction", "0,1");
                        } catch (InstantiationException e) {}
                        catch (IllegalAccessException e) {}
                        catch (InvocationTargetException e) {}
                    } else
                    {
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[0] = currentLocation[0];
                        newLocation[1] = currentLocation[1] + distDown;
                        newLocations.add(newLocation);
                        distancesToTravel.add(currentDistance - distDown);
                    }
                } else // Intersection
                {
                    double[] up = {0, -1};
                    double[] down = {0, 1};
                    double[] left = {-1, 0};
                    double[] right = {1, 0};
                    if(isAllowableMovement(currentLocation, up))
                    {
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[0] = currentLocation[0];
                        if(currentDistance <= tileDimensions[1])
                        {
                            newLocation[1] = currentLocation[1] - currentDistance;
                            try {
                                ThingNode futureState = (ThingNode) futureConstructor.newInstance(null, null, thingToFuture.getCategories(),
                                        thingToFuture.getAttributes(), thingToFuture.getConfidence());
                                futureState.setName(thingToFuture.getName());
                                StringBuilder locationBuilder = new StringBuilder();
                                locationBuilder.append(newLocation[0]);
                                locationBuilder.append(",");
                                locationBuilder.append(newLocation[1]);
                                futureState.setAttribute("location", locationBuilder.toString());
                                futureState.setAttribute("direction", "0,-1");
                                futureThings.add(futureState);
                            } catch (InstantiationException e) {}
                            catch (IllegalAccessException e) {}
                            catch (InvocationTargetException e) {}
                        } else
                        {
                            newLocation[1] = currentLocation[1] - tileDimensions[1];
                            newLocations.add(newLocation);
                            distancesToTravel.add(currentDistance - tileDimensions[1]);
                        }
                    }
                    if(isAllowableMovement(currentLocation, down))
                    {
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[0] = currentLocation[0];
                        if(currentDistance <= tileDimensions[1])
                        {
                            newLocation[1] = currentLocation[1] + currentDistance;
                            try {
                                ThingNode futureState = (ThingNode) futureConstructor.newInstance(null, null, thingToFuture.getCategories(),
                                        thingToFuture.getAttributes(), thingToFuture.getConfidence());
                                futureState.setName(thingToFuture.getName());
                                StringBuilder locationBuilder = new StringBuilder();
                                locationBuilder.append(newLocation[0]);
                                locationBuilder.append(",");
                                locationBuilder.append(newLocation[1]);
                                futureState.setAttribute("location", locationBuilder.toString());
                                futureState.setAttribute("direction", "0,1");
                                futureThings.add(futureState);
                            } catch (InstantiationException e) {}
                            catch (IllegalAccessException e) {}
                            catch (InvocationTargetException e) {}
                        } else
                        {
                            newLocation[1] = currentLocation[1] + tileDimensions[1];
                            newLocations.add(newLocation);
                            distancesToTravel.add(currentDistance - tileDimensions[1]);
                        }
                    }
                    if(isAllowableMovement(currentLocation, left))
                    {
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[1] = currentLocation[1];
                        if(currentDistance <= tileDimensions[0])
                        {
                            newLocation[0] = currentLocation[0] - currentDistance;
                            try {
                                ThingNode futureState = (ThingNode) futureConstructor.newInstance(null, null, thingToFuture.getCategories(),
                                        thingToFuture.getAttributes(), thingToFuture.getConfidence());
                                futureState.setName(thingToFuture.getName());
                                StringBuilder locationBuilder = new StringBuilder();
                                locationBuilder.append(newLocation[0]);
                                locationBuilder.append(",");
                                locationBuilder.append(newLocation[1]);
                                futureState.setAttribute("location", locationBuilder.toString());
                                futureState.setAttribute("direction", "-1,0");
                                futureThings.add(futureState);
                            } catch (InstantiationException e) {}
                            catch (IllegalAccessException e) {}
                            catch (InvocationTargetException e) {}
                        } else
                        {
                            newLocation[0] = currentLocation[0] - tileDimensions[0];
                            newLocations.add(newLocation);
                            distancesToTravel.add(currentDistance - tileDimensions[0]);
                        }
                    }
                    if(isAllowableMovement(currentLocation, right))
                    {
                        int[] newLocation = new int[N_DIMENSIONS];
                        newLocation[1] = currentLocation[1];
                        if(currentDistance <= tileDimensions[0])
                        {
                            newLocation[0] = currentLocation[0] + currentDistance;
                            try {
                                ThingNode futureState = (ThingNode) futureConstructor.newInstance(null, null, thingToFuture.getCategories(),
                                        thingToFuture.getAttributes(), thingToFuture.getConfidence());
                                futureState.setName(thingToFuture.getName());
                                StringBuilder locationBuilder = new StringBuilder();
                                locationBuilder.append(newLocation[0]);
                                locationBuilder.append(",");
                                locationBuilder.append(newLocation[1]);
                                futureState.setAttribute("location", locationBuilder.toString());
                                futureState.setAttribute("direction", "1,0");
                                futureThings.add(futureState);
                            } catch (InstantiationException e) {}
                            catch (IllegalAccessException e) {}
                            catch (InvocationTargetException e) {}
                        } else
                        {
                            newLocation[1] = currentLocation[0] + tileDimensions[0];
                            newLocations.add(newLocation);
                            distancesToTravel.add(currentDistance - tileDimensions[0]);
                        }
                    }
                }
            }
        } catch (NoSuchMethodException e) {}
        return futureThings;
    }

    @Override
    public List<Instruction> getPossibleActions()
    {
        List<Instruction> possibleActions = new ArrayList<>();
        String[] possibleMoves = object.getAttribute("move").split(",");
        if(possibleMoves[0].equalsIgnoreCase("both") || possibleMoves[0].equalsIgnoreCase("forward"))
        {
            List<String> params = new ArrayList<>();
            params.add("1");
            params.add("0");
            possibleActions.add(new Instruction(InstructionType.MOVE, params));
        }
        if(possibleMoves[0].equalsIgnoreCase("both") || possibleMoves[0].equalsIgnoreCase("backward"))
        {
            List<String> params = new ArrayList<>();
            params.add("-1");
            params.add("0");
            possibleActions.add(new Instruction(InstructionType.MOVE, params));
        }
        if(possibleMoves[1].equalsIgnoreCase("both") || possibleMoves[1].equalsIgnoreCase("forward"))
        {
            List<String> params = new ArrayList<>();
            params.add("0");
            params.add("1");
            possibleActions.add(new Instruction(InstructionType.MOVE, params));
        }
        if(possibleMoves[1].equalsIgnoreCase("both") || possibleMoves[1].equalsIgnoreCase("backward"))
        {
            List<String> params = new ArrayList<>();
            params.add("0");
            params.add("-1");
            possibleActions.add(new Instruction(InstructionType.MOVE, params));
        }
        return possibleActions;
    }

    public List<ThingNode> generateFutureWorlds(Instruction action)
    {
        double[] moveAction = new double[N_DIMENSIONS];
        moveAction[0] = Double.parseDouble(action.getParameters().get(0));
        moveAction[1] = Double.parseDouble(action.getParameters().get(1));
        ThingNode player = getWorld().getThing("Player");
        String[] playerLocationStrings = player.getAttribute("location").split(",");
        int[] newPlayerLocation = new int[N_DIMENSIONS];
        newPlayerLocation[0] = Integer.parseInt(playerLocationStrings[0]);
        newPlayerLocation[1] = Integer.parseInt(playerLocationStrings[1]);
        List<ThingNode> futurePlayers = new ArrayList<>();
        List<Integer> playerDistance = new ArrayList<>();
        if(newPlayerLocation[0] % tileDimensions[0] != 0 || newPlayerLocation[1] % tileDimensions[1] != 0)
        {
            if(moveAction[0] != 0 && newPlayerLocation[0] % tileDimensions[0] != 0)
            {
                ThingNode futurePlayer = new ThingNode(null, null, player.getCategories(), null, player.getConfidence());
                futurePlayer.setName(player.getName());
                for(String attributeName : player.getAttributes().keySet())
                {
                    futurePlayer.setAttribute(attributeName, player.getAttribute(attributeName));
                }
                if(moveAction[0] > 0)
                {
                    int dist = (((newPlayerLocation[0] / tileDimensions[0]) + 1) * tileDimensions[0]) - newPlayerLocation[0];
                    playerDistance.add(dist);
                    newPlayerLocation[0] += dist;
                    StringBuilder locationBuilder = new StringBuilder();
                    locationBuilder.append(newPlayerLocation[0]);
                    locationBuilder.append(",");
                    locationBuilder.append(newPlayerLocation[1]);
                    futurePlayer.setAttribute("location", locationBuilder.toString());
                    futurePlayer.setAttribute("direction", "1,0");
                    futurePlayers.add(futurePlayer);
                } else
                {
                    int dist = newPlayerLocation[0] - ((newPlayerLocation[0] / tileDimensions[0]) * tileDimensions[0]);
                    playerDistance.add(dist);
                    newPlayerLocation[0] -= dist;
                    StringBuilder locationBuilder = new StringBuilder();
                    locationBuilder.append(newPlayerLocation[0]);
                    locationBuilder.append(",");
                    locationBuilder.append(newPlayerLocation[1]);
                    futurePlayer.setAttribute("location", locationBuilder.toString());
                    futurePlayer.setAttribute("direction", "-1,0");
                    futurePlayers.add(futurePlayer);
                }
            } else if(moveAction[1] != 0 && newPlayerLocation[1] % tileDimensions[0] != 0)
            {
                ThingNode futurePlayer = new ThingNode(null, null, player.getCategories(), null, player.getConfidence());
                futurePlayer.setName(player.getName());
                for(String attributeName : player.getAttributes().keySet())
                {
                    futurePlayer.setAttribute(attributeName, player.getAttribute(attributeName));
                }
                if(moveAction[1] > 0)
                {
                    int dist = (((newPlayerLocation[1] / tileDimensions[1]) + 1) * tileDimensions[1]) - newPlayerLocation[1];
                    playerDistance.add(dist);
                    newPlayerLocation[1] += dist;
                    StringBuilder locationBuilder = new StringBuilder();
                    locationBuilder.append(newPlayerLocation[0]);
                    locationBuilder.append(",");
                    locationBuilder.append(newPlayerLocation[1]);
                    futurePlayer.setAttribute("location", locationBuilder.toString());
                    futurePlayer.setAttribute("direction", "0,1");
                    futurePlayers.add(futurePlayer);
                } else
                {
                    int dist = newPlayerLocation[1] - ((newPlayerLocation[1] / tileDimensions[1]) * tileDimensions[1]);
                    playerDistance.add(dist);
                    newPlayerLocation[1] -= dist;
                    StringBuilder locationBuilder = new StringBuilder();
                    locationBuilder.append(newPlayerLocation[0]);
                    locationBuilder.append(",");
                    locationBuilder.append(newPlayerLocation[1]);
                    futurePlayer.setAttribute("location", locationBuilder.toString());
                    futurePlayer.setAttribute("direction", "0,-1");
                    futurePlayers.add(futurePlayer);
                }
            } else if(player.hasAttribute("direction"))
            {
                ThingNode futurePlayer = new ThingNode(null, null, player.getCategories(), null, player.getConfidence());
                futurePlayer.setName(player.getName());
                for(String attributeName : player.getAttributes().keySet())
                {
                    futurePlayer.setAttribute(attributeName, player.getAttribute(attributeName));
                }
                String[] directionStrings = player.getAttribute("direction").split(",");
                if(moveAction[0] % tileDimensions[0] != 0)
                {
                    if(Integer.parseInt(directionStrings[0]) > 0)
                    {
                        int dist = (((newPlayerLocation[0] / tileDimensions[0]) + 1) * tileDimensions[0]) - newPlayerLocation[0];
                        playerDistance.add(dist);
                        newPlayerLocation[0] += dist;
                        StringBuilder locationBuilder = new StringBuilder();
                        locationBuilder.append(newPlayerLocation[0]);
                        locationBuilder.append(",");
                        locationBuilder.append(newPlayerLocation[1]);
                        futurePlayer.setAttribute("location", locationBuilder.toString());
                        futurePlayer.setAttribute("direction", "1,0");
                        futurePlayers.add(futurePlayer);
                    } else
                    {
                        int dist = newPlayerLocation[0] - ((newPlayerLocation[0] / tileDimensions[0]) * tileDimensions[0]);
                        playerDistance.add(dist);
                        newPlayerLocation[0] -= dist;
                        StringBuilder locationBuilder = new StringBuilder();
                        locationBuilder.append(newPlayerLocation[0]);
                        locationBuilder.append(",");
                        locationBuilder.append(newPlayerLocation[1]);
                        futurePlayer.setAttribute("location", locationBuilder.toString());
                        futurePlayer.setAttribute("direction", "-1,0");
                        futurePlayers.add(futurePlayer);
                    }
                } else
                {
                    if(Integer.parseInt(directionStrings[1]) > 0)
                    {
                        int dist = (((newPlayerLocation[1] / tileDimensions[1]) + 1) * tileDimensions[1]) - newPlayerLocation[1];
                        playerDistance.add(dist);
                        newPlayerLocation[1] += dist;
                        StringBuilder locationBuilder = new StringBuilder();
                        locationBuilder.append(newPlayerLocation[0]);
                        locationBuilder.append(",");
                        locationBuilder.append(newPlayerLocation[1]);
                        futurePlayer.setAttribute("location", locationBuilder.toString());
                        futurePlayer.setAttribute("direction", "0,1");
                        futurePlayers.add(futurePlayer);
                    } else
                    {
                        int dist = newPlayerLocation[1] - ((newPlayerLocation[1] / tileDimensions[1]) * tileDimensions[1]);
                        playerDistance.add(dist);
                        newPlayerLocation[1] -= dist;
                        StringBuilder locationBuilder = new StringBuilder();
                        locationBuilder.append(newPlayerLocation[0]);
                        locationBuilder.append(",");
                        locationBuilder.append(newPlayerLocation[1]);
                        futurePlayer.setAttribute("location", locationBuilder.toString());
                        futurePlayer.setAttribute("direction", "0,-1");
                        futurePlayers.add(futurePlayer);
                    }
                }
            } else if(moveAction[0] % tileDimensions[0] != 0)
            {
                int lDist = newPlayerLocation[0] - (newPlayerLocation[0] / tileDimensions[0]) * tileDimensions[0];
                int rDist = (((newPlayerLocation[0] / tileDimensions[0]) + 1) * tileDimensions[0]) - newPlayerLocation[0];
                int[] otherNewPlayerLocation = new int[N_DIMENSIONS];
                otherNewPlayerLocation[0] = newPlayerLocation[0];
                otherNewPlayerLocation[1] = newPlayerLocation[1];
                playerDistance.add(lDist);
                playerDistance.add(rDist);
                newPlayerLocation[0] -= lDist;
                otherNewPlayerLocation[0] += rDist;
                StringBuilder location1Builder = new StringBuilder();
                location1Builder.append(newPlayerLocation[0]);
                location1Builder.append(",");
                location1Builder.append(newPlayerLocation[1]);
                StringBuilder location2Builder = new StringBuilder();
                location2Builder.append(otherNewPlayerLocation[0]);
                location2Builder.append(",");
                location2Builder.append(otherNewPlayerLocation[1]);
                ThingNode futurePlayer1 = new ThingNode(null, null, player.getCategories(), null, player.getConfidence());
                ThingNode futurePlayer2 = new ThingNode(null, null, player.getCategories(), null, player.getConfidence());
                futurePlayer1.setName(player.getName());
                futurePlayer2.setName(player.getName());
                for(String attributeName : player.getAttributes().keySet())
                {
                    futurePlayer1.setAttribute(attributeName, player.getAttribute(attributeName));
                    futurePlayer2.setAttribute(attributeName, player.getAttribute(attributeName));
                }
                futurePlayer1.setAttribute("location", location1Builder.toString());
                futurePlayer1.setAttribute("direction", "-1,0");
                futurePlayer2.setAttribute("location", location2Builder.toString());
                futurePlayer2.setAttribute("direction", "1,0");
                futurePlayers.add(futurePlayer1);
                futurePlayers.add(futurePlayer2);
            } else
            {
                int uDist = newPlayerLocation[1] - (newPlayerLocation[1] / tileDimensions[1]) * tileDimensions[1];
                int dDist = (((newPlayerLocation[1] / tileDimensions[1]) + 1) * tileDimensions[1]) - newPlayerLocation[1];
                int[] otherNewPlayerLocation = new int[N_DIMENSIONS];
                otherNewPlayerLocation[0] = newPlayerLocation[0];
                otherNewPlayerLocation[1] = newPlayerLocation[1];
                playerDistance.add(uDist);
                playerDistance.add(dDist);
                newPlayerLocation[1] -= uDist;
                otherNewPlayerLocation[1] += dDist;
                StringBuilder location1Builder = new StringBuilder();
                location1Builder.append(newPlayerLocation[0]);
                location1Builder.append(",");
                location1Builder.append(newPlayerLocation[1]);
                StringBuilder location2Builder = new StringBuilder();
                location2Builder.append(otherNewPlayerLocation[0]);
                location2Builder.append(",");
                location2Builder.append(otherNewPlayerLocation[1]);
                ThingNode futurePlayer1 = new ThingNode(null, null, player.getCategories(), null, player.getConfidence());
                ThingNode futurePlayer2 = new ThingNode(null, null, player.getCategories(), null, player.getConfidence());
                futurePlayer1.setName(player.getName());
                futurePlayer2.setName(player.getName());
                for(String attributeName : player.getAttributes().keySet())
                {
                    futurePlayer1.setAttribute(attributeName, player.getAttribute(attributeName));
                    futurePlayer2.setAttribute(attributeName, player.getAttribute(attributeName));
                }
                futurePlayer1.setAttribute("location", location1Builder.toString());
                futurePlayer1.setAttribute("direction", "-1,0");
                futurePlayer2.setAttribute("location", location2Builder.toString());
                futurePlayer2.setAttribute("direction", "1,0");
                futurePlayers.add(futurePlayer1);
                futurePlayers.add(futurePlayer2);
            }
        } else // Player is at an intersection
        {
            ThingNode futurePlayer = new ThingNode(null, null, player.getCategories(), null, player.getConfidence());
            futurePlayer.setName(player.getName());
            for(String attributeName : player.getAttributes().keySet())
            {
                futurePlayer.setAttribute(attributeName, player.getAttribute(attributeName));
            }
            if(isAllowableMovement(newPlayerLocation, moveAction))
            {
                int dist = 0;
                if(Math.abs(moveAction[0]) > Math.abs(moveAction[1]))
                {
                    dist = tileDimensions[0];
                    if(moveAction[0] > 0)
                    {
                        newPlayerLocation[0] += dist;
                        futurePlayer.setAttribute("direction", "1,0");
                    } else
                    {
                        newPlayerLocation[0] -= dist;
                        futurePlayer.setAttribute("direction", "-1,0");
                    }
                } else
                {
                    dist = tileDimensions[1];
                    if(moveAction[1] > 0)
                    {
                        newPlayerLocation[1] += dist;
                        futurePlayer.setAttribute("direction", "0,1");
                    } else
                    {
                        newPlayerLocation[1] -= dist;
                        futurePlayer.setAttribute("direction", "0,-1");
                    }
                }
                StringBuilder locationBuilder = new StringBuilder();
                locationBuilder.append(newPlayerLocation[0]);
                locationBuilder.append(",");
                locationBuilder.append(newPlayerLocation[1]);
                futurePlayer.setAttribute("location", locationBuilder.toString());
                playerDistance.add(dist);
            }
            futurePlayers.add(futurePlayer);
        }
        int playerSpeed = Integer.parseInt(player.getAttribute("speed"));
        List<ThingNode> futureWorlds = new ArrayList<>();
        for(int i = 0; i < futurePlayers.size(); i++)
        {
            List<List<ThingNode>> selectedPlayerFutureWorldElements = new ArrayList<>();
            List<ThingNode> playerList = new ArrayList<>();
            playerList.add(futurePlayers.get(i));
            selectedPlayerFutureWorldElements.add(playerList);
            for(ThingNode element : getWorld().getThingElements())
            {
                if(!element.hasAttribute("Player"))
                {
                    selectedPlayerFutureWorldElements.add(generateFutureStates(element, playerSpeed, playerDistance.get(i)));
                }
            }
            futureWorlds.addAll(generateFutureWorlds(selectedPlayerFutureWorldElements));
        }
        return futureWorlds;
    }

    private List<ThingNode> generateFutureStates(ThingNode currentState, int playerSpeed, int playerDist)
    {
        return null;
    }

    private List<ThingNode> generateFutureWorlds(List<List<ThingNode>> possibleStates)
    {
        return null;
    }

}
