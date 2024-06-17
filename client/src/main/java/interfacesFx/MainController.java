package interfacesFx;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import commandHelpers.CommandInitializator;
import commandHelpers.ExecuteScript;
import helpers.*;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;
import jsonHelper.ReadFromJson;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;
import supportive.MusicBand;
import supportive.MusicGenre;

import java.awt.*;
import javafx.scene.paint.Color;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.PortUnreachableException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import javafx.scene.text.Font;

import javax.swing.plaf.basic.BasicSplitPaneUI;

import static javafx.scene.control.cell.TextFieldTableCell.forTableColumn;

public class MainController {
    public Button exitButton;
    public Button sendButton;
    public TextField commandInput;
    public Label userNameLabel;
    public TextArea infoText;
    public TableColumn<AddToTableView, Long> idObj;
    public TableColumn<AddToTableView, String> nameObj;
    public TableColumn<AddToTableView, Long> coordX;
    public TableColumn<AddToTableView, String> coordY;
    public TableColumn<AddToTableView, Integer> particip;
    public TableColumn<AddToTableView, MusicGenre> genre;
    public TableColumn<AddToTableView, String> studName;
    public TableColumn<AddToTableView, String> studAdr;
    public TableColumn<AddToTableView, Integer> albums;
    public TableView<AddToTableView> table;
    public TableColumn<AddToTableView, String> creationDate;
    public AnchorPane canvas;
    public AnchorPane mainPane;
    //public Button refreshButton;
    public ComboBox languageCB;
    public VBox canvasVbox;
    public Button executeButton;
    public TextField filterText;
    public ComboBox filterColumn;
    Language l;
    public Tab tableTab;
    public Tab canvasTab;
    private Map<Shape, Long> shapeMap;
    private Map<Long, Text> textMap;
    private Random randomGenerator;
    private Shape prevClicked;
    MainWindow MW;
    String command;
    FileLoaderWindowController FLWC;
    ElementWindowController EWC;
    private Map<Long, Color> userColorMap;
    ClientToServer clientToServer = ServerConnector.clientToServer;
    CommandInitializator commandsInitializator = new CommandInitializator(clientToServer.getCollection());
    String[] currCommand;
    Request req;
    AddToTableView selectedRow;
    ArrayList<String> needCrateMbObject = new ArrayList<>();
    AddToTableView attv;
    private Map<String, Locale> localeMap;
    private ObjectProperty<ResourceBundle> resources;

    private Color prevColor;
    boolean shapeclicked = false;
    int canvasClicked = 0;
    boolean needRefreshTable = false;
    boolean sendToCheckUpdates;
    String lastCorrectData = "";
    Thread th;
    HashMap<String, String> elements;
    List<String> elementsInObject;
    public static boolean stopThread = false;
    Pattern pattern;
    Matcher matcher;
    List<String> genres = new ArrayList<>();
    //ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

    public MainController() throws IOException {
        needCrateMbObject.add("add");
        needCrateMbObject.add("update");
        needCrateMbObject.add("add_if_max");
        needCrateMbObject.add("remove_greater");
        needCrateMbObject.add("remove_lower");
    }

    public void initializeP() throws IOException, ClassNotFoundException {
        l = new Language();

        genres.add("RAP");
        genres.add("HIP_HOP");
        genres.add("JAZZ");
        genres.add("POST_PUNK");
        genres.add("BRIT_POP");

        elementsInObject = new ArrayList<>();
        elementsInObject.add("\"id\":");
        elementsInObject.add(",\"name\":\"");
        elementsInObject.add("\"coordinates\":.?\"x\":");
        elementsInObject.add("\"y\":");
        elementsInObject.add("\"creationDate\":\"");
        elementsInObject.add("\"numberOfParticipants\":");
        elementsInObject.add("\"albumsCount\":");
        elementsInObject.add("\"genre\":\"");
        elementsInObject.add("\"studio\":.?\"name\":\"");
        elementsInObject.add("\"address\":\"");

        resources = l.getResources();
        localeMap = new HashMap<>();
        localeMap.put("Deutsch", new Locale("de", "DE"));
        localeMap.put("Svensk", new Locale("sv", "SV"));
        localeMap.put("English (Canada)", new Locale("en", "CA"));
        localeMap.put("Русский", new Locale("ru", "RU"));
        languageCB.setItems(FXCollections.observableArrayList(localeMap.keySet()));
        bindGuiLanguage();
        initializeTable();
        shapeMap = new HashMap<>();
        textMap = new HashMap<>();
        randomGenerator = new Random(1821L);
        userColorMap = new HashMap<>();
        refreshTable();
        table.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                try {
                    updateCell();
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }});
        table.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                try {
                    removeElement();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }});

        canvasVbox.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.DELETE) {
                try {
                    removeElement();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }});
        canvas.setOnMouseClicked(this::shapeClicked);

        mainPane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    onSendClick();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        filterText.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    filter();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        /*filterText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    filter();
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        });*/
        startThread();
        filterColumn.getSelectionModel().select(1);
    }

    public void filter() throws IOException, ClassNotFoundException {
        sendToCheckUpdates = true;
        onSendClick();
        if (!(filterText.getText().length() == 0)) {
            if (!(ClientToServer.correctData.strip().equals("Collection is empty") || ClientToServer.correctData.strip().equals("No matches found"))) {
                String futureCorrectData = "";
                String[] ctss = ClientToServer.correctData.split("\n");
                List<String> r = Arrays.stream(ctss).filter(x ->
                        Pattern.compile(String.format("%s.*%s.*", elements.get(filterColumn.getSelectionModel().getSelectedItem()), filterText.getText()), Pattern.CASE_INSENSITIVE)
                                .matcher(x.substring(x.indexOf(String.format("%s", elements.get(filterColumn.getSelectionModel().getSelectedItem()).replace("\":.?\"", "\":{\"")))).split("\",\"")[0])
                                .find()
                ).toList();
                for (String s: r){
                    futureCorrectData += s;
                    futureCorrectData += "\n";
                }
                if (futureCorrectData.equals(""))
                    futureCorrectData = "Collection is empty";
                ClientToServer.correctData = futureCorrectData.strip();
                refreshTable();
            }
        }
        else{
            sendToCheckUpdates = true;
            onSendClick();
        }

    }

    public void removeElement() throws IOException {
        stopThread = true;
        needRefreshTable = true;
        selectedRow = table.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            commandInput.setText(String.format("remove_by_id %s", selectedRow.getId()));
            onSendClick();
        }
    }

    private void bindGuiLanguage() throws IOException {
        try {
            resources.set(ResourceBundle.getBundle
                    ("bundles.gui", localeMap.get(languageCB.getSelectionModel().getSelectedItem())));
        } catch (Exception ignored) {
            String[] langList = l.readLangFromFile();
            languageCB.getSelectionModel().select(langList[2]);
            resources.set(ResourceBundle.getBundle
                    ("bundles.gui", new Locale(langList[0], langList[1])));
        }
        l.setResources(resources);
        l.saveLanguage(String.format("%s_%s", localeMap.get(languageCB.getSelectionModel().getSelectedItem()), languageCB.getSelectionModel().getSelectedItem()));

        idObj.textProperty().bind(getStringBinding("idObj"));
        nameObj.textProperty().bind(getStringBinding("nameObj"));
        coordX.textProperty().bind(getStringBinding("coordX"));
        coordY.textProperty().bind(getStringBinding("coordY"));
        creationDate.textProperty().bind(getStringBinding("creationDate"));
        particip.textProperty().bind(getStringBinding("particip"));
        albums.textProperty().bind(getStringBinding("albums"));
        genre.textProperty().bind(getStringBinding("genreObj"));
        studName.textProperty().bind(getStringBinding("studName"));
        studAdr.textProperty().bind(getStringBinding("studAdr"));

        tableTab.textProperty().bind(getStringBinding("tableTab"));
        canvasTab.textProperty().bind(getStringBinding("canvasTab"));

        sendButton.textProperty().bind(getStringBinding("sendButton"));
        exitButton.textProperty().bind(getStringBinding("exitButton"));
        //refreshButton.textProperty().bind(getStringBinding("refreshButton"));
        executeButton.textProperty().bind(getStringBinding("executeButton"));
        filterText.promptTextProperty().bind(getStringBinding("filterText"));
        commandInput.promptTextProperty().bind(getStringBinding("commandInput"));
        setFilterBoxElements();
    }
    private void setFilterBoxElements(){
        int c = 0;
        elements = new HashMap<>();
        List<String> columns = new ArrayList<>();
        for (TableColumn tc: table.getColumns()){
            elements.put(tc.getText(), elementsInObject.get(c));
            columns.add(tc.getText());
            c += 1;
        }
        filterColumn.setItems(FXCollections.observableArrayList(columns));
    }
    @FXML
    protected void changeLang() throws IOException, ClassNotFoundException {
        stopThread = true;
        bindGuiLanguage();
        setFilterBoxElements();
        refreshTable();
        filterColumn.getSelectionModel().select(1);
    }

    public void updateCell() throws IOException, InterruptedException {
        stopThread = true;
        selectedRow = table.getSelectionModel().getSelectedItem();
        if (selectedRow != null) {
            MW.createWindowWithElement();
            EWC.id.setText(String.format("%s", selectedRow.getId()));
            EWC.name.setText(String.format("%s", selectedRow.getName()));
            EWC.cX.setText(String.format("%s", selectedRow.getCoordX()));
            EWC.cY.setText(String.format("%s", selectedRow.getCoordY()));
            EWC.crDate.setText(String.format("%s", selectedRow.getCreationDate()));
            EWC.part.setText(String.format("%s", selectedRow.getNumberOfParticipants()));
            EWC.alb.setText(String.format("%s", selectedRow.getAlbumsCount()));
            //EWC.genre.setText(String.format("%s", selectedRow.getGenre()));

            EWC.genreCombo.setItems(FXCollections.observableArrayList(genres));
            EWC.genreCombo.getSelectionModel().select(String.format("%s", selectedRow.getGenre()));

            EWC.sName.setText(String.format("%s", selectedRow.getStudName()));
            EWC.sAddr.setText(String.format("%s", selectedRow.getStudAddr()));
        }
    }
    public void startThread(){
        /*th = new Thread(() -> {
            try {
                while (!stopThread) {
                    ServerConnector.clientToServer.listenForThread();
                    try {
                        if (!lastCorrectData.equals(ClientToServer.correctData)) {
                            lastCorrectData = ClientToServer.correctData;
                            refreshTable();
                        }
                    } catch (NullPointerException | IllegalStateException ignored) {
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

        });
        th.start();*/


        th = new Thread(() -> {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                try {
                    lastCorrectData = ClientToServer.correctData;
                    ClientToServer.correctData = "";
                    clientToServer.send(new Request("show", "", null, CreateUser.userName, "password", null, null));
                } catch (IOException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                if (!ClientToServer.correctData.equals(lastCorrectData)) {
                    lastCorrectData = ClientToServer.correctData;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //onSendClick();
                                refreshTable();
                                filter();
                            } catch (IOException | ClassNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    });
                }
                ClientToServer.response = "";
            }
        });
        th.start();
    }

    public void addNew() throws IOException {
        /*fixedThreadPool.shutdown();
        fixedThreadPool = Executors.newFixedThreadPool(3);*/
        stopThread = true;
        MW.createWindowWithElement();
        EWC.idText.setVisible(false);
        EWC.id.setVisible(false);
        EWC.crDate.setVisible(false);
        EWC.crDateText.setVisible(false);
        EWC.genreCombo.setItems(FXCollections.observableArrayList(genres));
        /*fixedThreadPool.submit(() -> {
            try {
                ServerConnector.clientToServer.listen();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });*/
    }

    public void initializeTable(){
        idObj.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getId()));
        nameObj.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getName()));
        coordX.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getCoordX()));
        coordY.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getCoordY()));
        particip.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getNumberOfParticipants()));
        albums.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getAlbumsCount()));
        genre.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getGenre()));
        studName.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getStudName()));
        studAdr.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getStudAddr()));
        creationDate.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getCreationDate()));
    }

    public void refreshTable() throws IOException, ClassNotFoundException {
        ObservableList<AddToTableView> OL = FXCollections.observableArrayList();
        String corData = getDataForTable();
        if (ClientToServer.correctData.strip().equals("Collection is empty") || ClientToServer.correctData.strip().equals("No matches found")) {
            OL.clear();
            table.setItems(OL);
        } else {
            String[] responseList = corData.split("\n");
            for (int i = 0; i < responseList.length; i++) {
                attv = createAddableElement(responseList[i]);
                OL.add(attv);
            }
            table.setItems(null);
            table.setItems(OL);
            table.refresh();
        }
        refreshCanvas();
    }

    @FXML
    protected void onRefreshClick() throws IOException {
        sendToCheckUpdates = true;
        onSendClick();

    }
    @FXML
    protected void onExecuteClick() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("script files", "*.script")
        );
        File selectedFile = fileChooser.showOpenDialog(MW.stage);
        String filename = selectedFile.getName();
        commandInput.setText(String.format("execute_script %s", filename));
        onSendClick();
    }

    @FXML
    protected void onExitClick(){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure to exit?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.showAndWait();
        if (alert.getResult() == ButtonType.YES) {
            System.exit(0);
        }
    }

    @FXML
    protected void onSendClick() throws IOException {
        //fixedThreadPool.shutdown();
        //fixedThreadPool = Executors.newFixedThreadPool(3);
        stopThread = true;
        if (sendToCheckUpdates) {
            command = "show";
            sendToCheckUpdates = false;
            needRefreshTable = true;
        }
        else {
            command = commandInput.getText();
            commandInput.setText("");
        }
        if (command.length() != 0) {
            try {
                currCommand = command.replaceFirst(" ", "|").split("\\|");
                if (currCommand.length > 1)
                    req = new Request(currCommand[0], currCommand[1], null, CreateUser.userName, "password", null, null);
                else
                    req = new Request(currCommand[0], "", null, CreateUser.userName, "password", null, null);
                if (commandsInitializator.validate(command)) {
                    if (needCrateMbObject.contains(currCommand[0]) && !req.getArgs().contains("-h")) {
                        ClassObjectCreator coc = new ClassObjectCreator(clientToServer.getCollection());
                        MusicBand mb;
                        if (Objects.equals(currCommand[0], "update")) {
                            mb = coc.create(req.getArgs().replaceFirst(" ", "|").split("\\|")[1]);
                            mb = coc.setId(currCommand[1].replaceFirst(" ", "|").split("\\|")[0], mb);
                        } else
                            mb = coc.create(req.getArgs());
                        req.setMbElement(mb);
                        needRefreshTable = true;
                    }
                    if (command.equals("clear") || command.split(" ")[0].equals("filter_by_albums_count") || command.split(" ")[0].equals("filter_contains_name") || command.split(" ")[0].contains("remove_by_id")){
                        needRefreshTable = true;
                    }
                    else if (command.equals("exit"))
                        req.setArgs(String.format("%sexit|%s", CreateUser.userName, ReadFromJson.fileName));
                    if (currCommand[0].equals("execute_script")) {
                        ExecuteScriptReader esr = new ExecuteScriptReader();

                        ExecuteScript es = new ExecuteScript(clientToServer.getCollection(), null);
                        es.execute(esr.read(currCommand[1]), clientToServer, CreateUser.userName, needCrateMbObject);
                        //}
                        req.setArgs(esr.read(currCommand[1]));
                        needRefreshTable = true;
                    }
                    clientToServer.send(req);
                    if (ClientToServer.response.contains("|END|")){
                        if (!(command.split(" ")[0].equals("filter_by_albums_count") || command.split(" ")[0].equals("filter_contains_name") || command.equals("show")))
                            infoText.setText(ClientToServer.response.replace("|END|", ""));
                        else
                            infoText.setText("");
                        if (needRefreshTable){
                            if (command.split(" ")[0].equals("filter_by_albums_count") || command.split(" ")[0].equals("filter_contains_name")){
                                ClientToServer.correctData = ClientToServer.response.replace("|END|", "");
                            }
                            else
                                ClientToServer.correctData = "";
                            ClientToServer.response = "";
                            needRefreshTable = false;
                            refreshTable();
                        }
                        else
                            ClientToServer.response = "";
                    }
                    //infoText.setText(ClientToServer.response);
                    commandsInitializator = new CommandInitializator(clientToServer.getCollection());
                }
                else
                    infoText.setText("Error. Type help to get list of commands\nThe command cannot contain the \"|\" character");
                //}
            } catch (PortUnreachableException portError) {
                infoText.setText("Can not send data to server, check server status and your internet connection.");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        stopThread = false;
        //startThread();
    }

    public void setUserName(String name){
        userNameLabel.setText(name);
    }

    private String getDataForTable() throws IOException, ClassNotFoundException {
        if (Objects.equals(ClientToServer.correctData, "")){
            clientToServer.send(new Request("show", "", null, CreateUser.userName, "password", null, null));
        }
        ClientToServer.response = ClientToServer.response.replace(ClientToServer.correctData, "");
        return ClientToServer.correctData;
    }

    public void setMW(MainWindow MW){
        this.MW = MW;
    }

    public void setEWC(ElementWindowController EWC){
        this.EWC = EWC;
    }

    public void setFLWC(FileLoaderWindowController FLWC){
        this.FLWC = FLWC;
    }

    private AddToTableView createAddableElement(String n){
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeTypeAdapter())
                .create();
        MusicBand myMap = gson.fromJson(n, MusicBand.class);
        AddToTableView attv = new AddToTableView();
        attv.setId(myMap.getId());
        attv.setName(myMap.getName());
        attv.setCoordX(myMap.getCoordinates().getX());

        NumberFormat fd;
        try {
            fd = NumberFormat.getNumberInstance(localeMap.get(languageCB.getSelectionModel().getSelectedItem()));
        } catch (Exception e){
            fd = NumberFormat.getNumberInstance(new Locale("en", "CA"));
        }
        //System.out.println(myMap.getCoordinates().getY());
        Double d = myMap.getCoordinates().getY();
        String out = fd.format(d);

        attv.setCoordY(out);

        DateTimeFormatter f;
        try {
            f = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(localeMap.get(languageCB.getSelectionModel().getSelectedItem()));
        } catch (Exception e){
            f = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(new Locale("en", "CA"));
        }
        ZonedDateTime zdt = myMap.getCreationDate();
        String output = zdt.format(f);

        attv.setCreationDate(output);

        attv.setNumberOfParticipants(myMap.getNumberOfParticipants());
        attv.setAlbumsCount(myMap.getAlbumsCount());
        attv.setGenre(myMap.getGenre());
        attv.setStudName(myMap.getStudio().getName());
        attv.setStudAddr(myMap.getStudio().getAddress());
        return attv;
    }

    private void shapeClicked(MouseEvent event) {
        try {
            Shape shape = (Shape) event.getSource();
            long id = shapeMap.get(shape);
            for (AddToTableView mbElement : table.getItems()) {
                if (mbElement.getId() == id) {
                    table.getSelectionModel().select(mbElement);
                    break;
                }
            }
            if (prevClicked != null) {
                prevClicked.setFill(prevColor);
            }
            prevClicked = shape;
            prevColor = (Color) shape.getFill();
            shape.setFill(prevColor.brighter());
            if (event.getClickCount() == 2) {
                try {
                    updateCell();
                } catch (IOException ignored) {
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            shapeclicked = true;
            canvasClicked = 0;
        } catch (ClassCastException e){
            if (canvasClicked == 1) {
                if (prevClicked != null && !shapeclicked) {
                    prevClicked.setFill(prevColor);
                }
                shapeclicked = false;
                canvasClicked -= 1;
            }
            canvasClicked += 1;
            if (canvasClicked == 2){
                canvasClicked = 0;
            }
        }
    }

    private void refreshCanvas() throws IOException, ClassNotFoundException {
        shapeMap.keySet().forEach(s -> canvas.getChildren().remove(s));
        shapeMap.clear();
        textMap.values().forEach(s -> canvas.getChildren().remove(s));
        textMap.clear();
        canvas.getChildren().clear();
        clientToServer.send(new Request("GET CREATORS", "", null, CreateUser.userName, "password", null, null));
        int c = 0;
        for (List<Long> el: ClientToServer.creators.values()){
            for (AddToTableView mbElement : table.getItems()) {
                if (!userColorMap.containsKey(Long.parseLong(String.valueOf(c)))){
                    int n = 0;
                    while (true){
                        Color col = Color.color(randomGenerator.nextDouble(), randomGenerator.nextDouble(), randomGenerator.nextDouble());
                        if (!userColorMap.containsValue(col) || n > 5000) {
                            userColorMap.put(Long.parseLong(String.valueOf(c)), col);
                            break;
                        }
                        n += 1;
                    }
                }
                if (el.contains(mbElement.getId())) {
                    Color color = userColorMap.get(Long.parseLong(String.valueOf(c)));
                    int width = Math.max(mbElement.getName().length() * 10, 50);
                    int height = 50;
                    Shape rectangle = new Rectangle(width, height, color);
                    rectangle.setOnMouseClicked(this::shapeClicked);
                    //rectangle.setOnKeyPressed(this::shapePressed);
                    rectangle.translateXProperty().bind(canvas.widthProperty().divide(2).add(mbElement.getCoordX()));
                    rectangle.translateYProperty().bind(canvas.heightProperty().divide(2).subtract(Double.parseDouble(mbElement.getCoordY().replace(",", "."))));
                    Text text = new Text(String.format("%s\n%s", mbElement.getName(), mbElement.getId()));
                    text.setOnMouseClicked(rectangle::fireEvent);
                    text.setFont(Font.font((double) height / 4));
                    text.setFill(color.darker());
                    text.translateXProperty().bind(rectangle.translateXProperty().add(width / 8));
                    text.translateYProperty().bind(rectangle.translateYProperty().add(27));

                    canvas.getChildren().add(rectangle);
                    canvas.getChildren().add(text);
                    shapeMap.put(rectangle, mbElement.getId());
                    textMap.put(mbElement.getId(), text);

                    RotateTransition circleAnimation = new RotateTransition(Duration.millis(800), rectangle);
                    RotateTransition textAnimation = new RotateTransition(Duration.millis(800), text);
                    circleAnimation.setFromAngle(0);
                    circleAnimation.setToAngle(360);
                    textAnimation.setToAngle(0);
                    textAnimation.setToAngle(360);
                    circleAnimation.play();
                    textAnimation.play();
                }
            }
        c += 1;
        }
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
