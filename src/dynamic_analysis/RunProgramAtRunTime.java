package dynamic_analysis;

import data_storage.DynamicClassDataEntry;
import data_storage.DynamicData;
import data_storage.DynamicMethodDataEntry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Stack;

import static data_storage.SystemConfig.*;

public class RunProgramAtRunTime
{
    public static void RunOutsideProgram()
    {
        try
        {
            Runtime runTime = Runtime.getRuntime();
            Process process = runTime.exec("java \"" + AGENTCOMMAND + "\" -jar \"" + OUTSIDEPROGRAMJARPATH + "\"");
            //Process process = runTime.exec("java -jar \"" + OUTSIDEPROGRAMJARPATH + "\"");

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
				//System.out.println(standardOutput.toString());
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
            //System.out.println(line);

            String enteringExitingOrOther;
            String className;
            String methodName;

            enteringExitingOrOther = getEnterExitStatus(line);

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

        applyTimeEntries(splitProgramOutput);

        //System.out.println();
        //displayClassData();
    }

    private static void applyTimeEntries(String[] splitProgramOutput)
    {
        Stack<MethodNameTime> methodTimeStack = new Stack();

        for (String line : splitProgramOutput)
        {
            String enteringExitingOrOther = getEnterExitStatus(line);

            // If entering push to stack
            if(enteringExitingOrOther.equals("Entering"))
            {
                MethodNameTime methodNameTime = new MethodNameTime(getMethodName(line), Long.valueOf(getTimeStamp(line)));

                methodTimeStack.push(methodNameTime);
            }
            // if exiting then pop off the stack and set the time
            else if (enteringExitingOrOther.equals("Exiting"))
            {
                String methodName = getMethodName(line);

                // Peek looks at the top of the stack
                if (methodTimeStack.peek().getName().equals(methodName))
                {
                    long startTime = methodTimeStack.pop().getTimeStamp();

                    long endTime = Long.valueOf(getTimeStamp(line));

                    DynamicData.getInstance().get(getClassName(line)).get(methodName).addTimeSpentEntry(endTime - startTime);
                }
            }
        }
    }

    private static String getTimeStamp(String line)
    {
        String[] splitLine = line.split(" ");

        return splitLine[0];
    }

    public static void displayClassData()
    {
        for (DynamicClassDataEntry classEntry : DynamicData.getInstance().getData().values())
        {
            System.out.println(classEntry.getClassName());

            for (DynamicMethodDataEntry methodEntry : classEntry.getData().values())
            {
                System.out.println("\t" + methodEntry.getMethodName());
                System.out.println("\t\tCall Count: " + methodEntry.getCallCount());
                System.out.println("\t\tTotal Time: " + methodEntry.getTotalTime());
                System.out.println("\t\tAvg Time:   " + methodEntry.getAverageTime());

                for (Long entry : methodEntry.getTimesSpentInMethod())
                {
                    System.out.println("\t\tTime Entry: " + entry);
                }

                System.out.println();
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

    private static String getEnterExitStatus(String line) {

        if (line.contains(" "))
        {
            line = line.split(" ")[1];

            if (line.equals("Entering")) {
                return "Entering";
            } else if (line.equals("Exiting")) {
                return "Exiting";
            } else {
                return "Other";
            }
        }

        return "Other";
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

    private static class MethodNameTime {

        private String name;
        private long timeStamp;

        public MethodNameTime(String name, long timeStamp)
        {
            this.name = name;
            this.timeStamp = timeStamp;
        }

        public String getName()
        {
            return name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public long getTimeStamp()
        {
            return timeStamp;
        }

        public void setTimeStamp(long timeStamp)
        {
            this.timeStamp = timeStamp;
        }
    }
}
