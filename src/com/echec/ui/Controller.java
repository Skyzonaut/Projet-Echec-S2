package com.echec.ui;

import com.echec.game.Jeu;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.swing.*;
import java.util.Optional;

public class Controller{

    @FXML public MenuItem save;
    private EchecApplication echecApplication;
    private Jeu jeu;
    private FileChooser.ExtensionFilter File;
    private Stage stage;

    public void initializeController(EchecApplication echecApplication, Jeu jeu, Stage stage) {
        this.echecApplication = echecApplication;
        this.jeu = jeu;
        this.stage = stage;
    }

    public Alert confirmCancelDialog(String titre) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sauvegarder " + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle(titre);
        alert.setHeaderText("Toutes avancée non sauvegardée sera écrasée");
        alert.setContentText("Voulez-vous sauvegarder votre partie avant de charger une nouvelle sauvegarde ?");
        return alert;
    }

    @FXML
    public void menuItemNewGameOnClick() {
        Alert alert = confirmCancelDialog("Nouvelle partie");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES){
            System.out.println("paizeoiaze");
            this.menuItemSaveOnClick();
            this.jeu.plateau.init();
            this.echecApplication.reinitialize();
        } else if (result.isPresent() && result.get() == ButtonType.NO) {
            this.jeu.plateau.init();
            this.echecApplication.reinitialize();
        }
    }

    @FXML
    public void menuItemSaveOnClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sauvegarder");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Json Files", "*.json"));
        java.io.File selectedFile = fileChooser.showSaveDialog(this.stage);

        if (selectedFile != null) {
            this.jeu.saveFromUi(selectedFile);
        }
    }

    @FXML
    public void menuItemChargerOnClick() {
        Alert alert = confirmCancelDialog("Chargement");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.NO) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chargement");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Json Files", "*.json"));
            java.io.File selectedFile = fileChooser.showOpenDialog(this.stage);

            if (selectedFile != null) {
                this.jeu.chargerJeuFromFile(selectedFile);
                this.echecApplication.reinitialize();
                this.echecApplication.getHistoriqueTableFromSave(this.jeu.plateau.historique);
            }
        } else if (result.isPresent() && result.get() == ButtonType.YES) {
            this.menuItemSaveOnClick();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Chargement");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Json Files", "*.json"));
            java.io.File selectedFile = fileChooser.showOpenDialog(this.stage);

            if (selectedFile != null) {
                this.jeu.chargerJeuFromFile(selectedFile);
                this.echecApplication.reinitialize();
                this.echecApplication.getHistoriqueTableFromSave(this.jeu.plateau.historique);
            }
        }
    }

    public void confirmOnQuit(WindowEvent event) {
        Alert alert = confirmCancelDialog("Quitter");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            this.menuItemSaveOnClick();
            Platform.exit();
            System.exit(0);
        } else if (result.isPresent() && result.get() == ButtonType.CANCEL) {
            event.consume();
        } else if (result.isPresent() && result.get() == ButtonType.NO) {
            Platform.exit();
            System.exit(0);
        }
    }

    @FXML
    public void openHelp() {
        this.echecApplication.openBrowser();
        // https://www.youtube.com/watch?v=fKxG8KjH1Qg
    }

    @FXML
    public void abandonner() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez vous en lancer une nouvelle ?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Abandon");
        alert.setHeaderText("Vous allez abandonner votre partie");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            this.menuItemNewGameOnClick();
        } else {
            Platform.exit();
            System.exit(0);
        }
    }
}
