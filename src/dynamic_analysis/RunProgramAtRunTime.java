package dynamic_analysis;

import java.io.IOException;

import static data_storage.SystemConfig.AGENTCOMMAND;
import static data_storage.SystemConfig.OUTSIDEPROGRAMPATH;

public class RunProgramAtRunTime
{
    public static void RunOutsideProgram()
    {
        try
        {
            Runtime runTime = Runtime.getRuntime();

            Process process = runTime.exec("java " + AGENTCOMMAND + " -jar " + OUTSIDEPROGRAMPATH);

            // TODO only here to show output dont delete use for debuging or as a feature in the future
            // TODO maybe have switch for this on/off in the config file
            /*InputStream inputStream = process.getInputStream();
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
            System.out.println("Standard Output: \n" + standardOutput.toString());

            int n2;
            char[] c2 = new char[1024];
            StringBuffer standardError = new StringBuffer();
            while ((n2 = esr.read(c2)) > 0)
            {
                standardError.append(c2, 0, n2);
            }

            System.out.println("Standard Error: \n" + standardError.toString());*/

        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        RunOutsideProgram();
    }
}
