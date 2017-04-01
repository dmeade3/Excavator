package execute_jar;

import dynamic_analysis.ProcessJarOutput;
import dynamic_analysis.StreamProcessor;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import static util.SystemConfig.*;

/**
 * Project: Excavator
 * File Name: ExecuteJar.java
 * <p>
 * Created by David on 3/31/2017.
 */
public class ExecuteJar
{
	private int exitVal;
	private String[] command;

	public ExecuteJar(String jarCommand)
	{
		String osName = System.getProperty("os.name");

		command = new String[3];

		// TODO Eventually find more os's (linux)
		switch (osName)
		{
			case "Windows 95":
				command[0] = "command.com";
				command[1] = "/C";
				command[2] = jarCommand;
				break;

			case "Windows 10":
				command[0] = "cmd.exe";
				command[1] = "/C";
				command[2] = jarCommand;
				break;

			default:
				System.err.println("Operation system: \"" + osName + "\" not handled in this program");
				System.exit(1);
		}
	}

	public int runJar()
	{
		try
		{
			System.out.println("Executing " + command[0] + " " + command[1] + " " + command[2]);
			Process proc = Runtime.getRuntime().exec(command);

			StreamProcessor errorProcessor  = new StreamProcessor(proc.getErrorStream(), "", ExecuteJarUtil.SHOW_OUTSIDE_PROGRAM_OUTPUT);
			StreamProcessor outputProcessor = new StreamProcessor(proc.getInputStream(), "", ExecuteJarUtil.SHOW_OUTSIDE_PROGRAM_OUTPUT);

			// Start Stream Processor Threads
			errorProcessor.start();
			outputProcessor.start();

			// any error???
			exitVal = proc.waitFor();
			System.out.println("Outside Program ExitValue: " + exitVal);

			ProcessJarOutput.processOutput(outputProcessor.getOutputList());

			// Print out the error
			if (!errorProcessor.getOutputList().isEmpty())
			{
				StringBuilder stringBuilderError = new StringBuilder();

				for (String error : errorProcessor.getOutputList())
				{
					stringBuilderError.append(error + "\n");
				}

				final Stage errorPopupStage = new Stage();
				errorPopupStage.initModality(Modality.APPLICATION_MODAL);
				errorPopupStage.setTitle("External Program Error");
				ScrollPane scrollPane = new ScrollPane();
				TextArea textArea = new TextArea(stringBuilderError.toString());
				scrollPane.setContent(textArea);
				Scene dialogScene = new Scene(scrollPane, DIALOG_WIDTH, DIALOG_HEIGHT);
				errorPopupStage.setScene(dialogScene);
				errorPopupStage.show();
			}
		}
		catch (Throwable t)
		{
			t.printStackTrace();
		}

		return exitVal;
	}
}
