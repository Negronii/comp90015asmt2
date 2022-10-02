package com.ruiming.comp90015asmt2;

import com.ruiming.comp90015asmt2.Messages.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static com.ruiming.comp90015asmt2.Messages.MessageFactory.*;

public class WhiteBoardController implements Initializable {
    @FXML
    public TextField textInput;

    @FXML
    public Label chatRoomLabel;

    @FXML
    public ListView<String> usersInBoard;

    @FXML
    private Slider slider;

    @FXML
    public Canvas canvas;

    @FXML
    private ComboBox<String> tool;

    @FXML
    private ColorPicker colorPicker;

    @FXML
    private Button button_send;

    @FXML
    private VBox vbox_messages;

    @FXML
    private TextField tf_message;

    @FXML
    private ScrollPane sp_main;

    private int triangleCount = 0;
    private final double[] triangleXs = new double[3];
    private final double[] triangleYs = new double[3];

    private final double[] point = new double[2];

    public static Date date = new Date();

    BufferedWriter bufferedWriter;
    String username;


    public static boolean isManager = false;

    Stage window;

    @FXML
    public void onSavePNG() {
        try {
            Image snapShot = canvas.snapshot(null, null);
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Choose location to save");
            File choseDirectory = directoryChooser.showDialog(window);
            if (choseDirectory == null) return;
            ImageIO.write(SwingFXUtils.fromFXImage(snapShot, null),
                    "png", new File(choseDirectory.getPath() + "/paint.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onExit() {
        Platform.exit();
    }

    @FXML
    public void onOpen() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(window);
        if (selectedFile == null) return;
        BufferedImage image = ImageIO.read(selectedFile);
        Image img = SwingFXUtils.toFXImage(image, null);
        canvas.getGraphicsContext2D().drawImage(img, 0, 0);
    }

    @FXML
    public void onNew() throws IOException {
        canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        writeMsg(bufferedWriter, new ClearPanelMessage(username, date.getTime()));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        GraphicsContext g = canvas.getGraphicsContext2D();
        bufferedWriter = (isManager) ? CreateWhiteBoard.bufferedWriter : JoinWhiteBoard.bufferedWriter;
        username = (isManager) ? CreateWhiteBoard.username : JoinWhiteBoard.username;
        window = (isManager) ? CreateWhiteBoard.window : JoinWhiteBoard.window;
        tool.getItems().addAll("Free-hand", "Eraser", "Line", "Circle", "Triangle", "Rectangle", "Text");
        textInput.setVisible(false);
        tool.getSelectionModel().selectedIndexProperty().addListener((v, oldValue, newValue) -> {
            if (oldValue.equals(6)) {
                textInput.setVisible(false);
            }
            if (newValue.equals(6)) {
                textInput.setVisible(true);
            } else if (newValue.equals(4)) {
                triangleCount = 0;
            }
        });

        window.setOnCloseRequest(e -> {
            onExit();
        });

        canvas.setOnMouseDragged(e -> {
            if (tool.getValue() == null) {
                showAlert("No Tool Alert", "Please enter a tool");
            } else {
                double brushSize = slider.getValue();
                double x = e.getX() - brushSize / 2;
                double y = e.getY() - brushSize / 2;
                if (tool.getValue().equals("Free-hand")) {
                    Color color = colorPicker.getValue();
                    g.setFill(color);
                    g.fillRect(x, y, brushSize, brushSize);
                    try {
                        MessageFactory.writeMsg(bufferedWriter,
                                new DrawRectMessage(username, date.getTime(), x, y, brushSize, brushSize, color));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else if (tool.getValue().equals("Eraser")) {
                    g.clearRect(x, y, brushSize, brushSize);
                    try {
                        MessageFactory.writeMsg(bufferedWriter,
                                new EraseMessage(username, date.getTime(), x, y, brushSize));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
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
                    g.setFill(color);
                    if (tool.getValue().equals("Rectangle")) {
                        g.fillRect(startX, startY, width, height);
                        try {
                            writeMsg(bufferedWriter, new DrawRectMessage(username,
                                    date.getTime(), startX, startY, width, height, color));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                    if (tool.getValue().equals("Circle")) {
                        g.fillOval(startX, startY, width, height);
                        try {
                            writeMsg(bufferedWriter, new DrawCircleMessage(username,
                                    date.getTime(), startX, startY, width, height, color));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }

                } else if (tool.getValue().equals("Line")) {
                    Color color = colorPicker.getValue();
                    double size = slider.getValue();
                    g.setStroke(color);
                    g.setLineWidth(size);
                    g.strokeLine(point[0], point[1], x, y);
                    try {
                        writeMsg(bufferedWriter, new DrawLineMessage(username, date.getTime(), point[0], point[1], x, y, size, color));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else if (tool.getValue().equals("Triangle")) {
                    triangleXs[triangleCount] = x;
                    triangleYs[triangleCount] = y;
                    if (++triangleCount == 3) {
                        Color color = colorPicker.getValue();
                        g.setFill(color);
                        g.fillPolygon(triangleXs, triangleYs, 3);
                        try {
                            writeMsg(bufferedWriter, new DrawTriangleMessage(username, date.getTime(), triangleXs, triangleYs, color));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        triangleCount = 0;
                    }
                } else if (tool.getValue().equals("Text")) {
                    if (textInput.getText().equals("")) {
                        showAlert("No text entered", "Please enter text field besides slider");
                    } else {
                        Color color = colorPicker.getValue();
                        String text = textInput.getText();
                        double size = slider.getValue();
                        g.setFill(color);
                        g.setFont(new Font("Arial", size));
                        g.fillText(text, x, y);
                        try {
                            writeMsg(bufferedWriter, new DrawTextMessage(username, date.getTime(), x, y, text, color, size));
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            }
        });

        vbox_messages.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                sp_main.setVvalue((Double) newValue);
            }
        });

        button_send.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String messageToSend = tf_message.getText();
                if (!messageToSend.isEmpty()) {
                    HBox hBox = new HBox();
                    hBox.setAlignment(Pos.CENTER_RIGHT);
                    hBox.setPadding(new Insets(5, 5, 5, 10));

                    Text text = new Text(messageToSend);
                    TextFlow textFlow = new TextFlow(text);

                    textFlow.setStyle("-fx-color: rgb(239, 242, 255); " +
                            "-fx-background-color: rgb(15, 125, 242); " +
                            "-fx-background-radius: 20px");

                    textFlow.setPadding(new Insets(5, 5, 5, 10));
                    text.setFill(Color.color(0.934, 0.945, 0.996));

                    hBox.getChildren().add((textFlow));
                    vbox_messages.getChildren().add(hBox);

                    try {
                        writeChatMsg(bufferedWriter, messageToSend);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    tf_message.clear();
                }
            }
        });
    }

    public static void addLabel(String messageFromClient, VBox vbox) {
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER_LEFT);
        hBox.setPadding(new Insets(5, 5, 5, 10));

        Text text = new Text(messageFromClient);
        TextFlow textFlow = new TextFlow(text);
        textFlow.setStyle("-fx-background-color: rgb(233, 233, 235); " +
                "-fx-background-radius: 20px");

        textFlow.setPadding(new Insets(5, 10, 5, 10));
        hBox.getChildren().add(textFlow);

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                vbox.getChildren().add(hBox);
            }
        });
    }

    public void showAlert(String title, String message) {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL); //make user deal with alert first
        window.setTitle(title);
        window.setWidth(250);
        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button();
        closeButton.setText("Got it");
        closeButton.setOnAction(e -> window.close()); //window.close() to close the stage
        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.showAndWait(); // wait until user close stage
    }


}