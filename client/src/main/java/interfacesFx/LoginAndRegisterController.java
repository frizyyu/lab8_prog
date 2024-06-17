package interfacesFx;

import helpers.*;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import validators.ClearValidator;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.PortUnreachableException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.*;

public class LoginAndRegisterController {
    @FXML
    public Label wrongData;
    public Button enter;
    public PasswordField confirmPassword;
    public Button register;
    public VBox vbox;
    public Button continueButton;
    public Text helloText;
    @FXML
    private TextField login;
    @FXML
    private PasswordField pswd;
    private ObjectProperty<ResourceBundle> resources = new SimpleObjectProperty<>();
    Language l = new Language();

    LoginWindow LW;
    //private final ServerConnector serverConnector = new ServerConnector();
    //ClientToServer clientToServer = ServerConnector.clientToServer;
    ClientToServer clientToServer = null;

    @FXML
    protected void onLoginButtonClick() throws IOException, ClassNotFoundException, NoSuchAlgorithmException {
        try {
            //System.out.println(clientToServer);
            if (enter.isDisabled()) {
                loginOrRegister("CHECKUSEREXIST");
            } else {
                if (!pswd.getText().equals(confirmPassword.getText())) {
                    wrongData.textProperty().bind(getStringBinding("notEquals"));
                } else
                    loginOrRegister("REGISTERUSER");
            }
        } catch (PortUnreachableException e){
            wrongData.setText("Server is offline. Try later");
        }
    }

    @FXML
    protected void onRegisterClick(){
        confirmPassword.setOpacity(1.0);
        enter.setDisable(false);
        register.setDisable(true);
    }
    @FXML
    protected void onEnterClick(){
        confirmPassword.setOpacity(0.0);
        enter.setDisable(true);
        register.setDisable(false);
    }

    public void initialize(){
        vbox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    if (register.isDisabled()) {
                            loginOrRegister("REGISTERUSER");
                    }
                    else
                        loginOrRegister("CHECKUSEREXIST");
                } catch (IOException | NoSuchAlgorithmException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    void loginOrRegister(String command) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        wrongData.textProperty().unbind();
        wrongData.setText("");
        PasswordHasher ph = new PasswordHasher(pswd.getText());
        CreateUser.userName = login.getText();
        CreateUser.pswd = ph.hashing();
        if (CreateUser.userName.length() >= 40)
            wrongData.textProperty().bind(getStringBinding("longLogin"));
            //wrongData.setText("Login lenght must be less than 40 charachters");
        else if (CreateUser.userName.contains("|"))
            wrongData.setText("Invalid char \"|\"");
        else if (pswd.getText().length() < 6)
            wrongData.textProperty().bind(getStringBinding("shortPassword"));
            //wrongData.setText("Password lenght mustn`t be less than 6 charachters");
        else {
            try {
                if (clientToServer == null) {
                    ServerConnector serverConnector = new ServerConnector();
                    serverConnector.createConnection();
                    clientToServer = ServerConnector.clientToServer;
                }
                clientToServer.send(new Request(command, CreateUser.pswd, null, CreateUser.userName, "password", null, null)); // "password" -> CreateUser.pswd, просто по ощущениям, если так паролем раскидываться ирл, то украдут
                if (CreateUser.userName.equals("|")){
                    wrongData.textProperty().bind(getStringBinding("notFound"));
                    //wrongData.setText("User not found. Check your username and password");
                }
                else {
                    LW.setMainScene();
                }
            } catch (Exception e) {
                System.out.println(Arrays.toString(e.getStackTrace()));
                wrongData.setText("Server is offline. Try later");
            }
        }
        //System.out.println(login.getText());
        //System.out.println(pswd.getText()); //проверка данных и закрытие окна
    }
    public void initLanguages() throws IOException {
        String[] langList = l.readLangFromFile();
        resources.set(ResourceBundle.getBundle
                ("bundles.gui", new Locale(langList[0], langList[1])));
        login.promptTextProperty().bind(getStringBinding("loginField"));
        pswd.promptTextProperty().bind(getStringBinding("pswdField"));
        confirmPassword.promptTextProperty().bind(getStringBinding("confirmPswd"));
        enter.textProperty().bind(getStringBinding("enterButton"));
        register.textProperty().bind(getStringBinding("registerButton"));
        continueButton.textProperty().bind(getStringBinding("loginButton"));
        helloText.textProperty().bind(getStringBinding("text"));
    }
    public void setLW(LoginWindow LW){
        this.LW = LW;
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