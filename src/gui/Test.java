package gui;

import javafx.application.Application;
import javafx.css.PseudoClass;
import javafx.scene.Scene;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class Test extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        final BorderPane uiRoot = new BorderPane();

        TreeItem<Integer> root = createTreeItem(1);

        final TreeView<Integer> tree = new TreeView<>(root);

        PseudoClass leaf = PseudoClass.getPseudoClass("leaf");
        tree.setCellFactory(tv ->
        {
            TreeCell<Integer> cell = new TreeCell<>();
            cell.itemProperty().addListener((obs, oldValue, newValue) ->
            {
                if (newValue == null)
                {
                    cell.setText("");
                }
                else
                {
                    cell.setText(newValue.toString());
                }
            });

            cell.treeItemProperty().addListener((obs, oldTreeItem, newTreeItem) -> cell.pseudoClassStateChanged(leaf, newTreeItem != null && newTreeItem.isLeaf()));
            return cell ;
        });

        uiRoot.setCenter(tree);

        final Scene scene = new Scene(uiRoot);
        scene.getStylesheets().add("bold-non-leaf-nodes.css");

        primaryStage.setScene(scene);
        primaryStage.setTitle(getClass().getSimpleName());
        primaryStage.show();
    }

    private TreeItem<Integer> createTreeItem(int value)
    {
        TreeItem<Integer> item = new TreeItem<>(value);

        if (value < 10000)
        {
            for (int i=0; i<10; i++)
            {
                item.getChildren().add(createTreeItem(10 * value+ i));
            }
        }
        return item ;
    }

    public static void main(String[] args) {
        launch(args);
    }
}  