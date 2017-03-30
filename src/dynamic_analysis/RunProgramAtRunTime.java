package dynamic_analysis;

import data_storage.DynamicClassDataEntry;
import data_storage.DynamicData;
import data_storage.DynamicMethodDataEntry;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static Util.SystemConfig.*;

public class RunProgramAtRunTime
{
    private RunProgramAtRunTime()
    {
        throw new IllegalAccessError("Utility Class");
    }

    public static void runOutsideProgram()
    {
        try
        {
	        //System.out.println("java \"" + AGENT_COMMAND + "\" -jar \"" + OUTSIDE_PROGRAM_JAR_PATH + "\"");
	        //System.out.println("java -jar \"C:\\Users\\David\\Desktop\\Intelij Workspace\\Hello-World\\out\\artifacts\\Hello_World_jar\\Hello World.jar\"");


            Process process= Runtime.getRuntime().exec("java \"" + AGENT_COMMAND + "\" -jar \"" + OUTSIDE_PROGRAM_JAR_PATH + "\"");


	        process.waitFor();





            /*while (true)
            {
                System.out.println(process.waitFor());

                if ((process.waitFor() == 1) || (process.waitFor() == 0))
                {
                    break;
                }
            }*/









            InputStream inputStream = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream);

            InputStream errorStream = process.getErrorStream();
            InputStreamReader esr = new InputStreamReader(errorStream);

            ///// Get the output of the program /////
            int n1;
            char[] c1 = new char[1024];
            StringBuffer standardOutput = new StringBuffer();
            while ((n1 = isr.read(c1)) > 0)
            {
                standardOutput.append(c1, 0, n1);
            }

            if (SHOW_OUTSIDE_PROGRAM_OUTPUT)
            {
                System.out.println(standardOutput.toString());
            }

            // Analyze the output of the program
            outputAnalysis(standardOutput.toString());

            int n2;
            char[] c2 = new char[1024];
            StringBuffer standardError = new StringBuffer();
            while ((n2 = esr.read(c2)) > 0)
            {
                standardError.append(c2, 0, n2);
            }

            // If there is no error dont show anything
            if (!standardError.toString().equals(""))
            {
                // Filter out the non-useful errors
                List<String> errorList = new ArrayList<>();
                List<String> restrictedErrorList = new ArrayList<>();
                restrictedErrorList.add("no method body");

                for (String error : standardError.toString().split("\n"))
                {
                    //Restrict the errors that come though
                    if (!restrictedErrorList.contains(error.trim()))
                    {
                        errorList.add(error);
                    }
                }

                if (errorList.isEmpty())
                {
                    System.out.println("Standard Error:");

                    for (String error : standardError.toString().split("\n"))
                    {
                        System.out.println("\t" + error);
                    }

                    final Stage errorPopupStage = new Stage();
                    errorPopupStage.initModality(Modality.APPLICATION_MODAL);
                    errorPopupStage.setTitle("External Program Error");
                    ScrollPane scrollPane = new ScrollPane();
                    TextArea textArea = new TextArea(standardError.toString());
                    scrollPane.setContent(textArea);
                    Scene dialogScene = new Scene(scrollPane, DIALOG_HEIGHT, DIALOG_WIDTH);
                    errorPopupStage.setScene(dialogScene);
                    errorPopupStage.show();
                }
            }
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }


    }

    private static void outputAnalysis(String programOutput)
    {
        String [] splitProgramOutput = programOutput.split("\n");

        // Get all available classes methods
        // Needs to stay in from so constructors can be added to the right class
        for (String line : splitProgramOutput)
        {
            String enteringExitingOrOther;
            String className;

            enteringExitingOrOther = getEnterExitStatus(line);

            if(enteringExitingOrOther.equals(ENTERING) || enteringExitingOrOther.equals(EXITING))
            {
                // Get class and method name
                className = getClassName(line);

                // Add class
                if (!DynamicData.getInstance().contains(className))
                {
                    DynamicClassDataEntry classDataEntry = new DynamicClassDataEntry(className);

                    DynamicData.getInstance().put(classDataEntry.getClassName(), classDataEntry);
                }
            }
        }

        for (String line : splitProgramOutput)
        {
            String enteringExitingOrOther;
            String className;
            String methodName;

            enteringExitingOrOther = getEnterExitStatus(line);

            if(enteringExitingOrOther.equals(ENTERING) || enteringExitingOrOther.equals(EXITING))
            {
                // Get class and method name
                className = getClassName(line);
                methodName = getMethodName(line);

                String tmpMethodName = methodName.replaceAll("\\(.*", "");

                for (String key : DynamicData.getInstance().getData().keySet())
                {
                    String classEnding;

                    try
                    {
                        classEnding = key.substring(key.lastIndexOf(".") + 1, key.lastIndexOf(".") + tmpMethodName.length());
                    }
                    catch (Exception e)
                    {
                        continue;
                    }

                    if (classEnding.trim().contains(tmpMethodName.trim()))
                    {
                        className = key.trim();
                    }
                }

                if (!DynamicData.getInstance().getData().get(className).contains(methodName))
                {
                    DynamicMethodDataEntry methodDataEntry = new DynamicMethodDataEntry(methodName);
                    DynamicData.getInstance().get(className).getData().put(methodName, methodDataEntry);
                }
                // Increment Call Count
                if(enteringExitingOrOther.equals(ENTERING))
                {
                    DynamicData.getInstance().get(className).get(methodName).incrementCallCount();
                }
            }
        }

        List<String> classesToDelete = new ArrayList<>();

        // Find classes to delete
        for (DynamicClassDataEntry value : DynamicData.getInstance().getData().values())
        {
            int totalCallCount = 0;

            for (DynamicMethodDataEntry methodEntry : value.getData().values())
            {
                totalCallCount += methodEntry.getCallCount();
            }

            if (totalCallCount == 0)
            {
                classesToDelete.add(value.getClassName());
            }
        }

        // Delete the classes
        for (String classToDelete: classesToDelete)
        {
            DynamicData.getInstance().getData().remove(classToDelete);
        }

        applyTimeEntries(splitProgramOutput);
    }

    private static void applyTimeEntries(String[] splitProgramOutput)
    {
        Stack<MethodNameTime> methodTimeStack = new Stack<>();

        for (String line : splitProgramOutput)
        {
            String enteringExitingOrOther = getEnterExitStatus(line);

            // If entering push to stack
            if(enteringExitingOrOther.equals(ENTERING))
            {
                MethodNameTime methodNameTime = new MethodNameTime(getMethodName(line), Long.valueOf(getTimeStamp(line)));

                methodTimeStack.push(methodNameTime);
            }
            // if exiting then pop off the stack and set the time
            else if (enteringExitingOrOther.equals(EXITING))
            {
                // Get class and method name
                String className = getClassName(line);
                String methodName = getMethodName(line);

                final String tmpMethodName = methodName.replaceAll("\\(.*", "");

                // Peek looks at the top of the stack
                if (methodTimeStack.peek().getName().equals(methodName))
                {
                    long startTime = methodTimeStack.pop().getTimeStamp();

                    long endTime = Long.parseLong(getTimeStamp(line));

                    for (String key : DynamicData.getInstance().getData().keySet())
                    {
                        String classEnding;

                        try
                        {
                            classEnding = key.substring(key.lastIndexOf(".") + 1, key.lastIndexOf(".") + tmpMethodName.length());
                        }
                        catch (Exception e)
                        {
                            continue;
                        }

                        if (classEnding.trim().contains(tmpMethodName.trim()))
                        {
                            className = key.trim();
                        }
                    }

                    //System.out.println("Classname: " + className);
                    //System.out.println("Methodname: " + methodName);
                    DynamicData.getInstance().get(className).get(methodName).addTimeSpentEntry(endTime - startTime);
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

            if (line.equals(ENTERING))
            {
                return ENTERING;
            }
            else if (line.equals(EXITING))
            {
                return EXITING;
            }
            else
                {
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
        line = line.substring(0, line.length() - 1);

        return line;
    }

    private static class MethodNameTime
    {
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

    public static void main(String[] args)
    {
        System.out.println("Executing RunProgramAtRunTime.main");

        runOutsideProgram();
    }
}
