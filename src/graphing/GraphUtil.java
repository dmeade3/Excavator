package graphing;


import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import java.util.Map;

/**
 * Created by dcmeade on 3/23/2017.
 */
public class GraphUtil
{
    // Constants ///////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final boolean SHOW_GRAPH_LENGEND = true;
    public static final boolean SHOW_GRAPH_TOOLTIP = true;
    public static final boolean SHOW_GRAPH_URL = false;

    public static final int LINECHART_WINDOW_HEIGHT = 500;

    public static final int LINECHART_WINDOW_WIDTH = 500;


    // Util methods ////////////////////////////////////////////////////////////////////////////////////////////////////


	public static PieDataset createPieDataset(Map<String, Number> dataMap)
	{
		DefaultPieDataset dataset = new DefaultPieDataset();

		for (Map.Entry<String, Number> entry : dataMap.entrySet())
		{
			dataset.setValue(entry.getKey(), entry.getValue());
		}

		return dataset;
	}
}
