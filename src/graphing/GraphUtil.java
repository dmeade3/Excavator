package graphing;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dcmeade on 3/23/2017.
 */
public class GraphUtil
{
    // Constants ///////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int CHART_WINDOW_HEIGHT = 700;

    public static final int CHART_WINDOW_WIDTH = 700;

    public static final int TOOLTIP_DELAY = 50;


    // Util methods ////////////////////////////////////////////////////////////////////////////////////////////////////


	public static ObservableList<PieChart.Data> createPieDataset(Map<String, Double> dataMap)
	{
		List<PieChart.Data> dataList = new ArrayList<>();

		for (Map.Entry<String, Double> entry : dataMap.entrySet())
		{
			dataList.add(new PieChart.Data(entry.getKey(), entry.getValue()));
		}

		return FXCollections.observableArrayList(dataList);
	}
}
