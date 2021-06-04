package com.echec.ui;

import com.echec.game.Case;
import com.echec.game.Evenement;
import com.echec.game.Historique;
import com.echec.game.Jeu;
import com.echec.pieces.Piece;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.net.URL;
import java.util.*;

public class EchecApplication extends Application {

    private Jeu jeu;
    private UiHistorique uiHistorique;
    private FXMLLoader loader;
    private final HashMap<String, Pane> listePane = new HashMap<>();
    private final TreeMap<String, ImageView> listeImageView = new TreeMap<>();
    private TableView tableHistorique;
    private Controller controller;
    private Stage primaryStage;
    private Label labelTour;
    private ArrayList<Pane> paintedCase = new ArrayList<Pane>();
    private ArrayList<Case> listeDeplacementsPossibles;
    private Case caseRoiEnEchec;

    @Override
    public void start(Stage primaryStage) throws Exception{
        jeu = new Jeu();
        uiHistorique = new UiHistorique();
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("WindowEchec.fxml"));
        Parent root = loader.load();

        setPanneauConseil();
        initializeGrid();
        setStyleDamier();

        addPionToGrid();
        initializeHistoriqueTable();
        initializeController();


        Scene scene = new Scene(root);
        scene.getStylesheets().add("com/echec/stylesheets/style.css");


        labelTour = (Label) loader.getNamespace().get("labelTour");
        primaryStage.setOnCloseRequest(event -> controller.confirmOnQuit(event));
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("/icon/icon.jpg"))));
        primaryStage.setTitle("Echec");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void initializeController() {
        this.controller = loader.getController();
        this.controller.initializeController(this, this.jeu, primaryStage);
    }

    public void initializeGrid() {
        EventHandler<MouseEvent> onClickHandler = this::onClick;
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                Pane pane = setPaneIdEchecFriendly(x, y);
                ImageView imgView = setImageViewIdEchecFriendly(x, y);
                imgView.addEventHandler(MouseEvent.MOUSE_CLICKED, onClickHandler);
                this.listePane.put(pane.getId(), pane);
                this.listeImageView.put(imgView.getId(), imgView);
            }
        }
    }

    public Pane setPaneIdEchecFriendly(int x, int y) {
        Pane img = (Pane) loader.getNamespace().get(String.format("p%d%d", x, y));
        img.setId(String.format("p%d%d", x+1, 8-y));
        return img;
    }

    public ImageView setImageViewIdEchecFriendly(int x, int y) {
        ImageView img = (ImageView) loader.getNamespace().get(String.format("v%d%d", x, y));
        img.setId(String.format("v%d%d", x+1, 8-y));
        return img;
    }

    public void setPanneauConseil() {
        ImageView imgView = (ImageView) loader.getNamespace().get("imgView");
        URL file = getClass().getResource("/rules/complete-rules.png");
        Image image = new Image(Objects.requireNonNull(file).toString());
        imgView.setImage(image);
    }

    public void setStyleDamier() {
        for (Map.Entry<String, Pane> entry : this.listePane.entrySet()) {
            int x = Integer.parseInt(entry.getKey().split("")[1]);
            int y = Integer.parseInt(entry.getKey().split("")[2]);
            String couleur = this.jeu.plateau.getGrille().getCase(x, y).couleur;
            if ((couleur.equals("noir"))) this.listePane.get(entry.getKey()).getStyleClass().add("case-black-neutral");
            else this.listePane.get(entry.getKey()).getStyleClass().add("case-white-neutral");
        }
    }

    public void addPionToGrid() {
        for (Map.Entry<String, ImageView> entry : this.listeImageView.entrySet()) {
            int x = Integer.parseInt(entry.getKey().split("")[1]);
            int y = Integer.parseInt(entry.getKey().split("")[2]) ;
            if (this.jeu.plateau.getGrille().getCase(x, y).piece != null) {
                Piece piece = this.jeu.plateau.getGrille().getCase(x, y).piece;
                entry.getValue().setImage(getImageViewPion(piece.getClassePiece().toLowerCase(), piece.getCouleur().toLowerCase()));
            }
        }
    }

    public void viderGrille() {
        for (Map.Entry<String, ImageView> entry : this.listeImageView.entrySet()) {
            entry.getValue().setImage(null);
        }
    }

    public Image getImageViewPion(String classe, String couleur) {
        URL file;
        file = getClass().getResource(String.format("/%s/%s_%s.png", couleur, classe, couleur));
        return new Image(Objects.requireNonNull(file).toExternalForm());
    }

    public void reinitialize() {
        viderGrille();
        addPionToGrid();
        initializeHistoriqueTable();
        initializeController();
        this.jeu.setTour("blanc");
        setLabelTour();
    }

    public void onClick(MouseEvent mouseEvent) {

        ImageView targetImgView = (ImageView) mouseEvent.getTarget();

        int x = Integer.parseInt(targetImgView.getId().split("")[1]);
        int y = Integer.parseInt(targetImgView.getId().split("")[2]);
        Case targetClickCase = this.jeu.plateau.getGrille().getCase(x,y);

        if (this.uiHistorique.isClicked())
        {
            if (this.uiHistorique.getLastUiEvent().hasPiece() && !uiHistorique.getLastUiEvent().getComponentOriginId().equals("CréationUiHistorique"))
            {
                int xD = Integer.parseInt(this.uiHistorique.getLastUiEvent().getComponentOriginId().split("")[1]);
                int yD = Integer.parseInt(this.uiHistorique.getLastUiEvent().getComponentOriginId().split("")[2]);
                Case lastEventClickCase = this.jeu.plateau.getGrille().getCase(xD, yD);

                    if (this.listeDeplacementsPossibles.contains(targetClickCase))
                    {
                        String retour;
                        if (targetClickCase.piece == null)
                        {
                            retour = this.jeu.plateau.deplacerPiece(lastEventClickCase, targetClickCase);
                        }
                        else
                        {
                            retour = this.jeu.plateau.prendrePiece(lastEventClickCase, targetClickCase);
                        }

                        if (retour.equals("ok"))
                        {
                            Case caseRoi = this.jeu.plateau.getGrille().getRoi(targetClickCase.piece.getCouleur());
                            if (this.jeu.plateau.detecterEchec2(caseRoi.piece, caseRoi))
                            {
                                PopupWindow popupWindow = new PopupWindow("Déplacement impossible,\n il mettrait le roi en echec");
                                popupWindow.display();
                                this.jeu.undo();
                            }
                            else
                            {
                                String lastEventClickId = this.uiHistorique.getLastUiEvent().getComponentOriginId();
                                String targetClickId = targetImgView.getId();
                                this.deplacerPieceUI(lastEventClickId, targetClickId);
                                this.updateHistoriqueTable();
                                this.jeu.changerCouleurTour();
                                this.setLabelTour();
                            }
                        }
                        String couleurEnnemie = targetClickCase.piece.getCouleur().equals("noir") ? "blanc" : "noir";
                        caseRoiEnEchec = this.jeu.plateau.getGrille().getRoi(couleurEnnemie);
                        if (this.jeu.plateau.detecterEchec2(caseRoiEnEchec.piece, caseRoiEnEchec))
                        {
                            this.marquerRoi(caseRoiEnEchec);
                            this.jeu.plateau.setEnEchec(true);
                            listeDeplacementsPossibles = this.jeu.plateau.deplacementsPossible(caseRoiEnEchec);

                            if (this.jeu.plateau.isEnEchec() && listeDeplacementsPossibles.size() == 0)
                            {
                                this.perdu();
                            }
                        }
                        else
                        {
                            this.jeu.plateau.setEnEchec(false);
                        }
                    }
                    else
                    {
                        System.out.println("Ce déplacement n'est pas autorisé");
                    }
//                } else {
//                    System.out.println("Ce n'est pas à votre tour");
//                }
            }
            else
            {
                System.out.println("Vous n'avez sélectionné aucune pièces");
            }
            resetMarquageDeplacement();
            this.setOriginalStyle(this.uiHistorique.getLastUiEvent().getComponentOriginId());
        }
        else
        {
            if (this.jeu.plateau.isEnEchec()) {
                if (targetClickCase.equals(this.caseRoiEnEchec)) {
                    listeDeplacementsPossibles = this.jeu.plateau.deplacementsPossible(targetClickCase);
                    marquerDeplacement(listeDeplacementsPossibles);
                }
            }
            else
            {
                this.listePane.get(String.format("p%d%d", x, y)).setStyle("-fx-background-color: #679065");
                if (targetClickCase.piece != null)
                {
                    listeDeplacementsPossibles = this.jeu.plateau.deplacementsPossible(targetClickCase);
                    marquerDeplacement(listeDeplacementsPossibles);
                }
            }
        }
        uiHistorique.addUiEvent(targetImgView.getId(), targetClickCase);
        uiHistorique.clicked();
    }

    public void perdu() {
        PopupWindow popupWindow = new PopupWindow(String.format("Les %ss l'emportent !", this.jeu.getTour()));
        popupWindow.display();

    }

    public void marquerDeplacement(ArrayList<Case> listeDeplacementsPossibles) {
        for (Case c : listeDeplacementsPossibles) {
            if (!c.equals(caseRoiEnEchec)) {
                Pane pane = this.listePane.get(String.format("p%d%d", c.x, c.y));
                pane.setStyle("-fx-background-color: yellow");
                this.paintedCase.add(pane);
            }
        }
    }

    public void resetMarquageDeplacement() {
        for (Pane pane : this.paintedCase) {
            this.setOriginalStyle(pane.getId());
        } this.paintedCase.clear();

    }

    public void marquerRoi(Case roi) {
        this.listePane.get(String.format("p%d%d", roi.x, roi.y)).setStyle("-fx-background-color: #e85353");
    }

    public void demarquerRoi(Case roi) {
        this.setOriginalStyle(String.format("p%d%d", roi.x, roi.y));
    }

    public void setOriginalStyle(String id) {
        int x = Integer.parseInt(id.split("")[1]);
        int y = Integer.parseInt(id.split("")[2]);
        Case c = this.jeu.plateau.getGrille().getCase(x, y);
        if (c.couleur.equalsIgnoreCase("noir")) {
            this.listePane.get(String.format("p%d%d", x, y)).setStyle("-fx-background-color: #686868");
        } else {
            this.listePane.get(String.format("p%d%d", x, y)).setStyle("-fx-background-color: #ffffff");
        }
    }

    public void deplacerPieceUI(String originId, String targetId) {
        Image image = this.listeImageView.get(originId).getImage();
        this.listeImageView.get(targetId).setImage(image);
        this.listeImageView.get(originId).setImage(null);
//        this.jeu.plateau.historique.afficher();
    }

    public void initializeHistoriqueTable() {
        this.tableHistorique = (TableView) loader.getNamespace().get("listeImageView");

        javafx.scene.control.TableColumn<Evenement, String> colonneOrigineHistorique = (javafx.scene.control.TableColumn<Evenement, String>) loader.getNamespace().get("historiqueOriginColumn");

        colonneOrigineHistorique.setCellValueFactory(event -> new SimpleObjectProperty<>(event.getValue().getContenuOrigineEchecNotation()));

        javafx.scene.control.TableColumn<Evenement, String> colonneDestinationHistorique = (javafx.scene.control.TableColumn<Evenement, String>) loader.getNamespace().get("historiqueDestinationColumn");
        colonneDestinationHistorique.setCellValueFactory(event -> new SimpleObjectProperty<>(event.getValue().getContenuDestinationEchecNotation()));
        this.tableHistorique.setSelectionModel(null);

        this.tableHistorique.getItems().setAll(this.jeu.plateau.historique.getDernierEvenement());

        this.tableHistorique.setRowFactory(tableView -> {
            TableRow<Evenement> row = new TableRow<>();
            row.getStyleClass().add("table-row-cell");
            return row;
        });
        this.tableHistorique.getStyleClass().add("table-view");
        this.tableHistorique.getStyleClass().add(".item-column-historic");
    }

    public void updateHistoriqueTable() {
        this.tableHistorique.getItems().add(this.jeu.plateau.historique.getDernierEvenement());
    }

    public void getHistoriqueTableFromSave(Historique historique) {
        for (Map.Entry<Integer, Evenement> entry : historique.getHistorique().entrySet()) {
            this.tableHistorique.getItems().add(entry.getValue());
        }
    }

    public void setLabelTour(){
        if (this.jeu.getTour().equals("blanc")) {
            labelTour.setText("Au tour des blancs");
            labelTour.setStyle("-fx-background-color: white; -fx-text-fill: black");
        } else if (this.jeu.getTour().equals("noir")) {
            labelTour.setText("Au tour des noirs");
            labelTour.setStyle("-fx-background-color: black; -fx-text-fill: white");
        }
    }

    public void openBrowser() {
        getHostServices().showDocument("https://www.youtube.com/watch?v=n1T1knKg_58");
    }

    public static void main(String[] args) {
        launch(args);
    }
}

