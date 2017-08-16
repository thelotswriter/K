package memory;

import java.io.Closeable;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import kaiExceptions.MemoryAccessException;

/**
 * Provides a means of communication with memory
 * @author BLJames
 *
 */
public class MemoryManager implements Closeable
{
	
	private static MemoryManager SINGLETON = null;
	private static String MemoryLocation = "Memory.db";
	
	private Connection connection = null;
	private Set<String> keys;
	
	/**
	 * Creates a new MemoryManager, connecting to the relevant database
	 * @throws MemoryAccessException Thrown if there is a problem connecting to the database
	 */
	private MemoryManager() throws MemoryAccessException
	{
		StringBuilder urlBuilder = new StringBuilder();
		urlBuilder.append("jdbc:sqlite:");
		String folder = new File("./file.txt").getParentFile().getAbsolutePath();
		urlBuilder.append(folder.substring(0, folder.lastIndexOf('\\')));
		urlBuilder.append('\\');
		urlBuilder.append(MemoryLocation);
		try
		{
			connection = DriverManager.getConnection(urlBuilder.toString());
			keys = new HashSet<>();
		} catch (SQLException e) 
		{
			throw new MemoryAccessException();
		}
	}
	
	/**
	 * Gets the Memory Manager
	 * @return The Memory Manager, connected to memory
	 * @throws MemoryAccessException Thrown if the Memory Manager is unable to connect to memory
	 */
	public static MemoryManager getInstance() throws MemoryAccessException
	{
		if(SINGLETON == null)
		{
			SINGLETON = new MemoryManager();
		}
		return SINGLETON;
	}
	
	/**
	 * Creates a new key table in memory
	 * @param name The name of the new key table
	 * @throws MemoryAccessException Thrown if there is a problem accessing memory
	 */
	public void createKeyTable(String name) throws MemoryAccessException
	{
		try 
		{
			Statement statement = connection.createStatement();
			StringBuilder tableBuilder = new StringBuilder();
			tableBuilder.append("CREATE TABLE ");
			tableBuilder.append(name.replace(' ', '_'));
			tableBuilder.append(" (NAME TEXT PRIMARY KEY NOT NULL, ");
			tableBuilder.append("TABLE_NAME TEXT NOT NULL)");
			statement.executeUpdate(tableBuilder.toString());
			keys.add(name);
			statement.close();
		} catch (SQLException e) 
		{
			throw new MemoryAccessException();
		}
	}
	
	/**
	 * Drops a key table and all the tables it references
	 * @param name The name of the key table
	 * @throws MemoryAccessException Thrown if there is a problem accessing memory
	 */
	private void dropKeyTable(String name) throws MemoryAccessException
	{
		try 
		{
			Statement statement = connection.createStatement();
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("SELECT TABLE_NAME FROM ");
			sqlBuilder.append(name.replace(' ', '_'));
			ResultSet resultSet = statement.executeQuery(sqlBuilder.toString());
			List<String> tables = new ArrayList<>();
			while(resultSet.next())
			{
				tables.add(resultSet.getString("TABLE_NAME"));
			}
			resultSet.close();
			for(String table : tables)
			{
				StringBuilder dropBuilder = new StringBuilder();
				dropBuilder.append("DROP TABLE ");
				dropBuilder.append(table);
				statement.execute(dropBuilder.toString());
			}
			StringBuilder keyDropBuilder = new StringBuilder();
			keyDropBuilder.append("DROP TABLE ");
			keyDropBuilder.append(name.replace(' ', '_'));
			statement.execute(keyDropBuilder.toString());
			statement.close();
		} catch (SQLException e) 
		{
			throw new MemoryAccessException();
		}
	}
	
	/**
	 * Creates a new table in memory
	 * @param keyTableName The name of the key table
	 * @param tableName The name of the table
	 * @param fields The fields of the table
	 * @throws MemoryAccessException Thrown if there is a problem accessing memory
	 */
	public void createTable(String keyTableName, String tableName, List<String> fields) throws MemoryAccessException
	{
		if(fields.size() <= 0)
		{
			throw new MemoryAccessException();
		}
		try 
		{
			DatabaseMetaData metaData = connection.getMetaData();
			StringBuilder tableNameBuilder = new StringBuilder();
			tableNameBuilder.append(keyTableName.replace(' ', '_'));
			tableNameBuilder.append("_");
			tableNameBuilder.append(tableName);
			ResultSet rs = metaData.getTables(null, null, tableNameBuilder.toString(), null);
			// Check if the table name is already in use 
			while(rs.next())
			{
				rs.close();
				tableNameBuilder.append("K");
				rs = metaData.getTables(null, null, tableNameBuilder.toString(), null);
			}
			rs.close();
			
			Statement statement = connection.createStatement();
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("CREATE TABLE ");
			sqlBuilder.append(tableNameBuilder.toString());
			sqlBuilder.append(" (ID INTEGER PRIMARY KEY AUTOINCREMENT");
			for(int i = 0; i < fields.size(); i++)
			{
				sqlBuilder.append(", ");
				sqlBuilder.append(fields.get(i).replace(' ', '_'));
				sqlBuilder.append(" TEXT NOT NULL");
			}
			sqlBuilder.append(")");
			statement.executeUpdate(sqlBuilder.toString());
			statement.close();
			
			StringBuilder keyBuilder = new StringBuilder();
			keyBuilder.append("INSERT INTO ");
			keyBuilder.append(keyTableName.replace(' ', '_'));
			keyBuilder.append(" (NAME, TABLE_NAME) VALUES(?,?)");
			PreparedStatement preparedStatement = connection.prepareStatement(keyBuilder.toString());
			preparedStatement.setString(1, tableName);
			preparedStatement.setString(2, tableNameBuilder.toString());
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) 
		{
			throw new MemoryAccessException();
		}
	}

	/**
	 * Adds a new row of data to the specified table 
	 * @param keyName The name of the key table
	 * @param tableName The name of the table
	 * @param fields The fields of the table
	 * @param entries The current entries being added to the table
	 * @throws MemoryAccessException Thrown if there is a problem accessing memory
	 */
	public void updateTable(String keyTableName, String tableName, List<String> fields, List<String> entries) throws MemoryAccessException
	{
		try 
		{
			String trueTableName = getTrueTableName(keyTableName, tableName);
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("INSERT INTO ");
			sqlBuilder.append(trueTableName);
			sqlBuilder.append(" (");
			sqlBuilder.append(fields.get(0));
			for(int i = 1; i < fields.size(); i++)
			{
				sqlBuilder.append(", ");
				sqlBuilder.append(fields.get(i));
			}
			sqlBuilder.append(") VALUES(?");
			for(int i = 1; i < entries.size(); i++)
			{
				sqlBuilder.append(",?");
			}
			sqlBuilder.append(")");
			
			PreparedStatement preparedStatement = connection.prepareStatement(sqlBuilder.toString());
			for(int i = 0; i < entries.size(); i++)
			{
				preparedStatement.setString(i + 1, entries.get(i));
			}
			preparedStatement.executeUpdate();
			preparedStatement.close();
		} catch (SQLException e) 
		{
			throw new MemoryAccessException();
		}
	}
	
	/**
	 * Gets the values of the fields from a particular row of a table
	 * @param keyTableName The name of the key table
	 * @param tableName The name of the table
	 * @param reverseIndex The index, from the end of the table - 0 is the last element, 1 is the second to last, etc.
	 * @return The value of the fields at teh given row
	 * @throws MemoryAccessException Thrown if the memory can't be accessed
	 */
	public List<String> getFields(String keyTableName, String tableName, int reverseIndex) throws MemoryAccessException
	{
		try 
		{
			String trueTableName = getTrueTableName(keyTableName, tableName);
			int nRows = getRowCount(trueTableName);
			int rowIndex = nRows - reverseIndex;
			StringBuilder sqlBuilder = new StringBuilder();
			sqlBuilder.append("SELECT * FROM ");
			sqlBuilder.append(trueTableName);
			sqlBuilder.append(" WHERE ID=");
			sqlBuilder.append(rowIndex);
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(sqlBuilder.toString());
			ResultSetMetaData rsMetaData = resultSet.getMetaData();
			int nFields = rsMetaData.getColumnCount();
			resultSet.next();
			List<String> fields = new ArrayList<>();
			for(int i = 2; i <= nFields; i++)
			{
				fields.add(resultSet.getString(i));
			}
			resultSet.close();
			return fields;
		} catch (SQLException e) 
		{
			throw new MemoryAccessException();
		}
	}
	
	/**
	 * Gets the actual name of the table
	 * @param keyTable The table where the relevent keys are
	 * @param name The supposed name of the table
	 * @return The true name of the table
	 * @throws SQLException Thrown if there is a problem connecting to the database
	 */
	private String getTrueTableName(String keyTable, String name) throws SQLException
	{
		StringBuilder keyQueryBuilder = new StringBuilder();
		keyQueryBuilder.append("SELECT TABLE_NAME FROM ");
		keyQueryBuilder.append(keyTable.replace(' ', '_'));
		keyQueryBuilder.append(" WHERE NAME='");
		keyQueryBuilder.append(name);
		keyQueryBuilder.append("'");
		Statement statement;
		statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(keyQueryBuilder.toString());
		resultSet.next();
		String tableName = resultSet.getString("TABLE_NAME");
		resultSet.close();
		return tableName;
	}
	
	/**
	 * Gets the number of rows in a given table
	 * @param trueTableName The name of the table
	 * @return The number of rows in the table
	 * @throws SQLException Thrown if there is a problem connecting to the database
	 */
	private int getRowCount(String trueTableName) throws SQLException
	{
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT count(*) FROM ");
		sqlBuilder.append(trueTableName);
		Statement statement = connection.createStatement();
		ResultSet resultSet = statement.executeQuery(sqlBuilder.toString());
		resultSet.next();
		int rowCount = resultSet.getInt(1);
		resultSet.close();
		return rowCount;
	}
	
	@Override
	public void close() 
	{
		try 
		{
			for(String key : keys)
			{
				dropKeyTable(key);
			}
			connection.close();
		} 
		catch (Exception e) {}
		SINGLETON = null;
	}
	
}