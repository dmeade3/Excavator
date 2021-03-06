package gui;


import data_storage.DynamicClassDataEntry;
import data_storage.DynamicData;
import data_storage.DynamicMethodDataEntry;
import dynamic_analysis.ProcessJarOutput;
import execute_jar.ExecuteJar;
import execute_jar.ExecuteJarUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import util.SystemConfig;
import util.TimeUnit;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

import static graphing.GraphUtil.CHART_WINDOW_HEIGHT;
import static graphing.GraphUtil.CHART_WINDOW_WIDTH;
import static util.SystemConfig.*;
import static util.TimeUnit.NANOSECOND;

public class GUIMain extends Application
{
    // Fields //////////////////////////////////////////////////////////////////////////////////////////////////////////
	private ApplicationStat overallStat = new ApplicationStat("", 0, "0", "0");
	private final TreeItem<ApplicationStat> rootTreeItem = new TreeItem<>(overallStat);
	private final TreeTableView<ApplicationStat> treeTableView = new TreeTableView<>(rootTreeItem);
    private final ScrollPane rightScrollPane = new ScrollPane();
	private final TextArea textArea = TextAreaBuilder.create().prefWidth(SCENE_HEIGHT - SPACE_FROM_BOTTOM_BUTTONS).prefHeight(SCENE_HEIGHT - SPACE_FROM_BOTTOM_BUTTONS - 2).wrapText(true).build();
    private final GridPane bottomButtons = new GridPane();
    private final ComboBox<TimeUnit> timeSelectorComboBox = new ComboBox<>(SystemConfig.TIME_UNIT_OPTIONS);
	private final Color backgroundColor = new Color(.225, .228, .203, .5);
	private final Scene scene = new Scene(new Group(), SCENE_WIDTH, SCENE_HEIGHT);
	private final Group sceneRoot = (Group) scene.getRoot();
	private final BorderPane mainBorderPane = new BorderPane();
    private Thread updateDynamicAnalysisThread;
    private Label dynamicStateLabel;
    private Stage stage;
    private Button runDynamicAnalysisButton;
    private Button expandCollapseButton;
    private Map<String, Double> chartDataHolderCallCount;
    private Map<String, Double> chartDataHolderTimeSpent;

	// Methods /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void start(Stage stage)
    {
	    System.out.println("Starting Excavator");

        // TODO break this function up
        this.stage = stage;

	    // TODO make default read in by config here

	    ///// Defaults /////
        rootTreeItem.setExpanded(true);
	    timeSelectorComboBox.setValue(NANOSECOND);
	    treeTableView.setTableMenuButtonVisible(true);
	    textArea.setEditable(false);
	    resetRightScrollPane();
	    bottomButtons.setHgap(5);
	    bottomButtons.setVgap(5);
        

	    // Set up scene
        scene.getStylesheets().add(SystemConfig.NAME_OF_MAIN_STYLESHEET);
        scene.setFill(backgroundColor);

        // Tree table size adjusting
        treeTableView.setPrefWidth(SCENE_WIDTH - SCROLL_PANE_WIDTH);
        treeTableView.setPrefHeight(SCENE_HEIGHT - SPACE_FROM_BOTTOM_BUTTONS);

        // Right Right ScrollPane
        rightScrollPane.setPrefWidth(SCROLL_PANE_WIDTH);
        rightScrollPane.setPrefHeight(SCENE_HEIGHT - SPACE_FROM_BOTTOM_BUTTONS);
        rightScrollPane.setContent(textArea);
        rightScrollPane.setFitToWidth(true);

		// Fill in tree table
        updateTreeTable();
	    initBottomButtonGridPane(sceneRoot, mainBorderPane);
	    mainBorderPane.setCenter(treeTableView);
        mainBorderPane.setRight(rightScrollPane);

	    ///// Listeners for scene growth /////
	    scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) ->
	    {
		    treeTableView.setPrefWidth((Double)   ((newSceneWidth.doubleValue() - oldSceneWidth.doubleValue()) / 2)  + treeTableView.getPrefWidth());
            rightScrollPane.setPrefWidth((Double) ((newSceneWidth.doubleValue() - oldSceneWidth.doubleValue()) / 2)  + rightScrollPane.getPrefWidth());
	    });

	    scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) ->
	    {
		    // -2 is buffer space for the textArea fits nicely in the scrollPane
		    treeTableView.setPrefHeight((Double) newSceneHeight - SPACE_FROM_BOTTOM_BUTTONS);
		    textArea.setPrefHeight((Double) newSceneHeight - SPACE_FROM_BOTTOM_BUTTONS - 2);
		    rightScrollPane.setPrefHeight((Double) newSceneHeight - SPACE_FROM_BOTTOM_BUTTONS - 2);
	    });

	    ///// Setup / Show Stage /////
	    stage.setTitle("Excavator");
	    stage.setScene(scene);
	    stage.show();

        scene.getWindow().setOnCloseRequest(ev ->
        {
            System.out.println("Closing");

            Platform.exit();
        });
    }

    private void updateTreeTable()
    {
        ApplicationStat classApplicationStat;
        ApplicationStat methodApplicationStat;

        // Add the data to rootTreeItem
        for (DynamicClassDataEntry dynamicClassDataEntry : DynamicData.getInstance().getData().values())
        {
            // Make the application stat
            classApplicationStat = new ApplicationStat(dynamicClassDataEntry.getClassName(), 0, "0", "0");

	        // Create an application entry for the current class
            TreeItem<ApplicationStat> classEntry = new TreeItem<>(classApplicationStat);

            // TODO here make the class entries bold
            //classEntry.

            long totalMethodCalls = 0;
            BigDecimal totalAverageTime = new BigDecimal("0");
            BigDecimal totalTotalTime = new BigDecimal("0");

            // Add method info to the class object
            for (DynamicMethodDataEntry dynamicMethodDataEntry : dynamicClassDataEntry.getData().values())
            {
	            // Add to totals
                totalMethodCalls += dynamicMethodDataEntry.getCallCount();
                totalAverageTime = totalAverageTime.add(BigDecimal.valueOf(dynamicMethodDataEntry.getAverageTime()));
                totalTotalTime   = totalTotalTime.add(BigDecimal.valueOf(dynamicMethodDataEntry.getTotalTime()));

                methodApplicationStat = new ApplicationStat(
                        dynamicMethodDataEntry.getMethodName(),
                        dynamicMethodDataEntry.getCallCount(),
		                getCurrentTimeSelection().convertTimeLongToString(dynamicMethodDataEntry.getAverageTime()),
		                getCurrentTimeSelection().convertTimeLongToString(dynamicMethodDataEntry.getTotalTime())
                );

                TreeItem<ApplicationStat> methodEntry = new TreeItem<>(methodApplicationStat);

                classEntry.getChildren().add(methodEntry);
            }

	        // Add to class totals
            classApplicationStat.setCallCount(totalMethodCalls);
            classApplicationStat.setAverageMethodTime(getCurrentTimeSelection().convertTimeBigDecimal(totalAverageTime).toString());
            classApplicationStat.setTotalMethodTime(getCurrentTimeSelection().convertTimeBigDecimal(totalTotalTime).toString());

	        // Add to rootTreeItem totals
            rootTreeItem.getValue().setCallCount(rootTreeItem.getValue().getCallCount() + totalMethodCalls);

            rootTreeItem.getValue().setAverageMethodTime(new BigDecimal(rootTreeItem.getValue().getTotalMethodTime()).divide((new BigDecimal(rootTreeItem.getValue().getCallCount())), ExecuteJarUtil.TIME_PRECISION_PLACES, RoundingMode.HALF_UP).toString());

            rootTreeItem.getValue().setTotalMethodTime( new BigDecimal(rootTreeItem.getValue().getTotalMethodTime()).add(getCurrentTimeSelection().convertTimeBigDecimal(totalTotalTime)).toString());

	        // Add class to rootTreeItem
            rootTreeItem.getChildren().add(classEntry);
        }

	    // Set sorting mode
        treeTableView.setSortMode(TreeSortMode.ALL_DESCENDANTS);

	    // Create the columns
        TreeTableColumn<ApplicationStat, String> methodClassNameColumn = new TreeTableColumn<>("Class / Method Names");
        methodClassNameColumn.setPrefWidth(500);
        methodClassNameColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) -> new ReadOnlyStringWrapper(param.getValue().getValue().getMethodName()));

        TreeTableColumn<ApplicationStat, FormattedNumber> methodCallCountColumn = new TreeTableColumn<>("Call Count");
        methodCallCountColumn.setPrefWidth(115);
        methodCallCountColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<ApplicationStat, FormattedNumber> param) -> new SimpleObjectProperty<>(new FormattedNumber(param.getValue().getValue().getCallCount(), "")));

        TreeTableColumn<ApplicationStat, FormattedNumber> averageTimeColumn = new TreeTableColumn<>("Average Time");
        averageTimeColumn.setPrefWidth(185);
        averageTimeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<ApplicationStat, FormattedNumber> param) -> new SimpleObjectProperty<>(new FormattedNumber(Float.parseFloat(param.getValue().getValue().getAverageMethodTime()), getTimeAbbreviation())));

        TreeTableColumn<ApplicationStat, FormattedNumber> totalTimeColumn = new TreeTableColumn<>("Total Time");
        totalTimeColumn.setPrefWidth(185);
        totalTimeColumn.setCellValueFactory((TreeTableColumn.CellDataFeatures<ApplicationStat, FormattedNumber> param) -> new SimpleObjectProperty<>(new FormattedNumber(Float.parseFloat(param.getValue().getValue().getTotalMethodTime()), getTimeAbbreviation())));


        TreeTableColumn<ApplicationStat, String> appStatsRootColumn = new TreeTableColumn<>("Application Stats");

        // Add sub columns
        appStatsRootColumn.getColumns().addAll(methodCallCountColumn, averageTimeColumn, totalTimeColumn);




        // Add all the columns to the tree table
        treeTableView.getColumns().setAll(methodClassNameColumn, appStatsRootColumn);
    }

	public TimeUnit getCurrentTimeSelection()
	{
		return timeSelectorComboBox.getSelectionModel().getSelectedItem();
	}

    private void populateTreeDataForChartCallCount(TreeItem<ApplicationStat> rooti)
    {
        if (chartDataHolderCallCount == null)
        {
            chartDataHolderCallCount = new HashMap<>();
        }

        for(TreeItem<ApplicationStat> child: rooti.getChildren())
        {
            if(child.getChildren().isEmpty())
            {
                chartDataHolderCallCount.put(child.getValue().getMethodName(), (double) child.getValue().getCallCount());
            }
            else
            {
                populateTreeDataForChartCallCount(child);
            }
        }
    }

    private void populateTreeDataForChartTimeSpent(TreeItem<ApplicationStat> rooti)
    {
        if (chartDataHolderTimeSpent == null)
        {
            chartDataHolderTimeSpent = new HashMap<>();
        }

        for(TreeItem<ApplicationStat> child: rooti.getChildren())
        {
            if(child.getChildren().isEmpty())
            {
                chartDataHolderTimeSpent.put(child.getValue().getMethodName(), Double.valueOf(child.getValue().getTotalMethodTime()));
            }
            else
            {
                populateTreeDataForChartTimeSpent(child);
            }
        }
    }

    private void initBottomButtonGridPane(Group sceneRoot, BorderPane mainBorderPane)
    {
        // TODO path is field at top that is set by button window
        // TODO verify the path from the config file can be used
        // TODO if path wrong show a sample format of a path with spaces and complicated things that might have gone wrong, sample format should tak into account whether its a linux or windows machine


	    // Outside jar button label group
        Button linkJarUpButton = new Button("Select / Change Jar File Path");
        Label jarPathLabel = new Label(ExecuteJarUtil.OUTSIDE_PROGRAM_JAR_PATH);

        jarPathLabel.setTextFill(Color.BLACK);

        bottomButtons.add(linkJarUpButton, 0, 0);
        bottomButtons.add(jarPathLabel, 1, 0);

	    // Outside source button label group
        Button graphResultsButton = new Button("Graph Results");
        bottomButtons.add(graphResultsButton, 0, 2);

	    // Run dynamic analysis button
        runDynamicAnalysisButton = new Button("Run Dynamic Analysis         ");
	    bottomButtons.add(runDynamicAnalysisButton, 0, 1);
	    dynamicStateLabel = new Label("");
	    bottomButtons.add(dynamicStateLabel, 1, 1);

        // Expand Collapse button
        expandCollapseButton = new Button("Expand / Collapse All");
        bottomButtons.add(expandCollapseButton, 0, 5);

        // Time selector comboBox
        bottomButtons.add(timeSelectorComboBox, 0, 4);

        mainBorderPane.setBottom(bottomButtons);
        sceneRoot.getChildren().add(mainBorderPane);


        ///// Listeners /////
        graphResultsButton.setOnAction(event ->
        {
            // Get the tree data
            populateTreeDataForChartCallCount(rootTreeItem);
            populateTreeDataForChartTimeSpent(rootTreeItem);

            // TODO Refactor all things related to the charts
            TabbedGraphInfoPane tabbedGraphInfoPane = new TabbedGraphInfoPane(chartDataHolderCallCount, chartDataHolderTimeSpent, getCurrentTimeSelection());

            Stage stage = new Stage();
            Scene scene = new Scene(tabbedGraphInfoPane, CHART_WINDOW_WIDTH, CHART_WINDOW_HEIGHT);

            scene.getStylesheets().add(CHART_STYLESHEET);
            stage.setTitle("Graphed Results");
            stage.setScene(scene);
            stage.show();
        });

        expandCollapseButton.setOnAction(event -> setRootExpandedCollapsed(rootTreeItem));

        timeSelectorComboBox.setOnAction(event ->
        {
            overallStat = new ApplicationStat(overallStat.getMethodName(), 0, "0", "0");

            rootTreeItem.setValue(overallStat);

            rootTreeItem.getChildren().clear();

            updateTreeTable();

            updateRightScrollPane();
        });

        linkJarUpButton.setOnAction(event ->
        {
            FileChooser fileChooser = new FileChooser();

            // Filter out all but jar file
            FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Jar Files (*.jar)", "*.jar");

            fileChooser.getExtensionFilters().add(extFilter);

            String newChosenPath = fileChooser.showOpenDialog(stage).getAbsolutePath();

            jarPathLabel.setText(newChosenPath);

	        ExecuteJarUtil.OUTSIDE_PROGRAM_JAR_PATH = newChosenPath;
        });

        runDynamicAnalysisButton.setOnMousePressed(event ->
        {
            // Needs to be in its own Thread to make sure it happens exactly when it is clicked
            Thread t = new Thread(() -> runDynamicAnalysisButton.setDisable(true));
            t.start();

            // Needs to be before the thread so it updates correctly
            dynamicStateLabel.setText("Running...");

            // What for button to gray out
            while (!runDynamicAnalysisButton.isDisabled())
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                }
            }

            // clear existing data
            DynamicData.getInstance().clear();
            overallStat = new ApplicationStat("", 0, "0", "0");
            rootTreeItem.setValue(overallStat);

            rootTreeItem.getChildren().clear();

            UpdateDynamicAnalysis u1 = new UpdateDynamicAnalysis();

            updateDynamicAnalysisThread = new Thread(u1, "Update Dynamic Analysis Thread");

            updateDynamicAnalysisThread.setDaemon(true);

            updateDynamicAnalysisThread.start();

            // Set Root title
            String rootTitle = ExecuteJarUtil.OUTSIDE_PROGRAM_JAR_PATH.toString();
            String[] splitString = rootTitle.split("\\\\");
            rootTitle = splitString[splitString.length-1];
            rootTreeItem.getValue().setMethodName(rootTitle);
        });
    }

    class UpdateDynamicAnalysis implements Runnable
    {
        @Override
        public void run()
        {
            Platform.setImplicitExit(false);

            Platform.runLater(() ->
            {
                System.out.println("Starting Dynamic Analysis.....");

                long start = System.nanoTime();

	            // Run the outside jar
	            ExecuteJar executeJar = new ExecuteJar(ExecuteJarUtil.OUTSIDE_PROGRAM_COMMAND);
	            executeJar.runJar();

	            long end = System.nanoTime();
                TimeUnit.OUTSIDE_PROGRAM_DYNAMIC_EXECUTION_TIME = end - start;

	            for (String s : executeJar.outputProcessor.getOutputList())
	            {
		            System.out.println(s);
	            }

	            ProcessJarOutput.processOutput(executeJar.outputProcessor.getOutputList());

                updateTreeTable();

                updateRightScrollPane();

                dynamicStateLabel.setText("Done");

                runDynamicAnalysisButton.setDisable(false);

                System.out.println("Dynamic Analysis Complete\n");
            });
        }
    }

    private void updateRightScrollPane()
    {
        float totalExecutionTime = Float.parseFloat(  getCurrentTimeSelection().convertTimeLongToString(TimeUnit.OUTSIDE_PROGRAM_DYNAMIC_EXECUTION_TIME));
        BigDecimal totalOverheadTime = new BigDecimal(getCurrentTimeSelection().convertTimeLongToString(TimeUnit.OUTSIDE_PROGRAM_DYNAMIC_EXECUTION_TIME)).subtract(new BigDecimal(overallStat.getTotalMethodTime()));

        resetRightScrollPane();

        appendToScrollPanel("Dynamic Analysis Total Execution Time:\n" + FORMATTER_NANO.format(totalExecutionTime) + getTimeAbbreviation());

        appendToScrollPanel("Dynamic Analysis Overhead Time:\n"  + FORMATTER_NANO.format(totalOverheadTime) + getTimeAbbreviation());

        // TODO check for the divisor to not be zero
	    appendToScrollPanel("Percentage of time spent on overhead:\n"  + "%" + FORMATTER_NANO.format(totalOverheadTime.divide(BigDecimal.valueOf(totalExecutionTime), 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))));

        appendToScrollPanel("\n");
    }

    private void resetRightScrollPane()
    {
        textArea.setText("Additional Results:\n");
    }

    private void appendToScrollPanel(String input)
    {
        textArea.setText(textArea.getText() + "\n" + input);
    }

    private void setRootExpandedCollapsed(TreeItem<ApplicationStat> root)
    {
        if (!MAIN_TREE_EXPANDED)
        {
            root.setExpanded(true);

            for(TreeItem child : root.getChildren())
            {
                child.setExpanded(true);
            }

            MAIN_TREE_EXPANDED = true;
        }
        else
        {
            root.setExpanded(true);

            for(TreeItem child : root.getChildren())
            {
                child.setExpanded(false);
            }

            MAIN_TREE_EXPANDED = false;
        }
    }

	public String getTimeAbbreviation()
	{
        return getCurrentTimeSelection().abbreviation;
	}

    public static void main(String[] args)
    {
        Application.launch(GUIMain.class, args);
    }
}