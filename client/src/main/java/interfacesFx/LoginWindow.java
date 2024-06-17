package interfacesFx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginWindow extends Application {

    FXMLLoader fxmlLoader = new FXMLLoader(LoginWindow.class.getResource("/loginregister-view.fxml"));
    Parent loader;
    Scene scene;
    Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        loader = fxmlLoader.load();
        scene = new Scene(loader, 800, 500);
        LoginAndRegisterController LARC = fxmlLoader.getController();
        LARC.setLW(this);
        LARC.initialize();
        LARC.initLanguages();
        //fxmlLoader.setLocation(LoginWindow.class.getResource("/loginregister-view.fxml"));
        stage.setTitle("Login/Register");
        stage.setScene(scene);
        stage.setResizable(false);
        this.stage = stage;
        stage.show();
    }

    public void loginWindowLaunch(){
        launch();
    }
    public void loginWindowClose() throws Exception {
        Stage st = (Stage) scene.getWindow();
        st.hide();
    }

    public LoginAndRegisterController getLWController(){
        return fxmlLoader.getController();
    }

    public void setMainScene() throws IOException, ClassNotFoundException {
        MainWindow MW = new MainWindow();
        MW.createMainWindow(stage);
    }
}
