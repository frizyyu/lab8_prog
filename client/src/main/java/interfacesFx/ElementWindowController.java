package interfacesFx;

import com.sun.tools.javac.Main;
import helpers.Language;
import helpers.ServerConnector;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ElementWindowController {
    public TextField name;
    public TextField part;
    public TextField alb;
    public TextField genre;
    public TextField sName;
    public TextField sAddr;
    public TextField cX;
    public TextField cY;
    public Label crDate;
    public Label id;
    public Button noButton;
    public Button yesButton;
    public Text idText;
    public Text nameText;
    public Text participText;
    public Text albText;
    public Text genreText;
    public Text studNameText;
    public Text studAddrText;
    public Text coordXText;
    public Text coordYText;
    public Text crDateText;
    public ComboBox genreCombo;
    private ObjectProperty<ResourceBundle> resources;
    Language l;

    MainController MC;
    Stage stage;
    String toSend;
    //ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

    public void onYesClick() throws IOException {
        if (this.idText.isVisible()) {
            toSend = String.format("update %s {\"name\":\"%s\",\"coordinates\":{\"x\":%s,\"y\":%s},\"numberOfParticipants\":%s,\"albumsCount\":%s,\"genre\":\"%s\",\"studio\":{\"name\":\"%s\",\"address\":\"%s\"}}",
                    id.getText(), name.getText(), cX.getText(), cY.getText(), part.getText(), alb.getText(), genreCombo.getSelectionModel().getSelectedItem(), sName.getText(), sAddr.getText());
        }
        else{
            toSend = String.format("add {\"name\":\"%s\",\"coordinates\":{\"x\":%s,\"y\":%s},\"numberOfParticipants\":%s,\"albumsCount\":%s,\"genre\":\"%s\",\"studio\":{\"name\":\"%s\",\"address\":\"%s\"}}",
                    name.getText(), cX.getText(), cY.getText(), part.getText(), alb.getText(), genreCombo.getSelectionModel().getSelectedItem(), sName.getText(), sAddr.getText());
        }

        MC.commandInput.setText(toSend);
        MainController.stopThread = true;
        stage.close();
        //fixedThreadPool.shutdown();
        MC.onSendClick();
    }

    public void onNoClick(){
        MainController.stopThread = true;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to exit?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            stage.close();
            //MC.startThread();
        }
        MainController.stopThread = false;
    }

    public void setMC(MainController MC){
        this.MC = MC;
        MainController.stopThread = false;
        //MC.startThread();
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void initLanguages(){
        /*fixedThreadPool = Executors.newFixedThreadPool(3);
        fixedThreadPool.submit(() -> {
            try {
                ServerConnector.clientToServer.listen();
                MC.refreshTable();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });*/
        resources = l.getResources();
        idText.textProperty().bind(getStringBinding("idObj"));
        nameText.textProperty().bind(getStringBinding("nameObj"));
        coordXText.textProperty().bind(getStringBinding("coordX"));
        coordYText.textProperty().bind(getStringBinding("coordY"));
        crDateText.textProperty().bind(getStringBinding("creationDate"));
        participText.textProperty().bind(getStringBinding("particip"));
        albText.textProperty().bind(getStringBinding("albums"));
        genreText.textProperty().bind(getStringBinding("genreObj"));
        studNameText.textProperty().bind(getStringBinding("studName"));
        studAddrText.textProperty().bind(getStringBinding("studAdr"));
    }

    public StringBinding getStringBinding(String key) {
        return new StringBinding() {
            {
                bind(resources);
            }

            @Override
            public String computeValue() {
                return resources.get().getString(key);
            }
        };
    }

    public void setLanguage(Language l){
        this.l = l;
    }
}
