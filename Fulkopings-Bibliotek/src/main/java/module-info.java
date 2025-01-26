module se.gritacademy.fulkopingsbibliotek {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens se.gritacademy.fulkopingsbibliotek to javafx.fxml;
    exports se.gritacademy.fulkopingsbibliotek;
    exports se.gritacademy.fulkopingsbibliotek.controller;
    opens se.gritacademy.fulkopingsbibliotek.controller to javafx.fxml;
}