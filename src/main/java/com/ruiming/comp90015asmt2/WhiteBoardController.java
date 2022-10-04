package com.ruiming.comp90015asmt2;

import com.ruiming.comp90015asmt2.Messages.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

import static com.ruiming.comp90015asmt2.Messages.MessageFactory.writeMsg;

public class WhiteBoardController implements Initializable {
    @FXML
    public TextField textInput;

    @FXML
    public ScrollPane sp_user;

    @FXML
    public ScrollPane sp_chat;

    @FXML
    public VBox vbox_user;

    @FXML
    public VBox vbox_chat;

    @FXML
    public TextArea textEntered;

    @FXML
    private Slider slider;

    @FXML
    public Canvas canvas;

    @FXML
    private ComboBox<String> tool;

    @FXML
    private ColorPicker colorPicker;

    private int triangleCount = 0;
    private final double[] triangleXs = new double[3];
    private final double[] triangleYs = new double[3];

    boolean newPoint = true;
    private final double[] point = new double[2];

    public static Date date = new Date();

    BufferedWriter bufferedWriter;
    String username;


    public static boolean isManager = false;

    Stage window;

    /**
     * open a directory chooser to store png file
     */
    @FXML
    public void onSavePNG() {
        final File[] choseDirectory = new File[1];
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Save As PNG File");

        // first line
        Label text = new Label("Please enter the filename and choose directory to save");

        // second line
        Label filenameLabel = new Label("Filename: ");
        TextField textField = new TextField("paint");
        textField.setPrefSize(140, 26);
        HBox secondLine = new HBox(filenameLabel, textField);
        secondLine.setAlignment(Pos.CENTER);
        secondLine.setPrefSize(360, 40);
        secondLine.setAlignment(Pos.CENTER);
        HBox.setMargin(filenameLabel, new Insets(0, 10, 0, 0));

        // third line
        Label directory = new Label("Directory: ");
        directory.setPrefSize(60, 17);
        Button directoryButton = new Button("Select ...");
        directoryButton.setPrefSize(140, 26);
        directoryButton.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose location to save");
            choseDirectory[0] = directoryChooser.showDialog(window);
            if (choseDirectory[0] != null)
                directoryButton.setText(choseDirectory[0].getAbsolutePath());
        });
        HBox thirdLine = new HBox(directory, directoryButton);
        thirdLine.setPrefWidth(360);
        thirdLine.setAlignment(Pos.CENTER);
        HBox.setMargin(directory, new Insets(0, 8, 0, 0));

        // last line
        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            if (choseDirectory[0] == null)
                showAlert("No directory selected", "please selected a directory");
            else if (textField.getText().isEmpty()) {
                showAlert("No filename entered", "Please enter a filename");
            } else {
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(canvas.snapshot(null, null), null),
                            "png", new File(choseDirectory[0].getPath() + "/" + textField.getText() + ".png"));
                    window.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        saveButton.setOnMouseEntered(e -> saveButton.setEffect(new DropShadow()));
        saveButton.setOnMouseExited(e -> saveButton.setEffect(null));
        saveButton.setStyle("-fx-text-fill: white; -fx-background-color: #3A86FF;");

        // integrate
        VBox layout = new VBox(text, secondLine, thirdLine, saveButton);
        VBox.setMargin(saveButton, new Insets(10, 0, 0 ,0));
        layout.setAlignment(Pos.CENTER);
        layout.setPrefSize(330, 160);
        Scene scene = new Scene(layout);
        window.setWidth(330);
        window.setHeight(160);
        window.setScene(scene);
        window.showAndWait();
    }

    /**
     * store png file to project directory
     */
    @FXML
    public void onSave() {
        try {
            Image snapShot = canvas.snapshot(null, null);
            File file = new File("paint.png");
            ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null),
                    "png", file);
            showAlert("Saved", "File has been saved to " + file.getAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * exit platform
     */
    @FXML
    public void onExit() {
        Platform.exit();
    }

    /**
     * open an image file from file chooser
     *
     * @throws IOException cannot open
     */
    @FXML
    public void onOpen() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files",
                "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile == null) return;
        Image img = SwingFXUtils.toFXImage(ImageIO.read(selectedFile), null);
        canvas.getGraphicsContext2D().drawImage(img, 0, 0);
        writeMsg(bufferedWriter, new ImageMessage(username, img));
    }

    /**
     * open an image file from file chooser
     *
     * @throws IOException cannot open
     */
    @FXML
    public void onOpenSaved() throws IOException {
        File selectedFile = new File("paint.png");
        if (!selectedFile.exists()) {
            showAlert("Local saved not found", "No paint.png found");
        } else {
            Image img = SwingFXUtils.toFXImage(ImageIO.read(selectedFile), null);
            canvas.getGraphicsContext2D().drawImage(img, 0, 0);
            writeMsg(bufferedWriter, new ImageMessage(username, img));
        }
    }

    /**
     * clear the canvas
     */
    @FXML
    public void onNew() {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        writeMsg(bufferedWriter, new ClearPanelMessage(username));
    }

    /**
     * set on set actions to fxml
     *
     * @param url            no use
     * @param resourceBundle no use
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // identify role and set up writer, username and window
        bufferedWriter = (isManager) ? CreateWhiteBoard.bufferedWriter : JoinWhiteBoard.bufferedWriter;
        username = (isManager) ? CreateWhiteBoard.username : JoinWhiteBoard.username;
        window = (isManager) ? CreateWhiteBoard.window : JoinWhiteBoard.window;
        tool.getItems().addAll("Free-hand", "Eraser", "Line", "Circle", "Triangle", "Rectangle", "Text");

        // text input should not appear when selecting other tools
        textInput.setVisible(false);

        // user list and chat list
        vbox_user.heightProperty().addListener((v, oldValue, newValue) -> sp_user.setVvalue((double) newValue));
        vbox_chat.heightProperty().addListener((v, oldValue, newValue) -> sp_chat.setVvalue((double) newValue));

        textEntered.setOnKeyPressed(e -> {
            if (e.getCode().equals(KeyCode.ENTER)) {
                String toSend = textEntered.getText();
                StringBuilder stringBuilder = new StringBuilder();
                for (char c : toSend.toCharArray())
                    if (c != '\n')
                        stringBuilder.append(c);
                toSend = stringBuilder.toString();
                if (!toSend.isEmpty() && !toSend.equals("\n")) {
                    writeMsg(bufferedWriter, new ChatMessage(username, toSend));
                    textEntered.clear();
                }
            }
        });

        tool.getSelectionModel().selectedIndexProperty().addListener((v, oldValue, newValue) -> {
            newPoint = true;
            Arrays.fill(point, 0);
            triangleCount = 0;
            if (oldValue.equals(6)) {
                textInput.setVisible(false);
            }
            if (newValue.equals(6)) {
                textInput.setVisible(true);
            }
        });

        window.setOnCloseRequest(e -> onExit());

        canvas.setOnMouseDragged(e -> {
            if (tool.getValue() == null) {
                showAlert("No Tool Alert", "Please enter a tool");
            } else {
                double brushSize = slider.getValue();
                double x = e.getX() - brushSize / 2;
                double y = e.getY() - brushSize / 2;
                if (tool.getValue().equals("Free-hand")) {
                    if (newPoint)
                        newPoint = false;
                    else {
                        Color color = colorPicker.getValue();
                        double size = slider.getValue();
                        writeMsg(bufferedWriter, new DrawLineMessage(username, point[0],
                                point[1], x, y, size, color));
                    }
                    point[0] = x;
                    point[1] = y;
                } else if (tool.getValue().equals("Eraser")) {
                    writeMsg(bufferedWriter, new EraseMessage(username, x, y, brushSize));
                }
            }
        });

        canvas.setOnMousePressed(e -> {
            point[0] = e.getX();
            point[1] = e.getY();
        });

        canvas.setOnMouseReleased(e -> {
            if (tool.getValue() == null) {
                showAlert("No Tool Alert", "Please enter a tool");
            } else {
                double x = e.getX();
                double y = e.getY();
                if (tool.getValue().equals("Rectangle") || tool.getValue().equals("Circle")) {
                    double width = Math.abs(x - point[0]);
                    double height = Math.abs(y - point[1]);
                    double startX = Math.min(point[0], x);
                    double startY = Math.min(point[1], y);
                    Color color = colorPicker.getValue();
                    if (tool.getValue().equals("Rectangle")) {
                        writeMsg(bufferedWriter, new DrawRectMessage(username,
                                startX, startY, width, height, color));
                    }
                    if (tool.getValue().equals("Circle")) {
                        writeMsg(bufferedWriter, new DrawCircleMessage(username,
                                startX, startY, width, height, color));
                    }

                } else if (tool.getValue().equals("Line")) {
                    Color color = colorPicker.getValue();
                    double size = slider.getValue();
                    writeMsg(bufferedWriter, new DrawLineMessage(username, point[0],
                            point[1], x, y, size, color));
                } else if (tool.getValue().equals("Triangle")) {
                    triangleXs[triangleCount] = x;
                    triangleYs[triangleCount] = y;
                    if (++triangleCount == 3) {
                        writeMsg(bufferedWriter, new DrawTriangleMessage(username,
                                triangleXs, triangleYs, colorPicker.getValue()));
                        triangleCount = 0;
                    }
                } else if (tool.getValue().equals("Text")) {
                    if (textInput.getText().equals("")) {
                        showAlert("No text entered", "Please enter text field besides slider");
                    } else {
                        writeMsg(bufferedWriter, new DrawTextMessage(username, x, y,
                                textInput.getText(), colorPicker.getValue(), slider.getValue()));
                    }
                } else if (tool.getValue().equals("Free-hand")) {
                    Arrays.fill(point, 0);
                    newPoint = true;
                }
            }
        });
    }

    public void showAlert(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); //make user deal with alert first
        window.setTitle(title);
        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button();
        closeButton.setText("Got it");
        closeButton.setOnAction(e -> window.close()); //window.close() to close the stage
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setWidth(300);
        window.setHeight(100);
        window.setScene(scene);
        window.showAndWait(); // wait until user close stage
    }

    public void removeUser(String username) {
        Node tobeDelete = null;
        for (Node node : vbox_user.getChildren())
            if (node instanceof HBox hBox)
                for (Node node1 : hBox.getChildren())
                    if (node1 instanceof TextFlow textFlow)
                        for (Node node2 : textFlow.getChildren())
                            if (node2 instanceof Text text)
                                if (text.getText().equals(username))
                                    tobeDelete = node;
        vbox_user.getChildren().remove(tobeDelete);
    }

    public void managerLetJoinWindow(Message message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); //make manager deal with alert first
        window.setTitle("new User Join Request");
        window.setWidth(250);
        window.setHeight(150);
        Label label = new Label();
        label.setText(message.sender + " want to join the white board");
        Button acceptButton = new Button();
        acceptButton.setText("Accept");
        acceptButton.setOnAction(e -> {
            writeMsg(bufferedWriter, new ApprovalRequestMessage(username, message.sender));
            window.close();
        });
        Button closeButton = new Button();
        closeButton.setText("Refuse");
        closeButton.setOnAction(e -> {
            writeMsg(bufferedWriter, new RefuseRequestMessage(username, message.sender));
            window.close();
        });
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, acceptButton, closeButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait();
    }
}