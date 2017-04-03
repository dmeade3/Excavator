package gui;

import graphing.GraphUtil;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.util.Map;


public class TabbedGraphInfo extends TabPane
{

    public TabbedGraphInfo(Map<String, Double> callData, Map<String, Double> timeData)
    {
        // Call count data tab
        Tab callCountTab = new Tab();
        callCountTab.setText("Call Count Data");

        ObservableList<PieChart.Data> pieChartDataCallCount = GraphUtil.createPieDataset(callData);

        System.out.println("size: " + pieChartDataCallCount.size());
        for (PieChart.Data entry : pieChartDataCallCount)
        {
            System.out.println(entry);
        }

        PieChart callCountChart = new PieChart(pieChartDataCallCount);

        System.out.println("Final: " + callCountChart.getData().size());


        callCountChart.setTitle("Call Count Data");

        //callCountChart.setData(GraphUtil.createPieDataset(callData));

        callCountTab.setContent(callCountChart);
        getTabs().add(callCountTab);


        // Time Spent in each method tab
        Tab timeDataTab = new Tab();
        timeDataTab.setText("Time Spent In Each Method");

        ObservableList<PieChart.Data> pieChartDataTimeSpent = GraphUtil.createPieDataset(timeData);

        PieChart timeSpentChart = new PieChart(pieChartDataTimeSpent);
        timeSpentChart.setTitle("Time Spent In Each Method");
        timeDataTab.setContent(timeSpentChart);
        getTabs().add(timeDataTab);
    }
}