package se.gritacademy.fulkopingsbibliotek.controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class ViewController {
    //Referens till Stage (huvudfönstret) som utför scenbyten
    private final Stage primaryStage;
    //Referens som hanterar logiken och interaktionen för bibliotekssidan
    private LibaryController libaryController;

    /**
     * Konstruktor för ViewController som tar emot Stage (huvudfönstret)
     * och sätter det till referensen för att kunna hantera scenbyten
     *
     * @param stage  - Huvudfönstret som används för att visa och byta mellan vyer
     */
    public ViewController(Stage stage) {
        this.primaryStage = stage;
    }

    /**
     * Startar applikationen genom att byta till den första vyn samt sätter storlek och fönstertext för fönstret
     *
     * @param firstView - Namnet vyn som ska visas först när applikationen startar
     * @throws IOException
     */
    public void start(String firstView) throws IOException {
        changeScene(firstView);
        primaryStage.setTitle("Fulköpings bibliotek");
        primaryStage.setWidth(1090);
        primaryStage.setHeight(730);
        primaryStage.show();
    }

    /**
     * Byter scen baserat på den angivna FXML-filen
     *
     * @param fxmlFile - FXML-filen som ska laddas och visas
     * @throws IOException
     */
    public void changeScene(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource(fxmlFile));
        Parent root = loader.load();
        Object controller = loader.getController();
        setControllerReference(controller);
        primaryStage.setScene(new Scene(root));
    }

    /**
     * Sätter Controllerreferensen beroende på vilken Controller-klass som är kopplad till FXML-filen
     *
     * @param controller - Den kopplade Controller-klassen
     */
    private void setControllerReference(Object controller) {
        if (controller instanceof LoginController) {
            ((LoginController) controller).setViewController(this);
        } else if (controller instanceof LibaryController) {
            this.libaryController = (LibaryController) controller;
            this.libaryController.setViewController(this);
        }
    }

    /**
     * Hämtar referensen till den laddade LibraryController för att få tillgång till
     * logiken för bibliotekssidan vid instansiering
     *
     * @return LibaryController-objekt som hanterar logiken för bibliotekssidan
     */
    public LibaryController getLibaryController() {
        return this.libaryController;
    }
}