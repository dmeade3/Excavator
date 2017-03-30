package dynamic_analysis;

import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import static Util.SystemConfig.*;
import static Util.SystemConfig.DIALOG_WIDTH;

/**
 * Project: Excavator
 * File Name: Test.java
 * <p>
 * Created by David on 3/29/2017.
 */
public class Test
{
	public static void main(String... args)
	{

		try
		{
			// Execute command

			Process process = Runtime.getRuntime().exec("java \"-javaagent:C:\\Users\\David\\Desktop\\Intelij Workspace\\Excavator\\out\\artifacts\\Profiler_jar\\Profiler.jar\" -jar \"C:\\Users\\David\\Desktop\\Intelij Workspace\\Hello-World\\out\\artifacts\\Hello_World_jar\\Hello World.jar\"");



			process.waitFor();

			/**
			 *
			 * output is too fast to process
			 *
			 */









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
