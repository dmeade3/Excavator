package static_analysis;

import data_storage.StaticClassDataEntry;
import data_storage.StaticMethodDataEntry;
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

        for (File sourceFile : sourceFiles)
        {
            System.out.println(sourceFile);
            analyzeSourceFile(sourceFile);
        }


        //analyzeSourceFile(sourceFiles.get(0));
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


            // TODO parsing could be its own utility class
            // TODO because of the possblity of other classes / inner classes in the file must return a list of lists
            List<List<String>> classStrings = parseClassStrings(stringFile);

            // Inside Class
            for (List<String> classStringList : classStrings)
            {
                System.out.println("############################################################");

                System.out.println("Class Name: " + classStringList.get(1));


                StaticClassDataEntry currentClassEntry = new StaticClassDataEntry(classStringList.get(1));




                // TODO Will insert methoddata into class data
                parseMethods(classStringList, currentClassEntry);






                System.out.println("############################################################");

                // TODO remove
                //break;
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

    private static void parseMethods(List<String> classStringList, StaticClassDataEntry currentClassEntry)
    {

        List<List<String>> methodStringList = new ArrayList<>();


        // Break up class into method
        for (int i = 0; i < classStringList.size(); i++)
        {
            //System.out.println(classStringList.get(i));

            if (classStringList.get(i).contains("{"))
            {
                int parenthesisBalanceHolder = 0;
                int methodIndexStart = 0;

                String name;
                int forLoopCount = 0;
                int whileLoopCount = 0;
                int complexityScore = 0;

                // get the name of the method
                if (classStringList.get(i-1).contains(")"))
                {

                    int j = i;
                    while (!classStringList.get(j).contains("("))
                    {
                        j--;
                    }

                    name = classStringList.get(j).replaceAll("\\(.*", "");

                    methodIndexStart = j;

                    if (name.equals(""))
                    {
                        name = classStringList.get(j - 1);
                        methodIndexStart = j - 1;
                    }

                    // Filter out non method names
                    if (filterMethodName(name))
                    {
                        continue;
                    }

                    System.out.println("\tMethod Name: " + name);

/////////////////////// Get the rest of the info by iterating over the method///////////////////////////////////////////

                    // get to the first { of the method
                    while (!classStringList.get(methodIndexStart).contains("{"))
                    {
                        methodIndexStart++;
                    }

                    int methodIndex = methodIndexStart++;

                    do
                    {
                        // Evaluate forloopcount
                        if (classStringList.get(methodIndex).equals("for") || classStringList.get(methodIndex).startsWith("for("))
                        {
                            forLoopCount++;
                        }

                        // Whileloopcount
                        if (classStringList.get(methodIndex).equals("while") || classStringList.get(methodIndex).startsWith("while("))
                        {
                            whileLoopCount++;
                        }

                        // Complexity
                        complexityScore = -1;
                        // TODO
                        // TODO
                        // TODO


                        // Balance the parenthesis
                        if (classStringList.get(methodIndex).contains("}"))
                        {
                            parenthesisBalanceHolder--;
                        }
                        if(classStringList.get(methodIndex).contains("{"))
                        {
                            parenthesisBalanceHolder++;
                        }

                        methodIndex++;

                    } while(parenthesisBalanceHolder != 0);


                    System.out.println("\t\tFor loop count:   " + forLoopCount);
                    System.out.println("\t\tWhile loop count: " + whileLoopCount);
                    System.out.println("\t\tComplexity Score: " + complexityScore);


                    currentClassEntry.getData().put(name, new StaticMethodDataEntry(name, forLoopCount, whileLoopCount, complexityScore));
                }
            }
        }
    }

    private static boolean filterMethodName(String name)
    {
        List<String> methodFilterList = new ArrayList<>();

        methodFilterList.add("switch");
        methodFilterList.add("for");
        methodFilterList.add("while");
        methodFilterList.add("if");

        return methodFilterList.contains(name);
    }

    private static List<List<String>> parseClassStrings(String stringFile)
    {
        List<List<String>> allClassList = new ArrayList<>();

        String[] splitFile = stringFile.split("\\s");

        List<Integer> classOccurrenceMarkers = new ArrayList<>();

        int parensBalanceHolder = 0;


        // TODO process class -> if the word "class" seen inside the other classes processing mark it and go over the class just processed
        // TODO if class after the main class process this separately
        // TODO testing: one class in file, one main class one inner class, 2 separate classes in a file, class with inner class and an aditional class after
        // TODO Does not move past the balanced parenthesis marking if there was a class detected inside, after no more inner detected move on
        // TODO pseudocode lay this out bellow before start
        // TODO test for multiple inner classes within inner classes

        // TODO if theres an inner class take it out of the main string list

        // mark all instances of the word class and have an array
        // then have a function that creates a class based on a starting point and goes till the parens are balaced

        int ctr = 0;
        for (String token : splitFile)
        {
            if (token.equals("class"))
            {
                classOccurrenceMarkers.add(ctr);
            }

            ctr++;
        }

        for (int i : classOccurrenceMarkers)
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

        // Get rid of inner classes within larger classes
        allClassList = removeInnerClasses(allClassList);

        return allClassList;
    }

    private static List<List<String>> removeInnerClasses(List<List<String>> allClassList)
    {
        // TODO remove the public private protected if it exists here, ie. has the same name of class
        // TODO skip its own inner class index so it doesnt delete itself

        List<List<String>> returnClassLists = new ArrayList<>();


        for (List<String> classListComparedAgainst : allClassList)
        {

            for (List<String> classListTest : allClassList)
            {
                // If the two String lists are not equalCheck then see if on contains the other
                if (!equalCheck(classListComparedAgainst, classListTest))
                {
                    // If classListComparedAgainst contains classListTest then remove it from classListCompareAgainst
                    returnClassLists.add(containsRemoves(classListComparedAgainst, classListTest));

                    break;
                }
            }
        }

        if (returnClassLists.size() != 0)
        {
            return returnClassLists;
        }

        return allClassList;
    }

    private static boolean equalCheck(List<String> classListComparedAgainst, List<String> classListTest)
    {
        if (classListComparedAgainst.size() != classListTest.size())
        {
            return false;
        }


        for (int i=0; i < classListComparedAgainst.size(); i++)
        {
            if (!classListComparedAgainst.get(i).equals(classListTest.get(i)))
            {
                return false;
            }
        }

        return true;
    }

    private static List<String> containsRemoves(List<String> classListComparedAgainst, List<String> classListTest)
    {
        if (classListComparedAgainst.size() < classListTest.size())
        {
            return classListComparedAgainst;
        }

        for (int i=0; i < classListComparedAgainst.size(); i++)
        {

            if ((classListComparedAgainst.size() - i) < classListTest.size())
            {
                break;
            }

            if (classListComparedAgainst.get(i).equals(classListTest.get(0)))
            {
                boolean matchResult = true;

                // Check to see if from this point on it contains
                for (int j=i; (j - i) < classListTest.size(); j++)
                {
                    if (!classListComparedAgainst.get(j).equals(classListTest.get(j - i)))
                    {
                        matchResult = false;
                    }
                }

                //System.out.println(matchResult);

                // if complete match start back at i and only add into the return list the items not included in the match range
                if (matchResult)
                {
                    List<String> tmpClassList = new ArrayList<>();

                    for (int j=0; j < classListComparedAgainst.size(); j++)
                    {
                        if ((j < i) || j > (classListTest.size() + i))
                        {
                            tmpClassList.add(classListComparedAgainst.get(j));
                        }
                    }

                    return tmpClassList;
                }
            }
        }

        return classListComparedAgainst;
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
