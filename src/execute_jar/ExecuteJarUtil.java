package execute_jar;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Project: Excavator
 * File Name: ExecuteJarUtil.java
 * <p>
 * Created by David on 3/31/2017.
 */
public class ExecuteJarUtil
{

	// TODO make the other paths consist more of variables


	////// PATHS - PATH HELPERS //////
	public static final String USER_NAME = System.getProperty("user.name");
	public static final String PROJECT_DIR        = System.getProperty("user.dir");
	public static String OUTSIDE_PROGRAM_JAR_PATH = "C:\\Users\\David\\Desktop\\Intelij Workspace\\Hello-World\\out\\artifacts\\Hello_World_jar\\Hello World.jar";
	public static final String AGENT_PATH         = PROJECT_DIR + "\\out\\artifacts\\Profiler_jar\\Profiler.jar";

	public static final String AGENT_COMMAND = "-javaagent:" + "\"" + AGENT_PATH + "\"";
	public static final String CLASSPATH_COMMAND = "-classpath \";C:\\Users\\David\\Desktop\\Intelij Workspace\\Excavator\\lib\\javassist.jar;\"";     // + PROJECT_DIR + "\\lib\\javassist.jar\"";

	public static String OUTSIDE_PROGRAM_COMMAND = "java " + CLASSPATH_COMMAND + " " + AGENT_COMMAND + " -jar \"" + OUTSIDE_PROGRAM_JAR_PATH + "\"";


	// The solution to the problem of the javaassist path is the \" \" aroung and subsection of the path that has a space in it
	// TODO need a method that will convert paths to windows classpath usable format
	// TODO Maybe use path object


	public static String handlePathSpaces(String path)
	{
		String[] splitPath = path.split("\\\\");

		StringBuilder stringBuilder = new StringBuilder();

		for (String s : splitPath)
		{
			System.out.println(s);

			if (s.contains(" "))
			{
				s = "\"" + s + "\"";
			}

			stringBuilder.append(s + "\\");
		}

		System.out.println(stringBuilder.toString());

		return stringBuilder.toString().substring(0, stringBuilder.toString().length()-1);
	}

	////// RUN OPTIONS //////
	public static final boolean SHOW_OUTSIDE_PROGRAM_OUTPUT = true;
	public static final boolean FILTER_OUT_NON_USER_METHODS = true; // Running a gui will be much easier for no with this as true, much faster in general
	public static final boolean FILTER_OUT_ERROR_CAUSING_METHODS = true;
	public static final int     TIME_PRECISION_PLACES = 5;
}
