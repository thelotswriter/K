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
import java.util.List;

import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import kaiExceptions.NotAnActionNodeException;
import kaiExceptions.UnknownActionException;
import kaiExceptions.UnreadableActionNodeException;
import processTree.ActionNode;
import processTree.CommandNode;
import processTree.ThingNode;
import words.Adverb;

public class ActionNodeLoader 
{
	
	private static ActionNodeLoader SINGLETON = null;
	
	private ActionNodeLoader()
	{
		
	}
	
	public static ActionNodeLoader getInstance()
	{
		if(SINGLETON == null)
		{
			SINGLETON = new ActionNodeLoader();
		}
		return SINGLETON;
	}
	
	public ActionNode loadNode(String actionVerb, CommandNode root, ThingNode subject, ThingNode directObject, ThingNode indirectObject, List<Adverb> adverbs)
			throws UnknownActionException, UnreadableActionNodeException, NotAnActionNodeException, FileNotFoundException, IOException
	{
		KnowledgePacket packet = KnowledgeAccessor.getInstance().getVerbKnowledge(actionVerb);
		
		File nodeFile = packet.getFile();
		List<ActionElement> elements = packet.getActionElements();
		double confidence = packet.getConfidence();
		double priority = packet.getPriority();
		double urgency = packet.getUrgency();
		
		// ---------------------------Copied Code (from NodeLoader) Inserted Here---------------------------
		checkJavaHome();
    	try {
			ensureCorrectJavaFormat(nodeFile);
		} catch (IOException e) 
    	{
			throw new UnreadableActionNodeException(nodeFile.getAbsolutePath());
		}
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
                    loadedNode.load(root, subject, directObject, indirectObject, adverbs, elements, confidence, priority, urgency);
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
		// ------------------------------------End copied code-------------------------------------------
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
