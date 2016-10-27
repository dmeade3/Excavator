package gui;

import data_storage.SystemConfig;
import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static data_storage.SystemConfig.SCENEHEIGHT;
import static data_storage.SystemConfig.SCENEWIDTH;
import static data_storage.SystemConfig.SPACEFROMBOTTOMBUTTONS;

public class GUIMain extends Application
{
    // Fields //////////////////////////////////////////////////////////////////////////////////////////////////////////

    // TODO Get rid of this once there is real data coming in
    List<ApplicationStat> applicationStats = Arrays.<ApplicationStat>asList( new ApplicationStat("method 1", 789),
                                                               new ApplicationStat("method 2", 321),
                                                               new ApplicationStat("method 3", 123456789),
                                                               new ApplicationStat("method 4", 1254),
                                                               new ApplicationStat("method 5", 80),
                                                               new ApplicationStat("method 6", 70));

    final TreeItem<ApplicationStat> root = new TreeItem<>(new ApplicationStat("Program name here", 0));

    TreeTableView<ApplicationStat> treeTableView;



    // This can go in configinfo Static file


    // Methods /////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void start(Stage stage)
    {
        // Todo organize this

        root.setExpanded(true);
        applicationStats.forEach((applicationStat) -> root.getChildren().add(new TreeItem<>(applicationStat)));

        final Scene scene = new Scene(new Group(), SCENEWIDTH, SCENEHEIGHT);
        scene.getStylesheets().add(SystemConfig.PATHTOMAINSTYLESHEET);
        scene.setFill(new Color(.225, .228, .203, .5));

        // Listeners for scene growth
        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) -> treeTableView.setPrefWidth((Double)  newSceneWidth));
        scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> treeTableView.setPrefHeight((Double) newSceneHeight - SPACEFROMBOTTOMBUTTONS));

        Group sceneRoot = (Group) scene.getRoot();
        BorderPane mainBorderPane = new BorderPane();

        initTreeTable();
        mainBorderPane.setCenter(treeTableView);

        initBottomButtonGridPane(sceneRoot, mainBorderPane);

        stage.setTitle("Excavator");
        stage.setScene(scene);
        stage.show();
    }

    private void initTreeTable()
    {
        TreeTableColumn<ApplicationStat, String> methodNameColumn = new TreeTableColumn<>("Method Name");
        methodNameColumn.setPrefWidth(170);
        methodNameColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) ->  new ReadOnlyStringWrapper(param.getValue().getValue().getmethodName()));

        TreeTableColumn<ApplicationStat, String> methodCallCountColumn = new TreeTableColumn<>("Call Count");
        methodCallCountColumn.setPrefWidth(100);
        methodCallCountColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) ->  new ReadOnlyStringWrapper(String.valueOf(param.getValue().getValue().getCallCount())));

        treeTableView = new TreeTableView<>(root);
        treeTableView.setTableMenuButtonVisible(true);
        treeTableView.setPrefWidth(SCENEWIDTH);
        treeTableView.setPrefHeight(SCENEHEIGHT - SPACEFROMBOTTOMBUTTONS);

        treeTableView.getColumns().setAll(methodNameColumn, methodCallCountColumn);
    }


    // TODO add more functionality from here https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/tree-table-view.htm


    private void initBottomButtonGridPane(Group sceneRoot, BorderPane mainBorderPane)
    {
        // TODO cant press when running
        // TODO organize this
        // TODO buttons bring up help windows ask for path
        // TODO path is field at top that is set by button window
        // TODO verify the path can be used
        // TODO if path wrong show a sample format of a path with spaces and complicated things that might have gone wrong
        GridPane bottomButtons = new GridPane();

        bottomButtons.setHgap(5);
        bottomButtons.setVgap(5);

        // TODO Make button label into a function
        Button linkJarUpButton = new Button("Select / Change Jar File Path");
        // Displays current path to jar
        Label jarPathLabel = new Label("null");
        bottomButtons.add(linkJarUpButton, 0, 0);
        bottomButtons.add(jarPathLabel, 1, 0);

        Button linkSourceUpButton = new Button("Select / Change Source Path");
        // Displays current path to jar
        Label sourcePathLabel = new Label("null");
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

        mainBorderPane.setBottom(bottomButtons);
        sceneRoot.getChildren().add(mainBorderPane);
    }

    public static void main(String[] args)
    {
        Application.launch(GUIMain.class, args);
    }
}