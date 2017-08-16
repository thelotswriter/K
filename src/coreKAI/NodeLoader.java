package coreKAI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnreadableActionNodeException;
import processTree.ActionNode;
import processTree.CommandNode;
import processTree.ThingNode;

/**
 * Loads the nodes
 * @author BLJames
 *
 */
public class NodeLoader 
{
	
	private static NodeLoader SINGLETON = null;
	
	private NodeLoader(){}
	
	/**
	 * Gets the Node Loader
	 * @return The Node Loader
	 */
	public static NodeLoader getInstance()
	{
		if(SINGLETON == null)
		{
			SINGLETON = new NodeLoader();
		}
		return SINGLETON;
	}
	
	/**
	 * Converts the given file into a command node
	 * @param nodeFile The file with the data to be loaded
	 * @return The command node which had been stored in the file
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public CommandNode convertToCommandNode(File nodeFile) throws FileNotFoundException, IOException
	{
		try(Reader reader = new FileReader(nodeFile);)
		{
			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			JsonElement json = parser.parse(reader);
			return gson.fromJson(json, new TypeToken<CommandNode>(){}.getType());
		}
	}
	
	/**
	 * Converts the given file into a thing node
	 * @param nodeFile The file with the data to be loaded
	 * @return The thing node which had been stored in the file
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ThingNode convertToThingNode(File nodeFile) throws FileNotFoundException, IOException
	{
		try(Reader reader = new FileReader(nodeFile);)
		{
			Gson gson = new Gson();
			JsonParser parser = new JsonParser();
			JsonElement json = parser.parse(reader);
			return gson.fromJson(json, new TypeToken<ThingNode>(){}.getType());
		}
	}
	
	/**
	 * Gets an instance of the action node saved to the given file
	 * @param nodeFile The file containing the action node
	 * @return An instance of the action node
	 * @throws UnreadableActionNodeException 
	 * @throws NotAnActionNodeException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ActionNode convertToActionNode(File nodeFile) throws UnreadableActionNodeException, NotAnActionNodeException, FileNotFoundException, IOException
	{
		checkJavaHome();
    	ensureCorrectJavaFormat(nodeFile);
    	// Compile!
        JavaCompiler.CompilationTask task = getCompilationTask(nodeFile);
        if(task.call())
        {
        	try
        	{
        		// Create a new custom class loader, pointing to the directory that contains the compiled classes
            	URLClassLoader classLoader = new URLClassLoader(new URL[]{nodeFile.getParentFile().getParentFile().toURI().toURL()});
            	// Load the class
                Class<?> loadedClass = classLoader.loadClass(javaFileToPackageDotClass(nodeFile));
                // Create a new instance...
                Object obj = loadedClass.newInstance();
                ActionNode loadedNode;
                if(obj instanceof ActionNode) 
                {
                    // Cast to ActionNode
                    loadedNode = (ActionNode) obj;
                    classLoader.close();
                    return loadedNode;
                } else
                {
                	classLoader.close();
                	throw new NotAnActionNodeException(nodeFile.getAbsolutePath());
                }        		
        	} catch(ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e)
        	{
        		throw new UnreadableActionNodeException(nodeFile.getAbsolutePath());
        	}
        } else
        {
        	throw new UnreadableActionNodeException(nodeFile.getAbsolutePath());
        }
	}
	/**
	 * Makes sure the system property java.home is correctly configured for the compiler
	 */
	private void checkJavaHome()
	{
		String javaHome = System.getProperty("java.home");
    	if(!javaHome.matches(".*jdk.*"))
    	{
    		File jHome = new File(javaHome);
    		String[] possibleFolders = jHome.getParentFile().list();
    		String match = ".*jdk" + System.getProperty("java.version");
    		for( String possibleFolder : possibleFolders)
    		{
    			if(possibleFolder.matches(match))
    			{
    				System.setProperty("java.home", jHome.getParent() + "\\" + possibleFolder);
    				break;
    			}
    		}
    		if(!javaHome.matches(".*jdk.*"))
    		{
    			for( String possibleFolder : possibleFolders)
        		{
        			if(possibleFolder.matches(".*jdk.*"))
        			{
        				System.setProperty("java.home", jHome.getParent() + "\\" + possibleFolder);
        				break;
        			}
        		}    			
    		}
    	}
	}
	
	/**
	 * Ensures the java file's package declaration matches its parent directory 
	 * @param javaFile A .java file
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	private void ensureCorrectJavaFormat(File javaFile) throws FileNotFoundException, IOException
	{
		File temp = new File("action.temp");
		PrintWriter printer = new PrintWriter(new BufferedWriter(new FileWriter(temp)));
		String fileName = javaFile.getName();
		String directoryName = javaFile.getParentFile().getName();
		try(BufferedReader buffyTheFileSlayer = new BufferedReader(new FileReader(javaFile)))
		{
			boolean foundPackage = false;
			boolean foundClass = false;
			String line;
			while((line = buffyTheFileSlayer.readLine()) != null)
			{
				if(!foundPackage && line.matches("package .+"))
				{
					foundPackage = true;
					if(!line.matches(".*" + directoryName + ".*"))
					{
						line = "package " + directoryName + ";";
					}
				} else if(!foundClass && line.matches("public class .+ implements ActionNode"))
				{
					foundClass = true;
					if(!line.matches(".*" + fileName + ".*"))
					{
						line = "public class " + fileName + " extends ActionNode";
					}
				}
				printer.println(line);
			}
			printer.close();
			javaFile.delete();
			temp.renameTo(javaFile);
		}
	}

	/**
	 * Converts a java file to the "package.class" format
	 * @param java The class the string references
	 * @return A string of the form package.class
	 */
	private String javaFileToPackageDotClass(File java)
	{
		StringBuilder bob = new StringBuilder();
		bob.append(java.getParentFile().getName());
		bob.append('.');
		bob.append(java.getName().subSequence(0, java.getName().lastIndexOf('.')));
		return bob.toString();
	}
	
	/**
	 * Creates a new compilation task based on the given file
	 * @param file
	 * @return
	 */
	private JavaCompiler.CompilationTask getCompilationTask(File file)
	{
		DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(diagnostics, null, null);

        Iterable<? extends JavaFileObject> compilationUnit
                = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(file));
        return compiler.getTask(null, fileManager, diagnostics, null, null, compilationUnit);
	}
	
}
