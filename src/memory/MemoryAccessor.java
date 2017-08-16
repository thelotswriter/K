package memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kaiExceptions.MemoryAccessException;
import processTree.CommandNode;
import processTree.ThingNode;

public class MemoryAccessor 
{
	private String keyTable;
	
	public MemoryAccessor(CommandNode root) throws MemoryAccessException
	{
		keyTable = root.getName();
		MemoryManager.getInstance().createKeyTable(keyTable);
	}
	
	/**
	 * Prepares memory to remember attributes about a thing
	 * @param thing The thing to be remembered
	 * @throws MemoryAccessException Thrown if there is a problem accessing memory
	 */
	public void prepareForThing(ThingNode thing) throws MemoryAccessException
	{
		MemoryManager.getInstance().createTable(keyTable, thing.getName(), thing.listAttributes());
	}
	
	/**
	 * Remembers the thing's current state
	 * @param thing The thing which will be remembered in its current state
	 * @throws MemoryAccessException Thrown if there is a problem accessing memory
	 */
	public void rememberThing(ThingNode thing) throws MemoryAccessException
	{
		List<String> attributes = thing.listAttributes();
		List<String> currentAttributes = new ArrayList<String>();
		for(int i = 0; i < attributes.size(); i++)
		{
			currentAttributes.add(thing.getAttribute(attributes.get(i)));
		}
		MemoryManager.getInstance().updateTable(keyTable, thing.getName(), attributes, currentAttributes);
	}
	
	/**
	 * Gets the memory of how a thing was some number of states previously 
	 * @param thing The thing about which memories are being recalled
	 * @param numberOfStatesAgo How many states have passed since the state being recalled
	 * @return A map, connecting attributes and their former state
	 * @throws MemoryAccessException 
	 */
	public Map<String, String> getMemory(ThingNode thing, int numberOfStatesAgo) throws MemoryAccessException
	{
		List<String> memory = MemoryManager.getInstance().getFields(keyTable, thing.getName(),numberOfStatesAgo);
		List<String> attributes = thing.listAttributes();
		Map<String, String> mappedMemory = new HashMap<>();
		for(int i = 0; i < memory.size(); i++)
		{
			mappedMemory.put(attributes.get(i), memory.get(i));
		}
		return mappedMemory;
	}
	
}
