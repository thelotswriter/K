//package memory;
//
//import static org.junit.Assert.*;
//
//import java.util.List;
//import java.util.Map;
//import java.util.ArrayList;
//
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Test;
//
//import kaiExceptions.MemoryAccessException;
//import processTree.CommandNode;
//import processTree.ThingNode;
//
//public class MemoryAccessorTest 
//{
//	private String[] testCommand;
//	private ThingNode testThing1;
//	
//	@Before
//	public void executedBeforeEach()
//	{
//		testCommand = new String[2];
//		testCommand[0] = "Test";
//		testCommand[1] = "Command";
//		
//		List<String> attributes1 = new ArrayList<String>();
//		List<String> attrVals1 = new ArrayList<String>();
//		
//		attributes1.add("City");
//		attributes1.add("State");
//		
//		attrVals1.add("Provo");
//		attrVals1.add("Utah");
//		
//		testThing1 = ThingNode.generateTestThingNode("test1", attributes1, attrVals1);
//	}
//	
//	@After
//	public void executedAfterEach()
//	{
//		try 
//		{
//			MemoryManager.getInstance().close();
//		} catch (MemoryAccessException e) 
//		{
//			fail("Memory not appropriately cleared");
//		}
//	}
//
//	@Test
//	public void memoryAccessorConstructorTest()
//	{
//		try 
//		{
//			MemoryAccessor testAccessor = new MemoryAccessor(CommandNode.generateTestCommandNode(testCommand));
//			assertNotNull(testAccessor);
//		} catch (MemoryAccessException e) 
//		{
//			fail("Failed to construct MemoryAccessor");
//		}
//	}
//	
//	@Test
//	public void prepareForThingTest()
//	{
//		try 
//		{
//			MemoryAccessor testAccessor = new MemoryAccessor(CommandNode.generateTestCommandNode(testCommand));
//			testAccessor.prepareForThing(testThing1);
//		} catch (MemoryAccessException e) 
//		{
//			fail();
//		}
//	}
//	
//	@Test
//	public void rememberThingTest()
//	{
//		MemoryAccessor testAccessor;
//		try 
//		{
//			testAccessor = new MemoryAccessor(CommandNode.generateTestCommandNode(testCommand));
//			testAccessor.prepareForThing(testThing1);
//			
//			testAccessor.rememberThing(testThing1);
//			testThing1.setAttribute("State", "New York");
//			testThing1.setAttribute("City", "New York");
//			
//			testAccessor.rememberThing(testThing1);
//			
//			testThing1.setAttribute("State", "Nevada");
//			testThing1.setAttribute("City", "Las Vegas");
//			
//			testThing1.setAttribute("State", "Texas");
//			testThing1.setAttribute("City", "Houston");
//			
//			testAccessor.rememberThing(testThing1);
//		} catch (MemoryAccessException e) 
//		{
//			fail();
//		}
//	}
//	
//	@Test
//	public void getMemoryTest()
//	{
//		MemoryAccessor testAccessor;
//		try 
//		{
//			testAccessor = new MemoryAccessor(CommandNode.generateTestCommandNode(testCommand));
//			testAccessor.prepareForThing(testThing1);
//			
//			testAccessor.rememberThing(testThing1);
//			testThing1.setAttribute("State", "New York");
//			testThing1.setAttribute("City", "New York");
//			
//			testAccessor.rememberThing(testThing1);
//			
//			testThing1.setAttribute("State", "Nevada");
//			testThing1.setAttribute("City", "Las Vegas");
//			
//			testAccessor.rememberThing(testThing1);
//			
//			Map<String, String> lastMemory = testAccessor.getMemory(testThing1, 0);
//			Map<String, String> middleMemory = testAccessor.getMemory(testThing1, 1);
//			Map<String, String> firstMemory = testAccessor.getMemory(testThing1, 2);
//			
//			assertEquals(lastMemory.get("State"), "Nevada");
//			assertEquals(lastMemory.get("City"), "Las Vegas");
//			assertEquals(middleMemory.get("State"), "New York");
//			assertEquals(middleMemory.get("City"), "New York");
//			assertEquals(firstMemory.get("State"), "Utah");
//			assertEquals(firstMemory.get("City"), "Provo");
//		} catch (MemoryAccessException e) 
//		{
//			fail();
//		}
//	}
//}
