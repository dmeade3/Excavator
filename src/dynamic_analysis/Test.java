package dynamic_analysis;

/**
 * Project: Excavator
 * File Name: Test.java
 * <p>
 * Created by David on 3/29/2017.
 */
/*public class Test
{
	public static void main(String... args)
	{

		try
		{
			// Execute command
			Process process = Runtime.getRuntime().exec("java \"" + AGENT_COMMAND + "\" -jar \"" + OUTSIDE_PROGRAM_JAR_PATH + "\"");


			System.out.println("Waiting");
			process.waitFor();
			System.out.println("Done");

			/**
			 *
			 * output inputStream too fast to process
			 *
			 */








/*
			InputStream inputStream = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(inputStream);

			InputStream errorStream = process.getErrorStream();
			InputStreamReader esr = new InputStreamReader(errorStream);

			int n1;
			char[] c1 = new char[1024];
			StringBuffer standardOutput = new StringBuffer();
			while ((n1 = isr.read(c1)) > 0)
			{
				System.out.println(c1);

				standardOutput.append(c1, 0, n1);
			}

			//System.out.println(standardOutput.toString());

			// Analyze the output of the program
			//outputAnalysis(standardOutput.toString());

			int n2;
			char[] c2 = new char[1024];
			StringBuffer standardError = new StringBuffer();
			while ((n2 = esr.read(c2)) > 0)
			{
				System.out.println(c2);

				standardError.append(c2, 0, n2);
			}
		}
		catch (IOException e)
		{
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
	}
}
*/

























import static Util.SystemConfig.AGENT_COMMAND;
import static Util.SystemConfig.OUTSIDE_PROGRAM_JAR_PATH;


public class Test
{
	public static void main(String... args)
	{
		args = new String[3];

		args[0] = "java \"" + AGENT_COMMAND + "\" -jar \"" + OUTSIDE_PROGRAM_JAR_PATH + "\"";

		try {
			String osName = System.getProperty("os.name");

			System.out.println(osName);

			String[] cmd = new String[3];

			switch (osName)
			{
				case "Windows 95":
					cmd[0] = "command.com";
					cmd[1] = "/C";
					cmd[2] = args[0];
					break;

				case "Windows 10":

					cmd[0] = "cmd.exe";
					cmd[1] = "/C";
					cmd[2] = args[0];
					break;

				default:
					System.err.println("Operation system: \"" + osName + "\" not handled in this program");
					System.exit(1);
			}

			Runtime rt = Runtime.getRuntime();
			System.out.println("Executing " + cmd[0] + " " + cmd[1] + " " + cmd[2]);
			Process proc = rt.exec(cmd);

			// any error message?
			StreamProcessor errorGobbler = new StreamProcessor(proc.getErrorStream(), "ERROR", true);

			// any output?
			StreamProcessor outputGobbler = new StreamProcessor(proc.getInputStream(), "OUTPUT", true);

			// kick them off
			errorGobbler.start();
			outputGobbler.start();

			// any error???
			int exitVal = proc.waitFor();
			System.out.println("ExitValue: " + exitVal);

		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}
	}
}