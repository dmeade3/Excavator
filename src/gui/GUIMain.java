package gui;

import data_storage.DynamicClassDataEntry;
import data_storage.DynamicData;
import data_storage.DynamicMethodDataEntry;
import data_storage.SystemConfig;
import dynamic_analysis.RunProgramAtRunTime;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Optional;

import static data_storage.SystemConfig.*;

public class GUIMain extends Application
{
    // Fields //////////////////////////////////////////////////////////////////////////////////////////////////////////

    ApplicationStat overallStat = new ApplicationStat("", 0, 0, 0);
    final TreeItem<ApplicationStat> root = new TreeItem<>(overallStat);

    TreeTableView<ApplicationStat> treeTableView = new TreeTableView<>(root);

    GridPane bottomButtons = new GridPane();

    // Methods /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void start(Stage stage)
    {
        // Todo organize this
        root.setExpanded(true);


        final Scene scene = new Scene(new Group(), SCENEWIDTH, SCENEHEIGHT);
        scene.getStylesheets().add(SystemConfig.PATHTOMAINSTYLESHEET);
        scene.setFill(new Color(.225, .228, .203, .5));

        // Tree table size adjusting
        treeTableView.setTableMenuButtonVisible(true);
        treeTableView.setPrefWidth(SCENEWIDTH);
        treeTableView.setPrefHeight(SCENEHEIGHT - SPACEFROMBOTTOMBUTTONS);

        // Listeners for scene growth
        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> treeTableView.setPrefWidth((Double)  newSceneWidth));
        scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> treeTableView.setPrefHeight((Double) newSceneHeight - SPACEFROMBOTTOMBUTTONS));

        Group sceneRoot = (Group) scene.getRoot();
        BorderPane mainBorderPane = new BorderPane();

        updateTreeTable();
        mainBorderPane.setCenter(treeTableView);

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
            classApplicationStat = new ApplicationStat(dynamicClassDataEntry.getClassName(), 0, 0, 0);

            TreeItem classEntry = new TreeItem<>(classApplicationStat);

            long totalMethodCalls = 0;
            long totalAverageTime = 0;
            long totalTotalTime = 0;
            // Add method info to the class object
            for (DynamicMethodDataEntry dynamicMethodDataEntry : dynamicClassDataEntry.getData().values())
            {
                totalMethodCalls += dynamicMethodDataEntry.getCallCount();
                totalAverageTime += dynamicMethodDataEntry.getAverageTime();
                totalTotalTime += dynamicMethodDataEntry.getTotalTime();

                methodApplicationStat = new ApplicationStat(dynamicMethodDataEntry.getMethodName(), dynamicMethodDataEntry.getCallCount(),
                                                            dynamicMethodDataEntry.getAverageTime(), dynamicMethodDataEntry.getTotalTime());

                TreeItem methodEntry = new TreeItem(methodApplicationStat);

                classEntry.getChildren().add(methodEntry);
            }

            classApplicationStat.setCallCount(totalMethodCalls);
            classApplicationStat.setAverageMethodTime(totalAverageTime);
            classApplicationStat.setTotalMethodTime(totalTotalTime);

            root.getValue().setCallCount(root.getValue().getCallCount() + totalMethodCalls);
            root.getValue().setAverageMethodTime(root.getValue().getAverageMethodTime() + totalAverageTime);
            root.getValue().setTotalMethodTime(root.getValue().getTotalMethodTime() + totalTotalTime);

            root.getChildren().add(classEntry);
        }


        TreeTableColumn<ApplicationStat, String> methodClassNameColumn = new TreeTableColumn<>("");
        methodClassNameColumn.setPrefWidth(280);
        methodClassNameColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) -> new ReadOnlyStringWrapper(param.getValue().getValue().getmethodName()));

        TreeTableColumn<ApplicationStat, String> methodCallCountColumn = new TreeTableColumn<>("Call Count");
        methodCallCountColumn.setPrefWidth(115);
        methodCallCountColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) -> new ReadOnlyStringWrapper(String.valueOf(param.getValue().getValue().getCallCount())));

        TreeTableColumn<ApplicationStat, String> averageTimeColumn = new TreeTableColumn<>("Average Time");
        averageTimeColumn.setPrefWidth(150);
        averageTimeColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) -> new ReadOnlyStringWrapper(String.valueOf(param.getValue().getValue().getAverageMethodTime())));

        TreeTableColumn<ApplicationStat, String> totalTimeColumn = new TreeTableColumn<>("Total Time");
        totalTimeColumn.setPrefWidth(150);
        totalTimeColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) -> new ReadOnlyStringWrapper(String.valueOf(param.getValue().getValue().getTotalMethodTime())));

        // Add all the columns to the tree table
        treeTableView.getColumns().setAll(methodClassNameColumn, methodCallCountColumn, averageTimeColumn, totalTimeColumn);
    }

    private void initBottomButtonGridPane(Group sceneRoot, BorderPane mainBorderPane)
    {
        // TODO cant press when running
        // TODO organize this
        // TODO path is field at top that is set by button window
        // TODO verify the path can be used
        // TODO if path wrong show a sample format of a path with spaces and complicated things that might have gone wrong
        bottomButtons.setHgap(5);
        bottomButtons.setVgap(5);

        Button linkJarUpButton = new Button("Select / Change Jar File Path");
        // Displays current path to jar
        Label jarPathLabel = new Label(OUTSIDEPROGRAMPATH);
        bottomButtons.add(linkJarUpButton, 0, 0);
        bottomButtons.add(jarPathLabel, 1, 0);

        Button linkSourceUpButton = new Button("Select / Change Source Path");
        // Displays current path to Source
        Label sourcePathLabel = new Label(OUTSIDEPROGRAMSOURCEPATH);
        bottomButtons.add(linkSourceUpButton, 0, 1);
        bottomButtons.add(sourcePathLabel, 1, 1);

        // Todo label saying idle or running or done
        Button runStaticAnalysisButton  = new Button("Run Static Analysis             ");
        Button runDynamicAnalysisButton = new Button("Run Dynamic Analysis         ");

        bottomButtons.add(runDynamicAnalysisButton, 0, 2);
        bottomButtons.add(runStaticAnalysisButton, 0, 3);

        // Listeners
        linkJarUpButton.setOnAction(event -> {

                // TODO path from last time goes here in label also come up with a default if the old path is not here");
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Select / Change Jar File Path");
                dialog.setHeaderText("Select / Change Jar File Path");

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
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Select / Change Source Path");
                dialog.setHeaderText("Select / Change Source Path");

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
            overallStat = new ApplicationStat("", 0, 0, 0);
            root.setValue(overallStat);

            RunProgramAtRunTime.RunOutsideProgram();

            root.getChildren().clear();

            updateTreeTable();

            setRootExpandedRecursively(root);
        });


        mainBorderPane.setBottom(bottomButtons);
        sceneRoot.getChildren().add(mainBorderPane);
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
}