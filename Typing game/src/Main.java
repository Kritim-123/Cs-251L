import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application {


    public static void main(String[] args) {


        launch(args);



    }

    /**
     * Setups up all the JavaFX GUI controls and creates instances of
     * all the helper classes.
     * @param primaryStage the primary stage for this application, onto which
     * the application scene can be set.
     * Applications may create other stages, if needed, but they will not be
     * primary stages.
     * The speed lets you choose who fast the word will spawn
     * The duration will let you how many cycle will the words go in circle before getting removed.
     * @throws Exception
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Always make sure to set the title of the window
        primaryStage.setTitle("Key Shooter");
        // Width/height variables so that we can mess with the size of the window
        double width = 600;
        double height = 600;
        // BorderPane (https://openjfx.io/javadoc/18/javafx.graphics/javafx/scene/layout/BorderPane.html)
        // Provides the basis which we basis the rest of the GUI on
        BorderPane window = new BorderPane();
        // VBox for the top part of the GUI
        VBox topVBox = new VBox(5);
        topVBox.setAlignment(Pos.CENTER);

        // Label which displays the score
        Label scoreLabel = new Label("0");
        scoreLabel.setFont(new Font(40));

        // Label which displays the currently typed letters
        Label typedLabel = new Label();

        Font font = Font.font("Arial", FontWeight.BOLD, FontPosture.ITALIC, 20);

        typedLabel.setFont(font);


        // --------------speed of word------------


        ComboBox<Integer> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(1, 2, 3, 4, 5);
        comboBox.setValue(1);

        AtomicInteger selectedValue = new AtomicInteger(1);
        
        comboBox.setOnAction(event -> {
            selectedValue.set(comboBox.getValue());


        });


        Label timeOfWord = new Label("Speed");
        timeOfWord.setFont(new Font(15));


        //----------Duration button-------


        ComboBox<Integer> comboBoxDuration = new ComboBox<>();
        comboBoxDuration.getItems().addAll(1, 2, 3, 4);
        comboBoxDuration.setValue(1);

        AtomicInteger selectedValueDuration = new AtomicInteger(1);

        comboBoxDuration.setOnAction(event -> {
            selectedValueDuration.set(comboBoxDuration.getValue());
        });


        Label durationOfWord = new Label("Duration");
        durationOfWord.setFont(new Font(15));
        VBox DurationBox = new VBox(durationOfWord, comboBoxDuration);

        durationOfWord.setLayoutY(50);
        durationOfWord.setLayoutX(50);



        VBox timeBox = new VBox(timeOfWord, comboBox, DurationBox, comboBoxDuration);
        window.setLeft(timeBox);




        //-----------------------------------



        // Add them all to the VBox
        topVBox.getChildren().addAll(scoreLabel, typedLabel);
        // Put them in the top of the BorderPane
        window.setTop(topVBox);



        // Create an instance of our helper Words class


        Words words = new Words("../docs/words.txt", width-40, ((height * 3) / 6)-25,
                                scoreLabel, typedLabel);
        // Put it in the middle of the BorderPane

        window.setCenter(words.getWordsPane());


        // Create a VBox for the keyboard
        VBox keyBoardWindow = new VBox(10);

        // Create an instance of our helper class Keyboard
        Keyboard keyboard = new Keyboard(width, height / 4, 10);
        // Add a horizontal line above the keyboard to create clear seperation
        keyBoardWindow.getChildren().addAll(new Separator(Orientation.HORIZONTAL), keyboard.getKeyboard());
        // Put it in the bottom of the BorderPane
        window.setBottom(keyBoardWindow);
        // Create the scene
        Scene scene = new Scene(window, width, height);
        // The scene is the best place to capture keyboard input
        // First get the KeyCode of the event
        // Then start the fill transition, which blinks the key
        // Then add it to the typed letters
        scene.setOnKeyPressed(event -> {
            KeyCode keyCode = event.getCode();
            keyboard.startFillTransition(keyCode);
            words.addTypedLetter(keyCode);
            words.checkForCorrectWord();

        });
        // Set the scene
        primaryStage.setScene(scene);
        // Showtime!
        primaryStage.show();
        // We also need an AnimationTimer to create words on the
        // screen every 3 seconds. This is done by call createWord
        // from the Words class.



        AnimationTimer timer = new AnimationTimer() {


            private long lastTime = 0;


            @Override
            public void handle(long currentTime) {
                if (lastTime == 0 || currentTime - lastTime >= (1_000_000_000L) * (6- selectedValue.get())) {
                    lastTime = currentTime;
                    words.createWord(selectedValueDuration.get());

                }

            }

        };

        timer.start();
        long startTime = System.currentTimeMillis();

        // End button to stop the game

        Button endButton = new Button("END GAME");
        endButton.setLayoutX(width - 150);
        words.getWordsPane().getChildren().add(endButton);


        endButton.setOnAction(event -> {
            long stopTime = System.currentTimeMillis();
            timer.stop();
            words.endGame(startTime, stopTime);

        });

    }
}
