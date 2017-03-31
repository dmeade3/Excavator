package util;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dcmeade on 3/30/2017.
 */
public class GeneralUtil
{
    public static boolean pathReachable(String filepath)
    {
        File file = new File(filepath);

        if ((file.exists()) && (file.isFile()))
        {
            return true;
        }

        return false;
    }

    public static ObservableList<TimeUnit> getTimesObservableList()
    {
        List<TimeUnit> timesList = new ArrayList<>();

        // Add all the times into the timesList
        Collections.addAll(timesList, TimeUnit.values());

        return FXCollections.observableArrayList(timesList);
    }
}
