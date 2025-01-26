package se.gritacademy.fulkopingsbibliotek;

import javafx.stage.Stage;
import se.gritacademy.fulkopingsbibliotek.controller.ViewController;

public class Application extends javafx.application.Application {

    /**
     * Startar JavaFX-applikationen genom att initialisera en vycontroller och visa inloggningssidan
     *
     * @param stage - Den primära scenen för applikationen
     * @throws Exception
     */
    @Override
    public void start(Stage stage) throws Exception {
        ViewController viewController = new ViewController(stage);
        viewController.start("loginPage.fxml");
    }

    /**
     * Huvudmetod/main som startar JavaFX-applikationen genom att anropa launch-metoden
     *
     * @param args - Kommandoradsargument som kan användas vid körning av programmet
     */
    public static void main(String[] args) {
        launch(args);
    }
}