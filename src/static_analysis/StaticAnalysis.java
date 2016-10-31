package static_analysis;


import data_storage.SystemConfig;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class StaticAnalysis
{

    ///// Fields /////
    private static String[] validEndings = {".java"};

    ///// Methods /////
    public static void runStaticAnalysis()
    {
        List<File> sourcefiles = getFileNames(SystemConfig.OUTSIDEPROGRAMSOURCEPATH);




    }

    public static List<File> getFileNames(String directoryName)
    {
        File directory = new File(directoryName);

        ArrayList<File> sourceFiles = new ArrayList<>();

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList)
        {
            if (file.isFile())
            {
                if (filterFileByType(file))
                {
                    sourceFiles.add(file);

                    System.out.println("Found: " + file);

                    printFile(file);
                }
            }
            else if (file.isDirectory())
            {
                getFileNames(file.getAbsolutePath());

                //System.out.println("Directory: " + file);
            }
        }

        return sourceFiles;
    }

    public static void printFile(File file)
    {

        BufferedReader in = null;
        try
        {
            in = new BufferedReader(new FileReader(file.toString()));

            String line;
            while((line = in.readLine()) != null)
            {
                System.out.println(line);
            }
            in.close();

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        finally {
            try
            {
                in.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static boolean filterFileByType(File file)
    {
        for (String filterOutFileEnd : validEndings)
        {
            if (file.toString().endsWith(filterOutFileEnd))
            {
                return true;
            }
        }

        return false;
    }

    public static void main(String... args)
    {
        System.out.println("Static Analysis");


        runStaticAnalysis();

    }
}
