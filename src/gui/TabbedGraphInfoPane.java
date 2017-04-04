package gui;

import graphing.GraphUtil;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.Glow;
import javafx.util.Duration;
import util.TimeUnit;

import java.lang.reflect.Field;
import java.util.Map;

import static graphing.GraphUtil.TOOLTIP_DELAY;

/**
 * Is an object that holds all graphed out tabs
 */
public class TabbedGraphInfoPane extends TabPane
{
    public TabbedGraphInfoPane(Map<String, Double> callData, Map<String, Double> timeData, TimeUnit currentTimeSelection)
    {
        createPieGraph("Call Count For Each Method", getTabs(), callData, "");

        createPieGraph("Time Spent In Each Method", getTabs(), timeData, currentTimeSelection.abbreviation);
    }

    private static void createPieGraph(String title, ObservableList<Tab> tabs, Map<String, Double> data, String unit)
    {
        Tab tab = new Tab();
        tab.setText(title);

        PieChart chart = new PieChart(GraphUtil.createPieDataset(data));

        chart.setTitle(title);
        chart.setLabelLineLength(10);

        Tooltip tooltip = new Tooltip("");

        setToolTipStartTime(tooltip, TOOLTIP_DELAY);

        for (final PieChart.Data chartData : chart.getData())
        {
            Tooltip.install(chartData.getNode(),tooltip);
            applyMouseEvents(chartData, tooltip, unit);
        }

        tab.setContent(chart);
        tabs.add(tab);
    }

    private static void applyMouseEvents(final PieChart.Data data, Tooltip tooltip, String unit)
    {
        final Node node = data.getNode();

        node.setOnMouseEntered(arg0 ->
        {
            node.setEffect(new Glow());
            String styleString = "-fx-border-color: white; -fx-border-width: 3; -fx-border-style: dashed;";
            node.setStyle(styleString);
            tooltip.setText(String.valueOf(data.getName() + "\n" + (long)data.getPieValue()) + unit);
        });

        node.setOnMouseExited(arg0 ->
        {
            node.setEffect(null);
            node.setStyle("");
        });
    }

    private static void setToolTipStartTime(Tooltip tooltip, long millis)
    {
        try
        {
            Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
            fieldBehavior.setAccessible(true);
            Object objBehavior = fieldBehavior.get(tooltip);

            Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
            fieldTimer.setAccessible(true);
            Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

            objTimer.getKeyFrames().clear();
            objTimer.getKeyFrames().add(new KeyFrame(new Duration(millis)));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}