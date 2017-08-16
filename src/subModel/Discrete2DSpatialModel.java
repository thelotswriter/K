package subModel;

import java.util.ArrayList;
import java.util.List;

import processTree.ThingNode;

public class Discrete2DSpatialModel 
{
	private final int N_DIMENSIONS = 2;
	private final int MAX_SEARCH = 10;
	private final double STEP_COEFFICIENT = 10;
	
	private ThingNode thing;
	private ThingNode world;
	private int[] tileDimensions;
	private int[] worldDimensions;
	private int[] thingDimensions;
	private int[] objectDimensions;
	private int[] thingLocation;
	private int[] objectLocation;
	private MoveType[] moveCapabilities;
	private boolean[][] allowedSpaces;
	private double[][] workingMap;
	private double[][] finalMap;
	private int steps;
	
	public Discrete2DSpatialModel(ThingNode thing, ThingNode world)
	{
		this.thing = thing;
		this.world = world;
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
		ThingNode object = world.getThing(thing.getAttribute("goal").split(",")[1]);
		
		for(int i = 0; i < N_DIMENSIONS; i++)
		{
			tileDimensions[i] = Integer.parseInt(world.getAttribute("grid").split(",")[i]);
			worldDimensions[i] = Integer.parseInt(world.getAttribute("dimensions").split(",")[i]);
			thingDimensions[i] = Integer.parseInt(thing.getAttribute("dimensions").split(",")[i]);
			objectDimensions[i] = Integer.parseInt(object.getAttribute("dimensions").split(",")[i]);
		}
		
		allowedSpaces = new boolean[worldDimensions[0] / tileDimensions[0]][worldDimensions[1] / tileDimensions[1]];
		workingMap = new double[worldDimensions[0] / tileDimensions[0]][worldDimensions[1] / tileDimensions[1]];
		finalMap = new double[worldDimensions[0] / tileDimensions[0]][worldDimensions[1] / tileDimensions[1]];
		for(int i = 0; i < worldDimensions[0] / tileDimensions[0]; i++)
		{
			for(int j = 0; j < worldDimensions[1] / tileDimensions[1]; j++)
			{
				allowedSpaces[i][j] = true;
				workingMap[i][j] = 0;
				finalMap[i][j] = 0;
			}
		}
		setUnallowedSpaces();
	}
	
	/**
	 * Determines which spaces are blocked and blocks them
	 */
	private void setUnallowedSpaces()
	{
		for(ThingNode node : world.getElements())
		{
			if(node.getCategories().contains("obstacle"))
			{
				if(node.isPlural())
				{
					for(ThingNode singleNode : node.getElements())
					{
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
		moveCapabilities = new MoveType[N_DIMENSIONS];
		String[] moveStrings = thing.getAttribute("move").split(",");
		for(int i = 0; i < N_DIMENSIONS; i++)
		{
			moveCapabilities[i] = MoveType.stringToMoveType(moveStrings[i]);
		}
	}
	
	/**
	 * Sets how many tiles to search based on the speed of the thing and object
	 */
	private void setTilesToSearch()
	{
		double thingSpeed = 0;
		double objectSpeed = 0;
		if(thing.hasAttribute("speed"))
		{
			thingSpeed = Double.parseDouble(thing.getAttribute("speed"));
		}
		ThingNode object = world.getThing(thing.getAttribute("goal").split(",")[1]);
		if(object.hasAttribute("speed"))
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
	
	/**
	 * Updates the locations of the thing and object in the world
	 */
 	private void updateLocations()
	{
		ThingNode object = world.getThing(thing.getAttribute("goal").split(",")[1]);
		for(int i = 0; i < N_DIMENSIONS; i++)
		{
			thingLocation[i] = Integer.parseInt(thing.getAttribute("location").split(",")[i]);
			objectLocation[i] = Integer.parseInt(object.getAttribute("location").split(",")[i]);
		}
	}
	
 	/**
 	 * Generates a 2D array of values representing the likelihood that the thing being modeled will be in a given space,
 	 * weighted by how soon they will be in the specified location
 	 * @return A 2D array representing the likelihood that the thing being modeled will be in a given location, weighted 
 	 * by how soon they will be in the specified location
 	 */
	public double[][] generateProbabilityMap()
	{
		if(thing.hasAttribute("intelligence") && thing.getAttribute("intelligence").equalsIgnoreCase("random"))
		{
			generateRandomPath();
		} else
		{
			IDDFS searcher = new IDDFS(thingLocation, objectLocation);
			searcher.findPaths();
		}
		return finalMap;
	}
	
	private void generateRandomPath()
	{
		// TODO Code this
	}
	
	private enum MoveType
	{
		
		FORWARD, BACKWARD, BOTH, NEITHER;
		
		static MoveType stringToMoveType(String move)
		{
			if(move.equalsIgnoreCase("forward"))
			{
				return FORWARD;
			} else if(move.equalsIgnoreCase("backward"))
			{
				return BACKWARD;
			} else if(move.equalsIgnoreCase("both"))
			{
				return BOTH;
			} else
			{
				return NEITHER;
			}
		}
		
	}
	
	private class IDDFS
	{
		
		public double total;
		
		private List<int[]> startTiles;
		private List<int[]> goalTiles;
		private boolean[][] visitedSpaces;
		private Node[] roots;
		private boolean pathFound;
		
		public IDDFS(int[] startState, int[] goalState)
		{
			startTiles = new ArrayList<>();
			goalTiles = new ArrayList<>();
			int[] startTile0 = new int[N_DIMENSIONS];
			int[] goalTile0 = new int[N_DIMENSIONS];
			startTile0[0] = startState[0] / tileDimensions[0];
			startTile0[1] = startState[1] / tileDimensions[1];
			startTiles.add(startTile0);
			goalTile0[0] = goalState[0] / tileDimensions[0];
			goalTile0[0] = goalState[1] / tileDimensions[1];
			goalTiles.add(goalTile0);
			if(startState[0] % tileDimensions[0] != 0)
			{
				int[] startTile1 = new int[N_DIMENSIONS];
				startTile1[0] = startTile0[0] + 1;
				startTile1[1] = startTile0[1];
				startTiles.add(startTile1);
			}
			if(startState[1] % tileDimensions[1] != 0)
			{
				int[] startTile1 = new int[N_DIMENSIONS];
				startTile1[0] = startTile0[0];
				startTile1[1] = startTile0[1] + 1;
				startTiles.add(startTile1);
			}
			if(goalState[0] % tileDimensions[0] != 0)
			{
				int[] goalTile1 = new int[N_DIMENSIONS];
				goalTile1[0] = goalTile0[0] + 1;
				goalTile1[1] = goalTile0[1];
				goalTiles.add(goalTile1);
			}
			if(goalState[1] % tileDimensions[1] != 0)
			{
				int[] goalTile1 = new int[N_DIMENSIONS];
				goalTile1[0] = goalTile0[0];
				goalTile1[1] = goalTile0[1] + 1;
				goalTiles.add(goalTile1);
			}
			roots = new Node[startTiles.size()];
			for(int i = 0; i < roots.length; i++)
			{
				roots[i] = new Node(startTiles.get(i), 0);
			}
		}
		
		public void findPaths()
		{
			total = 0;
			visitedSpaces = new boolean[allowedSpaces[0].length][allowedSpaces[1].length];
			// If a space is not allowed we can say we have already "visited it" to simplify the search
			for(int i = 0; i < visitedSpaces[0].length; i++)
			{
				for(int j = 0; j < visitedSpaces[1].length; j++)
				{
					visitedSpaces[i][j] = !allowedSpaces[i][j];
				}
			}
			pathFound = false;
			for(int i = 0; i < MAX_SEARCH; i ++)
			{
				for(Node root : roots)
				{
					root.search(i);
				}
				if(pathFound)
				{
					break;
				}
			}
			if(total != 0)
			{
				for(double[] tileRow : finalMap)
				{
					for(double tileWeight : tileRow)
					{
						tileWeight /= total;
					}
				}
			}
		}
		
		private class Node
		{
			
			private int[] location;
			private int depth;
			private double pValue;
			
			public Node(int[] location, int depth)
			{
				this.location = location;
				this.depth = depth;
			}
			
			public void search(int maxDepth)
			{
				boolean isGoal = false;
				for(int[] goalTile : goalTiles)
				{
					if(location[0] == goalTile[0] && location[1] == goalTile[1])
					{
						isGoal = true;
						break;
					}
				}
				if(isGoal)
				{
					pathFound = true;
					finalMap[location[0]][location[1]]++;
				} else if(maxDepth > depth)
				{
					switch(moveCapabilities[0])
					{
						case BOTH:
						{
							switch(moveCapabilities[1])
							{
								case BOTH:
								{
									
									break;
								} case FORWARD:
								{
									
									break;
								} case BACKWARD:
								{
									
									break;
								} default:
								{
									
								}
							}
							break;
						} case FORWARD:
						{
							switch(moveCapabilities[1])
							{
								case BOTH:
								{
									
									break;
								} case FORWARD:
								{
									
									break;
								} case BACKWARD:
								{
									
									break;
								} default:
								{
									
								}
							}
							break;
						} case BACKWARD:
						{
							switch(moveCapabilities[1])
							{
								case BOTH:
								{
									
									break;
								} case FORWARD:
								{
									
									break;
								} case BACKWARD:
								{
									
									break;
								} default:
								{
									
								}
							}
							break;
						} default:
						{
							switch(moveCapabilities[1])
							{
								case BOTH:
								{
									
									break;
								} case FORWARD:
								{
									
									break;
								} case BACKWARD:
								{
									
									break;
								} default:
								{
									
								}
							}
						}
					}
				}
			}
			
		}
		
	}
	
}
