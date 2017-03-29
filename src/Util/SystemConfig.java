package Util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.text.DecimalFormat;

public class SystemConfig
{
    ///// Read-In Fields /////

    ///// Non-Final Fields /////

    // TODO Needs to be a default, and if in config file it is loaded and used instead
    // TODO have an on close listener that saves off the user data in GUIMain
    // TODO make a utilities class and pull general methods from classes and things that dont belong here into it
    // TODO categorize these

    public static final String PROJECT_DIR = System.getProperty("user.dir");

    public static String OUTSIDE_PROGRAM_JAR_PATH = "C:\\Users\\dcmeade\\Desktop\\Intelij Workspace\\Hello-World\\out\\artifacts\\Hello_World_jar\\Hello World.jar";


    // TODO rethink this location / the first val / it shouldnt be in config it should be in some kind of data store or the source
    public static float OUTSIDE_PROGRAM_DYNAMIC_EXECUTION_TIME = -1;


    ////// Final Fields ///////
    public static final String PATH_TO_MAIN_STYLESHEET = "mainStyleSheet.css";
    public static final String AGENT_PATH = PROJECT_DIR + "\\out\\artifacts\\Profiler_jar\\Profiler.jar";
    public static final String AGENT_COMMAND = "-javaagent:" + AGENT_PATH;


    public static final String ENTERING = "_1_2_3_4_5_6_&_8_9_Entering_0987dfh";
    public static final String EXITING  = "_1__2_3_4_5_6_4_7_6_8_9_Exiting_2345dfouhy";

    public static final String TIMESTAMP_VARIABLE = "_123234345_timestamp129387346587_";


    public static final boolean SHOW_OUTSIDE_PROGRAM_OUTPUT = false;
    public static final boolean FILTER_OUT_NON_USER_METHODS = false; // Times get inacurate when timing outside the programs main methods


    public static final int SPACE_FROM_BOTTOM_BUTTONS = 200;
    public static final int SCENE_WIDTH = 1600;
    public static final int SCENE_HEIGHT = 1000;
	public static final int DIALOG_HEIGHT = 500;
	public static final int DIALOG_WIDTH = 400;
    public static final int SCROLL_PANE_WIDTH = 400;


    public static final DecimalFormat NUMBER_FORMATTER_NANO = new DecimalFormat("#,###.##########");


    public static final int INITIAL_DYNAMIC_DATA_SIZE = 100;


    public static final ObservableList<String> TIME_OPTIONS = FXCollections.observableArrayList( // make time options an enum
                    "Nanosecond",
                    "Millisecond",
                    "Second"
            );


    // TODO make all these read in by config maybe make this a static block so it doesnt have to be called if that is a thing
    public void readInConfigFile()
    {

    }
}
