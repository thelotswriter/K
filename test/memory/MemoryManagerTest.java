package memory;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import kaiExceptions.MemoryAccessException;

public class MemoryManagerTest 
{

	@BeforeClass
	public static void runOnceBeforeClass()
	{
		try 
		{
			assertNotNull(MemoryManager.getInstance());
			System.out.println("Run before class passed!");
		} catch (MemoryAccessException e) 
		{
			fail("MemoryManager failed to load");
		}
	}
	
	@AfterClass
	public static void runOnceAfterClass()
	{
		try 
		{
			MemoryManager.getInstance().close();
			System.out.println("Run after class passed!");
		} catch (MemoryAccessException e) 
		{
			fail("MemoryManager failed to close");
		}
	}
	
	/**
	 * Tests:
	 * Creating two key tables (pass)
	 * Creating a key table with the same name as a previously existing table (fail)
	 * Creating a key table with an empty string as a name (fail)
	 * Creating a key table with a null parameter (fail)
	 */
	@Test
	public void createKeyTableTest()
	{
		boolean passed = true;
		try 
		{
			MemoryManager.getInstance().createKeyTable("test");
			assertTrue(true);
			MemoryManager.getInstance().createKeyTable("test2");
			assertTrue(true);
			
		} catch (MemoryAccessException e) 
		{
			passed = false;
			fail("Failed to create key table");
		}
		try
		{
			MemoryManager.getInstance().createKeyTable("test");
			passed = false;
			fail("Failed to throw exception on repeated key table");
		} catch (MemoryAccessException e)
		{
			assertTrue(true);
		}
		try
		{
			MemoryManager.getInstance().createKeyTable("");
			passed = false;
			fail("Failed to throw exception on empty string");
		} catch (MemoryAccessException e)
		{
			assertTrue(true);
		}
		try
		{
			MemoryManager.getInstance().createKeyTable(null);
			passed = false;
			fail("Failed to throw exception on null string");
		} catch (MemoryAccessException e)
		{
			assertTrue(true);
		}
		if(passed)
		{
			System.out.println("Key Table Test Passed!");			
		} else
		{
			System.out.println("Key Table Test Failed!");
		}
	}
	
	/**
	 * Tests:
	 * Creating two tables with the same name but different keys (pass)
	 * Creating multiple tables with varying numbers of fields (pass)
	 * Creating tables with whitespace in the names (pass)
	 * Creating tables with no fields (fail)
	 */
	@Test
	public void createTableTest()
	{
		String key1 = "Table_Test";
		String key2 = "Table_Test_2";
		
		List<String> l0 = new ArrayList<>();
		List<String> l1 = new ArrayList<>();
		List<String> l2 = new ArrayList<>();
		List<String> l3 = new ArrayList<>();
		List<String> l4 = new ArrayList<>();
		
		l1.add("attr1");
		l2.add("attr1");
		l2.add("attr2");
		l3.add("attr 1");
		l4.add("attr1");
		l4.add("attr1");
		
		boolean passed = true;
		try 
		{
			MemoryManager.getInstance().createKeyTable(key1);
			MemoryManager.getInstance().createKeyTable(key2);
			
			MemoryManager.getInstance().createTable(key1, "test1", l1);
			MemoryManager.getInstance().createTable(key2, "test1", l1);
			MemoryManager.getInstance().createTable(key1, "test2", l2);
			MemoryManager.getInstance().createTable(key1, "test3", l3);
		} catch (MemoryAccessException e) 
		{
			fail("Failed to create table!");
			passed = false;
		}
		try
		{
			MemoryManager.getInstance().createTable(key1, "test4", l4);
			fail("Duplicate field names shouldn't be allowed");
			passed = false;
		} catch (MemoryAccessException e) {}
		try
		{
			MemoryManager.getInstance().createTable(key1, "test4", l0);
			fail("Fieldless tables shouldn't be allowed");
			passed = false;
		} catch (MemoryAccessException e) {}
		if(passed)
		{
			System.out.println("Create Table Test Passed!");
		} else
		{
			System.out.println("Create Table Test Failed!");
		}
	}
	
	/**
	 * Tests:
	 * Add entries to tables with one and two fields (pass)
	 * Add entries to tables with too many fields (fail)
	 * Add entries to tables with too few fields (fail)
	 */
	@Test
	public void updateTableTest()
	{
		String key1 = "UPDATE_KEY_1";
		
		String table1 = "test1";
		String table2 = "test2";
		
		List<String> a1 = new ArrayList<>();
		List<String> a2 = new ArrayList<>();
		
		a1.add("attr1");
		a2.add("attr1");
		a2.add("attr2");
		
		// Passing fields
		List<String> u11 = new ArrayList<>();
		List<String> u12 = new ArrayList<>();
		List<String> u21 = new ArrayList<>();
		List<String> u22 = new ArrayList<>();
		
		// Failing fields
		List<String> u13 = new ArrayList<>();
		List<String> u23 = new ArrayList<>();
		
		u11.add("orc");
		u12.add("elf");
		
		u21.add("robot");
		u21.add("jones");
		u22.add("johnny");
		u22.add("bravo");
		
		u13.add("river");
		u13.add("folk");
		
		u23.add("dexter");
		
		try 
		{
			MemoryManager.getInstance().createKeyTable(key1);
			MemoryManager.getInstance().createTable(key1, table1, a1);
			MemoryManager.getInstance().createTable(key1, table2, a2);
		} catch (MemoryAccessException e) 
		{
			fail("Setup of updateTableTest failed");
		}
		try 
		{
			MemoryManager.getInstance().updateTable(key1, table1, a1, u11);
			MemoryManager.getInstance().updateTable(key1, table1, a1, u12);
			MemoryManager.getInstance().updateTable(key1, table2, a2, u21);
			MemoryManager.getInstance().updateTable(key1, table2, a2, u22);
		} catch (MemoryAccessException e) 
		{
			fail("Failed to update correctly");
		}
		try
		{
			MemoryManager.getInstance().updateTable(key1, table1, a1, u13);
			fail("Added row with incorrect number of fields");
		} catch (MemoryAccessException e) {}
		try
		{
			MemoryManager.getInstance().updateTable(key1, table2, a2, u23);
			fail("Added row with incorrect number of fields");
		} catch (MemoryAccessException e) {}
		System.out.println("Update Table Test Passed!");
		
	}
	
	/**
	 * Get fields from a table which exist (pass)
	 * Get fields from a table which don't exist (fail)
	 * Get fields from a table using an invalid index, both too great and too small (fail)
	 */
	@Test
	public void getFieldsTest()
	{
		String key1 = "GET_TABLE_1";
		String table1 = "test1";
		
		List<String> fields1 = new ArrayList<>();
		
		List<String> data1 = new ArrayList<>();
		List<String> data2 = new ArrayList<>();
		List<String> data3 = new ArrayList<>();
		
		fields1.add("State");
		fields1.add("Country");
		
		data1.add("New York");
		data1.add("USA");
		data2.add("Paris");
		data2.add("France");
		data3.add("Berlin");
		data3.add("Germany");
		
		try
		{
			MemoryManager manager = MemoryManager.getInstance();
			manager.createKeyTable(key1);
			manager.createTable(key1, table1, fields1);
			manager.updateTable(key1, table1, fields1, data1);
			manager.updateTable(key1, table1, fields1, data2);
			manager.updateTable(key1, table1, fields1, data3);
		} catch (MemoryAccessException e) 
		{
			fail("Setup of getFieldsTest failed");
		}
		try
		{
			MemoryManager manager = MemoryManager.getInstance();
			List<String> data3Copy = manager.getFields(key1, table1, 0);
			List<String> data2Copy = manager.getFields(key1, table1, 1);
			List<String> data1Copy = manager.getFields(key1, table1, 2);
			if(!data1Copy.equals(data1) || !data2Copy.equals(data2) || !data3Copy.equals(data3))
			{
				fail("Contents returned different from contents submitted!");
			}
		} catch (MemoryAccessException e)
		{
			fail("Failed to get data from table");
		}
		try
		{
			MemoryManager.getInstance().getFields(key1, table1, 4);
			fail("Shouldn't read nonexistant rows!");
		} catch (MemoryAccessException e) {}
		try
		{
			MemoryManager.getInstance().getFields(key1, table1, -1);
			fail("Can't read future rows!");
		} catch (MemoryAccessException e) {}
		System.out.println("Get Field Test Passed!");
	}

}
