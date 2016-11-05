package gui;

import data_storage.DynamicClassDataEntry;
import data_storage.DynamicData;
import data_storage.DynamicMethodDataEntry;
import data_storage.SystemConfig;
import dynamic_analysis.RunProgramAtRunTime;
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
import javafx.stage.Stage;
import static_analysis.StaticAnalysis;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static data_storage.SystemConfig.*;

public class GUIMain extends Application
{
    // Fields //////////////////////////////////////////////////////////////////////////////////////////////////////////

	private ApplicationStat overallStat = new ApplicationStat("", 0, "0", "0");

	private final TreeItem<ApplicationStat> rootTreeItem = new TreeItem<>(overallStat);

	private final TreeTableView<ApplicationStat> treeTableView = new TreeTableView<>(rootTreeItem);

    private final ScrollPane rightScrollPane = new ScrollPane();

	private final TextArea textArea = TextAreaBuilder.create().prefWidth(SCENEHEIGHT - SPACEFROMBOTTOMBUTTONS).prefHeight(SCENEHEIGHT - SPACEFROMBOTTOMBUTTONS - 2).wrapText(true).build();

    private final GridPane bottomButtons = new GridPane();

    private final ComboBox<String> timeSelectorComboBox = new ComboBox<>(SystemConfig.TIMEOPTIONS);

	private final Color backgroundColor = new Color(.225, .228, .203, .5);

	private final Scene scene = new Scene(new Group(), SCENEWIDTH, SCENEHEIGHT);

	private final Group sceneRoot = (Group) scene.getRoot();

	private final BorderPane mainBorderPane = new BorderPane();

    private Thread updateDynamicAnalysisThread;

    private Label dynamicStateLabel;

	// Methods /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void start(Stage stage)
    {

	    // TODO make default read in by config

	    ///// Defaults /////
        rootTreeItem.setExpanded(true);
	    timeSelectorComboBox.setValue("Nanosecond");
	    treeTableView.setTableMenuButtonVisible(true);
	    textArea.setEditable(false);
	    resetRightScrollPane();
	    bottomButtons.setHgap(5);
	    bottomButtons.setVgap(5);

	    // Set up scene
        scene.getStylesheets().add(SystemConfig.PATHTOMAINSTYLESHEET);
        scene.setFill(backgroundColor);

        // Tree table size adjusting
        treeTableView.setPrefWidth(SCENEWIDTH - SCROLLPANEWIDTH);
        treeTableView.setPrefHeight(SCENEHEIGHT - SPACEFROMBOTTOMBUTTONS);

        // Right Right ScrollPane
        rightScrollPane.setPrefWidth(SCROLLPANEWIDTH);
        rightScrollPane.setPrefHeight(SCENEHEIGHT - SPACEFROMBOTTOMBUTTONS);
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
		    treeTableView.setPrefHeight((Double) newSceneHeight - SPACEFROMBOTTOMBUTTONS);
		    textArea.setPrefHeight((Double) newSceneHeight - SPACEFROMBOTTOMBUTTONS - 2);
		    rightScrollPane.setPrefHeight((Double) newSceneHeight - SPACEFROMBOTTOMBUTTONS - 2);
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

                methodApplicationStat = new ApplicationStat(dynamicMethodDataEntry.getMethodName(), dynamicMethodDataEntry.getCallCount(),
                                                            convertTime(dynamicMethodDataEntry.getAverageTime()), convertTime(dynamicMethodDataEntry.getTotalTime()));

                TreeItem<ApplicationStat> methodEntry = new TreeItem<>(methodApplicationStat);

                classEntry.getChildren().add(methodEntry);
            }

	        // Add to class totals
            classApplicationStat.setCallCount(totalMethodCalls);
            classApplicationStat.setAverageMethodTime(convertTimeBigDecimal(totalAverageTime));
            classApplicationStat.setTotalMethodTime(convertTimeBigDecimal(totalTotalTime));

	        // Add to rootTreeItem totals
            rootTreeItem.getValue().setCallCount(rootTreeItem.getValue().getCallCount() + totalMethodCalls);
            rootTreeItem.getValue().setAverageMethodTime(new BigDecimal(rootTreeItem.getValue().getAverageMethodTime()).add(new BigDecimal(convertTimeBigDecimal(totalAverageTime))).toString());
            rootTreeItem.getValue().setTotalMethodTime(  new BigDecimal(rootTreeItem.getValue().getTotalMethodTime()).add(new BigDecimal(convertTimeBigDecimal(totalTotalTime))).toString());

	        // Add class to rootTreeItem
            rootTreeItem.getChildren().add(classEntry);
        }

	    // Set sorting mode
        treeTableView.setSortMode(TreeSortMode.ALL_DESCENDANTS);

	    // Create the columns
        TreeTableColumn<ApplicationStat, String> methodClassNameColumn = new TreeTableColumn<>("");
        methodClassNameColumn.setPrefWidth(500);
        methodClassNameColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) -> new ReadOnlyStringWrapper(param.getValue().getValue().getMethodName()));

        TreeTableColumn<ApplicationStat, FormattedNumber> methodCallCountColumn = new TreeTableColumn<>("Call Count");
        methodCallCountColumn.setPrefWidth(115);
        methodCallCountColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, FormattedNumber> param) -> new SimpleObjectProperty<>(new FormattedNumber(param.getValue().getValue().getCallCount(), "")));

        TreeTableColumn<ApplicationStat, FormattedNumber> averageTimeColumn = new TreeTableColumn<>("Average Time");
        averageTimeColumn.setPrefWidth(185);
        averageTimeColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, FormattedNumber> param) -> new SimpleObjectProperty<>(new FormattedNumber(Float.parseFloat(param.getValue().getValue().getAverageMethodTime()), getTimeShortDescription())));

        TreeTableColumn<ApplicationStat, FormattedNumber> totalTimeColumn = new TreeTableColumn<>("Total Time");
        totalTimeColumn.setPrefWidth(185);
        totalTimeColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, FormattedNumber> param) -> new SimpleObjectProperty<>(new FormattedNumber(Float.parseFloat(param.getValue().getValue().getTotalMethodTime()), getTimeShortDescription())));

        // Add all the columns to the tree table
        treeTableView.getColumns().setAll(methodClassNameColumn, methodCallCountColumn, averageTimeColumn, totalTimeColumn);
    }

    private String convertTimeBigDecimal(BigDecimal in)
    {
        return in.divide(new BigDecimal(String.valueOf(getTimeDivisor(String.valueOf(timeSelectorComboBox.getValue()))))).toString();
    }

    private void initBottomButtonGridPane(Group sceneRoot, BorderPane mainBorderPane)
    {
        // TODO cant press when running
        // TODO path is field at top that is set by button window
        // TODO verify the path from the config file can be used
        // TODO if path wrong show a sample format of a path with spaces and complicated things that might have gone wrong, sample format should tak into account whether its a linux or windows machine


	    // Outside jar button label group
        Button linkJarUpButton = new Button("Select / Change Jar File Path");
        Label jarPathLabel = new Label(OUTSIDEPROGRAMJARPATH);
        bottomButtons.add(linkJarUpButton, 0, 0);
        bottomButtons.add(jarPathLabel, 1, 0);

	    // Outside source button label group
        Button linkSourceUpButton = new Button("Select / Change Source Path");
        Label sourcePathLabel = new Label(OUTSIDEPROGRAMSOURCEPATH);
        bottomButtons.add(linkSourceUpButton, 0, 1);
        bottomButtons.add(sourcePathLabel, 1, 1);

	    // Run dynamic analysis button
        Button runStaticAnalysisButton  = new Button("Run Static Analysis             ");
	    Label staticStateLabel = new Label("");
	    bottomButtons.add(runStaticAnalysisButton, 0, 3);
		bottomButtons.add(staticStateLabel, 1, 3);

	    // Run static analysis button
        Button runDynamicAnalysisButton = new Button("Run Dynamic Analysis         ");
	    bottomButtons.add(runDynamicAnalysisButton, 0, 2);
	    dynamicStateLabel = new Label("");
	    bottomButtons.add(dynamicStateLabel, 1, 2);

        // Time selector comboBox
        bottomButtons.add(timeSelectorComboBox, 0, 4);

        mainBorderPane.setBottom(bottomButtons);
        sceneRoot.getChildren().add(mainBorderPane);

        ///// Listeners /////
        timeSelectorComboBox.setOnAction(event ->
        {
            overallStat = new ApplicationStat(overallStat.getMethodName(), 0, "0", "0");

            rootTreeItem.setValue(overallStat);

            rootTreeItem.getChildren().clear();

            updateTreeTable();

            updateRightScrollPane();
        });

        linkJarUpButton.setOnAction(event -> {

                // TODO path from last time goes here in label also come up with a default if the old path is not here");
                TextInputDialog dialog = new TextInputDialog(SystemConfig.OUTSIDEPROGRAMJARPATH);
                dialog.setTitle("Select / Change Jar File Path");
                dialog.setHeaderText("Select / Change Jar File Path");
	            dialog.setResizable(true);
	            dialog.getDialogPane().setPrefSize(DIALOGWIDTH, DIALOGHEIGHT);

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent()) {
                    // TODO check if a jar

                    if (pathReachable(result.get()))
                    {
                        jarPathLabel.setText(result.get());
                    }
                    else
                    {
                        // TODO popup says what went wrong and how to fix
                    }
                }
            }
        );

        linkSourceUpButton.setOnAction(event -> {

                 // TODO path from last time goes here in label also come up with a default if the old path is not here");
                TextInputDialog dialog = new TextInputDialog(SystemConfig.OUTSIDEPROGRAMSOURCEPATH);
                dialog.setTitle("Select / Change Source Path");
                dialog.setHeaderText("Select / Change Source Path");
	            dialog.setResizable(true);
	            dialog.getDialogPane().setPrefSize(DIALOGWIDTH, DIALOGHEIGHT);

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent())
                {
                    // Validate the path can be reached TODO check if it is a file or directory
                    sourcePathLabel.setText(result.get());
                }
            }
        );

        runDynamicAnalysisButton.setOnMousePressed(event -> {

            dynamicStateLabel.setText("Running...");
        });

        runDynamicAnalysisButton.setOnAction(event -> {

            // clear existing data
            DynamicData.getInstance().clear();
            overallStat = new ApplicationStat("", 0, "0", "0");
            rootTreeItem.setValue(overallStat);

            rootTreeItem.getChildren().clear();

            UpdateDynamicAnalysis u1 = new UpdateDynamicAnalysis();

            updateDynamicAnalysisThread = new Thread(u1, "T1");
            updateDynamicAnalysisThread.setDaemon(true);

            updateDynamicAnalysisThread.start();

            // Set Root title
            String rootTitle = OUTSIDEPROGRAMJARPATH;
            String[] splitString = rootTitle.split("\\\\");
            rootTitle = splitString[splitString.length-1];
            rootTreeItem.getValue().setMethodName(rootTitle);
        });

        runStaticAnalysisButton.setOnMousePressed(event -> staticStateLabel.setText("Running..."));

        runStaticAnalysisButton.setOnAction(event -> {

            // clear existing data

            // TODO fil out commented out likes from dynamic to be for static

            //DynamicData.getInstance().clear();
            overallStat = new ApplicationStat("", 0, "0", "0");
            rootTreeItem.setValue(overallStat);

            // TODO like in dynamic have in its own thread
            long start = System.nanoTime();
            StaticAnalysis.runStaticAnalysis();
            long end = System.nanoTime();
            OUTSIDEPROGRAMSTATICEXECUTIONTIME = end - start;

            resetRightScrollPane();

            appendToScrollPanel("Static Analysis Total Execution Time:\n\t" + OUTSIDEPROGRAMSTATICEXECUTIONTIME + "ns\n");

            staticStateLabel.setText("Done");

            rootTreeItem.getChildren().clear();

            //updateTreeTable();

            //appendToScrollPanel("Dynamic Analysis Overhead Time:\n\t"  + (SystemConfig.OUTSIDEPROGRAMEXECUTIONTIME - overallStat.getTotalMethodTime()));
            //appendToScrollPanel("\n");

            // Set Root title
            //String rootTitle = OUTSIDEPROGRAMJARPATH;
            //String[] splitString = rootTitle.split("\\\\");
            //rootTitle = splitString[splitString.length-1];
            //rootTreeItem.getValue().setMethodName(rootTitle);
        });
    }

    class UpdateDynamicAnalysis implements Runnable
    {
        public void run()
        {

            Platform.setImplicitExit(false);

            Platform.runLater(() ->
            {
                System.out.println("Starting Dynamic Analysis.....");

                long start = System.nanoTime();
                RunProgramAtRunTime.runOutsideProgram();
                long end = System.nanoTime();
                OUTSIDEPROGRAMDYNAMICEXECUTIONTIME = end - start;

                updateTreeTable();

                updateRightScrollPane();

                dynamicStateLabel.setText("Done");

                System.out.println("Dynamic Analysis Complete\n");
            });
        }
    }

    private static boolean pathReachable(String filepath)
    {
        File file = new File(filepath);

        if ((file.exists()) && (file.isFile()))
        {
            return true;
        }

        return false;
    }

    private void updateRightScrollPane()
    {

        float totalExecutionTime = Float.parseFloat(convertTime(OUTSIDEPROGRAMDYNAMICEXECUTIONTIME));
        BigDecimal totalOverheadTime = new BigDecimal(convertTime(OUTSIDEPROGRAMDYNAMICEXECUTIONTIME)).subtract(new BigDecimal(overallStat.getTotalMethodTime()));

        resetRightScrollPane();

        appendToScrollPanel("Dynamic Analysis Total Execution Time:\n" + NUMBERFORMATER.format(totalExecutionTime) + getTimeShortDescription());

        appendToScrollPanel("Dynamic Analysis Overhead Time:\n"  + NUMBERFORMATER.format(totalOverheadTime) + getTimeShortDescription());

        appendToScrollPanel("Percentage of time spent on overhead:\n"  + "%" + NUMBERFORMATER.format(totalOverheadTime.divide(BigDecimal.valueOf(totalExecutionTime), 5, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))));

        appendToScrollPanel("\n");
    }

    private void resetRightScrollPane()
    {
        textArea.setText("Additional Results:\n");
    }

    private String convertTime(float in)
    {
        if (String.valueOf(timeSelectorComboBox.getValue()).equals("Nanosecond"))
        {
            return String.valueOf((long)(in / getTimeDivisor(String.valueOf(timeSelectorComboBox.getValue()))));
        }

        return String.valueOf(in / getTimeDivisor(String.valueOf(timeSelectorComboBox.getValue())));
    }

    private long getTimeDivisor(String s)
    {
        switch(s)
        {
            case"Millisecond":
                return 1000000;

            case"Second":
                return 1000000000;

	        // nanosecond case
            default:
	            return 1;
        }
	}

    private void appendToScrollPanel(String input)
    {
        textArea.setText(textArea.getText() + "\n" + input);
    }

    private void setRootExpandedRecursively(TreeItem<ApplicationStat> root)
    {
        root.getChildren().forEach(this::setRootExpandedRecursively);

        root.setExpanded(true);
    }

	public String getTimeShortDescription()
	{
		switch(timeSelectorComboBox.getValue())
		{
			case "Nanosecond":
				return "ns";

			case"Millisecond":
				return "ms";

			case"Second":
				return "s";
		}

		// nanosecond case
		return "error in getTimeShortDescription()";
	}

    public static void main(String[] args)
    {
        Application.launch(GUIMain.class, args);
    }
}