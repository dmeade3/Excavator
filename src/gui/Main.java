package gui;

import javafx.application.Application;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
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
    List<Employee> employees = Arrays.<Employee>asList(
                    new Employee("Ethan Williams", "ethan.williams@example.com"),
                    new Employee("Emma Jones",     "emma.jones@example.com"),
                    new Employee("Michael Brown",  "michael.brown@example.com"),
                    new Employee("Anna Black",     "anna.black@example.com"),
                    new Employee("Rodger York",    "roger.york@example.com"),
                    new Employee("Susan Collins",  "susan.collins@example.com"));

    final TreeItem<Employee> root = new TreeItem<>(new Employee("Sales Department", ""));
    TreeTableView<Employee> treeTableView;

    @Override
    public void start(Stage stage)
    {
        // Todo organize this , numbers should be constants

        root.setExpanded(true);

        employees.forEach((employee) ->
        {
            root.getChildren().add(new TreeItem<>(employee));
        });

        stage.setTitle("Excavator");
        final Scene scene = new Scene(new Group(), 1400, 600);
        scene.setFill(Color.LIGHTGRAY);

        // Listeners for scene growth
        scene.widthProperty().addListener((observableValue, oldSceneWidth, newSceneWidth) ->
        {
            treeTableView.setPrefWidth((Double) newSceneWidth);
        });

        scene.heightProperty().addListener((observableValue, oldSceneHeight, newSceneHeight) ->
        {
            treeTableView.setPrefHeight((Double) newSceneHeight);
        });


        Group sceneRoot = (Group) scene.getRoot();

        TreeTableColumn<Employee, String> empColumn = new TreeTableColumn<>("Employee");
        empColumn.setPrefWidth(170);
        empColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<Employee, String> param) ->  new ReadOnlyStringWrapper(param.getValue().getValue().getName()));

        TreeTableColumn<Employee, String> emailColumn = new TreeTableColumn<>("Email");
        emailColumn.setPrefWidth(210);
        emailColumn.setCellValueFactory( (TreeTableColumn.CellDataFeatures<Employee, String> param) -> new ReadOnlyStringWrapper(param.getValue().getValue().getEmail()));

        treeTableView = new TreeTableView<>(root);
        treeTableView.setTableMenuButtonVisible(true);
        treeTableView.setPrefWidth(1400);
        treeTableView.setPrefHeight(600);



        treeTableView.getColumns().setAll(empColumn, emailColumn);
        sceneRoot.getChildren().add(treeTableView);
        stage.setScene(scene);
        stage.show();
    }



    // TODO add more functionality from here https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/tree-table-view.htm





























    public class Employee
    {
        private SimpleStringProperty name;
        private SimpleStringProperty email;

        public SimpleStringProperty nameProperty()
        {
            if (name == null)
            {
                name = new SimpleStringProperty(this, "name");
            }
            return name;
        }

        public SimpleStringProperty emailProperty()
        {
            if (email == null)
            {
                email = new SimpleStringProperty(this, "email");
            }
            return email;
        }

        private Employee(String name, String email)
        {
            this.name = new SimpleStringProperty(name);
            this.email = new SimpleStringProperty(email);
        }

        public String getName()
        {
            return name.get();
        }

        public void setName(String fName)
        {
            name.set(fName);
        }

        public String getEmail()
        {
            return email.get();
        }

        public void setEmail(String fName)
        {
            email.set(fName);
        }
    }

    public static void main(String[] args)
    {
        Application.launch(Main.class, args);
    }
}