package data_storage;

public class SystemConfig
{
    ///// Read-In Fields /////

    ///// Non-Final Fields /////
    public static String OUTSIDEPROGRAMJARPATH = "C:\\Users\\dcmeade\\Desktop\\Intelij Workspace\\Hello World\\out\\artifacts\\Hello_World_jar\\Hello World.jar";


	// Needs to be a default, and if in config file it is loaded and used instead
	//public static String OUTSIDEPROGRAMJARPATH = "C:\\Users\\David\\Desktop\\Intelij Workspace\\Hello-World\\out\\artifacts\\Hello_World_jar\\Hello World.jar";

    public static String OUTSIDEPROGRAMSOURCEPATH = "C:\\Users\\dcmeade\\Desktop\\Intelij Workspace\\Conways Game Of Life\\src";

    public static long OUTSIDEPROGRAMEXECUTIONTIME = -1;

    ///// Final Fields ///////
    public static final String PATHTOMAINSTYLESHEET = "/styleSheets/mainStyleSheet.css";
    public static final String AGENTCOMMAND = "-javaagent:C:\\Users\\dcmeade\\Desktop\\Intelij Workspace\\Excavator\\out\\artifacts\\Profiler_jar\\Profiler.jar";

	//public static final String AGENTCOMMAND = "-javaagent:C:\\Users\\David\\Desktop\\Intelij Workspace\\Excavator\\out\\artifacts\\Excavator_jar\\Excavator.jar";

    public static final boolean SHOWOUTSIDEPROGRAMOUTPUT = true;
    public static final boolean FILTEROUTNONUSERMETHODS  = false;

    public static final int SPACEFROMBOTTOMBUTTONS = 200;
    public static final int SCENEWIDTH = 1400;
    public static final int SCENEHEIGHT = 1000;

	public static final int DIALOGHEIGHT = 300;
	public static final int DIALOGWIDTH = 750;


    public static final int INITIALDYNAMICDATASIZE = 100;


    // TODO make all these read in by config
    public void readInConfigFile()
    {

    }
}
