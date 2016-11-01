package gui;

import data_storage.DynamicClassDataEntry;
import data_storage.DynamicData;
import data_storage.DynamicMethodDataEntry;
import data_storage.SystemConfig;
import dynamic_analysis.RunProgramAtRunTime;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.util.Optional;

import static data_storage.SystemConfig.*;

public class GUIMain extends Application
{
    // Fields //////////////////////////////////////////////////////////////////////////////////////////////////////////

    ApplicationStat overallStat = new ApplicationStat("", 0, "0", "0");
    final TreeItem<ApplicationStat> root = new TreeItem<>(overallStat);

    TreeTableView<ApplicationStat> treeTableView = new TreeTableView<>(root);

    ScrollPane rightScrollPane = new ScrollPane();
    final TextArea textArea = TextAreaBuilder.create().prefWidth(SCENEHEIGHT - SPACEFROMBOTTOMBUTTONS).prefHeight(SCENEHEIGHT - SPACEFROMBOTTOMBUTTONS - 2).wrapText(true).build();

    GridPane bottomButtons = new GridPane();

    ComboBox<String> timeSelectorComboBox = new ComboBox<>(SystemConfig.TIMEOPTIONS);

    // Methods /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void start(Stage stage)
    {
        root.setExpanded(true);
        Color backgroundColor = new Color(.225, .228, .203, .5);

        // TODO make default read in by config
        // TODO make time type an enum
        timeSelectorComboBox.setValue("Nanosecond");

        final Scene scene = new Scene(new Group(), SCENEWIDTH, SCENEHEIGHT);
        scene.getStylesheets().add(SystemConfig.PATHTOMAINSTYLESHEET);
        scene.setFill(backgroundColor);

        // Tree table size adjusting
        treeTableView.setTableMenuButtonVisible(true);
        treeTableView.setPrefWidth(SCENEWIDTH - SCROLLPANEWIDTH);
        treeTableView.setPrefHeight(SCENEHEIGHT - SPACEFROMBOTTOMBUTTONS);

        // Right Right Scrollpane
        // TODO make scrollpane width size a constant
        textArea.setEditable(false);
        rightScrollPane.setPrefWidth(SCROLLPANEWIDTH);
        rightScrollPane.setPrefHeight(SCENEHEIGHT - SPACEFROMBOTTOMBUTTONS);
        //rightScrollPane.getStyleClass().add("noborder-scroll-pane");
        rightScrollPane.setContent(textArea);
        rightScrollPane.setFitToWidth(true);

        textArea.setText("Additional Results:");


        // Listeners for scene growth
        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> treeTableView.setPrefWidth((Double)  newSceneWidth - SCROLLPANEWIDTH));
        scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> {

            treeTableView.setPrefHeight((Double) newSceneHeight - SPACEFROMBOTTOMBUTTONS);
            textArea.setPrefHeight((Double) newSceneHeight - SPACEFROMBOTTOMBUTTONS - 2);
            rightScrollPane.setPrefHeight((Double) newSceneHeight - SPACEFROMBOTTOMBUTTONS - 2);
        });

        Group sceneRoot = (Group) scene.getRoot();
        BorderPane mainBorderPane = new BorderPane();

        updateTreeTable();
        mainBorderPane.setCenter(treeTableView);

        mainBorderPane.setRight(rightScrollPane);

        initBottomButtonGridPane(sceneRoot, mainBorderPane);

        stage.setTitle("Excavator");
        stage.setScene(scene);
        stage.show();
    }

    private void updateTreeTable()
    {
        ApplicationStat classApplicationStat;
        ApplicationStat methodApplicationStat;

        // Add the data to root
        for (DynamicClassDataEntry dynamicClassDataEntry : DynamicData.getInstance().getData().values())
        {
            // Make the application stat
            classApplicationStat = new ApplicationStat(dynamicClassDataEntry.getClassName(), 0, "0", "0");

            TreeItem<ApplicationStat> classEntry = new TreeItem<>(classApplicationStat);

            long totalMethodCalls = 0;
            BigDecimal totalAverageTime = new BigDecimal("0");
            BigDecimal totalTotalTime = new BigDecimal("0");

            // Add method info to the class object
            for (DynamicMethodDataEntry dynamicMethodDataEntry : dynamicClassDataEntry.getData().values())
            {
                totalMethodCalls += dynamicMethodDataEntry.getCallCount();
                totalAverageTime = totalAverageTime.add(BigDecimal.valueOf(dynamicMethodDataEntry.getAverageTime()));
                totalTotalTime = totalTotalTime.add(BigDecimal.valueOf(dynamicMethodDataEntry.getTotalTime()));

                methodApplicationStat = new ApplicationStat(dynamicMethodDataEntry.getMethodName(), dynamicMethodDataEntry.getCallCount(),
                                                            convertTime(dynamicMethodDataEntry.getAverageTime()), convertTime(dynamicMethodDataEntry.getTotalTime()));

                TreeItem<ApplicationStat> methodEntry = new TreeItem<>(methodApplicationStat);

                classEntry.getChildren().add(methodEntry);
            }

            classApplicationStat.setCallCount(totalMethodCalls);
            classApplicationStat.setAverageMethodTime(convertTimeBigInt(totalAverageTime));
            classApplicationStat.setTotalMethodTime(convertTimeBigInt(totalTotalTime));

            root.getValue().setCallCount(root.getValue().getCallCount() + totalMethodCalls);

            root.getValue().setAverageMethodTime(new BigDecimal(root.getValue().getAverageMethodTime()).add(new BigDecimal(convertTimeBigInt(totalAverageTime))).toString());
            root.getValue().setTotalMethodTime(  new BigDecimal(root.getValue().getTotalMethodTime()).add(new BigDecimal(convertTimeBigInt(totalTotalTime))).toString());

            root.getChildren().add(classEntry);
        }


        // TODO number format call count too
        treeTableView.setSortMode(TreeSortMode.ALL_DESCENDANTS);

        TreeTableColumn<ApplicationStat, String> methodClassNameColumn = new TreeTableColumn<>("");
        methodClassNameColumn.setPrefWidth(500);
        methodClassNameColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) -> new ReadOnlyStringWrapper(param.getValue().getValue().getmethodName()));

        TreeTableColumn<ApplicationStat, Long> methodCallCountColumn = new TreeTableColumn<>("Call Count");
        methodCallCountColumn.setPrefWidth(115);
        methodCallCountColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, Long> param) -> new SimpleObjectProperty<>(param.getValue().getValue().getCallCount()));

        TreeTableColumn<ApplicationStat, String> averageTimeColumn = new TreeTableColumn<>("Average Time");
        averageTimeColumn.setPrefWidth(150);
        averageTimeColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) -> new SimpleObjectProperty<>(String.valueOf(NUMBERFORMATER.format(Float.parseFloat(param.getValue().getValue().getAverageMethodTime()))) + getTimeShortDescription()));

        TreeTableColumn<ApplicationStat, String> totalTimeColumn = new TreeTableColumn<>("Total Time");
        totalTimeColumn.setPrefWidth(150);
        totalTimeColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) -> new SimpleObjectProperty<>(String.valueOf(NUMBERFORMATER.format(Float.parseFloat(param.getValue().getValue().getTotalMethodTime()))) + getTimeShortDescription()));

        // Add all the columns to the tree table
        treeTableView.getColumns().setAll(methodClassNameColumn, methodCallCountColumn, averageTimeColumn, totalTimeColumn);
    }

    private String convertTimeBigInt(BigDecimal in)
    {
        return in.divide(new BigDecimal(String.valueOf(getTimeDivisor(String.valueOf(timeSelectorComboBox.getValue()))))).toString();
    }

    private void initBottomButtonGridPane(Group sceneRoot, BorderPane mainBorderPane)
    {
        // TODO cant press when running
        // TODO organize this
        // TODO path is field at top that is set by button window
        // TODO verify the path can be used
        // TODO if path wrong show a sample format of a path with spaces and complicated things that might have gone wrong, sample format should tak into account whether its a linux or windows machine
        bottomButtons.setHgap(5);
        bottomButtons.setVgap(5);

        Button linkJarUpButton = new Button("Select / Change Jar File Path");
        // Displays current path to jar
        Label jarPathLabel = new Label(OUTSIDEPROGRAMJARPATH);
        bottomButtons.add(linkJarUpButton, 0, 0);
        bottomButtons.add(jarPathLabel, 1, 0);

        Button linkSourceUpButton = new Button("Select / Change Source Path");
        // Displays current path to Source
        Label sourcePathLabel = new Label(OUTSIDEPROGRAMSOURCEPATH);
        bottomButtons.add(linkSourceUpButton, 0, 1);
        bottomButtons.add(sourcePathLabel, 1, 1);

        Button runStaticAnalysisButton  = new Button("Run Static Analysis             ");
	    Label staticStateLabel = new Label("");
	    bottomButtons.add(runStaticAnalysisButton, 0, 3);
		bottomButtons.add(staticStateLabel, 1, 3);

        Button runDynamicAnalysisButton = new Button("Run Dynamic Analysis         ");
	    bottomButtons.add(runDynamicAnalysisButton, 0, 2);
	    Label dynamicStateLabel = new Label("");
	    bottomButtons.add(dynamicStateLabel, 1, 2);

        // Time selector combobox
        bottomButtons.add(timeSelectorComboBox, 0, 4);

        mainBorderPane.setBottom(bottomButtons);
        sceneRoot.getChildren().add(mainBorderPane);

        ///// Listeners /////
        timeSelectorComboBox.setOnAction(event -> {

            overallStat = new ApplicationStat(overallStat.getmethodName(), 0, "0", "0");
            root.setValue(overallStat);

            root.getChildren().clear();

            updateTreeTable();
        });

        linkJarUpButton.setOnAction(event -> {

                // TODO path from last time goes here in label also come up with a default if the old path is not here");
                TextInputDialog dialog = new TextInputDialog(SystemConfig.OUTSIDEPROGRAMJARPATH);
                dialog.setTitle("Select / Change Jar File Path");
                dialog.setHeaderText("Select / Change Jar File Path");
	            dialog.setResizable(true);
	            dialog.getDialogPane().setPrefSize(DIALOGWIDTH, DIALOGHEIGHT);

                Optional<String> result = dialog.showAndWait();
                if (result.isPresent())
                {
                    // Validate the path can be reached TODO make validating a function and put it in a common area
                    jarPathLabel.setText(result.get());
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
                    // Validate the path can be reached TODO
                    sourcePathLabel.setText(result.get());
                }
            }
        );

        runDynamicAnalysisButton.setOnAction(event -> {

            // clear existing data
            DynamicData.getInstance().clear();
            overallStat = new ApplicationStat("", 0, "0", "0");
            root.setValue(overallStat);

            dynamicStateLabel.setText("Running...");

            long start = System.nanoTime();
            RunProgramAtRunTime.RunOutsideProgram();
            long end = System.nanoTime();
            OUTSIDEPROGRAMDYNAMICEXECUTIONTIME = end - start;

            appendToScrollPanel("Dynamic Analysis Total Execution Time:\n" + convertTime(OUTSIDEPROGRAMDYNAMICEXECUTIONTIME) + getTimeShortDescription());

            dynamicStateLabel.setText("Done");

            root.getChildren().clear();

            updateTreeTable();

            appendToScrollPanel("Dynamic Analysis Overhead Time:\n"  + (new BigDecimal(convertTime(OUTSIDEPROGRAMDYNAMICEXECUTIONTIME)).subtract(new BigDecimal(overallStat.getTotalMethodTime()))) + getTimeShortDescription());
            appendToScrollPanel("\n");

            // Set Root title
            String rootTitle = OUTSIDEPROGRAMJARPATH;
            String[] splitString = rootTitle.split("\\\\");
            rootTitle = splitString[splitString.length-1];
            root.getValue().setMethodName(rootTitle);
        });

        runStaticAnalysisButton.setOnAction(event -> {

            // clear existing data

            // TODO fil out commented out likes from dynamic to be for static

            //DynamicData.getInstance().clear();
            overallStat = new ApplicationStat("", 0, "0", "0");
            root.setValue(overallStat);

            staticStateLabel.setText("Running...");

            long start = System.nanoTime();
            //RunProgramAtRunTime.RunOutsideProgram();
            long end = System.nanoTime();
            OUTSIDEPROGRAMSTATICEXECUTIONTIME = end - start;

            appendToScrollPanel("Static Analysis Total Execution Time:\n\t" + OUTSIDEPROGRAMSTATICEXECUTIONTIME + "ns\n");

            staticStateLabel.setText("Done");

            root.getChildren().clear();

            //updateTreeTable();

            //appendToScrollPanel("Dynamic Analysis Overhead Time:\n\t"  + (SystemConfig.OUTSIDEPROGRAMEXECUTIONTIME - overallStat.getTotalMethodTime()));
            //appendToScrollPanel("\n");

            // Set Root title
            //String rootTitle = OUTSIDEPROGRAMJARPATH;
            //String[] splitString = rootTitle.split("\\\\");
            //rootTitle = splitString[splitString.length-1];
            //root.getValue().setMethodName(rootTitle);
        });
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
        }

        // nanosecond case
        return 1;
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

    public static void main(String[] args)
    {
        Application.launch(GUIMain.class, args);
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
}