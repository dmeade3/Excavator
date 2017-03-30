package Util;

import java.io.File;

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
}
