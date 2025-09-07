import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.event.*;

public class Controller extends Application{
    Searcher s = new Searcher();
    public void start(Stage primaryStage) {
        Pane  aPane = new Pane();

        View  view = new View();
        aPane.getChildren().add(view);

        primaryStage.setTitle("Search Engine");
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(aPane));
        primaryStage.show();

        EventHandler<ActionEvent> search = e -> view.update(s.search(view.getTextField().getText(), view.getRadioButton().isSelected(), 10));
        view.getButton().setOnAction(search);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
