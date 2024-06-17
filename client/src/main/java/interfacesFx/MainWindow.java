package interfacesFx;

import helpers.CreateUser;
import helpers.ElementValidCheck;
import helpers.ServerConnector;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;

public class MainWindow {//extends Application {
    /*@Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainWindow.class.getResource("/main-window-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1500, 900);
        stage.setTitle("Main window");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public void mainWindowLaunch(){
        launch();
    }*/
    Stage stage;
    MainController MC;
    public void createMainWindow(Stage stage) throws IOException, ClassNotFoundException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainWindow.class.getResource("/main-window-view.fxml"));
        Scene sceneMain = new Scene(fxmlLoader.load(), 981, 785);

        MC = fxmlLoader.getController();
        MC.setMW(this);
        MC.setUserName(CreateUser.userName);
        MC.sendButton.setDisable(true);
        MC.commandInput.setDisable(true);
        this.stage.setTitle("Main window");
        this.stage.setScene(sceneMain);
        this.stage.setResizable(false);
        createLoadFileWindow();
        //MC.initializeP();
    }

    public void setExitQuestion() throws IOException {
        QuestWindow QW = new QuestWindow();
        QW.createQuestionWindow(stage, "Exit window");
    }

    public void createWindowWithElement() throws IOException {
        Stage secondStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainWindow.class.getResource("/element-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 414, 493);
        ElementWindowController EWC = fxmlLoader.getController();
        MC.setEWC(EWC);
        EWC.setLanguage(MC.l);
        EWC.initLanguages();
        EWC.setMC(MC);
        EWC.setStage(secondStage);
        secondStage.setTitle("Element window");
        secondStage.setScene(scene);
        secondStage.setResizable(false);
        secondStage.show();
    }

    public void createLoadFileWindow() throws IOException {
        Stage secondStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(MainWindow.class.getResource("/file-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 493, 245);
        FileLoaderWindowController FLWC = fxmlLoader.getController();
        MC.setFLWC(FLWC);
        FLWC.setMC(MC);
        FLWC.initialize();
        //FLWC.setLanguage(MC.l);
        FLWC.initLanguages();
        FLWC.setStage(secondStage);
        secondStage.setTitle("Load file window");
        secondStage.setScene(scene);
        secondStage.setResizable(false);
        secondStage.show();
    }
}
