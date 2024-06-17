package interfacesFx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import supportive.Coordinates;
import supportive.MusicBand;
import supportive.Studio;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Main window controller.
 */
public class MainWindowController {
    public static final String LOGIN_COMMAND_NAME = "login";
    public static final String REGISTER_COMMAND_NAME = "register";
    public static final String REFRESH_COMMAND_NAME = "refresh";
    public static final String INFO_COMMAND_NAME = "info";
    public static final String ADD_COMMAND_NAME = "add";
    public static final String UPDATE_COMMAND_NAME = "update";
    public static final String REMOVE_COMMAND_NAME = "remove_by_id";
    public static final String CLEAR_COMMAND_NAME = "clear";
    public static final String EXIT_COMMAND_NAME = "exit";
    public static final String ADD_IF_MIN_COMMAND_NAME = "add_if_min";
    public static final String REMOVE_GREATER_COMMAND_NAME = "remove_greater";
    public static final String HISTORY_COMMAND_NAME = "history";
    public static final String SUM_OF_HEALTH_COMMAND_NAME = "sum_of_health";

    private final long RANDOM_SEED = 1821L;
    private final Duration ANIMATION_DURATION = Duration.millis(800);
    private final double MAX_SIZE = 250;

    @FXML
    private TableView<MusicBand> MusicBandTable;
    @FXML
    private TableColumn<MusicBand, Long> idColumn;
    @FXML
    private TableColumn<MusicBand, String> ownerColumn;
    @FXML
    private TableColumn<MusicBand, LocalDateTime> creationDateColumn;
    @FXML
    private TableColumn<MusicBand, String> nameColumn;
    @FXML
    private TableColumn<MusicBand, Integer> annualTurnoverColumn;
    @FXML
    private TableColumn<MusicBand, Long> coordinatesXColumn;
    @FXML
    private TableColumn<MusicBand, Integer> coordinatesYColumn;
    @FXML
    private TableColumn<MusicBand, String> fullNameColumn;
    @FXML
    private TableColumn<MusicBand, Long> employeesCountColumn;
    @FXML
    private TableColumn<MusicBand, Studio> MusicBandTypeColumn;
    @FXML
    private TableColumn<MusicBand, String> addressStreetColumn;
    @FXML
    private TableColumn<MusicBand, Coordinates> addressTownColumn;
    @FXML
    private AnchorPane canvasPane;
    @FXML
    private Tab tableTab;
    @FXML
    private Tab canvasTab;
    @FXML
    private Button infoButton;
    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private Button removeButton;
    @FXML
    private Button clearButton;
    @FXML
    private Button executeScriptButton;
    @FXML
    private Button addIfMinButton;
    @FXML
    private Button removeGreaterButton;
    @FXML
    private Button historyButton;
    @FXML
    private Button sumOfHealthButton;
    @FXML
    private Button refreshButton;
    @FXML
    private Tooltip infoButtonTooltip;
    @FXML
    private Tooltip addButtonTooltip;
    @FXML
    private Tooltip updateButtonTooltip;
    @FXML
    private Tooltip removeButtonTooltip;
    @FXML
    private Tooltip clearButtonTooltip;
    @FXML
    private Tooltip executeScriptButtonTooltip;
    @FXML
    private Tooltip addIfMinButtonTooltip;
    @FXML
    private Tooltip removeGreaterButtonTooltip;
    @FXML
    private Tooltip historyButtonTooltip;
    @FXML
    private Tooltip sumOfHealthButtonTooltip;
    @FXML
    private Tooltip refreshButtonTooltip;
    @FXML
    private ComboBox<String> languageComboBox;
    @FXML
    private Label usernameLabel;
    
    private Stage askStage;
    private Stage primaryStage;
    private FileChooser fileChooser;
    private Map<String, Color> userColorMap;
    private Map<Shape, Long> shapeMap;
    private Map<Long, Text> textMap;
    private Shape prevClicked;
    private Color prevColor;
    private Random randomGenerator;
    private Map<String, Locale> localeMap;

    /**
     * Initialize main window.
     */
    public void initializeP() {
        initializeTable();
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("."));
        userColorMap = new HashMap<>();
        shapeMap = new HashMap<>();
        textMap = new HashMap<>();
        randomGenerator = new Random(RANDOM_SEED);
        localeMap = new HashMap<>();
        localeMap.put("Македонски", new Locale("mk", "MK"));
        localeMap.put("Русский", new Locale("ru", "RU"));
        localeMap.put("Polski", new Locale("pl", "PL"));
        localeMap.put("Español", new Locale("es", "PR"));
        languageComboBox.setItems(FXCollections.observableArrayList(localeMap.keySet()));
    }

    /**
     * Initialize table.
     */
    private void initializeTable() {
          }

    /**
     * Bind gui language.
     */
    private void bindGuiLanguage() {
        }

    /**
     * Refresh button on action.
     */
    @FXML
    public void refreshButtonOnAction() {
        requestAction(REFRESH_COMMAND_NAME);
    }

    /**
     * Info button on action.
     */
    @FXML
    private void infoButtonOnAction() {
        requestAction(INFO_COMMAND_NAME);
    }

    /**
     * Add button on action.
     */
    @FXML
    private void addButtonOnAction() {
            }

    /**
     * Update button on action.
     */
    @FXML
    private void updateButtonOnAction() {


    }

    /**
     * Remove button on action.
     */
    @FXML
    private void removeButtonOnAction() {

    }

    /**
     * Clear button on action.
     */
    @FXML
    private void clearButtonOnAction() {
        requestAction(CLEAR_COMMAND_NAME);
    }

    /**
     * Execute script button on action.
     */
    @FXML
    private void executeScriptButtonOnAction() {

    }

    /**
     * Add if min button on action.
     */
    @FXML
    private void addIfMinButtonOnAction() {
            }

    /**
     * Remove greater button on action.
     */
    @FXML
    private void removeGreaterButtonOnAction() {
          }

    /**
     * History button on action.
     */
    @FXML
    private void historyButtonOnAction() {
        requestAction(HISTORY_COMMAND_NAME);
    }

    /**
     * Sum of health button on action.
     */
    @FXML
    private void sumOfHealthButtonOnAction() {
        requestAction(SUM_OF_HEALTH_COMMAND_NAME);
    }

    /**
     * Request action.
     */
    private void requestAction(String commandName, String commandStringArgument, Serializable commandObjectArgument) {
    }

    /**
     * Binds request action.
     */
    private void requestAction(String commandName) {
            }

    /**
     * Refreshes canvas.
     */
    private void refreshCanvas() {
        
    }

    /**
     * Shape on mouse clicked.
     */
    private void shapeOnMouseClicked(MouseEvent event) {
    }

    public void setClient() {
    }

    public void setUsername(String username) {
        usernameLabel.setText(username);
    }

    public void setAskStage(Stage askStage) {
        this.askStage = askStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public void setAskWindowController() {
    }

    public void initLangs() {
    }
}