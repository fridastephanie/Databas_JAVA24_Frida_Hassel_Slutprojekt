package se.gritacademy.fulkopingsbibliotek.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import se.gritacademy.fulkopingsbibliotek.data.Database;
import se.gritacademy.fulkopingsbibliotek.model.User;
import se.gritacademy.fulkopingsbibliotek.data.UserDAO;
import se.gritacademy.fulkopingsbibliotek.security.PasswordHashing;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginController {
    //Referens som hanterar scenbytena mellan loginsidan och libarysidan
    private ViewController viewController;
    //Referens som hanterar interaktionen med databasen för User-objekt
    private UserDAO userDAO;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private Button btnChooseLogIn;

    @FXML
    private Button btnChooseNewUser;

    @FXML
    private Button btnCreateUser;

    @FXML
    private Button btnLogIn;

    @FXML
    private Label lblEmailLogIn;

    @FXML
    private Label lblEmailNewUser;

    @FXML
    private Label lblFulkopingBibliotek;

    @FXML
    private Label lblNameNewUser;

    @FXML
    private Label lblPasswordLogIn;

    @FXML
    private Label lblPasswordNewUser;

    @FXML
    private AnchorPane paneLogIn;

    @FXML
    private AnchorPane paneNewUser;

    @FXML
    private PasswordField pswLogIn;

    @FXML
    private PasswordField pswNewUser;

    @FXML
    private TextField txfEmailLogIn;

    @FXML
    private TextField txfEmailNewUser;

    @FXML
    private TextField txfNameNewUser;

    @FXML
    public void initialize() {
        Image image = new Image("file:src/main/resources/images/libary.jpg");
        backgroundImage.setImage(image);
    }

    /**
     * Gör inloggningspanelen synlig och tömmer textfälten samt döljer panelen för att skapa nytt konto,
     * samt uppdaterar knappens tryckbarhet för att förhindra att båda panelerna visas samtidigt
     *
     * @param event - ActionEvent som triggar metoden
     */
    @FXML
    void btnChooseLogIn(ActionEvent event) {
        paneLogIn.setVisible(true);
        btnChooseLogIn.setDisable(true);
        paneNewUser.setVisible(false);
        btnChooseNewUser.setDisable(false);
        txfNameNewUser.setText("");
        txfEmailNewUser.setText("");
        pswNewUser.setText("");
    }

    /**
     * Gör "skapa nytt konto"-panelen synlig och tömmer textfälten samt döljer inloggningspanelen,
     * samt uppdaterar knappens tryckbarhet för att förhindra att båda panelerna visas samtidigt
     *
     * @param event - ActionEvent som triggar metoden
     */
    @FXML
    void btnChooseNewUser(ActionEvent event) {
        paneNewUser.setVisible(true);
        btnChooseNewUser.setDisable(true);
        paneLogIn.setVisible(false);
        btnChooseLogIn.setDisable(false);
        txfEmailLogIn.setText("");
        pswLogIn.setText("");
    }

    /**
     * Hanterar att skapa en ny användare genom att ta emot inmatade värden,
     * validera dem och skapa ett konto
     *
     * @param event - ActionEvent som triggar metoden
     */
    @FXML
    void btnCreateUser(ActionEvent event) {
        try {
            String name = txfNameNewUser.getText();
            String email = txfEmailNewUser.getText();
            String password = pswNewUser.getText();

            if (!validateCreateUserInput(name, email, password)) {
                return;
            }

            createUser(name, email, password);
            clearFields();
            btnChooseLogIn(new ActionEvent());
        } catch (Exception e) {
            handleError("Ett fel inträffade vid skapandet av kontot", e);
        }
    }

    /**
     * Hanterar inloggning av användaren genom att validera inmatningen och autentisera användaren
     *
     * @param event - ActionEvent som triggar metoden
     */
    @FXML
    void btnLogIn(ActionEvent event) {
        try {
            String email = txfEmailLogIn.getText();
            String password = pswLogIn.getText();

            if (email.isEmpty() || password.isEmpty()) {
                showAlert("Alla fält måste fyllas i!");
                return;
            }

            User user = validateLoginUser(email, password);

            if (user != null) {
                loginUser(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ett oväntat fel inträffade.");
        }
    }

    /**
     * Konstuktor för LoginController som skapar en databas anslutning
     * och initialiserar ett UserDAO-objekt för att integrera med användardata
     */
    public LoginController() {
        try {
            Connection connection = Database.getConnection();
            this.userDAO = new UserDAO(connection);
        } catch (SQLException e) {
            handleError("Kunde inte ansluta till databasen", e);
        }
    }

    /**
     * Validerar användarens inmatning för att säkerställa att inga fält är tomma
     * och att e-postadressen inte redan finns registrerad i databasen
     *
     * @param name - Användarens inmatade namn
     * @param email - Användarens inmatade e-postadress
     * @param password - Användarens inmatade lösenord
     * @return - True, om alla fält är ifyllda och e-postadressen är inte registrerad, annars False
     */
    private boolean validateCreateUserInput(String name, String email, String password) {
        try {
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                showAlert("Alla fält måste fyllas i!");
                return false;
            }

            if (!email.matches("^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
                showAlert("Ogiltigt e-postadressformat!");
                return false;
            }

            if (userDAO.checkUserExist(email)) {
                showAlert("E-postadressen är redan registrerad till ett konto!");
                return false;
            }
        } catch (Exception e) {
            handleError("Ett fel inträffade vid skapandet av kontot", e);
        }
        return true;
    }

    /**
     * Skapar en ny användare, hashar lösenord och sparar användaren i databasen
     *
     * @param name - Användarens inmatade namn
     * @param email - Användarens inmatade e-postadress
     * @param password - Användarens inmatade lösenord
     */
    private void createUser(String name, String email, String password) {
        try {
            String hashedPassword = PasswordHashing.createHash(password);
            User newUser = new User(name, email, hashedPassword);
            userDAO.createUser(newUser);
            showAlert("Konto skapades, du kan nu logga in!");
        } catch (Exception e) {
            handleError("Ett fel inträffade vid skapandet av kontot", e);
        }
    }

    /**
     * Rensar inmatningsfält
     */
    private void clearFields() {
        txfNameNewUser.clear();
        txfEmailNewUser.clear();
        pswNewUser.clear();
    }

    /**
     * Hämtar användaren från databasen baserat på e-postadress och om e-postadressen finns,
     * valideras det inmatade lösenordet med lösenordet i databasen
     *
     * @param email - Användarens inmatade email
     * @param password - Användarens inmatade lösenord
     * @return - User-objekt om autentiseringen lyckas, annars null
     */
    private User validateLoginUser(String email, String password) {
        try {
            User user = userDAO.getUserByEmail(email);
            if (user == null) {
                showAlert("Användaren finns inte!");
                return null;
            }

            String storedPassword = user.getPassword();
            if (!PasswordHashing.verifyPassword(password, storedPassword)) {
                showAlert("Fel lösenord!");
                return null;
            }
            return user;
        } catch (Exception e) {
            handleError("Ett fel uppstod vid validering av email och lösenord", e);
            return null;
        }
    }

    /**
     * Loggar in användaren och byter scen till bibliotekssidan
     *
     * @param user - Användaren som loggas in
     */
    private void loginUser(User user) {
        try {
            viewController.changeScene("libaryPage.fxml");
            LibaryController libaryController = viewController.getLibaryController();
            libaryController.setUserInformation(user);
        } catch (Exception e) {
            handleError("Ett fel uppstod vid inloggningen", e);
        }
    }

    /**
     * Popup-ruta med ett förbestämt informationsmeddelande till användaren
     *
     * @param message - Informationsmeddelandet som ska visas för användaren
     */
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Hanterar fel genom att logga undantaget och visa ett felmeddelande för användaren
     *
     * @param message - Meddelande som ska visas för användaren vid ett fel
     * @param e - Det undantag (Exception) som inträffade, vilket loggas via printStackTrace()
     */
    private void handleError(String message, Exception e) {
        e.printStackTrace();
        showAlert(message);
    }

    /**
     *Sätter den aktuella ViewController-instansen
     *
     * @param viewController - ViewController-objekt som hanterar visning och användargränssnitt
     */
    public void setViewController(ViewController viewController) {
        this.viewController = viewController;
    }
}