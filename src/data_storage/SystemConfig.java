package data_storage;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.DecimalFormat;

public class SystemConfig
{
    ///// Read-In Fields /////

    ///// Non-Final Fields /////

    // TODO Needs to be a default, and if in config file it is loaded and used instead
    // TODO have an on close listener that saves off the user data in GUIMain

    public static String OUTSIDEPROGRAMSOURCEPATH = "C:\\Users\\David\\Desktop\\Intelij Workspace\\Conways Game Of Life\\src";
    public static String OUTSIDEPROGRAMJARPATH = "C:\\Users\\dcmeade\\Desktop\\Intelij Workspace\\Hello-World\\out\\artifacts\\Hello_World_jar\\Hello World.jar";


    public static float OUTSIDEPROGRAMDYNAMICEXECUTIONTIME = -1;
    public static float OUTSIDEPROGRAMSTATICEXECUTIONTIME = -1;


    ///// Final Fields ///////
    public static final String PATHTOMAINSTYLESHEET = "/styleSheets/mainStyleSheet.css";
    public static final String AGENTPATH = "C:\\Users\\dcmeade\\Desktop\\Intelij Workspace\\Excavator\\out\\artifacts\\Profiler_jar\\Profiler.jar";
    public static final String AGENTCOMMAND = "-javaagent:" + AGENTPATH;


    public static final boolean SHOWOUTSIDEPROGRAMOUTPUT = false;
    public static final boolean FILTEROUTNONUSERMETHODS  = true; // Times get inacurate when timing outside the programs main methods


    public static final int SPACEFROMBOTTOMBUTTONS = 200;
    public static final int SCENEWIDTH = 1600;
    public static final int SCENEHEIGHT = 1000;
	public static final int DIALOGHEIGHT = 300;
	public static final int DIALOGWIDTH = 750;
    public static final int SCROLLPANEWIDTH = 400;


    public static final DecimalFormat NUMBERFORMATER = new DecimalFormat("#,###.##########");


    public static final int INITIALDYNAMICDATASIZE = 100;


    public static final ObservableList<String> TIMEOPTIONS = FXCollections.observableArrayList(
                    "Nanosecond",
                    "Millisecond",
                    "Second"
            );


    // TODO make all these read in by config
    public void readInConfigFile()
    {

    }
}
