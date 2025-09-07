import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import java.util.*;

public class View extends Pane{
    private TextField textField1;
    private RadioButton radioButton1;
    private Button button1;
    private ListView<String> resultListTitles;
    private ListView<Double> resultListScores;

    public TextField getTextField(){
        return textField1;
    }

    public RadioButton getRadioButton() {
        return radioButton1;
    }

    public Button getButton(){
        return button1;
    }

    public View(){
        textField1 = new TextField("Enter your search query");
        textField1.relocate(10, 10);

        radioButton1 = new RadioButton("Boost");
        radioButton1.relocate(10, 50);

        button1 = new Button("Search");
        button1.relocate(115, 45);

        resultListTitles = new ListView<>();
        resultListTitles.relocate(190, 10);
        resultListTitles.setPrefSize(60,245);

        resultListScores = new ListView<>();
        resultListScores.relocate(260, 10);
        resultListScores.setPrefSize(180,245);

        getChildren().addAll(textField1, radioButton1, button1, resultListTitles, resultListScores);
        setPrefSize(450, 265);
    }

    public void update(List<SearchResult> searchResults){
        resultListTitles.getItems().clear();
        resultListScores.getItems().clear();

        for(SearchResult result : searchResults){
            resultListTitles.getItems().add(result.getTitle());
            resultListScores.getItems().add(result.getScore());
        }
    }
}
