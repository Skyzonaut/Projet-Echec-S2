package com.echec.ui;

import com.echec.game.*;
import com.echec.pieces.Piece;
import javafx.application.Application;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.net.URL;
import java.util.*;

/**
 * Classe principale du projet d'Echec, sert aussi bien de main, que de Vue que de Controller.
 * @see Jeu
 * @see PlateauDeJeu
 * @see Grille
 * @see Controller
 * @author yohan
 */
public class EchecApplication extends Application {

    /**
     * Jeu représentant le modèle, et centralisteur principal des différentes classes et fonctions
     */
    private Jeu jeu;

    /**
     * Historique des évènements liés à l'interface graphique
     */
    public UiHistorique uiHistorique;

    /**
     * FMXLLoarder permettant de récupérer des éléments depuis le fichier FMXL
     */
    private FXMLLoader loader;

    /**
     * Map contenant les Pane et leur identifiant pour qu'elles soient plus facilement accessibles
     */
    private final HashMap<String, Pane> listePane = new HashMap<>();

    /**
     * Map contenant les ImageView et leur identifiant pour qu'elles soient plus facilement accessibles
     */
    private final TreeMap<String, ImageView> listeImageView = new TreeMap<>();

    /**
     * Table graphique qui contiendra l'historique de l'interface utilisateur
     */
    private TableView tableHistorique;

    /**
     * Controlleur du modèle FXML
     */
    private Controller controller;

    /**
     * Stage principale
     */
    private Stage primaryStage;

    /**
     * Label contenant l'indication sur le tour en cours
     */
    private Label labelTour;

    /**
     * Liste contenant toutes les Pane coloriées pour montrer à l'utilisateur les déplacements possibles
     */
    private ArrayList<Pane> paintedPane = new ArrayList<Pane>();

    /**
     * Liste contenant toutes les Pane coloriées pour montrer à l'utilisateur les potentiels sauveurs d'un roi lors
     * d'un echec
     * @see Echec
     */
    private ArrayList<Pane> paintedPaneSauveur = new ArrayList<Pane>();

    /**
     * Pane contenant le roi colorié lors d'un echec pour montrer l'echec à l'utilisateur
     */
    private Pane paintedPaneRoi;

    /**
     * Liste contenant les déplacements possibles du tour
     */
    private ArrayList<Case> listeDeplacementsPossibles;

    /**
     * Liste contenant les cases contenants les sauveurs d'un potentiel échec
     */
    private ArrayList<Case> sauveurs = new ArrayList<>();

    /**
     * Case contenant le roi mis en Echec
     */
    private Case caseRoiEnEchec;

    /**
     * Mode de jeu
     * <ul>
     *     <li>ia : Contre une IA Jihadise et depréssivo suicidaire</li>
     *     <li>pvp : Contre soi-même ou quelqu'un d'autre en local </li>
     * </ul>
     */
    private String mode = "";

    @Override
    /**
     * Main de l'Application
     * @author yohan
     */
    public void start(Stage primaryStage) throws Exception{

        // On créé un nouveau jeu
        jeu = new Jeu();
        // Et son historique
        uiHistorique = new UiHistorique();

        // On charge le loader et récupère le modèle FXML
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("WindowEchec.fxml"));
        Parent root = loader.load();

        // On initialise le controlleur pour lui donner les vues et modèles dont il a besoin
        initializeController();
        // On affiche la fenêtre de sélection des paramètres
        controller.windowParam();

        // on ajoute l'image au panneau d'aide
        setPanneauConseil();
        // On initialise les id des panneaux, et leurs EventHandler
        initializeGrid();
        // On colorie la grille en Damier
        setStyleDamier();

        // On ajoute les pions à la grille
        addPionToGrid();
        // Et on initialise la table graphique qui contiendra l'historique
        initializeHistoriqueTable();

        // On créé uine nouvelle scène
        Scene scene = new Scene(root);
        // On lui applique son style
        scene.getStylesheets().add("com/echec/stylesheets/style.css");

        // On charge le panneau d'affichage des tours
        labelTour = (Label) loader.getNamespace().get("labelTour");

        // On gère le onQuitHandler, on met une icone, un titre, on upgrade la scene, on la passe en static
        // ET ENFIN on l'affiche
        primaryStage.setOnCloseRequest(event -> controller.confirmOnQuit(event));
        primaryStage.getIcons().add(new Image(String.valueOf(getClass().getResource("/icon/icon.jpg"))));
        primaryStage.setTitle("Echec");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * Fonction liant les vues et modèles au controlleur
     * @see Jeu
     * @see Controller
     * @author yohan
     */
    public void initializeController() {
        this.controller = loader.getController();
        this.controller.initializeController(this, this.jeu, primaryStage);
    }

    /**
     * Fonction attribuant des identifiants aux panneaux, correspondant aux coordonnées utlisées dans les echecs.
     * Commeçant de 1 à 8 là ou les grilleLayout de JavaFx commencent de 0 à 7 <br>
     * En plus de ça on ajoute également le principal onClickHander à chacune des cases
     * @author yohan
     */
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

    /**
     * Fonction retournant un identifiant aux panneaux au format 1 - 8 à partir d'un format 0 - 7, pour qu'il soit employable par les
     * coordonnées d'echiquiers standards.
     * @param x Coordonnée x de la Pane
     * @param y Coordonnée y de la Pane
     * @return Un identifiant correspondant au bon format
     * @author yohan
     */
    public Pane setPaneIdEchecFriendly(int x, int y) {
        Pane img = (Pane) loader.getNamespace().get(String.format("p%d%d", x, y));
        img.setId(String.format("p%d%d", x+1, 8-y));
        return img;
    }

    /**
     * Fonction retournant un identifiant aux ImageView au format 1 - 8 à partir d'un format 0 - 7, pour qu'il soit employable par les
     * coordonnées d'echiquiers standards.
     * @param x Coordonnée x de l'ImageView
     * @param y Coordonnée y de l'ImageView
     * @return Un identifiant correspondant à la bonne ImageView
     * @author yohan
     */
    public ImageView setImageViewIdEchecFriendly(int x, int y) {
        ImageView img = (ImageView) loader.getNamespace().get(String.format("v%d%d", x, y));
        img.setId(String.format("v%d%d", x+1, 8-y));
        return img;
    }

    /**
     * Fonction chargeant le panneau d'aide, une image, et lui affichant dedans.
     * @author yohan
     */
    public void setPanneauConseil() {
        ImageView imgView = (ImageView) loader.getNamespace().get("imgView");
        URL file = getClass().getResource("/rules/complete-rules.png");
        Image image = new Image(Objects.requireNonNull(file).toString());
        imgView.setImage(image);
    }

    /**
     * Fonction coloriant la grille en damier
     * @see Grille
     * @author yohan
     */
    public void setStyleDamier() {
        for (Map.Entry<String, Pane> entry : this.listePane.entrySet()) {
            int x = Integer.parseInt(entry.getKey().split("")[1]);
            int y = Integer.parseInt(entry.getKey().split("")[2]);
            String couleur = this.jeu.plateau.getGrille().getCase(x, y).couleur;
            if ((couleur.equals("noir"))) this.listePane.get(entry.getKey()).getStyleClass().add("case-black-neutral");
            else this.listePane.get(entry.getKey()).getStyleClass().add("case-white-neutral");
        }
    }

    /**
     * Fonction ajoutant à chaque ImageView, une image correspondant à la pièce dans la grille de même coordonnées.
     * Dans les faits cette fonction affiche les pions sur le plateau.
     * @see PlateauDeJeu
     * @see Grille
     * @author yohan
     */
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

    /**
     * Fonction retirant toutes les images de la grille
     * @see PlateauDeJeu
     * @see Grille
     * @author yohan
     */
    public void viderGrille() {
        for (Map.Entry<String, ImageView> entry : this.listeImageView.entrySet()) {
            entry.getValue().setImage(null);
        }
    }

    /**
     * Fonction récupérant l'image correspondant à la pièce se trouvant dans le répertoire ressources <code>src/ressources</code>
     * @see Piece
     * @param classe Classe de la pièce
     * @param couleur Couleur de la pièce
     * @return {@linkplain Image} Image correspondant à la pièce
     * @author yohan
     */
    public Image getImageViewPion(String classe, String couleur) {
        URL file;
        file = getClass().getResource(String.format("/%s/%s_%s.png", couleur, classe, couleur));
        return new Image(Objects.requireNonNull(file).toExternalForm());
    }

    /**
     * Fonction réinitialisant graphiquement le Plateau mais aussi le plateau, les grilles, les pions etc...
     * @see PlateauDeJeu
     * @see Grille
     * @see Controller
     * @see Historique
     * @see UiHistorique
     * @author yohan
     */
    public void reinitialize() {
        viderGrille();
        addPionToGrid();
        initializeHistoriqueTable();
        initializeController();
        this.jeu.setTour("blanc");
        setLabelTour();
        resetMarquagesDeplacements();
        resetMarquagesSauveurs();
        resetMarquagesRoi();
        controller.windowParam();
        this.listeDeplacementsPossibles.clear();
        this.sauveurs.clear();
        this.caseRoiEnEchec = null;
    }

    /**
     * Fonction de controle principal. A chaque clic de l'utlisateur, elle va lancer une série d'action qui se compléteront/
     * Cette fonction représente en réalité le cycle de vie de l'application, qui ne vit que lorsqu'un clic est fait.
     * @param mouseEvent Imageview cliquée
     * @see Jeu
     * @author yohan
     */
    public void onClick(MouseEvent mouseEvent) {

        // On récupère l'imageview qui a été cliquée
        ImageView targetImgView = (ImageView) mouseEvent.getTarget();

        // Chaine qui contiendra le retour de validation des déplacements
        String retour = "";

        // On récupère les coordonnées de l'ImageView et grâce à elles ont récupère la case correspondante
        int x = Integer.parseInt(targetImgView.getId().split("")[1]);
        int y = Integer.parseInt(targetImgView.getId().split("")[2]);
        Case targetClickCase = this.jeu.plateau.getGrille().getCase(x,y);

        // ==================================================================================================================================================
        // Les clics fonctionnent par pairs. Le premier est un clic de pointage, le deuxième d'action
        // ==================================================================================================================================================

        // Si ce clic est le deuxième (Clic d'Action)
        if (this.uiHistorique.isClicked())
        {
            // Si le premier clic était sur une pièce
            if (this.uiHistorique.getLastUiEvent().hasPiece() && !uiHistorique.getLastUiEvent().getComponentOriginId().equals("CréationUiHistorique"))
            {

                // On récupère les coordonnées de l'ImageView et la case de la case qui a été précédemment cliquée
                int xD = Integer.parseInt(this.uiHistorique.getLastUiEvent().getComponentOriginId().split("")[1]);
                int yD = Integer.parseInt(this.uiHistorique.getLastUiEvent().getComponentOriginId().split("")[2]);
                Case lastEventClickCase = this.jeu.plateau.getGrille().getCase(xD, yD);

                // Si la pièce cliquée est bien de la couleur du tour ou est null
                if (lastEventClickCase.piece.getCouleur().equals(this.jeu.getTour()))
                {
                    // Si cette case appartient à la liste des déplacements possibles
                    if (this.listeDeplacementsPossibles.contains(targetClickCase))
                    {
                        // Si cette case est vide alors c'est un déplacement
                        if (targetClickCase.piece == null)
                        {
                            retour = this.jeu.plateau.deplacerPiece(lastEventClickCase, targetClickCase);
                        }
                        // Sinon c'est une prise
                        else
                        {
                            retour = this.jeu.plateau.prendrePiece(lastEventClickCase, targetClickCase);
                        }

                        // Si le message de retour est bon, et que le déplacement c'est bien passé
                        if (retour.equals("ok"))
                        {
                            Case caseRoi = this.jeu.plateau.getGrille().getRoi(targetClickCase.piece.getCouleur());

                            // Si maintenant que la pièce à bouger, que c'était un roi, et qu'il se retrouve en échec, il faut annuler le déplacement
                            if (this.jeu.plateau.detecterEchec(caseRoi.piece, caseRoi).equals("echec"))
                            {
                                // On affiche un message d'erreur
                                PopupWindow popupWindow = new PopupWindow("Déplacement impossible,\n il mettrait le roi en echec");
                                popupWindow.display();
                                // Et on annule le tour
                                this.jeu.undo();
                            }
                            else
                            {
                                String lastEventClickId = this.uiHistorique.getLastUiEvent().getComponentOriginId();
                                String targetClickId = targetImgView.getId();

                                // On met à jour le plateau en reproduisant le déplacement qui a été fait dans les données
                                this.deplacerPieceUI(lastEventClickId, targetClickId);
                                // Et bien sûr on met à jour la table d'historique de l'UI
                                this.updateHistoriqueTable();

                                // On change la couleur du tour et on l'affiche dans le label
                                this.jeu.changerCouleurTour();
                                this.setLabelTour();
                            }
                        }

                        // On regarde si le roi n'est pas seul, dans quel cas c'est une défaite : C'est perdu
                        if (this.jeu.plateau.getGrille().getListePieceCouleur(this.jeu.getTour()).size() == 1)
                        {
                            this.perdu(targetClickCase.couleur.equals("blanc") ? "noir" : "blanc");
                        }

                        // Si le roi blanc est mort par mégarde : C'est perdu
                        if (this.jeu.plateau.getGrille().getRoi("blanc") == null)
                        {
                            this.perdu("noir");
                        }

                        // Et/Ou si le roi noir est mort par mégarde : C'est perdu
                        if (this.jeu.plateau.getGrille().getRoi("noir") == null)
                        {
                            this.perdu("blanc");
                        }

                        // On verifie l'echec une nouvelle fois mais plus en profondeur
                        Echec(targetClickCase);

                    }
                    else
                    {
                        System.out.println("Ce déplacement n'est pas autorisé");
                    }
                }
                else
                {
                    System.out.println(String.format("C'est aux %ss de jouer", this.jeu.getTour()));
                }
            }
            else
            {
                System.out.println("Vous n'avez sélectionné aucune pièces");
            }

            // On décolorie les déplacements quand un coup est fait
            resetMarquagesDeplacements();

            // Si le pion sélectionné est un sauveur, on ne remet pas sa couleur par défaut pour qu'il reste
            // visible. Sa couleur sera reset quand il n'y aura plus d'échec
            String idOriginalClic = this.uiHistorique.getLastUiEvent().getComponentOriginId();
            Case c = this.jeu.plateau.getGrille().getCase(
                    Integer.parseInt(idOriginalClic.split("")[1]),
                    Integer.parseInt(idOriginalClic.split("")[2]));

            // On décolorie toutes les cases qui ne contiennent pas de sauveurs
            if (!this.sauveurs.contains(c)) {
                setOriginalStyle(this.uiHistorique.getLastUiEvent().getComponentOriginId());
            }

            // Si le jeu est en mode contre IA on lance avec un petit délai le prochain coup après
            // le dernier clic de l'utilisateur
            if (mode.equals("ia")) {
                if (retour.equals("ok")) {
                    IA();
                }
            }
        }
        // Si le clic est le premier (Clic de pointage)
        else
        {
            // Si le jeu est en echec
            if (this.jeu.plateau.isEnEchec())
            {
                // Si ce clic sélectionne le roi ou un des pions qui est considéré comme un potentiel sauveur
                if (targetClickCase.equals(this.caseRoiEnEchec) || this.sauveurs.contains(targetClickCase))
                {
                    // On met à jour ses déplacements possibles et on les affiche
                    listeDeplacementsPossibles = this.jeu.plateau.deplacementsPossible(targetClickCase);
                    marquerDeplacement(listeDeplacementsPossibles);
                }
            }
            else
            {
                // Si on clic sur un pion
                if (targetClickCase.piece != null)
                {
                    // On met à jour ses déplacements possibles et on les affiche
                    listeDeplacementsPossibles = this.jeu.plateau.deplacementsPossible(targetClickCase);
                    marquerDeplacement(listeDeplacementsPossibles);
                }
            }
        }
        // On met à jour l'historique de l'UI
        uiHistorique.addUiEvent(targetImgView.getId(), targetClickCase);

        // Et on passe le click de Pointeur -> Action ( de 1e clic -> 2e clic)
        uiHistorique.clicked();
    }

    /**
     * Fonction récupérant en cas d'échec les attaquants, le roi aggressé et en déduise des déplacements du roi
     * ou alliés pouvant le protéger de cet echec.
     * @param c Case qui donnera la couleur du roi en echec
     * @author yohan
     */
    public void Echec(Case c)
    {
        Echec(c.piece.getCouleur());
    }

    /**
     * Fonction récupérant en cas d'échec les attaquants, le roi aggressé et en déduise des déplacements du roi
     * ou alliés pouvant le protéger de cet echec.
     * @param couleur couleur du roi en echec
     * @author yohan
     */
    public void Echec(String couleur) {

        // On récupère la couleur des pions ennemis pour pouvoir faire des tests dessus
        String couleurEnnemie = couleur.equals("noir") ? "blanc" : "noir";
        caseRoiEnEchec = this.jeu.plateau.getGrille().getRoi(couleurEnnemie);

        // On regarde si le coup a mis le roi en echec
        Echec retourEchec = this.jeu.plateau.detecterEchec(caseRoiEnEchec.piece, caseRoiEnEchec);

        // Si la pièce est en échec et  que le roi peut bouger
        if (retourEchec.isEchec().equals("echec"))
        {
            // On affiche la case du roi en Rouge
            this.marquerRoi(caseRoiEnEchec);

            // On met le plateau en echec
            this.jeu.plateau.setEnEchec(true);

            // On récré la liste des sauveurs
            this.sauveurs.clear();

            // Si il y en a, on stocke les sauveurs
            if (retourEchec.hasSauveurs())
            {
                for (Deplacement d : retourEchec.getSauveur())
                {
                    this.sauveurs.add(d.getOrigine());
                }
            }

            // Et on les mets en bleu pour que le joueur puisse connaître ses possibilités de coups
            marquerSauveur(this.sauveurs);
        }
        // S'il n'y a ni mat ni echec, on annule l'échec, décolorie les sauveurs et le roi
        else if (this.jeu.plateau.isEnEchec() && retourEchec.isEchec().equals("no-echec"))
        {
            this.jeu.plateau.setEnEchec(false);
            resetMarquagesSauveurs();
            resetMarquagesRoi();
        }
        // Sinon si c'est un mat, on termine la game
        else if (retourEchec.isEchec().equals("mat"))
        {
            this.perdu(couleur);
        }
    }

    /**
     * Fonction faisant jouer l'application, en lui permettant de choisir les déplacements et prises les plus rentables.
     * Ou a défaut si rien n'est rentable, de choisir aléatoirement parmi les déplacements possibles.
     * @see PlateauDeJeu#IA() 
     * @see PlateauDeJeu#IAEchec(EchecApplication)
     * @author yohan, melissa
     */
    public void IA() {

        // On fait patienter 1/10 de secondes pour que l'utilisateur ait le temps de voir le mouvement de l'IA
        try {
            Thread.sleep(150);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Si le jeu n'est pas en situation d'échec on déclenche un coup d'IA normale
        if (!this.jeu.plateau.isEnEchec()) {
            this.jeu.plateau.IA();
        }
        else
        {
            this.jeu.plateau.IAEchec(this);
        }

        // Comme à ce niveau là l'IA a forcément joué, on récupère ses déplacements dans l'historique, où
        // elle y figure dernière
        // Coordonnée de l'origine du coup
        int originIdX = this.jeu.plateau.historique.getDernierEvenement().getCaseOrigine().x;
        int originIdY = this.jeu.plateau.historique.getDernierEvenement().getCaseOrigine().y;
        String origineId = String.format("v%d%d", originIdX, originIdY);

        // Coordonnée de la destination du coup
        int destinationIdX = this.jeu.plateau.historique.getDernierEvenement().getCaseDestination().x;
        int destinationIdY = this.jeu.plateau.historique.getDernierEvenement().getCaseDestination().y;
        String destinationId = String.format("v%d%d", destinationIdX, destinationIdY);

        // On déplace graphiquement en suivant l'historique
        this.deplacerPieceUI(origineId, destinationId);
        this.updateHistoriqueTable();

        // On vérifie si l'IA a déclenché un echec
        this.Echec("noir");

        // On set la couleur du tour et on l'affiche
        this.jeu.setTour("blanc");
        this.setLabelTour();
    }

    /**
     * Fonction Affichant une {@linkplain PopupWindow} indiquant le résultat final de la partie.
     * Sans pour autant bloquer le programme. Il est toujours possisble de commencer une nouvelle partie via l'onglet
     * "Partie"
     * @see Jeu#quitterJeu()
     * @see Controller#confirmOnQuit(WindowEvent)
     * @see Controller#menuItemNewGameOnClick()
     * @param couleur
     * @author yohan
     */
    public void perdu(String couleur) {
        PopupWindow popupWindow = new PopupWindow(String.format("Les %ss l'emportent !", couleur));
        popupWindow.display();

    }

    /**
     * Fonction changeant le style des Pane pour indiquer qu'elles se trouve sur le chemin d'une pièce
     * @param listeDeplacementsPossibles Liste des cases formant les déplacements possibles d'une pièce
     * @see EchecApplication#paintedPane
     * @see EchecApplication#resetMarquagesDeplacements()
     * @author yohan
     */
    public void marquerDeplacement(ArrayList<Case> listeDeplacementsPossibles) {
        for (Case c : listeDeplacementsPossibles) {
            if (!c.equals(caseRoiEnEchec)) {
                Pane pane = this.listePane.get(String.format("p%d%d", c.x, c.y));
                pane.setStyle("-fx-background-color: yellow");
                this.paintedPane.add(pane);
            }
        }
    }

    /**
     * Fonction changeant le style des Pane pour indiquer qu'elles sont considérée comme pouvant sauver
     * leur roi d'un echec
     * @param sauveurs Liste des cases contenant les sauveurs
     * @see EchecApplication#paintedPaneSauveur
     * @see EchecApplication#resetMarquagesSauveurs()
     * @author yohan
     */
    public void marquerSauveur(ArrayList<Case> sauveurs) {
        for (Case c : sauveurs) {
            if (!c.equals(caseRoiEnEchec)) {
                Pane pane = this.listePane.get(String.format("p%d%d", c.x, c.y));
                pane.setStyle("-fx-background-color: #406dcb");
                this.paintedPaneSauveur.add(pane);
            }
        }
    }

    /**
     * Fonction coloriant la case contenant le roi donnée en argument
     * @param roi Case contenant la case du roi à marquer
     * @see EchecApplication#paintedPaneRoi
     * @see EchecApplication#resetMarquagesRoi()
     * @author yohan
     */
    public void marquerRoi(Case roi) {
        this.paintedPaneRoi = this.listePane.get(String.format("p%d%d", roi.x, roi.y));
        this.paintedPaneRoi.setStyle("-fx-background-color: #e85353");
    }

    /**
     * Fonction décoloriant les marquages de déplacements
     * @see EchecApplication#paintedPane
     * @see EchecApplication#marquerDeplacement(ArrayList)
     * @author yohan
     */
    public void resetMarquagesDeplacements() {
        for (Pane pane : this.paintedPane) {
                this.setOriginalStyle(pane.getId());
        } this.paintedPane.clear();
    }

    /**
     * Fonction décoloriant les marquages de sauveurs
     * @see EchecApplication#paintedPaneSauveur
     * @see EchecApplication#marquerSauveur(ArrayList)
     * @author yohan
     */
    public void resetMarquagesSauveurs() {
        for (Pane pane : this.paintedPaneSauveur) {
            this.setOriginalStyle(pane.getId());
        } this.paintedPaneSauveur.clear();
    }

    /**
     * Fonction décoloriant le marquage du Roi
     * @see EchecApplication#paintedPaneRoi
     * @see EchecApplication#marquerRoi(Case)
     * @author yohan
     */
    public void resetMarquagesRoi() {
        if (this.paintedPaneRoi != null) {
            this.setOriginalStyle(this.paintedPaneRoi.getId());
        }
    }

    /**
     * Fonction récupérant le style original de la case du damier
     * @param id <code>String</code> Id de la Pane dont il faut reset le style
     * @see EchecApplication#setStyleDamier()
     * @author yohan
     */
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

    /**
     * Fonction déplaçant graphiquement d'une ImageView A à une ImageView B une image
     * @param originId id de l'ImageView d'origine du coup
     * @param targetId id de l'ImageView de destination du coup
     * @see Jeu#deplacerPiece()
     * @author yohan
     */
    public void deplacerPieceUI(String originId, String targetId) {
        Image image = this.listeImageView.get(originId).getImage();
        this.listeImageView.get(targetId).setImage(image);
        this.listeImageView.get(originId).setImage(null);
    }

    /**
     * Getter de {@linkplain EchecApplication#jeu}
     * @return {@linkplain EchecApplication#jeu}
     * @author yohan
     */
    public Jeu getJeu() {
        return jeu;
    }

    /**
     * Setter de {@linkplain EchecApplication#mode}
     * @param mode {@linkplain EchecApplication#mode}
     * @author yohan
     */
    public void setMode(String mode) {
        this.mode = mode;
    }

    /**
     * Fonction initialisant la table de l'historique de l'interface graphique, en se liant à l'{@linkplain Historique}
     * pour automatiquement récupérer ses entrées.
     * @see EchecApplication#updateHistoriqueTable()
     * @author yohan
     */
    public void initializeHistoriqueTable() {

        // On load la TableView
        this.tableHistorique = (TableView) loader.getNamespace().get("listeImageView");

        // On load la colonne des origines de l'historique depuis le modèle FXML
        javafx.scene.control.TableColumn<Evenement, String> colonneOrigineHistorique = (javafx.scene.control.TableColumn<Evenement, String>) loader.getNamespace().get("historiqueOriginColumn");

        // On précise à chaque évènement de modification de l'historique, la tableView va afficher dans la
        // colonne d'origine le champ : contenuOrigineEchecNotation de l'évènement
        colonneOrigineHistorique.setCellValueFactory(event -> new SimpleObjectProperty<>(event.getValue().getContenuOrigineEchecNotation()));

        // On load la colonne des destinations de l'historique depuis le modèle FXML
        javafx.scene.control.TableColumn<Evenement, String> colonneDestinationHistorique = (javafx.scene.control.TableColumn<Evenement, String>) loader.getNamespace().get("historiqueDestinationColumn");

        // On précise à chaque évènement de modification de l'historique, la tableView va afficher dans la
        // colonne d'origine le champ : contenuDestinationEchecNotation de l'évènement
        colonneDestinationHistorique.setCellValueFactory(event -> new SimpleObjectProperty<>(event.getValue().getContenuDestinationEchecNotation()));

        // On set la table en non sélectionnable
        this.tableHistorique.setSelectionModel(null);

        // On lui faire prendre le denier élément pour l'initialiser
        this.tableHistorique.getItems().setAll(this.jeu.plateau.historique.getDernierEvenement());

        // Pour chaque ligne on ajoute le style
        this.tableHistorique.setRowFactory(tableView -> {
            TableRow<Evenement> row = new TableRow<>();
            row.getStyleClass().add("table-row-cell");
            return row;
        });
        // Pour la table on ajoute le style
        this.tableHistorique.getStyleClass().add("table-view");
        this.tableHistorique.getStyleClass().add(".iAtem-column-historic");
    }

    /**
     * Fonction mettant à jour la table {@linkplain EchecApplication#tableHistorique} avec les dernières entrées
     * de {@linkplain PlateauDeJeu#historique}
     * @author yohan
     */
    public void updateHistoriqueTable() {
        this.tableHistorique.getItems().add(this.jeu.plateau.historique.getDernierEvenement());
    }

    /**
     * Fonction chargeant l'historique de l'UI depuis les fichiers de sauvegardes
     * @param historique {@linkplain Historique} précédemment extrait d'une sauvegarde, et qui servira ici
     *                                          à peupler {@linkplain EchecApplication#tableHistorique}
     * @see PlateauDeJeu#getJSONObject()
     * @see Jeu#chargerJeuFromFile(File)
     * @see Jeu#charger()
     * @see Controller#menuItemChargerOnClick()
     * @author yohan
     */
    public void getHistoriqueTableFromSave(Historique historique) {
        for (Map.Entry<Integer, Evenement> entry : historique.getHistorique().entrySet()) {
            this.tableHistorique.getItems().add(entry.getValue());
        }
    }

    public void setLabelTour() {
        if (this.jeu.getTour().equals("blanc")) {
            labelTour.setText("Au tour des blancs");
            labelTour.setStyle("-fx-background-color: white; -fx-text-fill: black");
        } else if (this.jeu.getTour().equals("noir")) {
            labelTour.setText("Au tour des noirs");
            labelTour.setStyle("-fx-background-color: black; -fx-text-fill: white");
        }
    }

    /**
     * Fonction ouvrant le navigateur par défaut de l'utilisateur et lui fait rechercher ce lien
     * <b><a rhef"https://www.youtube.com/watch?v=n1T1knKg_58">Lien vers un cours pour mieux comprendre les échecs</a></b>
     * @see Controller#openHelp()
     * @author yohan
     */
    public void openBrowser() {
//        getHostServices().showDocument(" https://www.youtube.com/watch?v=dQw4w9WgXcQ");
        getHostServices().showDocument("https://www.youtube.com/watch?v=n1T1knKg_58");
    }

    /**
     * Main
     * author melissa, yohan
     */
    public static void main(String[] args) {
        launch(args);
    }
}

