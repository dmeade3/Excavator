package dynamic_analysis;

import data_storage.DynamicClassDataEntry;
import data_storage.DynamicData;
import data_storage.DynamicMethodDataEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static data_storage.SystemConfig.*;

public class RunProgramAtRunTime
{
    public static void RunOutsideProgram()
    {
        try
        {
            Runtime runTime = Runtime.getRuntime();
            Process process = runTime.exec("java " + AGENTCOMMAND + " -jar " + OUTSIDEPROGRAMPATH);

            if (SHOWOUTSIDEPROGRAMOUTPUT)
            {
                InputStream inputStream = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(inputStream);
                InputStream errorStream = process.getErrorStream();
                InputStreamReader esr = new InputStreamReader(errorStream);

                int n1;
                char[] c1 = new char[1024];
                StringBuffer standardOutput = new StringBuffer();
                while ((n1 = isr.read(c1)) > 0)
                {
                    standardOutput.append(c1, 0, n1);
                }

                outputAnalysis(standardOutput.toString());

                int n2;
                char[] c2 = new char[1024];
                StringBuffer standardError = new StringBuffer();
                while ((n2 = esr.read(c2)) > 0)
                {
                    standardError.append(c2, 0, n2);
                }
                if (!standardError.toString().equals(""))
                {
                    System.out.println("Standard Error: \n" + standardError.toString());
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void outputAnalysis(String programOutput)
    {
        String [] splitProgramOutput = programOutput.split("\n");

        for (String line : splitProgramOutput)
        {
            System.out.println(line);

            String enteringExitingOrOther;
            String className;
            String methodName;

            enteringExitingOrOther= getEnterExitStatus(line);

            if(enteringExitingOrOther.equals("Entering") || enteringExitingOrOther.equals("Exiting"))
            {
                className = getClassName(line);
                methodName = getMethodName(line);

                //System.out.println("Classname: " + className);
                //System.out.println("Method Name: " + methodName);

                // Add class
                if (!DynamicData.getInstance().contains(className))
                {
                    DynamicClassDataEntry classDataEntry = new DynamicClassDataEntry(className);

                    DynamicData.getInstance().put(classDataEntry.getClassName(), classDataEntry);
                }

                // Add method
                if (!DynamicData.getInstance().get(className).contains(methodName))
                {
                    DynamicMethodDataEntry methodDataEntry = new DynamicMethodDataEntry(methodName);

                    DynamicData.getInstance().get(className).put(methodDataEntry.getMethodName(), methodDataEntry);
                }

                // Increment Call Count
                if(enteringExitingOrOther.equals("Entering"))
                {
                    DynamicData.getInstance().get(className).get(methodName).incrementCallCount();
                }
            }
        }

        displayClassData();
    }

    public static void displayClassData()
    {
        for (DynamicClassDataEntry classEntry : DynamicData.getInstance().getData().values())
        {
            System.out.println(classEntry.getClassName());

            for (DynamicMethodDataEntry methodEntry : classEntry.getData().values())
            {
                System.out.println("\t" + methodEntry.getMethodName());
                System.out.println("\t\t Call Count: " + methodEntry.getCallCount());
            }
        }
    }

    private static String getMethodName(String line)
    {
        String[] splitLine = line.split(" ");

        // Get rid of the entering exiting part
        for (int i=1; i < splitLine.length; i++)
        {
            if (splitLine[i] != "")
            {
                line = splitLine[i];
            }
        }

        // Find location of the last '.' before '('
        splitLine = line.split("\\(");

        // To be added back later
        String restOfMethodStatement = splitLine[1];
        line = splitLine[0];

        splitLine = line.split("\\.");

        line = splitLine[splitLine.length-1];

        line = line.concat("(" + restOfMethodStatement);

        return line;
    }

    private static String getEnterExitStatus(String line)
    {
        if (line.startsWith("Entering "))
        {
            return "Entering";
        }
        else if (line.startsWith("Exiting "))
        {
            return "Exiting";
        }
        else
        {
            return "Other";
        }
    }

    private static String getClassName(String line)
    {
        String[] splitLine = line.split(" ");

        // Get rid of the entering exiting part
        for (int i=1; i < splitLine.length; i++)
        {
            if (splitLine[i] != "")
            {
                line = splitLine[i];
            }
        }

        // Get rid of method
        splitLine = line.split("\\(");

        String methodString = splitLine[1];

        line = line.replace(methodString, "");

        splitLine = line.split("\\.");

        methodString = splitLine[splitLine.length - 1];

        line = line.replace(methodString, "");
        line = line.substring(0, line.length()-1);

        return line;
    }

    public static void main(String[] args)
    {
        RunOutsideProgram();
    }
}
