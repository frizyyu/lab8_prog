package interfacesFx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class QuestWindow {//extends Application {
    Stage stage;
    public void createQuestionWindow(Stage stage, String title) throws IOException {
        this.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(QuestWindow.class.getResource("/quest-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 300);
        QuestController QC = fxmlLoader.getController();
        QC.setQW(this);
        this.stage.setTitle(title);
        this.stage.setScene(scene);
        this.stage.setResizable(false);
    }

    public void setMainWindow() throws IOException, ClassNotFoundException {
        MainWindow MW = new MainWindow();
        MW.createMainWindow(stage);
    }
}
