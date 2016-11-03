package static_analysis;

import data_storage.SystemConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StaticAnalysis
{
    ///// Fields /////

    // For fastest filtering put the most common file types up front
    private static String[] validEndings = { ".java" };

    private static List<File> sourceFiles = new ArrayList<>();

    ///// Methods /////

    // TODO method to detect method call
    // TODO method to detect start of for loop
    // TODO method to detect start of while loop


    private StaticAnalysis() {}

    public static void runStaticAnalysis()
    {
        sourceFiles = getFileNames(SystemConfig.OUTSIDEPROGRAMSOURCEPATH);

        /*for (File sourceFile : sourceFiles)
        {
            System.out.println(sourceFile);
            analyzeSourceFile(sourceFile);
        }*/


        analyzeSourceFile(sourceFiles.get(0));
    }

    private static void analyzeSourceFile(File sourceFile)
    {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(sourceFile));

            // Todo instead of whole file line by line make file into a string, if cant fit in one string have error

            // Make file into one string
            String stringFile = "";
            String line;
            while((line = in.readLine()) != null)
            {
                stringFile = stringFile.concat(line + "\n");
            }


            //System.out.println(stringFile);

            ///// Evaluation here /////

            // TODO class detection -> method detection -> then the loop detections




            // TODO write own function for fetching classes
            // TODO parsing could be its own utility class
            // TODO because of the possblity of other classes / inner classes in the file must return a list of lists
            List<List<String>> classStrings = parseClassStrings(stringFile);

            // Inside Class
            for (List<String> classesString : classStrings)
            {
                System.out.println("############################################################");

                for (String classString : classesString)
                {
                    System.out.println(classString);
                }

                System.out.println("############################################################");

            }







            ///////////////////////////


            in.close();
        }
        catch (IOException e)
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

    private static List<List<String>> parseClassStrings(String stringFile)
    {
        List<List<String>> allClassList = new ArrayList<>();

        List<String> classStringList = new ArrayList<>();

        String[] splitFile = stringFile.split("\\s");

        List<Integer> classOccuranceMarkers = new ArrayList<>();

        int parensBalanceHolder = 0;


        // TODO process class -> if the word "class" seen inside the other classes processing mark it and go over the class just processed
        // TODO if class after the main class process this separately
        // TODO testing: one class in file, one main class one inner class, 2 separate classes in a file, class with inner class and an aditional class after
        // TODO Does not move past the balanced parenthesis marking if there was a class detected inside, after no more inner detected move on
        // TODO pseudocode lay this out bellow before start
        // TODO test for multiple inner classes within inner classes

        // TODO if theres an inner class think about taking it out of the main string list so results are less confusing

        // mark all instances of the word class and have an array
        // then have a function that creates a class based on a starting point and goes till the parens are balaced

        int ctr = 0;
        for (String token : splitFile)
        {
            if (token.equals("class"))
            {
                classOccuranceMarkers.add(ctr);
            }

            ctr++;
        }

        for (int i : classOccuranceMarkers)
        {
            int currentIndex = i;
            List<String> currentClass = new ArrayList<>();

            // Get to the first '{'
            while(!splitFile[currentIndex].equals("{"))
            {
                currentClass.add(splitFile[currentIndex]);

                currentIndex++;
            }

            // Count the first {
            parensBalanceHolder = 1;
            currentClass.add(splitFile[currentIndex]);
            currentIndex++;

            while (parensBalanceHolder != 0)
            {

                currentClass.add(splitFile[currentIndex]);

                if (splitFile[currentIndex].equals("{"))
                {
                    parensBalanceHolder++;
                }
                else if (splitFile[currentIndex].equals("}"))
                {
                    parensBalanceHolder--;
                }

                currentIndex++;
            }

            allClassList.add(currentClass);
        }

        // Filters out with blank spaces in the class string list list

        allClassList = filterClassList(allClassList);

        return allClassList;
    }

    private static List<List<String>> filterClassList(List<List<String>> allClassList)
    {
        List<List<String>> returnClassListList = new ArrayList<>();

        for (List<String> classesString : allClassList)
        {
            List<String> currentStringList = new ArrayList<>();

            for (String classString : classesString)
            {
                if(!classString.equals(""))
                {
                    currentStringList.add(classString);
                }
            }

            returnClassListList.add(currentStringList);
        }


        return returnClassListList;
    }

    public static List<File> getFileNames(String directoryName)
    {
        File directory = new File(directoryName);

        // get all the files from a directory
        File[] fList = directory.listFiles();
        for (File file : fList)
        {
            if (file.isFile())
            {
                if (filterFileByType(file))
                {
                    sourceFiles.add(file);
                }
            }
            else if (file.isDirectory())
            {
                getFileNames(file.getAbsolutePath());
            }
        }

        return sourceFiles;
    }

    // TODO put in general utility class
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
