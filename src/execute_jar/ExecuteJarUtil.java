package execute_jar;

/**
 * Project: Excavator
 * File Name: ExecuteJarUtil.java
 * <p>
 * Created by David on 3/31/2017.
 */
public class ExecuteJarUtil
{

	// TODO move everything from other parts of the program that have to do with executing and com


	////// PATHS - PATH HELPERS //////
	public static final String PROJECT_DIR = System.getProperty("user.dir");
	public static final String USER_NAME = System.getProperty("user.name");
	public static String OUTSIDE_PROGRAM_JAR_PATH = "C:\\Users\\" + USER_NAME + "\\Desktop\\Intelij Workspace\\Hello-World\\out\\artifacts\\Hello_World_jar\\Hello World.jar";
	public static final String AGENT_PATH = PROJECT_DIR + "\\out\\artifacts\\Profiler_jar\\Profiler.jar";
	public static final String AGENT_COMMAND = "-javaagent:" + AGENT_PATH;
	public static String OUTSIDE_PROGRAM_COMMAND = "java -classpath \"C:\\Users\\David\\Desktop\\\"Intelij Workspace\"\\Excavator\\lib\\javassist.jar\" " +  AGENT_COMMAND + "\" -jar \"" + OUTSIDE_PROGRAM_JAR_PATH + "\"";


	// The solution to the problem of the javaassist path is the \" \" aroung and subsection of the path that has a space in it
	// TODO need a method that will convert paths to windows classpath usable format







	////// RUN OPTIONS //////
	public static final boolean SHOW_OUTSIDE_PROGRAM_OUTPUT = true;
	public static final boolean FILTER_OUT_NON_USER_METHODS = true; // Running a gui will be much easier for no with this as true, much faster in general
	public static final boolean FILTER_OUT_ERROR_CAUSING_METHODS = true;
	public static final int     TIME_PRECISION_PLACES = 5;
}
