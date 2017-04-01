package util;

import javafx.collections.ObservableList;

import java.text.DecimalFormat;

import static util.GeneralUtil.getTimesObservableList;

public class SystemConfig
{
    // TODO Needs to be a default, and if in config file it is loaded and used instead
    // TODO have an on close listener that saves off the user data in GUIMain

    ////// VARIABLE IDENTIFIERS //////
    public static final String ENTERING = "_1_2_3_4_5_6_&_8_9_Entering_____";
    public static final String EXITING  = "_1__2_3_4_5_6_4_7_6_8_9_Exiting_";
    public static final String TIMESTAMP_VARIABLE = "_123234345_timestamp129387346587_";





    ////// GUI DIMENSIONS OPTIONS //////
    public static final int SPACE_FROM_BOTTOM_BUTTONS = 200;
    public static final int SCENE_WIDTH = 1600;
    public static final int SCENE_HEIGHT = 1000;
    public static final int DIALOG_HEIGHT = 500;
    public static final int DIALOG_WIDTH = 400;
    public static final int SCROLL_PANE_WIDTH = 400;


    ////// GUI STYLESHEETS //////
    public static final String NAME_OF_MAIN_STYLESHEET = "mainStyleSheet.css";


    ////// NUMBER FORMATTER'S //////
    public static final DecimalFormat FORMATTER_NANO = new DecimalFormat("#,###.#####");
    

    ////// MISC_OPTIONS //////
    public static final int INITIAL_DYNAMIC_DATA_SIZE = 100;
    public static final ObservableList<TimeUnit> TIME_UNIT_OPTIONS = getTimesObservableList();
    public static boolean MAIN_TREE_EXPANDED = false;



    // TODO make all these read in by config maybe make this a static block so it doesnt have to be called if that is a thing
    public void readInConfigFile()
    {

    }
}
