package subModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import instructions.Instruction;
import instructions.InstructionType;
import processTree.ThingNode;

public class DiscreteSpatialModel extends SubModel 
{
	
	AllowableSpaces allowableSpaces;
	int[] thingDimensions;
	int[] worldDimensions;
	int[] objectDimensions;
	int[] zeroCoordinate;
	int[] gridTileDimensions;
	
	int[] thingLocation;
	int[] objectLocation;
	
	MoveType[] movementCapabilities;
	
	public DiscreteSpatialModel(ThingNode thing, ThingNode world) 
	{
		super(thing, world);
		loadDimensions(thing, world);
		loadWorldProperties(world);
		loadLocations(thing, world);
		loadAllowedTiles(world);
		checkMoveCapabilities();
	}
	
	private void loadDimensions(ThingNode thing, ThingNode world)
	{
		int nThingDimensions = 0;
		int nWorldDimensions = 0;
		int nObjectDimensions = 0;
		int nDimensions = 0;
		if(thing.hasAttribute("dimensions"))
		{
			nThingDimensions = thing.getAttribute("dimensions").split(",").length;
		}
		if(world.hasAttribute("dimensions"))
		{
			nWorldDimensions = world.getAttribute("dimensions").split(",").length;
		}
		if(getObject() != null && getObject().hasAttribute("dimensions"))
		{
			nObjectDimensions = getObject().getAttribute("dimensions").split(",").length;
		}
		nDimensions = Math.max(nThingDimensions, Math.max(nWorldDimensions, nObjectDimensions));
		thingDimensions = new int[nDimensions];
		for(int i = 0; i < nThingDimensions; i++)
		{
			thingDimensions[i] = Integer.parseInt(thing.getAttribute("dimensions").split(",")[i]);
		}
		for(int i = nThingDimensions; i < nDimensions; i++)
		{
			thingDimensions[i] = 0;
		}
		worldDimensions = new int[nDimensions];
		for(int i = 0; i < nWorldDimensions; i++)
		{
			worldDimensions[i] = Integer.parseInt(world.getAttribute("dimensions").split(",")[i]);
		}
		for(int i = nWorldDimensions; i < nDimensions; i++)
		{
			worldDimensions[i] = 0;
		}
		if(getObject() != null)
		{
			objectDimensions = new int[nDimensions];
			for(int i = 0; i < nObjectDimensions; i++)
			{
				objectDimensions[i] = Integer.parseInt(getObject().getAttribute("dimensions").split(",")[i]);
			}
			for(int i = nObjectDimensions; i < nDimensions; i++)
			{
				objectDimensions[i] = 0;
			}
		} else
		{
			objectDimensions = null;
		}
		zeroCoordinate = new int[nDimensions];
		for(int i = 0; i < nDimensions; i++)
		{
			zeroCoordinate[i] = 0;
		}
	}
	
	/**
	 * Loads properties of the world, including the size of the grid which discretizes the world
	 * @param world The world about which properties are to be extracted
	 */
	private void loadWorldProperties(ThingNode world)
	{
		gridTileDimensions = new int[worldDimensions.length];
		if(world.hasAttribute("grid"))
		{
			String[] gridStrings = world.getAttribute("grid").split(",");
			if(gridStrings.length == 1)
			{
				int dimension = Integer.parseInt(gridStrings[0]);
				for(int i = 0; i < gridTileDimensions.length; i++)
				{
					gridTileDimensions[i] = dimension;
				}
			} else
			{
				for(int i = 0; i < gridStrings.length; i++)
				{
					gridTileDimensions[i] = Integer.parseInt(gridStrings[i]);
				}
				for(int i = gridStrings.length; i < worldDimensions.length; i++)
				{
					gridTileDimensions[i] = 1;
				}
			}
		} else
		{
			for(int i = 0; i < gridTileDimensions.length; i++)
			{
				gridTileDimensions[i] = 1;
			}
		}
	}
	
	private void loadLocations(ThingNode thing, ThingNode object)
	{
		thingLocation = new int[thingDimensions.length];
		String[] thingLocationStrings = thing.getAttribute("location").split(",");
		for(int i = 0; i < thingLocationStrings.length; i++)
		{
			thingLocation[i] = Integer.parseInt(thingLocationStrings[i]);
		}
		for(int i = thingLocationStrings.length; i < thingLocation.length; i++)
		{
			thingLocation[i] = 0;
		}
		if(getObject() != null)
		{
			objectLocation = new int[objectDimensions.length];
			String[] objectLocationStrings = getObject().getAttribute("location").split(",");
			for(int i = 0; i < thingLocationStrings.length; i++)
			{
				objectLocation[i] = Integer.parseInt(objectLocationStrings[i]);
			}
			for(int i = thingLocationStrings.length; i < thingLocation.length; i++)
			{
				objectLocation[i] = 0;
			}
		}
	}
	
	/**
	 * Loads the tiles which the thing can enter
	 * @param world The world about which information (such as obstacle locations) is being extracted
	 */
	private void loadAllowedTiles(ThingNode world)
	{
		//First, assume the entire space of the world can be entered
		int[] worldTiles = new int[worldDimensions.length];
		for(int i = 0; i < worldDimensions.length; i++)
		{
			worldTiles[i] = worldDimensions[i] / gridTileDimensions[i];
		}
		allowableSpaces = new AllowableSpaces(worldTiles);
		for(ThingNode element : world.getElements())
		{
			if(element.getCategories().contains("obstacle"))
			{
				if(element.isPlural())
				{
					for(ThingNode subElement : element.getElements())
					{
						blockStationaryObstacles(subElement);
					}
				} else
				{
					blockStationaryObstacles(element);
				}
			}
		}
	}
	
	/**
	 * Generates the next coordinate in a set of multidimensional array, increasing the last element first
	 * @param currentCoordinate The coordinate the "next" coordinate will be based off of
	 * @param smallestCoordinate The coordinate with the smallest values for each element
	 * @param largestCoordinate The coordinate with the largest values for each element
	 * @return The next coordinate, after the current coordinate
	 */
	private int[] generateNextCoordinate(int[] currentCoordinate, int[] smallestCoordinate, int[] largestCoordinate)
	{
		int[] nextCoordinate = new int[currentCoordinate.length];
		int carry = 1;
		for(int i = currentCoordinate.length - 1; i >= 0; i--)
		{
			if(carry > 0)
			{
				nextCoordinate[i] = currentCoordinate[i] + carry;
				if(nextCoordinate[i] > largestCoordinate[i])
				{
					nextCoordinate[i] = smallestCoordinate[i];
				} else
				{
					carry = 0;
				}
			} else
			{
				nextCoordinate[i] = currentCoordinate[i];
			}
		}
		return nextCoordinate;
	}
	
	/**
	 * Ensures spaces covered by stationary obstacles are not allowed to be entered by other entities
	 * @param obstacle The obstacle to be avoided
	 */
	private void blockStationaryObstacles(ThingNode obstacle)
	{
		String[] locationString = obstacle.getAttribute("location").split(",");
		String[] dimensionString = obstacle.getAttribute("dimensions").split(",");
		int[] location = new int[worldDimensions.length];
		int[] dimensions = new int[worldDimensions.length];
		int[] currentCoordinate = new int[worldDimensions.length];
		for(int i = 0; i < worldDimensions.length; i++)
		{
			location[i] = 0;
			dimensions[i] = 0;
			currentCoordinate[i] = 0;
		}
		for(int i = 0; i < locationString.length; i++)
		{
			location[i] = Integer.parseInt(locationString[i]);
			currentCoordinate[i] = location[i];
		}
		int[] finalCoordinate = new int[worldDimensions.length];
		for(int i = 0; i < dimensionString.length; i++)
		{
			dimensions[i] = Integer.parseInt(dimensionString[i]);
			finalCoordinate[i] = location[i] + dimensions[i] - 1;
		}
		while(!currentCoordinate.equals(finalCoordinate))
		{
			boolean same = true;
			for(int i = 0; i < worldDimensions.length; i++)
			{
				if(currentCoordinate[i] != finalCoordinate[i])
				{
					same = false;
					break;
				}
			}
			if(same)
			{
				break;
			}
			int[] currentTile = new int[worldDimensions.length];
			for(int i = 0; i < currentTile.length; i++)
			{
				currentTile[i] = currentCoordinate[i] / gridTileDimensions[i];
			}
			allowableSpaces.removeSpace(currentTile);
			currentCoordinate = generateNextCoordinate(currentCoordinate, location, finalCoordinate);
		}
	}
	
	/**
	 * Determines which moves are available to the thing
	 */
	private void checkMoveCapabilities()
	{
		movementCapabilities = new MoveType[worldDimensions.length];
		String[] moveStrings = getCapabilities().get("move").split(",");
		for(int i = 0; i < moveStrings.length; i++)
		{
			movementCapabilities[i] = MoveType.stringToMoveType(moveStrings[i]);
		}
		for(int i = moveStrings.length; i < movementCapabilities.length; i++)
		{
			movementCapabilities[i] = MoveType.NEITHER;
		}
	}
	
	@Override
	public List<Collection<Instruction>> generateActionSequence() 
	{
		IDDFS searcher = new IDDFS(thingLocation, objectLocation);
		List<MoveType[]> moveList = searcher.findPath();
		if(moveList == null)
		{
			return null;
		}
		List<Collection<Instruction>> actionSequence = new ArrayList<>();
		int distanceToFirstTile = 0;
		int firstMoveIndex = 0;
		MoveType firstMoveType = MoveType.NEITHER;
		boolean thingOnTile = true;
		for(int i = 0; i < worldDimensions.length; i++)
		{
			if(thingLocation[i] % gridTileDimensions[i] != 0)
			{
				thingOnTile = false;
				firstMoveIndex = i;
				if(moveList.get(0)[i] == MoveType.FORWARD)
				{
					firstMoveType = MoveType.FORWARD;
					distanceToFirstTile = ((thingLocation[i] / gridTileDimensions[i]) + 1) * gridTileDimensions[i] - thingLocation[i];
				} else
				{
					firstMoveType = MoveType.BACKWARD;
					distanceToFirstTile = thingLocation[i] - (thingLocation[i] / gridTileDimensions[i]) * gridTileDimensions[i];
				}
				break;
			}
		}
		int startIndex = 0;
		if(!thingOnTile)
		{
			startIndex = 1;
			List<String> moveStrings = new ArrayList<>();
			for(int i = 0; i < worldDimensions.length; i++)
			{
				if(firstMoveIndex == i && firstMoveType == MoveType.FORWARD)
				{
					moveStrings.add("1");
				} else if(firstMoveIndex == i && firstMoveType == MoveType.BACKWARD)
				{
					moveStrings.add("-1");
				} else
				{
					moveStrings.add("0");
				}
			}
			for(int i = 0; i < distanceToFirstTile; i++)
			{
				Collection<Instruction> instructionCollection = new ArrayList<>();
				instructionCollection.add(new Instruction(InstructionType.MOVE, moveStrings));
				actionSequence.add(instructionCollection);
			}
		}
		for(int i = startIndex; i < moveList.size(); i++)
		{
			List<String> moveStrings = new ArrayList<>();
			int moveSpaces = 0;
			for(int j = 0; j < worldDimensions.length; j++)
			{
				if(moveList.get(i)[j].equals(MoveType.FORWARD))
				{
					moveStrings.add("1");
					moveSpaces = gridTileDimensions[j];
				} else if(moveList.get(i)[j].equals(MoveType.BACKWARD))
				{
					moveStrings.add("-1");
					moveSpaces = gridTileDimensions[j];
				} else
				{
					moveStrings.add("0");
				}
			}
			for(int j = 0; j < moveSpaces; j++)
			{
				Collection<Instruction> instructionCollection = new ArrayList<>();
				instructionCollection.add(new Instruction(InstructionType.MOVE, moveStrings));
				actionSequence.add(instructionCollection);
			}
		}
		return actionSequence;
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
	
	private class AllowableSpaces
	{
		
		Node[] nodes;
		int size;
		
		public AllowableSpaces(int[] dimensionality)
		{
			nodes = new Node[dimensionality[0]];
			for(int i = 0; i < nodes.length; i++)
			{
				nodes[i] = new Node(dimensionality, 1);
			}
			size = 1;
			for(int i = 0; i < dimensionality.length; i++)
			{
				size *= dimensionality[i];
			}
		}
		
		public void removeSpace(int[] space)
		{
			if(nodes[space[0]].makeUnallowable(space))
			{
				size--;
			}
		}
		
		public boolean isAllowable(int[] space)
		{
			for(int i = 0; i < space.length; i++)
			{
				if(space[i] <0)
				{
					return false;
				}
			}
			if(space[0] >= nodes.length)
			{
				return false;
			}
			return nodes[space[0]].isAllowable(space);
		}
		
		public int size()
		{
			return size;
		}
		
		private class Node
		{
			private Node[] nextDimension;
			private int dimension;
			private boolean allowable;
			
			public Node(int[] dimensionality, int dimension)
			{
				this.dimension = dimension;
				this.allowable = true;
				if(dimension == dimensionality.length)
				{
					nextDimension = null;
				} else
				{
					nextDimension = new Node[dimensionality[dimension]];
					for(int i = 0; i < nextDimension.length; i++)
					{
						nextDimension[i] = new Node(dimensionality, dimension + 1);
					}
				}
			}
			
			public boolean makeUnallowable	(int[] coordinate)
			{
				if(nextDimension == null)
				{
					if(allowable == false)
					{
						return false;
					} else
					{
						allowable = false;
						return true;
					}
				} else
				{
					return nextDimension[coordinate[dimension]].makeUnallowable(coordinate);
				}
			}
			
			public boolean isAllowable(int[] coordinate)
			{
				if(nextDimension == null)
				{
					return allowable;
				} else if(coordinate[dimension] >= nextDimension.length)
				{
					return false;
				} else
				{
					return nextDimension[coordinate[dimension]].isAllowable(coordinate);
				}
			}
			
		}
		
	}
	
	private class IDDFS
	{
		private Node root;
		private List<Node> subRoot;
		private int[] distanceToSubRoot;
		private List<int[]> goals;
		private int[] distanceToGoalTile;
		
		public IDDFS(int[] startState, int[] goalState)
		{
			MoveType[] emptyAction = new MoveType[worldDimensions.length];
			for(int i = 0; i < emptyAction.length; i++)
			{
				emptyAction[i] = MoveType.NEITHER;
			}
			root = new Node(emptyAction, startState, -1);
			int[] subState = new int[worldDimensions.length];
			int[] subState2 = new int[worldDimensions.length];
			MoveType[] toSubstateAction = new MoveType[worldDimensions.length];
			MoveType[] toSubstate2Action = new MoveType[worldDimensions.length];
			int[] goal = new int[worldDimensions.length];
			int[] goal2 = new int[worldDimensions.length];
			boolean hasTwoSubstates = false;
			boolean hasTwoGoals = false;
			for(int i = 0; i < worldDimensions.length; i++)
			{
				if(startState[i] % gridTileDimensions[i] != 0)
				{
					subState[i] = startState[i] / gridTileDimensions[i];
					subState2[i] = startState[i] / gridTileDimensions[i] + 1;
					distanceToSubRoot = new int[2];
					distanceToSubRoot[0] = startState[i] - subState[i] * gridTileDimensions[i];
					distanceToSubRoot[1] = subState2[i] * gridTileDimensions[i] - startState[i];
					toSubstateAction[i] = MoveType.BACKWARD;
					toSubstate2Action[i] = MoveType.FORWARD;
					hasTwoSubstates = true;
				} else
				{
					subState[i] = startState[i] / gridTileDimensions[i];
					subState2[i] = startState[i] / gridTileDimensions[i];
					toSubstateAction[i] = MoveType.NEITHER;
					toSubstate2Action[i] = MoveType.NEITHER;
				}
				if(goalState[i] % gridTileDimensions[i] != 0)
				{
					goal[i] = goalState[i] / gridTileDimensions[i];
					goal2[i] = goalState[i] / gridTileDimensions[i] + 1;
					distanceToGoalTile = new int[2];
					distanceToGoalTile[0] = goalState[i] - goal[i] * gridTileDimensions[i];
					distanceToGoalTile[1] = goal2[i] * gridTileDimensions[i] - goalState[i];
					hasTwoGoals = true;
				} else
				{
					goal[i] = goalState[i] / gridTileDimensions[i];
					goal2[i] = goalState[i] / gridTileDimensions[i];
				}
			}
			subRoot = new ArrayList<>();
			goals = new ArrayList<>();
			subRoot.add(new Node(toSubstateAction, subState, 0));
			if(hasTwoSubstates)
			{
				subRoot.add(new Node(toSubstate2Action, subState2, 0));
			}
//			root.addChildren(subRoot);
			goals.add(goal);
			if(hasTwoGoals)
			{
				goals.add(goal2);
			}
		}
		
		/**
		 * Finds the sequence of moves that will get from the start to the goal
		 * @return A sequence of moves that gets from the start to the goal
		 */
		public List<MoveType[]> findPath()
		{
			int minPath = 0;
			for(int i = 0; i < worldDimensions.length; i++)
			{
				minPath += Math.abs(thingLocation[i] - objectLocation[i]) / gridTileDimensions[i];
			}
			int maxPath = allowableSpaces.size();
			for(int i = minPath; i < maxPath; i++)
			{
				List<List<Node>> nodePaths = new ArrayList<>();
				for(Node sRoot : subRoot)
				{
					List<Node> result = sRoot.boundedSearch(i);
					if(result != null)
					{
						maxPath = Math.min(maxPath, result.size());
						nodePaths.add(result);
					}
				}
				if(nodePaths.size() == 1)
				{
					List<MoveType[]> path = new ArrayList<>();
					List<Node> result = nodePaths.get(0);
					for(int j = result.size() - 1; j >= 0; j--)
					{
						path.add(result.get(j).getAction());
					}
					return path;
				} else if(nodePaths.size() > 1)
				{
					List<Node> bestResult = null;
					boolean unequalSpaces = false;
					int shortestPath = 0;
					int pathLength = nodePaths.get(0).size();
					for(int j = 1; j < nodePaths.size(); j++)
					{
						if(nodePaths.get(j).size() < pathLength)
						{
							shortestPath = j;
							pathLength = nodePaths.get(j).size();
							unequalSpaces = true;
						} else if(nodePaths.get(j).size() > pathLength)
						{
							unequalSpaces = true;
						}
					}
					if(unequalSpaces)
					{
						bestResult = nodePaths.get(shortestPath);
					} else
					{
						for(List<Node> result : nodePaths)
						{
							if(bestResult == null)
							{
								bestResult = result;
							} else
							{
								int currentBestExtraDistance = 0;
								int currentBestStartIndex = 0;
								int currentBestGoalIndex = 0;
								int resultExtraDistance = 0;
								int resultStartIndex = 0;
								int resultGoalIndex = 0;
								for(int j = 0; j < subRoot.size(); j++)
								{
									if(bestResult.get(bestResult.size() - 1).getState().equals(subRoot.get(j).getState()))
									{
										currentBestStartIndex = j;
									}
									if(result.get(result.size() - 1).getState().equals(subRoot.get(j).getState()))
									{
										resultStartIndex = j;
									}
								}
								for(int j = 0; j < goals.size(); j++)
								{
									if(bestResult.get(0).getState().equals(goals.get(j)))
									{
										currentBestGoalIndex = j;
									}
									if(result.get(0).getState().equals(goals.get(j)))
									{
										resultGoalIndex = j;
									}
								}
								if(distanceToGoalTile != null)
								{
									currentBestExtraDistance = distanceToSubRoot[currentBestStartIndex] + distanceToGoalTile[currentBestGoalIndex];
									resultExtraDistance = distanceToSubRoot[resultStartIndex] + distanceToGoalTile[resultGoalIndex];
								} else
								{
									currentBestExtraDistance = distanceToSubRoot[currentBestStartIndex];
									resultExtraDistance = distanceToSubRoot[resultStartIndex];
								}
								if(resultExtraDistance < currentBestExtraDistance)
								{
									bestResult = result;
								}
							}
						}
					}
					List<MoveType[]> path = new ArrayList<>();
					for(int j = bestResult.size() - 1; j >= 0; j--)
					{
						path.add(bestResult.get(j).getAction());
					}
					return path;
				}
			}
			return null;
		}
		
		private class Node
		{
			
			private List<Node> children;
			private MoveType[] action; // The action to arrive at the current state
			private int[] state;
			private int iteration;
			
			public Node(MoveType[] actionToReach, int[] state, int iteration)
			{
				this.action = actionToReach;
				this.state = state;
				this.iteration = iteration;
			}
			
			public void addChildren(List<Node> children)
			{
				this.children.addAll(children);
			}
			
			public List<Node> boundedSearch(int bound)
			{
				boolean containsState = true;
				for(int i = 0; i < goals.size(); i++)
				{
					containsState = true;
					for(int j = 0; j < goals.get(i).length; j++)
					{
						if(goals.get(i)[j] != state[j])
						{
							containsState = false;
							break;
						}
					}
					if(containsState)
					{
						break;
					}
				}
				if(containsState)
				{
					List<Node> result = new ArrayList<>();
					result.add(this);
					return result;
				} else if(bound < iteration)
				{
					return null;
				} else
				{
					for(int i = 0; i < worldDimensions.length; i++)
					{
						switch(movementCapabilities[i])
						{
							case FORWARD:
							{
								int[] nextState = new int[worldDimensions.length];
								MoveType[] nextMove = new MoveType[worldDimensions.length];
								for(int j = 0; j < worldDimensions.length; j++)
								{
									if(i == j)
									{
										nextState[j] = state[j] + 1;
										nextMove[j] = MoveType.FORWARD;
									} else
									{
										nextState[j] = state[j];
										nextMove[j] = MoveType.NEITHER;
									}
								}
								if(allowableSpaces.isAllowable(nextState))
								{
									boolean isStartState = true;
									for(int j = 0; j < subRoot.size(); j++)
									{
										isStartState = true;
										for(int k = 0; k < worldDimensions.length; k++)
										{
											if(subRoot.get(j).getState()[k] != nextState[k])
											{
												isStartState = false;
												break;
											}
										}
										if(isStartState)
										{
											break;
										}
									}
									if(!isStartState)
									{
										List<Node> nextResult = (new Node(nextMove, nextState, iteration + 1)).boundedSearch(bound);
										if(nextResult != null)
										{
											nextResult.add(this);
											return nextResult;
										}
									}
								}
								break;
							} case BACKWARD:
							{
								int[] nextState = new int[worldDimensions.length];
								MoveType[] nextMove = new MoveType[worldDimensions.length];
								for(int j = 0; j < worldDimensions.length; j++)
								{
									if(i == j)
									{
										nextState[j] = state[j] - 1;
										nextMove[j] = MoveType.BACKWARD;
									} else
									{
										nextState[j] = state[j];
										nextMove[j] = MoveType.NEITHER;
									}
								}
								if(allowableSpaces.isAllowable(nextState))
								{
									boolean isStartState = true;
									for(int j = 0; j < subRoot.size(); j++)
									{
										isStartState = true;
										for(int k = 0; k < worldDimensions.length; k++)
										{
											if(subRoot.get(j).getState()[k] != nextState[k])
											{
												isStartState = false;
												break;
											}
										}
										if(isStartState)
										{
											break;
										}
									}
									if(!isStartState)
									{
										List<Node> nextResult = (new Node(nextMove, nextState, iteration + 1)).boundedSearch(bound);
										if(nextResult != null)
										{
											nextResult.add(this);
											return nextResult;
										}
									}
								}
								break;
							} case BOTH:
							{
								int[] nextState1 = new int[worldDimensions.length];
								int[] nextState2 = new int[worldDimensions.length];
								MoveType[] nextMove1 = new MoveType[worldDimensions.length];
								MoveType[] nextMove2 = new MoveType[worldDimensions.length];
								for(int j = 0; j < worldDimensions.length; j++)
								{
									if(i == j)
									{
										nextState1[j] = state[j] + 1;
										nextState2[j] = state[j] - 1;
										nextMove1[j] = MoveType.FORWARD;
										nextMove2[j] = MoveType.BACKWARD;
									} else
									{
										nextState1[j] = state[j];
										nextState2[j] = state[j];
										nextMove1[j] = MoveType.NEITHER;
										nextMove2[j] = MoveType.NEITHER;
									}
								}
								if(allowableSpaces.isAllowable(nextState1))
								{
									boolean isStartState = true;
									for(int j = 0; j < subRoot.size(); j++)
									{
										isStartState = true;
										for(int k = 0; k < worldDimensions.length; k++)
										{
											if(subRoot.get(j).getState()[k] != nextState1[k])
											{
												isStartState = false;
												break;
											}
										}
										if(isStartState)
										{
											break;
										}
									}
									if(!isStartState)
									{
										List<Node> nextResult = (new Node(nextMove1, nextState1, iteration + 1)).boundedSearch(bound);
										if(nextResult != null)
										{
											nextResult.add(this);
											return nextResult;
										}
									}
								}
								if(allowableSpaces.isAllowable(nextState2))
								{
									boolean isStartState = true;
									for(int j = 0; j < subRoot.size(); j++)
									{
										isStartState = true;
										for(int k = 0; k < worldDimensions.length; k++)
										{
											if(subRoot.get(j).getState()[k] != nextState2[k])
											{
												isStartState = false;
												break;
											}
										}
										if(isStartState)
										{
											break;
										}
									}
									if(!isStartState)
									{
										List<Node> nextResult = (new Node(nextMove2, nextState2, iteration + 1)).boundedSearch(bound);
										if(nextResult != null)
										{
											nextResult.add(this);
											return nextResult;
										}
									}
								}
								break;
							}
							default:{}
						}
					}
				}
				return null;
			}
			
			/**
			 * Gets the array of MoveTypes describing the most recent move to get to the current state
			 * @return The array of MoveTypes describing the most recent move to get to the current state
			 */
			public MoveType[] getAction()
			{
				return action;
			}
			
			/**
			 * Gets the state represented by the node
			 * @return The state of the node
			 */
			public int[] getState()
			{
				return state;
			}
			
		}
		
	}

}
