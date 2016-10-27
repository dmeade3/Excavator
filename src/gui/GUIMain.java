package gui;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class GUIMain extends Application
{
    List<ApplicationStat> applicationStats = Arrays.<ApplicationStat>asList( new ApplicationStat("method 1", 789),
                                                               new ApplicationStat("method 2", 321),
                                                               new ApplicationStat("method 3", 123456789),
                                                               new ApplicationStat("method 4", 1254),
                                                               new ApplicationStat("method 5", 80),
                                                               new ApplicationStat("method 6", 70));

    final TreeItem<ApplicationStat> root = new TreeItem<>(new ApplicationStat("Program name here", 0));
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

	    // Make the columns of the results treetable
        TreeTableColumn<ApplicationStat, String> methodNameColumn = new TreeTableColumn<>("Method Name");
        methodNameColumn.setPrefWidth(170);
        methodNameColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) ->  new ReadOnlyStringWrapper(param.getValue().getValue().getmethodName()));

        TreeTableColumn<ApplicationStat, String> methodCallCountColumn = new TreeTableColumn<>("Call Count");
        methodCallCountColumn.setPrefWidth(100);
        methodCallCountColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<ApplicationStat, String> param) ->  new ReadOnlyStringWrapper(String.valueOf(param.getValue().getValue().getCallCount())));


        treeTableView = new TreeTableView<>(root);
        treeTableView.setTableMenuButtonVisible(true);
        treeTableView.setPrefWidth(1400);
        treeTableView.setPrefHeight(600 - 200);


        treeTableView.getColumns().setAll(methodNameColumn, methodCallCountColumn);


	    // use a grippane for these buttons
	    BorderPane mainBorderPane = new BorderPane();


	    mainBorderPane.setCenter(treeTableView);




	    // TODO cant press when running
	    // TODO button for running static analysis
	    // TODO button for running dynamic analysis
	    // TODO organize this
	    // TODO buttone bring up help windows ask forr path
	    // TODO path is field at top that is set by button window
	    // TODO verify the path can be used
	    // TODO if path wrong show a sample format of a path with spaces and complicated things that might have gone wrong
	    GridPane bottomButtons = new GridPane();
		bottomButtons.setHgap(10);
	    bottomButtons.setVgap(10);

	    // TODO Make button label into a function
	    Button linkJarUpButton = new Button("Select / Change Jar File        ");
	    // Displays current path to jar
	    Label jarPathLabel = new Label("null");
	    bottomButtons.add(linkJarUpButton, 0, 0);
	    bottomButtons.add(jarPathLabel, 1, 0);

	    Button linkSourceUpButton = new Button("Select / Change Source Path");
	    // Displays current path to jar
	    Label sourcePathLabel = new Label("null");
	    bottomButtons.add(linkSourceUpButton, 0, 1);
	    bottomButtons.add(sourcePathLabel, 1, 1);

	    Button runStaticAnalysisButton  = new Button("Run Static Analysis");
	    Button runDynamicAnalysisButton = new Button("Run Dynamic Analysis");

	    bottomButtons.add(runDynamicAnalysisButton, 0, 2);
	    bottomButtons.add(runStaticAnalysisButton, 0, 3);


	    mainBorderPane.setBottom(bottomButtons);


	    sceneRoot.getChildren().add(mainBorderPane);
        stage.setScene(scene);
        stage.show();
    }



    // TODO add more functionality from here https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/tree-table-view.htm




    public static void main(String[] args)
    {
        Application.launch(GUIMain.class, args);
    }
}