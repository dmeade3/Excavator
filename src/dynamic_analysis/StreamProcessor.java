package dynamic_analysis;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

class StreamProcessor extends Thread
{
    private InputStream inputStream;
    private String type;
    private boolean printOutput;

    List<String> outputList = new ArrayList<>();

    StreamProcessor(InputStream inputStream, String type, boolean printOutput)
    {
        this.inputStream = inputStream;
        this.type = type;
        this.printOutput = printOutput;
    }

    public void run()
    {
        try
        {
            InputStreamReader isr = new InputStreamReader(inputStream);
            BufferedReader br = new BufferedReader(isr);

            String line = null;
            while ( (line = br.readLine()) != null)
            {
                if (printOutput) System.out.println(type + " " + line);

                outputList.add(line);
            }
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
        }
    }

    public List<String> getOutputList()
    {
        return outputList;
    }
}