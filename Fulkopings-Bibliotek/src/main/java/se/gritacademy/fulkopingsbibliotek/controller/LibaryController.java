package se.gritacademy.fulkopingsbibliotek.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import se.gritacademy.fulkopingsbibliotek.data.LoanDAO;
import se.gritacademy.fulkopingsbibliotek.data.ReservationDAO;
import se.gritacademy.fulkopingsbibliotek.data.Database;
import se.gritacademy.fulkopingsbibliotek.model.*;
import se.gritacademy.fulkopingsbibliotek.data.MediaDAO;
import se.gritacademy.fulkopingsbibliotek.data.UserDAO;
import se.gritacademy.fulkopingsbibliotek.security.PasswordHashing;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LibaryController {
    //Referens som håller reda på den aktuella inloggade användaren
    private User currentUser;
    //Referens som hanterar scenbytena mellan loginsidan och libarysidan
    private ViewController viewController;
    //Referens som hanterar interaktionen med databasen för Media-objekt
    private MediaDAO mediaDAO;
    //Referens som hanterar interaktionen med databasen för User-objekt
    private UserDAO userDAO;
    //Referens som hanterar interaktionen med databasen för Loan-objekt
    private LoanDAO loanDAO;
    //Referens som hanterar interaktionen med databasen för Reservation-objekt
    private ReservationDAO reservationDAO;

    @FXML
    private ImageView backgroundImage;

    @FXML
    private Button btnActiveLoans;

    @FXML
    private Button btnActiveReservations;

    @FXML
    private Button btnHistory;

    @FXML
    private Button btnLogOut;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnUpdateUser;

    @FXML
    private ComboBox<String> comboSearchFilter;

    @FXML
    private Label lblFulkopingBibliotek;

    @FXML
    private Label lblWelcomeName;

    @FXML
    private AnchorPane paneUser;

    @FXML
    private ScrollPane scrPaneLibary;

    @FXML
    private ScrollPane scrPaneUser;

    @FXML
    private TextField txfSearch;

    @FXML
    private VBox vboxLibary;

    @FXML
    private VBox vboxUser;

    @FXML
    void comboSearchFilter(ActionEvent event) {
    }

    /**
     * Metoden körs automatiskt vid inläsning av användargränssnittet
     * och initierar objekt samt layout
     */
    @FXML
    public void initialize() {
        try {
            initializeDAOs();
            initializeBackgroundImage();
            addComboBoxFilter();
            setCommonVBoxStyle();
        } catch (SQLException e) {
            handleError("Fel vid initialisering", e);
        }
    }

    /**
     * Hämtar och visar användarens aktiva lån eller ett meddelande om att inga aktiva lån finns,
     * hanterar eventuella fel vid hämtning av data
     *
     * @param event - ActionEvent som triggar metoden
     */
    @FXML
    void btnActiveLoans(ActionEvent event) {
        vboxUser.getChildren().clear();
        try {
            List<Loan> activeLoans = getActiveLoans();

            if (activeLoans.isEmpty()) {
                showNoResultsMessage("Inga pågående lån");
            } else {
                for (Loan loan : activeLoans) {
                    displayActiveLoan(loan);
                }
            }
        } catch (SQLException e) {
            handleError("Kunde inte hämta aktiva lån", e);
        }
    }

    /**
     * Hämtar och visar användarens aktiva reservationer eller ett meddelande om att inga aktiva reservationer finns,
     * hanterar eventuella fel vid hämtning av data
     *
     * @param event - ActionEvent som triggar metoden
     */
    @FXML
    void btnActiveReservations(ActionEvent event) {
        vboxUser.getChildren().clear();
        try {
            List<Reservation> activeReservations = getActiveReservations();

            if (activeReservations.isEmpty()) {
                showNoResultsMessage("Inga pågående reservationer");
            } else {
                for (Reservation reservation : activeReservations) {
                    displayReservation(reservation);
                }
            }
        } catch (SQLException e) {
            handleError("Kunde inte hämta aktiva reservationer", e);
        }
    }

    /**
     * Hämtar och visar användarens lånehistorik eller ett meddelande om att ingen historik finns,
     * hanterar eventuella fel vid hämtning av data
     *
     * @param event - ActionEvent som triggar metoden
     */
    @FXML
    void btnHistory(ActionEvent event) {
        vboxUser.getChildren().clear();
        try {
            List<Loan> loanHistory = getLoanHistory();

            if (loanHistory.isEmpty()) {
                showNoResultsMessage("Ingen lånehistorik");
            } else {
                for (Loan loan : loanHistory) {
                    displayLoanHistoryItem(loan);
                }
            }
        } catch (SQLException e) {
            handleError("Kunde inte hämta lånehistorik", e);
        }
    }

    /**
     * Byter scen till startsidan/logga in sidan
     *
     * @param event - ActionEvent som triggar metoden
     */
    @FXML
    void btnLogOut(ActionEvent event) {
        try {
            viewController.changeScene("loginPage.fxml");
        } catch (Exception e) {
            handleError("Kunde inte logga ut", e);
        }
    }

    /**
     * Hanterar användarens inmatning i textfält och användarens val i ComboBoxen,
     * samut utför sökningen och visar matchande Media-objekt eller meddelar användaren att inga result hittades
     *
     * @param event - ActionEvent som triggar metoden
     */
    @FXML
    void btnSearch(ActionEvent event) {
        vboxLibary.getChildren().clear();
        String selectedFilter = comboSearchFilter.getValue();
        String searchText = txfSearch.getText().trim();

        if (!validateInput(selectedFilter, searchText)) {
            return;
        }

        try {
            List<Media> mediaList = searchMediaBasedOnFilter(selectedFilter, searchText);
            if (mediaList.isEmpty()) {
                showAlert("Inga resultat hittades.");
            } else {
                displayMedia(mediaList);
            }
        } catch (SQLException e) {
            handleError("Ett fel uppstod när media hämtades från databasen", e);
        }
    }

    /**
     * Lägger till inmatningsfält som låter användaren ändra sitt namn, sin e-post och sitt lösenord
     *
     * @param event - ActionEvent som triggar metoden
     */
    @FXML
    void btnUpdateUser(ActionEvent event) {
        vboxUser.getChildren().clear();
        addChangeNameField();
        addChangeEmailField();
        addChangePasswordField();
    }

    /**
     * Skapar objekt av alla DAO-klasserna och tilldelar dem databas anslutningen
     *
     * @throws SQLException
     */
    private void initializeDAOs() throws SQLException {
        this.mediaDAO = new MediaDAO(Database.getConnection());
        this.userDAO = new UserDAO(Database.getConnection());
        this.loanDAO = new LoanDAO(Database.getConnection());
        this.reservationDAO = new ReservationDAO(Database.getConnection());
    }

    /**
     * Skapar ett image objekt och sätter bakgrundsbilden
     */
    private void initializeBackgroundImage() {
        Image image = new Image("file:src/main/resources/images/libary2.jpg");
        backgroundImage.setImage(image);
    }

    /**
     * Sätter värdena/filterna i ComboBoxen vid sökrutan
     */
    private void addComboBoxFilter() {
        comboSearchFilter.getItems().addAll(
                "Sök bland allt",
                "Sök titel",
                "Sök författare",
                "Sök genre",
                "Sök ISBN",
                "Visa all media",
                "Visa böcker",
                "Visa tidningar",
                "Visa filmer",
                "Visa CD-skivor"
        );
    }

    /**
     * Sätter styling på VBoxarna
     */
    private void setCommonVBoxStyle() {
        vboxUser.setStyle("-fx-padding: 5px; -fx-background-color: white;");
        vboxLibary.setStyle("-fx-padding: 5px; -fx-background-color: white;");
    }

    /**
     * Hämtar en lista med aktiva lån för den aktuella användaren från databasen
     *
     * @return - lista med hittade Loan-objekt
     * @throws SQLException
     */
    private List<Loan> getActiveLoans() throws SQLException {
        return loanDAO.getActiveLoansByUserId(currentUser.getId());
    }

    /**
     * Visar information om användarens aktiva lån
     *
     * @param loan - Loan-objekt som innehåller information om användarens aktiva lån
     */
    private void displayActiveLoan(Loan loan) {
        String loanInfo = String.format("Lånades: %s\nÅterlämnas senast: %s", loan.getStartDate(), loan.getEndDate());
        createLoanOrReservationBox(loan.getMediaTitle(), loanInfo, "Återlämna", e -> handleReturnLoan(loan));
    }

    /**
     * Hämtar en lista med aktiva reservationer för den aktuella användaren från databasen
     *
     * @return - lista med hittade Reservation-objekt
     * @throws SQLException
     */
    private List<Reservation> getActiveReservations() throws SQLException {
        return reservationDAO.getReservationsByUserId(currentUser.getId());
    }

    /**
     * Visar information om användarens aktiva lån
     *
     * @param reservation - Reservation-objekt som innehåller information om användarens aktiva reservation
     */
    private void displayReservation(Reservation reservation) {
        String reservationInfo = String.format("Reservationen utgår: %s", reservation.getExpiryDate());
        createLoanOrReservationBox(reservation.getMediaTitle(), reservationInfo, "Ta bort reservation", e -> handleDeleteReservation(reservation));
    }

    /**
     * Hämtar en lista med lånehistorik för den aktuella användaren från databasen
     *
     * @return - lista med hittade Loan-objekt
     * @throws SQLException
     */
    private List<Loan> getLoanHistory() throws SQLException {
        return loanDAO.getLoanHistoryByUserId(currentUser.getId());
    }

    /**
     * Visar information om användarens lånehistorik
     *
     * @param loan - Loan-objekt som innehåller information om användarens lånehistorik
     */
    private void displayLoanHistoryItem(Loan loan) {
        String startDate = String.format("Lånades: %s", loan.getStartDate());
        String returnedDate = String.format("Återlämnades: %s", loan.getReturnedDate() != null ? loan.getReturnedDate() : "Ej återlämnad");
        createLoanOrReservationBox(loan.getMediaTitle(), startDate + "\n" + returnedDate, "", null);
    }

    /**
     * Kontrollerar att användaren har valt fyllt i sökfältet till de sökfilter som kräver det,
     * eller har lämnat sökfältsrutan tom om sökfiltret kräver det.
     * Varningsmeddelande i popupruta visas för användaren om något saknas eller är felaktigt
     *
     * @param selectedFilter - Det valda sökfiltret från ComboBoxen
     * @param searchText - Användarens inmatning i sökfältet
     * @return - True om inmatningen är giltig, annars false
     */
    private boolean validateInput(String selectedFilter, String searchText) {
        if (selectedFilter == null) {
            showAlert("Välj ett sökfilter!");
            return false;
        }

        switch (selectedFilter) {
            case "Sök bland allt":
            case "Sök titel":
            case "Sök författare":
            case "Sök ISBN":
            case "Sök genre":
                if (searchText.isEmpty()) {
                    showAlert("Ange ett sökord för att söka!");
                    return false;
                }
                break;
            case "Visa all media":
            case "Visa böcker":
            case "Visa CD-skivor":
            case "Visa filmer":
            case "Visa tidningar":
                if (!searchText.isEmpty()) {
                    showAlert("Töm sökfältet för att visa alla resultat!");
                    return false;
                }
                break;
            default:
                showAlert("Valt filter är inte implementerat!");
                return false;
        }
        return true;
    }

    /**
     * Söker och hämtar Media-objekt från databasen baserat på användarens val av sökfilter och inmatning i sökfältet
     *
     * @param selectedFilter - Det valda sökfiltret från ComboBoxen
     * @param searchText - Användarens inmatning i sökfältet
     * @return - Lista med matchande Media-objekt
     * @throws SQLException
     */
    private List<Media> searchMediaBasedOnFilter(String selectedFilter, String searchText) throws SQLException {
        switch (selectedFilter) {
            case "Sök bland allt":
                return mediaDAO.searchInAllMedia(searchText);
            case "Sök titel":
                return mediaDAO.searchMediaByTitle(searchText);
            case "Sök författare":
                return mediaDAO.searchMediaByAuthor(searchText);
            case "Sök ISBN":
                return mediaDAO.searchMediaByISBN(searchText);
            case "Sök genre":
                return mediaDAO.searchMediaByGenre(searchText);
            case "Visa all media":
                return mediaDAO.getAllMedia();
            case "Visa böcker":
                return mediaDAO.getMediaByType("BOOK");
            case "Visa CD-skivor":
                return mediaDAO.getMediaByType("CD");
            case "Visa filmer":
                return mediaDAO.getMediaByType("MOVIE");
            case "Visa tidningar":
                return mediaDAO.getMediaByType("MAGAZINE");
            default:
                throw new IllegalArgumentException("Okänt filter: " + selectedFilter);
        }
    }

    /**
     * Lägger till ett inmatningsfält för att ändra användarens namn
     * och en Update-knapp för att validera och genomföra ändringen
     */
    private void addChangeNameField() {
        Label lbl = createBoldLabel("Ändra namn:");
        TextField txf = createTextField(currentUser.getName());
        Button btnUpdate = createUserUpdateButton("Uppdatera", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateName(txf.getText().trim());
            }
        });
        addComponentsToVBox(vboxUser, lbl, txf, btnUpdate);
    }

    /**
     * Lägger till ett inmatningsfält för att ändra användarens email
     * och en Update-knapp för att validera och genomföra ändringen
     */
    private void addChangeEmailField() {
        Label lbl = createBoldLabel("Ändra e-post:");
        TextField txf = createTextField(currentUser.getEmail());
        Button btnUpdate = createUserUpdateButton("Uppdatera", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updateEmail(txf.getText().trim());
            }
        });
        addComponentsToVBox(vboxUser, lbl, txf, btnUpdate);
    }

    /**
     * Lägger till ett inmatningsfält för att ändra användarens lösenord
     * och en Update-knapp för att validera och genomföra ändringen
     */
    private void addChangePasswordField() {
        Label lbl = createBoldLabel("Ändra lösenord:");
        PasswordField txf = createPasswordField();
        Button btnUpdate = createUserUpdateButton("Uppdatera", new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                updatePassword(txf.getText().trim());
                txf.setText("");
            }
        });
        addComponentsToVBox(vboxUser, lbl, txf, btnUpdate);
    }

    /**
     * Validerar användarens inmatning av nytt namn och uppdaterar namnet i databasen och User-objektet,
     * och hanterar eventuella fel vid inmatningen/valideringen
     *
     * @param newName - Användarens inmatning i textfältet för nytt namn
     */
    private void updateName(String newName) {
        if (newName.isEmpty()) {
            showAlert("Namnet kan inte vara tomt!");
            return;
        }
        try {
            currentUser.setName(newName);
            userDAO.updateUserName(currentUser.getId(), currentUser.getName());
            lblWelcomeName.setText("Välkommen, " + currentUser.getName());
            showAlert("Namnet har uppdaterats!");
        } catch (SQLException e) {
            handleError("Ett fel inträffade vid uppdatering av namn", e);
        }
    }

    /**
     * Validerar användarens inmatning av ny email och uppdaterar emailen i databasen och User-objektet,
     * och hanterar eventuella fel vid inmatningen/valideringen
     *
     * @param newEmail - Användarens inmatning i textfältet för ny email
     */
    private void updateEmail(String newEmail) {
        if (newEmail.isEmpty()) {
            showAlert("E-postadressen kan inte vara tom!");
            return;
        }
        try {
            if (userDAO.checkUserExist(newEmail)) {
                showAlert("E-postadressen används redan av en annan användare!");
                return;
            }
            currentUser.setEmail(newEmail);
            userDAO.updateUserEmail(currentUser.getId(), currentUser.getEmail());
            showAlert("E-postadressen har uppdaterats!");
        } catch (SQLException e) {
            handleError("Ett fel inträffade vid uppdatering av e-post", e);
        }
    }

    /**
     * Validerar inmatningen, hashar det nya lösenordet och uppdaterar det hashade nya lösenordet i databasen och User-objektet,
     * och hanterar eventuella fel vid inmatningen/valideringen
     *
     * @param newPassword - Användarens inmatning i textfältet för nytt lösenord
     */
    private void updatePassword(String newPassword) {
        if (newPassword.isEmpty()) {
            showAlert("Lösenordet kan inte vara tomt!");
            return;
        }
        try {
            String hashedPassword = PasswordHashing.createHash(newPassword);
            userDAO.updateUserPassword(currentUser.getId(), hashedPassword);
            currentUser.setPassword(hashedPassword);
            showAlert("Lösenordet har uppdaterats!");
        } catch (SQLException e) {
            handleError("Ett fel inträffade vid uppdatering av lösenord", e);
        }
    }

    /**
     * Skapar en fetstilt label samt sätter style för labeln
     *
     * @param text - Beskrivningstext som ska visas i etiketten
     * @return - En fetstilt label med förifylld text och stil
     */
    private Label createBoldLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        return lbl;
    }

    /**
     * Skapar ett textfält samt sätter style för textfältet
     *
     * @param currentValue - Text som ska visas i textfältet
     * @return - Ett textfält med förifylld text och stil
     */
    private TextField createTextField(String currentValue) {
        TextField txf = new TextField(currentValue);
        txf.setStyle("-fx-max-width: 200px;");
        return txf;
    }

    /**
     * Skapar ett textfält av typen PasswordField samt sätter style för textfältet
     *
     * @return - Ett PasswordField med stil
     */
    private PasswordField createPasswordField() {
        PasswordField txf = new PasswordField();
        txf.setStyle("-fx-max-width: 200px;");
        return txf;
    }

    /**
     * Skapar en knapp med tilldelad text och tilldelar en händelsehanterare för klickhändelser
     *
     * @param text - Text som ska visas på knappen
     * @param eventHandler - Händelsehanterare som ska köras när knappen klickas
     * @return - En knapp med tilldelad text och händelsehanterare
     */
    private Button createUserUpdateButton(String text, EventHandler<ActionEvent> eventHandler) {
        Button btnUpdate = new Button(text);
        btnUpdate.setOnAction(eventHandler);
        return btnUpdate;
    }

    /**
     * Lägger till flera objekt till en VBox och sätter avstånd mellan objekten
     *
     * @param vbox - VBox där objekten ska läggas till
     * @param nodes - En eller flera Node-objekt (använts för flexibilitet med vilka typer av objekt) som ska läggas till i VBox
     */
    private void addComponentsToVBox(VBox vbox, Node... nodes) {
        vbox.setSpacing(6);
        vbox.getChildren().addAll(nodes);
    }

    /**
     * Kollar vilken mediatyp objektet är och returnerar objekttypen på svenska
     *
     * @param media - Media-objektet som ska identifiera objekttyp
     * @return - Text med objekttypen på svenska
     */
    private String getMediaTypeString(Media media) {
        if (media.getMediaType() == null) {
            return "Okänd";
        }
        switch (media.getMediaType()) {
            case BOOK:
                return "Bok";
            case MAGAZINE:
                return "Tidning";
            case MOVIE:
                return "Film";
            case CD:
                return "CD";
            default:
                return "Okänd";
        }
    }

    /**
     * Visar en lista av mediaobjekt i vboxLibary och hanterar eventuella fel vid utskriften
     *
     * @param mediaList - Lista med mediaobjekt som ska visas
     */
    private void displayMedia(List<Media> mediaList) {
        vboxLibary.getChildren().clear();

        try {
            for (Media media : mediaList) {
                vboxLibary.getChildren().add(createMediaVBox(media));
            }
        } catch (SQLException e) {
            handleError("Ett fel inträffade vid utskriften av median", e);
        }
    }

    /**
     * Skapar en HBox för varje enskilt Media-objekt, inklusive en bild baserad på mediatyp
     *
     * @param media - Media-objektet som ska visas
     * @return - En HBox som innehåller information och knappar för Media-objektet
     * @throws SQLException
     */
    private HBox createMediaVBox(Media media) throws SQLException {
        Label lblMediaTitle = createMediaTitleLabel(media.getTitle());
        Label lblAboutMedia = createMediaDetailsLabel(media);

        VBox mediaInfoBox = createMediaInfoBox(lblMediaTitle, lblAboutMedia, media);
        ImageView imageView = createConfiguredImageView(media.getMediaType());

        HBox mediaContentBox = new HBox(10);
        mediaContentBox.getChildren().addAll(imageView, mediaInfoBox);
        VBox.setMargin(mediaContentBox, new Insets(5, 0, 5, 0));

        return mediaContentBox;
    }

    /**
     * Skapar en VBox för varje enskilt Media-objekt med titel och information om objektet
     *
     * @param lblTitle - Media-objektets titel
     * @param lblInfo - Media-objektets övriga efterfrågade information (som författare, genre)
     * @param media - Det specifika Media-objektet
     * @return - En VBox som innehåller text med Media-objektets titel och annan information
     * @throws SQLException
     */
    private VBox createMediaInfoBox(Label lblTitle, Label lblInfo, Media media) throws SQLException {
        VBox mediaInfoBox = new VBox(2);
        mediaInfoBox.getChildren().addAll(lblTitle, lblInfo);

        HBox buttonBox = createMediaHBox(media);
        mediaInfoBox.getChildren().add(buttonBox);

        return mediaInfoBox;
    }

    /**
     * Skapar ett ImageView object samt sätter storleken för objektet
     *
     * @param mediaType - Media-objektet mediatyp, för att kunna använda rätt bild
     * @return - En ImageView med en bild beroende på vilken mediatyp Media-objektet är
     */
    private ImageView createConfiguredImageView(MediaType mediaType) {
        ImageView imageView = createMediaImageView(mediaType);
        imageView.setFitHeight(89);
        imageView.setPreserveRatio(true);
        return imageView;
    }

    /**
     * Skapar en ImageView baserat på MediaType
     *
     * @param mediaType - Mediatypen för Media-objektet
     * @return - En ImageView med rätt bild baserad på mediatypen
     */
    private ImageView createMediaImageView(MediaType mediaType) {
        String imagePath = "/images/";

        switch (mediaType) {
            case BOOK:
                imagePath += "book.png";
                break;
            case MAGAZINE:
                imagePath += "magazine.png";
                break;
            case MOVIE:
                imagePath += "movie.png";
                break;
            case CD:
                imagePath += "cd.png";
                break;
            default:
                imagePath += "unknown.png";
                break;
        }

        Image image = new Image(getClass().getResourceAsStream(imagePath));
        return new ImageView(image);
    }

    /**
     * Skapar en Label för Media-objektets titel
     *
     * @param title - Media-objektets titel
     * @return - En Label med Media-objektets titel som text
     */
    private Label createMediaTitleLabel(String title) {
        Label lblMediaTitle = new Label(title);
        lblMediaTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
        return lblMediaTitle;
    }

    /**
     * Skapar en Label som innehåller mer detaljerad information om Media-objektet
     *
     * @param media - Media-objektet vars information ska visas
     * @return - En Label med mer information/egenskaper om Media-objektet som text
     */
    private Label createMediaDetailsLabel(Media media) {
        String mediaType = getMediaTypeString(media);
        Label lblAboutMedia = new Label(String.format("Av: %s | Genre: %s | Typ: %s",
                media.getAuthor(),
                media.getGenre(),
                mediaType));
        lblAboutMedia.setStyle("-fx-font-size: 14px;");
        return lblAboutMedia;
    }

    /**
     * Skapar en HBox som innehåller knappar för att kunna låna/reservera Media-objektet
     *
     * @param media - Media-objektet som visas
     * @return - En HBox med knappar för låning och reservation av Media-objektet
     * @throws SQLException
     */
    private HBox createMediaHBox(Media media) throws SQLException {
        HBox buttonBox = new HBox(10);

        Button btnLoan = createMediaButton(media.isRented() ? "Utlånad" : "Låna", media.isRented());
        btnLoan.setOnAction(event -> handleLoanMedia(media, btnLoan));

        Button btnReserve = createMediaButton(media.isReserved() ? "Reserverad" : "Reservera", media.isReserved());
        configureReserveButton(media, btnReserve);

        buttonBox.getChildren().addAll(btnLoan, btnReserve);
        return buttonBox;
    }

    /**
     * Konfigurerar knappen för att reservera ett Media-objekt, baserat på Media-Objektets låne- & reservations status
     * samt användarens Id (ser till att användaren som lånar boken inte kan reservera den också)
     *
     * @param media - Media-objektet som visas
     * @param btnReserve - Reserveringsknappen för Media-objektet som ska konfigureras
     * @throws SQLException
     */
    private void configureReserveButton(Media media, Button btnReserve) throws SQLException {
        int rentedByUserId = loanDAO.getRentedByUserId(media.getId());

        if (media.isRented() && !media.isReserved() && rentedByUserId != currentUser.getId()) {
            btnReserve.setDisable(false);
            btnReserve.setOnAction(event -> handleReserveMedia(media, btnReserve));
        } else {
            btnReserve.setDisable(true);
        }
    }

    /**
     * Skapar en knapp med en tilldelad text samt kontrollerar om knappen ska vara tryckbar
     *
     * @param text - Förbestämd text om knappen är för att låna eller reservera
     * @param isDisabled - Om boken redan är utlånad/reserverad eller ledig
     * @return - En knapp med tilldelad text som antingen är tryckbar eller inte beroende på Media-objektets tillgänglighet
     */
    private Button createMediaButton(String text, boolean isDisabled) {
        Button button = new Button(text);
        button.setDisable(isDisabled);
        return button;
    }

    /**
     * Skapar en Label som informerar användaren om det inte finns några funna result,
     * sätter stil för Labeln och addar Labeln till vboxUser
     *
     * @param text - Tilldelad text som informerar användaren om där inte finns några lån/resverationer/lånehistorik att visa
     */
    private void showNoResultsMessage(String text) {
        Label noResultLabel = new Label(text);
        noResultLabel.setStyle("-fx-font-size: 14px;");
        vboxUser.getChildren().add(noResultLabel);
    }

    /**
     *Skapar en låne- eller reservationsruta med titel, information och en valfri knapp
     *
     * @param title - Titel för Media-objektet som ska visas
     * @param info - Mer information/egenskaper om Media-objektet som ska visas
     * @param buttonText - Tilldelad text som ska visas på knappen (om texten är null eller tom skapas ingen knapp)
     * @param buttonHandler - Händelsehanterare för knappen (om värdet är null skapas ingen åtgärd vid knapptryckning)
     */
    private void createLoanOrReservationBox(String title, String info, String buttonText, EventHandler<ActionEvent> buttonHandler) {
        Label lblTitle = createBoldLabel(title);
        TextFlow flowInfo = new TextFlow(new Text(info));
        flowInfo.setStyle("-fx-font-size: 14px;");
        VBox vbox = new VBox(lblTitle, flowInfo);
        vbox.setSpacing(2);
        createButtonToBox(buttonText, buttonHandler, vbox);
        vboxUser.getChildren().add(vbox);
        vboxUser.setSpacing(12);
    }

    /**
     * Skapar en knapp om texten och handlern är giltiga och lägger till den i den angivna VBox
     *
     * @param buttonText - Texten som ska visas på knappen
     * @param buttonHandler - Händelsehanterare för knappen
     * @param box - VBox som knappen ska läggas till i
     */
    private void createButtonToBox(String buttonText, EventHandler<ActionEvent> buttonHandler, VBox box) {
        if (buttonText != null && !buttonText.isEmpty()) {
            Button btnAction = new Button(buttonText);
            if (buttonHandler != null) {
                btnAction.setOnAction(buttonHandler);
            }
            box.getChildren().add(btnAction);
        }
    }

    /**
     * Hanterar utlåning av Mediaobjekt och kontrollerar reservationer
     *
     * @param media - Media-objekt som ska lånas ut
     * @param btnLoan - Knappen som styr uthyrningen
     * */
    private void handleLoanMedia(Media media, Button btnLoan) {
        try {
            reservationDAO.deleteExpiredReservations();
            if (media.isReserved()) {
                handleReservedMedia(media);
                return;
            }
            processLoan(media, btnLoan);
        } catch (SQLException e) {
            handleError("Ett fel inträffade vid uthyrning av media",e);
        }
    }

    /**
     * Hanterar logiken för ett Media-objekt redan är reserverat om användare trycker på låneknappen,
     * låter endast användaren som reserverat Media-objektet låna det, annars skrivs ett varningsmeddelande ut
     *
     * @param media - Media-objekt som är reserverat
     */
    private void handleReservedMedia(Media media) {
        try {
            int reservedByUserId = reservationDAO.getReservedByUserId(media.getId());
            if (reservedByUserId != currentUser.getId()) {
                showAlert("Reserverad av en annan användare!");
                return;
            }
            reservationDAO.deleteReservation(media.getId(), currentUser.getId());
            media.setReserved(false);
            mediaDAO.updateMediaReservedStatus(media.getId(), media.isReserved());
        } catch (SQLException e) {
            handleError("Ett fel inträffade vid reservation av media", e);
        }
    }

    /**
     * Hanterar utlåningen av Media-objektet och sätter starttid och sluttid för lånet
     *
     * @param media - Media-objekt som ska lånas
     * @param btnLoan - Knappen som styr utlåningen
     */
    private void processLoan(Media media, Button btnLoan) {
        try {
            int loanDays = media.getMediaType() == MediaType.BOOK ? 30 : 10;
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusDays(loanDays);

            loanDAO.loanMedia(media.getId(), currentUser.getId(), startDate, endDate);

            media.setRented(true);
            media.setLoanByUserId(currentUser.getId());
            updateUIAfterLoan(btnLoan, media);
        } catch (SQLException e) {
            handleError("Ett fel uppstod vid utlåningen", e);
        }
    }

    /**
     * Uppdaterar text på knappen för utlåning och rensar UI efter att Media-objektet har lånats ut
     *
     * @param btnLoan - knappen som styr utlåningen
     * @param media - Media-objekt som har lånats ut
     */
    private void updateUIAfterLoan(Button btnLoan, Media media) {
        btnLoan.setText("Utlånad");
        btnLoan.setDisable(true);

        showAlert("Du har nu lånat " + media.getTitle() + "!");
        vboxUser.getChildren().clear();
    }

    /**
     * Hanterar återlämning av ett lån och kontrollerar om det lånade objektet är reserverat
     *
     * @param loan - Lånet som ska återlämnas
     */
    private void handleReturnLoan(Loan loan) {
        try {
            LocalDate returnDate = LocalDate.now();
            returnLoanToDatabase(loan, returnDate);

            if (mediaDAO.isMediaReserved(loan.getMediaId())) {
                processNextReservation(loan, returnDate);
            }
            updateUIAfterReturn();
        } catch (SQLException e) {
            handleError("Ett fel inträffade vid återlämning av lånet", e);
        }
    }

    /**
     * Avslutar lånet i databasen genom att uppdatera lånet med återlämningsdatum och Media-objektets lånestatus
     *
     * @param loan - Lånet som ska återlämnas
     * @param returnDate - Datumet när lånet avslutades
     */
    private void returnLoanToDatabase(Loan loan, LocalDate returnDate) throws SQLException {
        loanDAO.returnLoan(loan.getId(), returnDate);
        mediaDAO.updateMediaRentedStatus(loan.getMediaId(), false);
    }

    /**
     * Om ett Media-objekt är reserverat vid återlämning,
     * uppdateras reserverings startdatumet till återlämningsdatumet för användaren som reserverat Media-objektet
     * samy uppdateras Media-objektets status till ej utlånad
     *
     * @param loan - Lånet som ska återlämnas
     * @param returnDate - Datumet för återlämningen
     */
    private void processNextReservation(Loan loan, LocalDate returnDate) throws SQLException {
        int nextUserId = reservationDAO.getUserInQueue(loan.getMediaId());
        if (nextUserId != -1) {
            reservationDAO.updateReservationDates(loan.getMediaId(), returnDate);
            mediaDAO.updateMediaRentedStatus(loan.getMediaId(), false);
        }
    }

    /**
     * Uppdaterar användargränssnittet efter återlämning av lån.
     */
    private void updateUIAfterReturn() {
        showAlert("Lånet är återlämnat!");
        btnActiveLoans(new ActionEvent());
        vboxUser.getChildren().clear();
        vboxLibary.getChildren().clear();
    }

    /**
     * Hanterar reservering av ett Media-objekt
     *
     * @param media - Media-objektet som ska reserveras
     * @param btnReserve - Knappen som styr reserveringen
     */
    private void handleReserveMedia(Media media, Button btnReserve) {
        try {
            if (media.isRented()) {
                LocalDate endDate = loanDAO.getEndDateOfLoan(media.getId());
                LocalDate startDate = endDate;
                LocalDate expiryDate = startDate.plusDays(30);

                reserveMedia(media, startDate, expiryDate);
                updateUIAfterReservation(btnReserve, media);

            } else {
                showAlert("Detta media är inte uthyrt och kan inte reserveras!");
            }
        } catch (SQLException e) {
            handleError("Ett fel inträffade vid reservering av media", e);
        }
    }

    /**
     * Reserverar Media-objektet i databasen och sätter Media-objektets status till reserverad
     *
     * @param media - Media-objekt som ska reserveras
     * @param startDate - Startdatum för reservationen
     * @param expiryDate - Slutdatum för reservationen
     */
    private void reserveMedia(Media media, LocalDate startDate, LocalDate expiryDate) throws SQLException {
        reservationDAO.reserveMedia(media.getId(), currentUser.getId(), startDate, expiryDate);
        media.setReserved(true);
        media.setReservedByUserId(currentUser.getId());
    }

    /**
     * Uppdaterar användargränssnittet efter en reservation
     *
     * @param btnReserve - Knappen som styr reserveringen
     * @param media - Media-objekt som har reserverats
     */
    private void updateUIAfterReservation(Button btnReserve, Media media) {
        btnReserve.setText("Reserverad");
        btnReserve.setDisable(true);
        showAlert("Du har nu reserverat " + media.getTitle() + "!");
        vboxUser.getChildren().clear();
    }

    /**
     * Hanterar borttagning av reservation för ett Media-objekt
     *
     * @param reservation - Reservation som ska tas bort
     */
    private void handleDeleteReservation(Reservation reservation) {
        try {
            deleteReservation(reservation);
            updateUIAfterDeleteReservation();

        } catch (SQLException e) {
            handleError("Kunde inte ta bort reservationen", e);
        }
    }

    /**
     * Tar bort reservationen från databasen
     *
     * @param reservation - Reservation som ska tas bort
     */
    private void deleteReservation(Reservation reservation) throws SQLException {
        reservationDAO.deleteReservation(reservation.getMediaId(), currentUser.getId());
        mediaDAO.updateMediaReservedStatus(reservation.getMediaId(), false);
    }

    /**
     * Uppdaterar användargränssnittet efter att reservationen tagits bort
     */
    private void updateUIAfterDeleteReservation() {
        showAlert("Reservationen har tagits bort!");
        btnActiveReservations(new ActionEvent());
        vboxUser.getChildren().clear();
        vboxLibary.getChildren().clear();
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
     * Sätter information om den aktuella användaren och uppdaterar gränssnittet med användarens namn
     *
     * @param user - User-objekt som representerar den inloggade användaren
     */
    public void setUserInformation(User user) {
        this.currentUser = user;
        lblWelcomeName.setText("Välkommen, " + currentUser.getName());
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