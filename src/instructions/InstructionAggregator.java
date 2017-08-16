package instructions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InstructionAggregator 
{
	
	private static InstructionAggregator SINGLETON = null;
	
	private InstructionAggregator()
	{
		
	}
	
	/**
	 * Gets the instruction aggregator
	 * @return The instruction aggregator
	 */
	public static InstructionAggregator getInstance()
	{
		if(SINGLETON == null)
		{
			SINGLETON = new InstructionAggregator();
		}
		return SINGLETON;
	}
	
	/**
	 * Aggregates instructions of the same type
	 * @param instructions The instructions to be aggregated
	 * @param weights The weights given to the instructions
	 * @return The aggregated instruction
	 */
	public Instruction aggregateSameInstructionType(List<InstructionPacket> instructionPackets)
	{
		switch(instructionPackets.get(0).getInstruction().getType())
		{
			case MOVE:
			{
				double[] weightedAverage = new double[instructionPackets.get(0).getInstruction().getParameters().size()];
				for(InstructionPacket instructionPacket : instructionPackets)
				{
					List<String> parameters = instructionPacket.getInstruction().getParameters();
					double weight = instructionPacket.getOriginNode().getScore() * instructionPacket.getOriginNode().getPriority()
							* instructionPacket.getOriginNode().getUrgency();
					for(int i = 0; i < weightedAverage.length; i++)
					{
						weightedAverage[i] += Double.parseDouble(parameters.get(i)) * weight;
					}
				}
				List<String> aggregateParameters = new ArrayList<>();
				for(int i = 0; i < weightedAverage.length; i++)
				{
					weightedAverage[i] /= instructionPackets.size();
					aggregateParameters.add(Double.toString(weightedAverage[i]));
				}
				return new Instruction(InstructionType.MOVE, aggregateParameters);
			} case MOVE_MOUSE:
			{
				
				return null;
			} case CLICK:
			{
				Set<String> parameterSet = new HashSet<>();
				for(InstructionPacket instructionPacket : instructionPackets)
				{
					parameterSet.addAll(instructionPacket.getInstruction().getParameters());
				}
				List<String> newParameters = new ArrayList<>(parameterSet);
				return new Instruction(InstructionType.CLICK, newParameters);
			} case SCROLL:
			{
				double weightedAverageScroll = 0;
				for(InstructionPacket instructionPacket : instructionPackets)
				{
					double weight = instructionPacket.getOriginNode().getScore() * instructionPacket.getOriginNode().getPriority()
							* instructionPacket.getOriginNode().getUrgency();
					weightedAverageScroll += weight * Double.parseDouble(instructionPacket.getInstruction().getParameters().get(0));
				}
				weightedAverageScroll /= instructionPackets.size();
				List<String> newParameters = new ArrayList<>();
				newParameters.add(Double.toString(weightedAverageScroll));
				return new Instruction(InstructionType.SCROLL, newParameters);
			} case PRESS_KEY:
			{
				
				return null;
			} case OPEN_FILE:
			{
				List<String> bestPaths = new ArrayList<>();
				double highestWeightSoFar = 0;
				for(InstructionPacket instructionPacket : instructionPackets)
				{
					double weight = instructionPacket.getOriginNode().getScore() * instructionPacket.getOriginNode().getPriority()
							* instructionPacket.getOriginNode().getUrgency();
					if(weight > highestWeightSoFar)
					{
						highestWeightSoFar = weight;
						bestPaths.clear();
						bestPaths.add(instructionPacket.getInstruction().getParameters().get(0));
					} else if(weight == highestWeightSoFar)
					{
						bestPaths.add(instructionPacket.getInstruction().getParameters().get(0));
					}
				}
				if(bestPaths.size() > 1)
				{
					double rand = Math.random();
					double nBestPaths = (double) bestPaths.size();
					for(double i = 1; i <= nBestPaths; i++)
					{
						if(rand < i / nBestPaths)
						{
							String chosenPath = bestPaths.get(((int) i) - 1);
							bestPaths.clear();
							bestPaths.add(chosenPath);
							break;
						}
					}
				}
				return new Instruction(InstructionType.OPEN_FILE, bestPaths);
			} default:
			{
				return instructionPackets.get(0).getInstruction();
			}
		}
	}
	
	/**
	 * Scores the similarity of two instructions. If the instructions are of the same type the score will be a positive number; 
	 * two instructions of different types will return a negative score
	 * @param instruction1 The first instruction being compared
	 * @param instruction2 The second instruction being compared
	 * @return A score of the similarity between the numbers. Positive results closer to zero are more similar; negative results signify different instruction types
	 */
	public double rateSimilarity(Instruction instruction1, Instruction instruction2)
	{
		if(instruction1.getType() != instruction2.getType())
		{
			return -1;
		}
		switch(instruction1.getType())
		{
			case START:
			{
				return 0;
			} case MOVE:
			{
				int dimensions = Math.max(instruction1.getParameters().size(), instruction2.getParameters().size());
				double[] vals1 = new double[dimensions];
				double[] vals2 = new double[dimensions];
				for(int i = 0; i < instruction1.getParameters().size(); i++)
				{
					vals1[i] = Double.parseDouble(instruction1.getParameters().get(i));
				}
				for(int i = 0; i < instruction2.getParameters().size(); i++)
				{
					vals2[i] = Double.parseDouble(instruction2.getParameters().get(i));
				}
				if(instruction1.getParameters().size() < dimensions)
				{
					for(int i = instruction1.getParameters().size(); i < dimensions; i++)
					{
						vals1[i] = 0;
					}
				} else if(instruction2.getParameters().size() < dimensions)
				{
					for(int i = instruction2.getParameters().size(); i < dimensions; i++)
					{
						vals2[i] = 0;
					}
				}
				double dist = 0;
				for(int i = 0; i < dimensions; i++)
				{
					dist += (vals1[i] - vals2[i]) * (vals1[i] - vals2[i]);
				}
				dist = Math.sqrt(dist);
				return dist;
			} case MOVE_MOUSE:
			{
				double x1 = Double.parseDouble(instruction1.getParameters().get(0));
				double y1 = Double.parseDouble(instruction1.getParameters().get(1));
				double x2 = Double.parseDouble(instruction2.getParameters().get(0));
				double y2 = Double.parseDouble(instruction2.getParameters().get(1));
				return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
			} case CLICK:
			{
				int matchingButtons = 0;
				for(String instruction1Parameter : instruction1.getParameters())
				{
					for(String instruction2Parameter : instruction2.getParameters())
					{
						if(instruction1Parameter.equalsIgnoreCase(instruction2Parameter))
						{
							matchingButtons++;
							break;
						}
					}
				}
				double numParams1 = instruction1.getParameters().size();
				double numParams2 = instruction2.getParameters().size();
				if(numParams1 == numParams2 && numParams1 == matchingButtons)
				{
					return 0;
				} else if(matchingButtons > 0)
				{
					return 1;
				} else
				{
					return 2;
				}
			} case SCROLL:
			{
				int scroll1 = Integer.parseInt(instruction1.getParameters().get(0));
				int scroll2 = Integer.parseInt(instruction2.getParameters().get(0));
				return Math.abs(scroll1 - scroll2);
			} case PRESS_KEY:
			{
				int matchingKeys = 0;
				for(String instruction1Parameter : instruction1.getParameters())
				{
					for(String instruction2Parameter : instruction2.getParameters())
					{
						if(instruction1Parameter.equalsIgnoreCase(instruction2Parameter))
						{
							matchingKeys++;
							break;
						}
					}
				}
				return Math.max((instruction1.getParameters().size() - matchingKeys) / instruction1.getParameters().size(), 
						(instruction2.getParameters().size() - matchingKeys) / instruction2.getParameters().size());
			} case OPEN_FILE:
			{
				if(instruction1.getParameters().get(0).equals(instruction2.getParameters().get(0)))
				{
					return 0;
				} else if(instruction1.getParameters().get(0).equalsIgnoreCase(instruction2.getParameters().get(0)))
				{
					return 1;
				} else
				{
					return Math.abs(instruction1.getParameters().get(0).length() - instruction2.getParameters().get(0).length()) + 2;
				}
			} case WRITE:
			{
				if(instruction1.getParameters().get(0).equals(instruction2.getParameters().get(0)))
				{
					return 0;
				} else if(instruction1.getParameters().get(0).equalsIgnoreCase(instruction2.getParameters().get(0)))
				{
					return 1;
				} else
				{
					return Math.abs(instruction1.getParameters().get(0).length() - instruction2.getParameters().get(0).length()) + 2;
				}
			} case FINISH:
			{
				return 0;
			} default:
			{
				return -1;
			}
		}
	}
	
}
