package interfacesFx;

import com.sun.tools.javac.Main;
import helpers.ClientToServer;
import helpers.CreateUser;
import helpers.Language;
import helpers.Request;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class FileLoaderWindowController {
    public Button loadButton;
    public TextField fileText;
    public Label logLabel;
    public AnchorPane mainPane;

    MainController MC;
    Language l = new Language();
    Stage stage;
    boolean createOrLoadNew = false;
    String lastName = null;
    private ObjectProperty<ResourceBundle> resources = new SimpleObjectProperty<>();

    @FXML
    protected void loadButtonClick() throws IOException, ClassNotFoundException, InterruptedException {
        logLabel.textProperty().unbind();
        if (!createOrLoadNew)
            MC.clientToServer.send(new Request("CONNECTCLIENT", fileText.getText(), null, CreateUser.userName, "password", null, null));
        else {
            createOrLoadNew = false;
            ClientToServer.response = "";
            if (lastName != null && lastName.equals(fileText.getText())){ //если имена одинаковые, то мы создаём файл. если разные, то пытаемся найти такой файл
                //создание|CREATEFILE
                MC.clientToServer.send(new Request("CONNECTCLIENT", String.format("%s|CREATEFILE", fileText.getText()), null, CreateUser.userName, "password", null, null));
                ClientToServer.response = "";
                MC.initializeP();
                MC.sendButton.setDisable(false);
                MC.commandInput.setDisable(false);
                stage.close();
            }
            else
                MC.clientToServer.send(new Request("CONNECTCLIENT", fileText.getText(), null, CreateUser.userName, "password", null, null));
        }
        if (!ClientToServer.response.contains("|NEEDCREATE")){
            ClientToServer.response = "";
            MC.initializeP();
            MC.sendButton.setDisable(false);
            MC.commandInput.setDisable(false);
            stage.close();
        }
        else {
            createOrLoadNew = true;
            logLabel.textProperty().bind(getStringBinding("notFoundFile"));
            //logLabel.setText("File not found\nClick load to create and load file or change filename and click to load another");
        }
        lastName = fileText.getText();
    }

    public void setMC(MainController MC){
        this.MC = MC;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }

    public void initialize(){
        mainPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    loadButtonClick();
                } catch (IOException | ClassNotFoundException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void initLanguages() throws IOException {
        String[] langList = l.readLangFromFile();
        resources.set(ResourceBundle.getBundle
                ("bundles.gui", new Locale(langList[0], langList[1])));
        fileText.promptTextProperty().bind(getStringBinding("fileName"));
        loadButton.textProperty().bind(getStringBinding("loadButton"));
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
}
