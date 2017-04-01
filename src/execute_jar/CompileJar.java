package execute_jar;

/**
 * Project: Excavator
 * File Name: compile_run.CompileJar.java
 * <p>
 * Created by David on 3/31/2017.
 */
public class CompileJar
{
	private int exitVal;
	private String[] command;
	private String jarToCompilePath;

	public CompileJar(String jarToCompilePath)
	{
		this.jarToCompilePath = jarToCompilePath;
		String osName = System.getProperty("os.name");
		command = new String[4];

		switch (osName)
		{
			/*case "Windows 95":
				command[0] = "command.com";
				command[1] = "/C";
				command[3] = jarToCompilePath;
				break;*/

			case "Windows 10":
				command[0] = "cmd.exe";
				command[1] = "/C";
				command[2] = "jar";
				command[3] = jarToCompilePath;
				break;

			default:
				System.err.println("Operation system: \"" + osName + "\" not handled in this program");
				System.exit(1);
		}
	}

	public void runCompile()
	{

	}


	public static void main(String... args)
	{

	}
}
