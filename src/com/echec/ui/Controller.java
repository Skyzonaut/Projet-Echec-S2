package com.echec.ui;

import com.echec.game.Jeu;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;
import java.util.Optional;

/**
 * Controlleur du FXML WindowEchec.fxml. Contenant les fonctions réagissant aux actionListener du modèle
 * de l'interface graphique
 * @see EchecApplication
 * @author yohan
 */
public class Controller{

    /**
     * Chargement de l'item "Save" du menu "Fichier"
     */
    @FXML public MenuItem save;

    /**
     * Vue {@linkplain EchecApplication}
     */
    private EchecApplication echecApplication;

    /**
     * Modèle {@linkplain Jeu}
     */
    private Jeu jeu;

    /**
     * Stage où se trouve l'interface graphique
     */
    private Stage stage;

    /**
     * Fonction invoquée depuis {@linkplain EchecApplication} affichant une fenêtre de paramètres et qui récupérera les valeurs
     * sélectionnées
     * @author yohan
     */
    public void windowParam() {
        try {
            // On charge le loader qui ira récupérer le FXML et ses éléments de controls
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("WindowParam.fxml"));

            // On initialise les conteneurs & background de la fenêtre
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);

            // Titre, ajout à la scène, affichage et mise au premier plan de l'écran
            stage.setTitle("Paramètres de la partie");
            stage.setScene(scene);
            stage.show();
            stage.setAlwaysOnTop(true);

            // On ajoute un onCloseActionListener
            stage.setOnCloseRequest(e -> {

                // Si difficile est sélectionné on met à jour le niveau de difficulté directement via le modèle
                ToggleButton difficile = (ToggleButton) loader.getNamespace().get("difficile");
                if (difficile.isSelected()) Controller.this.echecApplication.getJeu().setNiveauDeDifficulte(1);

                // Si facile est sélectionné on met à jour le niveau de difficulté directement via le modèle
                ToggleButton facile = (ToggleButton) loader.getNamespace().get("facile");
                if (facile.isSelected()) Controller.this.echecApplication.getJeu().setNiveauDeDifficulte(2);

                // Si ia est sélectionné on met à jour le mode de jeu directement via la vue
                ToggleButton ia = (ToggleButton) loader.getNamespace().get("ia");
                if (ia.isSelected()) Controller.this.echecApplication.setMode("ia");

                // Si ia est sélectionné on met à jour le mode de jeu directement via la vue
                ToggleButton pve = (ToggleButton) loader.getNamespace().get("pve");
                if (pve.isSelected()) Controller.this.echecApplication.setMode("pve");

                // Et puis on ferme cette fenêtre
                stage.close();
            });

            // On récupère le bouton "confirmer" de la vue param.
            Button confirmer = (Button) loader.getNamespace().get("confirmer");

            // Et on lui ajoute un actionListener au bouton "confirmer"
            confirmer.setOnAction(e -> {

                // Si difficile est sélectionné on met à jour le niveau de difficulté directement via le modèle
                ToggleButton difficile = (ToggleButton) loader.getNamespace().get("difficile");
                if (difficile.isSelected()) this.echecApplication.getJeu().setNiveauDeDifficulte(1);

                // Si facile est sélectionné on met à jour le niveau de difficulté directement via le modèle
                ToggleButton facile = (ToggleButton) loader.getNamespace().get("facile");
                if (facile.isSelected()) this.echecApplication.getJeu().setNiveauDeDifficulte(2);

                // Si ia est sélectionné on met à jour le mode de jeu directement via la vue
                ToggleButton ia = (ToggleButton) loader.getNamespace().get("ia");
                if (ia.isSelected()) this.echecApplication.setMode("ia");

                // Si ia est sélectionné on met à jour le mode de jeu directement via la vue
                ToggleButton pve = (ToggleButton) loader.getNamespace().get("pve");
                if (pve.isSelected()) this.echecApplication.setMode("pve");

                // Et puis on ferme cette fenêtre
                stage.close();
            });

        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction invoquée depuis {@linkplain EchecApplication} initialisant le controleur avec les vues, modèles et stages
     * courrantes nécessaires à son fonctionnement
     * @param echecApplication {@linkplain EchecApplication}
     * @param jeu {@linkplain Jeu}
     * @param stage {@linkplain Stage}
     * @author yohan
     */
    public void initializeController(EchecApplication echecApplication, Jeu jeu, Stage stage) {
        this.echecApplication = echecApplication;
        this.jeu = jeu;
        this.stage = stage;
    }

    /**
     * Fonction générant une fenêtre d'{@linkplain Alert} demande si l'utilisateur souhaite sauvegarder,
     * et où il sera possible de cliquer sur :
     * <ul>
     *     <li>yes</li>
     *     <li>no</li>
     *     <li>cancel</li>
     * </ul>
     * @param titre <code>String</code> Titre de la fenêtre d'alerte
     * @return {@linkplain Alert}
     * @author yohan
     */
    public Alert confirmCancelDialog(String titre) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Sauvegarder " + " ?", ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        alert.setTitle(titre);
        alert.setHeaderText("Toute avancée non sauvegardée sera écrasée");
        alert.setContentText("Voulez-vous sauvegarder votre partie avant de charger une nouvelle sauvegarde ?");
        return alert;
    }

    /**
     * Fonction générant une fenêtre d'{@linkplain Alert} demande si l'utilisateur souhaite sauvegarder,
     * et gère la réponse, en sauvegardant si décidé par l'utilisateur
     * <ul>
     *     <li>yes</li>
     *     <li>no</li>
     *     <li>cancel</li>
     * </ul>
     * @see Controller#menuItemSaveOnClick()
     * @author yohan
     */
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

    /**
     * Fonction invoquée au clic sur l'item "Nouvelle" du menu "file". Lançant une nouvelle partie
     * @author yohan
     */
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

    /**
     * Fonction invoquée au clic sur l'item "Save" du menu "file". Sauvegardant la partie
     * @author yohan
     */
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

    /**
     * Fonction invoquée au clic sur l'item "Charger" du menu "file". Chargeant une partie.
     * @author yohan
     */
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


    /**
     * Fonction invoquée au clic sur l'item "Oui" du menu "Abandonner". Abandonne la partie et propose d'en lancer une autre
     * @see Jeu#finirPartie() ()
     * @see Controller#confirmOnQuit(WindowEvent)
     * @author yohan
     */
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

    /**
     * Fonction invoquée au clic sur l'item "Help" dans le menu "Help". Mène à une vidéo d'explication du jeu d'échec
     * et de ses règles
     * @author yohan
     */
    @FXML
    public void openHelp() {
        this.echecApplication.openBrowser();
        // https://www.youtube.com/watch?v=dQw4w9WgXcQ
        // https://www.youtube.com/watch?v=fKxG8KjH1Qg
    }

    /**
     * Fonction invoquée au clic sur l'item "Undo" du menu "Help". Revient en arrière sur le dernier coup et son affichage.
     * @see Jeu#undo()
     * @see EchecApplication#deplacerPieceUI(String, String)
     * @author yohan
     */
    @FXML
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
}
