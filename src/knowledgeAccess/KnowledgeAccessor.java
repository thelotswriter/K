package knowledgeAccess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import instructions.InstructionInterpreter;
import kaiExceptions.InstructionInterpreterLoadException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnknownThingException;
import processTree.Model;

/**
 * A class for accessing KAI's knowledge (long-term memory)
 * @author BLJames
 *
 */
public class KnowledgeAccessor 
{
	private final Gson gson = new Gson();
	
	private final File thingFile = new File("thingData.json");
	private final File actionFile = new File("actionData.json");
	
	private static KnowledgeAccessor SINGLETON = null;
	
	private Map<String, String> thingMap;
	private Map<String, String> actionMap;
	
	private KnowledgeAccessor() throws FileNotFoundException, IOException
	{
		thingMap = new HashMap<String, String>();
		actionMap = new HashMap<String, String>();
		FileReader thingReader = new FileReader(thingFile);
		FileReader actionReader = new FileReader(actionFile);
		JsonParser parser = new JsonParser();
		JsonArray thingJArray = (JsonArray) parser.parse(thingReader);
		JsonArray actionJArray = (JsonArray) parser.parse(actionReader);
		JsonElement thingJson = thingJArray.get(0);
		JsonElement actionJson = actionJArray.get(0);
		thingMap = gson.fromJson(thingJson, new TypeToken<Map<String, String>>(){}.getType());
		actionMap = gson.fromJson(actionJson, new TypeToken<Map<String, String>>(){}.getType());
	}
	
	/**
	 * Gets the KnowledgeAccessor
	 * @return The Knowledge Accessor
	 * @throws IOException Thrown if there is a problem accessing one of the knowledge files
	 * @throws FileNotFoundException Thrown if there is a problem accessing one of the knowledge files
	 */
	public static KnowledgeAccessor getInstance() throws FileNotFoundException, IOException
	{
		if(SINGLETON == null)
		{
			SINGLETON = new KnowledgeAccessor();
		}
		return SINGLETON;
	}
	
	/**
	 * Gets a knowledge packet related to the given noun
	 * @param noun The noun about which information is being sought
	 * @return A knowledge packet with information pertaining to the node
	 * @throws UnknownThingException Thrown if there isn't enough knowledge about the given thing
	 */
	public KnowledgePacket getNounKnowledge(String noun) throws UnknownThingException
	{
		KnowledgePacket packet = new KnowledgePacket(KnowledgeType.THING);
		try(FileReader reader = new FileReader(new File(thingMap.get(noun))))
		{
			JsonParser parser = new JsonParser();
			JsonObject json = (JsonObject) parser.parse(reader);
			List<String> elements = gson.fromJson(json.get("ELEMENTS"), new TypeToken<List<String>>(){}.getType());
			List<String> categories = gson.fromJson(json.get("CATEGORIES"), new TypeToken<List<String>>(){}.getType());
			Map<String, String> attributes = gson.fromJson(json.get("ATTRIBUTES"), new TypeToken<Map<String, String>>(){}.getType());
			Map<String, Model> models = gson.fromJson(json.get("MODELS"), new TypeToken<Map<String, Model>>(){}.getType());
			String primaryModelName = json.get("PRIMARY_MODEL_NAME").getAsString();
			InstructionInterpreter interpreter = null;
			try 
			{
				interpreter = loadInterpreter(new File(json.get("INTERPRETER").getAsString()));
			} catch (InstructionInterpreterLoadException e) {}
			double confidence = json.get("CONFIDENCE").getAsDouble();
			
			packet.setElements(elements);
			packet.setCategories(categories);
			packet.setAttributes(attributes);
			packet.setModels(models);
			packet.setPrimaryModelName(primaryModelName);
			packet.setInstructionInterpreter(interpreter);
			packet.setConfidence(confidence);
		} catch (FileNotFoundException e) 
		{
			throw new UnknownThingException(noun);
		} catch (IOException e) 
		{
			throw new UnknownThingException(noun);
		} catch (NullPointerException e)
		{
			throw new UnknownThingException(noun);
		}
		return packet;
	}
	
	/**
	 * Gets a knowledge packet related to a verb
	 * @param verb The verb about which information is being gathered
	 * @return A knowledge packet with information about the verb
	 * @throws UnknownActionException Thrown if the action is unknown to KAI
	 */
	public KnowledgePacket getVerbKnowledge(String verb) throws UnknownActionException
	{
		KnowledgePacket packet = new KnowledgePacket(KnowledgeType.ACTION);
		try(FileReader reader = new FileReader(new File(actionMap.get(verb))))
		{
			JsonParser parser = new JsonParser();
			JsonObject json = (JsonObject) parser.parse(reader);
			String fileName = json.getAsJsonObject("FILE").getAsString();
			double confidence = json.getAsJsonObject("CONFIDENCE").getAsDouble();
			double priority = json.getAsJsonObject("PRIORITY").getAsDouble();
			double urgency = json.getAsJsonObject("URGENCY").getAsDouble();
			JsonElement actionElementJson = json.get("ACTION_ELEMENTS");
			List<ActionElement> elements = gson.fromJson(actionElementJson, new TypeToken<List<String>>(){}.getType());
			
			packet.setFile(new File(fileName));
			packet.setConfidence(confidence);
			packet.setPriority(priority);
			packet.setUrgency(urgency);
			packet.setActionElements(elements);
		} catch (FileNotFoundException e) 
		{
			throw new UnknownActionException(verb);
		} catch (IOException e) 
		{
			throw new UnknownActionException(verb);
		} catch (NullPointerException e)
		{
			throw new UnknownActionException(verb);
		}
		return packet;
	}
	
	private InstructionInterpreter loadInterpreter(File file) throws InstructionInterpreterLoadException
	{
		checkJavaHome();
		try 
		{
			ensureCorrectJavaFormat(file);
		} catch (FileNotFoundException e1) 
		{
			throw new InstructionInterpreterLoadException();
		} catch (IOException e1) 
		{
			throw new InstructionInterpreterLoadException();
		}
    	// Compile!
        JavaCompiler.CompilationTask task = getCompilationTask(file);
        if(task.call())
        {
        	try
        	{
        		// Create a new custom class loader, pointing to the directory that contains the compiled classes
            	URLClassLoader classLoader = new URLClassLoader(new URL[]{file.getParentFile().getParentFile().toURI().toURL()});
            	// Load the class
                Class<?> loadedClass = classLoader.loadClass(javaFileToPackageDotClass(file));
                // Create a new instance...
                Object obj = loadedClass.newInstance();
                if(obj instanceof InstructionInterpreter) 
                {
                    classLoader.close();
                    return (InstructionInterpreter) obj;
                } else
                {
                	classLoader.close();
                	throw new InstructionInterpreterLoadException();
                }        		
        	} catch(ClassNotFoundException | InstantiationException | IllegalAccessException | IOException e)
        	{
        		throw new InstructionInterpreterLoadException();
        	}
        } else
        {
        	throw new InstructionInterpreterLoadException();
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
