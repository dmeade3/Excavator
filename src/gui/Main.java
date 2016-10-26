package gui;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class Main extends Application
{
    List<ApplicationStat> applicationStats = Arrays.<ApplicationStat>asList( new ApplicationStat("method 1", 789),
                                                                             new ApplicationStat("method 2", 321),
                                                                             new ApplicationStat("method 3", 123456789),
                                                                             new ApplicationStat("method 4", 1254),
                                                                             new ApplicationStat("method 5", 80),
                                                                             new ApplicationStat("method 6", 70));

    final TreeItem<ApplicationStat> root = new TreeItem<>(new ApplicationStat("Application Statistics", 0));
    TreeTableView<ApplicationStat> treeTableView;

    @Override
    public void start(Stage stage)
    {
        // Todo organize this , numbers should be constants

        root.setExpanded(true);

        applicationStats.forEach((applicationStat) -> root.getChildren().add(new TreeItem<>(applicationStat)));

        stage.setTitle("Excavator");
        final Scene scene = new Scene(new Group(), 1400, 600);
        scene.setFill(Color.LIGHTGRAY);

        // Listeners for scene growth
        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) ->    treeTableView.setPrefWidth((Double)  newSceneWidth));
        scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) -> treeTableView.setPrefHeight((Double) newSceneHeight - 200));

        Group sceneRoot = (Group) scene.getRoot();

        TreeTableColumn<ApplicationStat, String> methodNameColumn = new TreeTableColumn<>("Method Name");
        methodNameColumn.setPrefWidth(170);
        methodNameColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) ->  new ReadOnlyStringWrapper(param.getValue().getValue().getmethodName()));

        TreeTableColumn<ApplicationStat, String> methodCallCountColumn = new TreeTableColumn<>("Method Name");
        methodCallCountColumn.setPrefWidth(170);
        methodCallCountColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) ->  new ReadOnlyStringWrapper(String.valueOf(param.getValue().getValue().getCallCount())));


        treeTableView = new TreeTableView<>(root);
        treeTableView.setTableMenuButtonVisible(true);
        treeTableView.setPrefWidth(1400);
        treeTableView.setPrefHeight(600 - 200);


        treeTableView.getColumns().setAll(methodNameColumn, methodCallCountColumn);
        sceneRoot.getChildren().add(treeTableView);
        stage.setScene(scene);
        stage.show();
    }



    // TODO add more functionality from here https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/tree-table-view.htm




    public static void main(String[] args)
    {
        Application.launch(Main.class, args);
    }
}