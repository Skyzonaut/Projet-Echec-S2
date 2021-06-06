package com.echec.ui;

import com.echec.game.Case;
import com.echec.game.Jeu;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.Optional;

public class Controller{

    @FXML public MenuItem save;
    private EchecApplication echecApplication;
    private Jeu jeu;
    private Stage stage;

    public void initializeController(EchecApplication echecApplication, Jeu jeu, Stage stage) {
        this.echecApplication = echecApplication;
        this.jeu = jeu;
        this.stage = stage;
    }

    public Alert getDifficulteJeu() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Difficulté facile" + " ?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("DIFFICULTÉ");
        alert.setHeaderText("Voulez vous jouer en difficulté facile ?");
        alert.setContentText("La difficulté facile vous permet de revenir sur vos coups");
        return alert;
    }

    public void selectionnerDifficulte() {
        Alert alert = getDifficulteJeu();
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES){
            this.echecApplication.getJeu().setNiveauDeDifficulte(2);
        } else if (result.isPresent() && result.get() == ButtonType.NO) {
            this.echecApplication.getJeu().setNiveauDeDifficulte(1);
        }
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

    public void undo() {
        if (this.echecApplication.getJeu().getNiveauDeDifficulte() == 2) {
            int lastEventClickIdX = this.echecApplication.uiHistorique.getUiEventByIndex(this.echecApplication.uiHistorique.getUiEvents().size()-2).getCaseOrigine().x;
            int lastEventClickIdY = this.echecApplication.uiHistorique.getUiEventByIndex(this.echecApplication.uiHistorique.getUiEvents().size()-2).getCaseOrigine().y;
            String lastEventClickId = String.format("v%d%d", lastEventClickIdX, lastEventClickIdY);
            String targetClickId = this.echecApplication.uiHistorique.getLastUiEvent().getComponentOriginId();
            this.echecApplication.getJeu().undo();
            this.echecApplication.deplacerPieceUI(targetClickId, lastEventClickId);
        }
        else
        {
            PopupWindow popupWindow = new PopupWindow("Impossible de retourner en \narrière en mode difficile", 300, 150);
            popupWindow.display();
        }
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
