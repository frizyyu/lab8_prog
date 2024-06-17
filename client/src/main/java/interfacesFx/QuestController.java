package interfacesFx;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class QuestController {

    public Label questionLabel;
    public Button yesButton;
    public Button noButton;
    QuestWindow QW;

    @FXML
    protected void onYesClick(){
        System.exit(0);
    }

    @FXML
    protected void onNoClick() throws IOException, ClassNotFoundException {
        QW.setMainWindow();
    }

    public void setQW(QuestWindow QW){
        this.QW = QW;
    }
}
