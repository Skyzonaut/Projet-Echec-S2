package com.echec.game;

import com.echec.Tools;
import com.echec.pieces.Piece;
import com.echec.ui.EchecApplication;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class PlateauDeJeu {

    /**
     * Identifiant du plateau de Jeu
     */
    private String id;

    /**
     * Grille contenant les cases et les pièces
     */
    private Grille grille;

    /**
     * Historique attitré à ce plateau contenant la liste des évènements
     */
    public Historique historique = new Historique();

    /**
     * Liste des déplacements des noirs, et qui sera mise à jour à chaque coup et mouvement
     */
    public ArrayList<Deplacement> listeDeplacementNoirs = new ArrayList<>();

    /**
     * Liste des déplacements des blancs, et qui sera mise à jour à chaque coup et mouvement
     */
    public ArrayList<Deplacement> listeDeplacementBlancs = new ArrayList<>();

    /**
     * Variable précisant si le plateau est en echec
     */
    private boolean enEchec = false;

    /**
     * Echec contenant les attaquants, sauveurs du roi et le roi lors d'un echec
     */
    private Echec echec;

    /**
     * Variable précisant si l'IA est en echec
     */
    private boolean IAEnEchec = false;

    /**
     * Constructeur par défaut du plateau.
     * <p>Par défaut son id est concaténé avec sa date et heure de création</p>
     */
    public PlateauDeJeu() {
        this.id = "plateau " + Tools.getFormatDate();
        this.grille = new Grille();
        this.initGrille();
    }

    /**
     * Constructeur par chargement d'un object JSON
     * @param jsonObject Object Json servant de sauvegarde à un précédant Jeu
     * @see PlateauDeJeu#getJSONObject()
     */
    public PlateauDeJeu(JSONObject jsonObject) {
        this.id = (String) jsonObject.get("id");
        this.grille = new Grille((JSONObject) jsonObject.get("grille"));
        this.historique = new Historique((JSONObject) jsonObject.get("historique"));
        this.enEchec = (Boolean) jsonObject.get("enEchec");
    }

    /**
     * Fonction simulant la création d'un nouveau plateau
     * @see PlateauDeJeu
     */
    public void init() {
        this.id = "plateau " + Tools.getFormatDate();
        this.grille = new Grille();
        this.historique = new Historique();
        this.initGrille();
    }

    /**
     * Fonction affichant le plateau au format texte sous la forme d'un échiquier dans la console
     * @param hauteur hauteur des cases
     * @param largeur largeur des cases
     */
    public void afficher(int hauteur, int largeur) {
        System.out.println(this.toString(hauteur, largeur));
    }

    /**
     * Fonction affichant le plateau au format texte sous la forme d'un échiquier dans la console
     */
    public void afficher() {
        this.afficher(2, 5);
    }

    /**
     * Réécriture de la fonction {@linkplain Object#toString()}, en retournant cette fois ci l'échiquier sous un
     * format texte dans la console
     * @return le plateau au format texte
     */

    public String toString() {
        return this.toString(2, 5);
    }
    /**
     * Réécriture de la fonction {@linkplain Object#toString()}, en retournant cette fois ci l'échiquier sous un
     * format texte dans la console
     * @param hauteur hauteur des cases
     * @param largeur largeur des cases
     * @return le plateau au format texte
     */
    public String toString(int hauteur, int largeur) {

        StringBuilder dessinPlateau = new StringBuilder(" ");

        for (int colonne = 1; colonne <= 8; colonne++) {
            for (int j = 0; j < largeur; j++) {
                if (j == (largeur / 2)) {
                    dessinPlateau.append(Tools.getLettreColonne(colonne)).append(" ");
                } else {
                    dessinPlateau.append(" ");
                }
            }
        }

        dessinPlateau.append("\n-");

        for (int i = 0; i < 8; i++) {
            dessinPlateau.append("-".repeat(largeur));
            dessinPlateau.append("-");
        }
        dessinPlateau.append("\n");

        int ligneCompte = 8;

        for (int ligne = 1; ligne < hauteur * 8; ligne++) {
            String contenu = " ";
            dessinPlateau.append("|");

            for (int colonne = 1; colonne <= 8; colonne++) {
                if (this.grille.getCase(colonne, ligneCompte).piece != null) {
                    contenu = this.grille.getCase(colonne, ligneCompte).piece.utfString();
                }

                for (int j = 0; j < largeur; j++) {
                    if (j == (largeur / 2)) {
                        if (!contenu.equals(" ")) {
                            dessinPlateau.append(ligne % hauteur == 0 ? " " : contenu);
                        } else {
                            dessinPlateau.append(" ");
                        }
                    } else {
                        dessinPlateau.append(ligne % hauteur == 0 ? "-" : " ");
                    }
                }
                contenu = " ";
                dessinPlateau.append(ligne % hauteur == 0 ? "+" : "|");

            }
            dessinPlateau.append(ligne % hauteur == 0 ? " " : " " + ligneCompte);
            dessinPlateau.append("\n");

            if (ligne % hauteur == 0) ligneCompte--;
        }
        dessinPlateau.append("-");

        for (int i = 0; i < 8; i++) {
            dessinPlateau.append("-".repeat(Math.max(0, largeur)));
            dessinPlateau.append("-");
        }
        dessinPlateau.append("\n");

        return dessinPlateau.toString();
    }

    /**
     * Getter de {@linkplain PlateauDeJeu#grille}
     * @return {@linkplain PlateauDeJeu#grille}
     */
    public Grille getGrille() {
        return this.grille;
    }

    /**
     * Fonction réinitialisant la grille de ce plateau
     * @see Grille#initialiserGrille()
     */
    public void initGrille() {
        this.grille.initialiserGrille();
    }

    /**
     * Getter de {@linkplain PlateauDeJeu#echec}
     * @return {@linkplain PlateauDeJeu#echec}
     */
    public Echec getEchec() {
        return  this.echec;
    }

    /**
     * Fonction déplaçant dans la grille un pion vers une case vide
     * @param origine Case du pion d'origine du coup
     * @param destination  Case de destination du pion d'origine du coup
     * @return <code>String</code> chaîne de validation
     * @see EchecApplication#deplacerPieceUI(String, String)
     * @author yohan
     */
    public String deplacerPiece(Case origine, Case destination) {
        return deplacerPiece(origine, destination, true);
    }

    /**
     * Fonction déplaçant dans la grille un pion vers une case vide
     * @param origine Case du pion d'origine du coup
     * @param destination  Case de destination du pion d'origine du coup
     * @param updateHistorique Boolean si l'on souhaite ou non rajouter ce mouvement dans l'historique
     * @return <code>String</code> chaîne de validation
     * @see EchecApplication#deplacerPieceUI(String, String)
     * @author yohan
     */
    public String deplacerPiece(Case origine, Case destination, boolean updateHistorique) {

        // Si la case de destination est vide et que l'origine non plus
        if (!origine.estVide())
        {
            if (destination.estVide())
            {
                // On met ce qui est dans l'origine dans la destination
                destination.piece = origine.piece;

                // Si on souhaite mettre à jour l'historique avec ce déplacement on ajoute l'évènement
                if (updateHistorique)
                {
                    this.historique.addEvenement("Déplacement", origine, destination);
                }

                // On vide l'origine
                origine.vider();
                this.updateListeDeplacements(false);
                return "ok";
            }
            else
            {
                System.out.println("La destination n'est pas vide, veuillez utiliser la commande [prendre]");
                return "nok";

            }
        }
        else
        {
            System.out.println("L'origine est vide!");
            return "nok";
        }

    }
    /**
     * Fonction prenant un pion par un autre
     * @param origine Case du pion d'origine du coup
     * @param destination  Case de destination du pion d'origine du coup
     * @return <code>String</code> chaîne de validation
     * @see EchecApplication#deplacerPieceUI(String, String)
     * @see PlateauDeJeu#deplacerPiece(Case, Case)
     * @author yohan
     */
    public String prendrePiece(Case origine, Case destination) {

        // Si la destination comporte bel et bien une piece
        if (!destination.estVide())
        {
            // On désactive cette pièce
            destination.piece.setEtat(false);

            // On ajoute l'évènement
            this.historique.addEvenement("Prise", origine, destination);

            // On vide la case de la pièce prise
            destination.vider();

            // Et on y met la pièce qui l'a prise
            deplacerPiece(origine, destination, false);
            return "ok";
        }
        else
        {
            System.out.println("La destination est pas vide, veuillez utiliser la commande [déplacer]");
            return "nok";
        }
    }

    /**
     * Setter de {@linkplain PlateauDeJeu#id}
     * @param value {@linkplain PlateauDeJeu#id}
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Getter de {@linkplain PlateauDeJeu#id}
     * @return {@linkplain PlateauDeJeu#id}
     */
    public String getId() {
        return this.id;
    }

    /**
     * Getter vérifiant si le plateau est en echec
     * @return {@linkplain PlateauDeJeu#enEchec}
     */
    public boolean isEnEchec() {
        return enEchec;
    }

    /**
     * Setter de {@linkplain PlateauDeJeu#enEchec}
     * @return {@linkplain PlateauDeJeu#enEchec}
     */
    public void setEnEchec(boolean enEchec) {
        this.enEchec = enEchec;
    }

    /**
     * Fonction retournant le plateau et ce qui le compose au format JSON
     * @return la sauvegarde au format JSON
     * @see Jeu#save()
     * @see Jeu#chargerJeuFromFile(File)
     * @see Grille#getJSONObject()
     */
    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", this.id);
        jsonObject.put("grille", this.grille.getJSONObject());
        jsonObject.put("historique", this.historique.getJSONObject());
        jsonObject.put("enEchec",this.enEchec );
        return jsonObject;
    }

    /**
     * Fonction traçant un chemin entre le roi et la case, et si un chemin est possible respectant les mouvements
     * de la pièce d'origine du mouvement, en retourne la liste des cases;
     * <p>Cette fonction permet de savoir si un pion est directement en face à face avec le roi sans avoir à passer par 
     * <ul>
     *     <li>{@linkplain PlateauDeJeu#generateListeDeplacementsBlancs(Boolean, Boolean, Boolean)}</li>
     *     <li>{@linkplain PlateauDeJeu#generateListeDeplacementsNoirs(Boolean, boolean, boolean)}}</li>
     *     <li>{@linkplain PlateauDeJeu#getDeplacementsPossiblesPions(Case)}</li>
     *     <li>{@linkplain PlateauDeJeu#getDeplacementsPossiblesTours(Case, boolean, boolean)}</li>
     *     <li>{@linkplain PlateauDeJeu#getDeplacementsPossiblesCavaliers(Case)}</li>
     *     <li>{@linkplain PlateauDeJeu#getDeplacementsPossiblesFou(Case, boolean, boolean)}</li>
     *     <li>{@linkplain PlateauDeJeu#getDeplacementsPossiblesReine(Case, boolean, boolean)}</li>
     *     <li>{@linkplain PlateauDeJeu#getDeplacementsPossiblesRoi(Case, boolean)}</li>
     * </ul>
     * </p>
     * @param origine case contenant le pion dont part le chemin jusqu'au roi
     * @param roi Case contenant le roi
     * @return Liste des cases entre le roi et l'origine. Retourne une liste vide si aucun chemin n'est possible
     */
    public ArrayList<Case> getCheminToRoi(Case origine, Case roi) {
        ArrayList<Case> chemin = new ArrayList<>();
        if (origine.piece.getClassePiece().equalsIgnoreCase("tour")) {
            if (origine.y == roi.y) {
                if (origine.x > roi.x) {
                    for (int x = 1; x <= origine.x - roi.x; x++) {
                        chemin.add(this.grille.getCase(origine.x - x, origine.y));
                    }
                } else {
                    for (int x = 1; x <= roi.x - origine.x; x++) {
                        chemin.add(this.grille.getCase(origine.x + x, origine.y));
                    }
                }
            } else if (origine.x == roi.x) {
                if (origine.y > roi.y) {
                    for (int y = 1; y <= origine.y - roi.y; y++) {
                        chemin.add(this.grille.getCase(origine.x, origine.y - y));
                    }
                } else {
                    for (int y = 1; y <= roi.y - origine.y; y++) {
                        chemin.add(this.grille.getCase(origine.x, origine.y - y));
                    }
                }
            }
        }
        if (origine.piece.getClassePiece().equalsIgnoreCase("reine")) {
            System.out.println("Origine " + origine + " roi " + roi);
            if (origine.y == roi.y) {
                if (origine.x > roi.x) {
                    for (int x = 1; x <= origine.x - roi.x; x++) {
                        chemin.add(this.grille.getCase(origine.x - x, origine.y));
                    }
                } else {
                    for (int x = 1; x <= roi.x - origine.x; x++) {
                        chemin.add(this.grille.getCase(origine.x + x, origine.y));
                    }
                }
            } else if (origine.x == roi.x) {
                if (origine.y > roi.y) {
                    for (int y = 1; y <= origine.y - roi.y; y++) {
                        chemin.add(this.grille.getCase(origine.x, origine.y - y));
                    }
                } else {
                    for (int y = 1; y <= roi.y - origine.y; y++) {
                        chemin.add(this.grille.getCase(origine.x, origine.y + y));
                    }
                }
            }
            if (origine.x > roi.x && origine.y > roi.y) {
                for (int i = 1; i <= origine.x - roi.x; i++) {
                    chemin.add(this.grille.getCase(origine.x - i, origine.y - i));
                }
            }
            if (origine.x > roi.x && origine.y < roi.y) {
                for (int i = 1; i <= origine.x - roi.x; i++) {
                    chemin.add(this.grille.getCase(origine.x - i, origine.y + i));
                }
            }
            if (origine.x < roi.x && origine.y > roi.y) {
                for (int i = 1; i <= roi.x - origine.x; i++) {
                    chemin.add(this.grille.getCase(origine.x + i, origine.y - i));
                }
            }
            if (origine.x < roi.x && origine.y < roi.y) {
                for (int i = 1; i <= roi.x - origine.x; i++) {
                    chemin.add(this.grille.getCase(origine.x + i, origine.y + i));
                }
            }
        }
        if (origine.piece.getClassePiece().equalsIgnoreCase("fou")) {
            if (origine.x > roi.x && origine.y > roi.y) {
                for (int i = 1; i <= origine.x - roi.x; i++) {
                    chemin.add(this.grille.getCase(origine.x - i, origine.y - i));
                }
            }
            if (origine.x > roi.x && origine.y < roi.y) {
                for (int i = 1; i <= origine.x - roi.x; i++) {
                    chemin.add(this.grille.getCase(origine.x - i, origine.y + i));
                }
            }
            if (origine.x < roi.x && origine.y > roi.y) {
                for (int i = 1; i <= roi.x - origine.x; i++) {
                    chemin.add(this.grille.getCase(origine.x + i, origine.y - i));
                }
            }
            if (origine.x < roi.x && origine.y < roi.y) {
                for (int i = 1; i <= roi.x - origine.x; i++) {
                    chemin.add(this.grille.getCase(origine.x + i, origine.y + i));
                }
            }
        }
        return chemin;
    }


    /**
     * Fonction permettant d'avoir les déplacements possibles d'une piece
     * @param posPiece <code>{@linkplain Case}</code> : Case de la piece dont on veut avoir les déplacements
     * @param prevoirPrise Boolean déterminant si un pion doit considérer une case contenant un de ses allié au cas où cette dernier
     *                     viendrait à être prise, dans quel cas cette case contenant initialement son allié, pourrait alors
     *                     contenir un ennemi
     * @param continuer Boolean indiquant si une pièce à projection linéaire doit ignorer les pions sur son passage et prendre
     *                  toutes les lignes comme déplacement possible malgré leur contenu. Cela permet de considérer une case
     *                  habituellement cachée par un roi met tout de même dans l'alignement avec la pièce. Case dans laquelle
     *                  dès lors le roi ne pourrait plus se déplacer car considérer dans les déplacements possibles de la pièce
     *                  grâce à ce paramètre
     * @return <code>ArrayList<{@linkplain Case}></code> : Retourne une liste de cases des déplacements possibles
     * @see Case
     * @author melissa, yohan
     */
    public ArrayList<Case> deplacementsPossible (Case posPiece, boolean prevoirPrise, boolean continuer) {

        ArrayList<Case> listeCases = new ArrayList<>();

        int x = posPiece.x;
        int y = posPiece.y;

        String classePiece = grille.getCase(posPiece).piece.getClassePiece();

        if (classePiece.equalsIgnoreCase("Pion"))
        {
            listeCases = getDeplacementsPossiblesPions(posPiece);
        }
        if (classePiece.equalsIgnoreCase("Tour"))
        {
            listeCases = getDeplacementsPossiblesTours(posPiece, prevoirPrise, continuer);
        }
        if (classePiece.equalsIgnoreCase("Reine"))
        {
            listeCases = getDeplacementsPossiblesReine(posPiece, prevoirPrise, continuer);
        }
        if (classePiece.equalsIgnoreCase("Fou"))
        {
            listeCases = getDeplacementsPossiblesFou(posPiece, prevoirPrise, continuer);
        }
        if (classePiece.equalsIgnoreCase("Cavalier"))
        {
            listeCases = getDeplacementsPossiblesCavaliers(posPiece);
        }
        if (classePiece.equalsIgnoreCase("Roi"))
        {
            listeCases = getDeplacementsPossiblesRoi(posPiece, prevoirPrise);
        }
        return listeCases;
    }

    /**
     * Fonction permettant d'avoir les déplacements possibles d'une piece
     * @param posPiece <code>{@linkplain Case}</code> : Case de la piece dont on veut avoir les déplacements
     * @return <code>ArrayList<{@linkplain Case}></code> : Retourne une liste de cases des déplacements possibles
     * @see Case
     * @see PlateauDeJeu#updateListeDeplacements(Boolean)
     * @author melissa, yohan
     */
    public ArrayList<Case> deplacementsPossible (Case posPiece) {
        return deplacementsPossible(posPiece, false, false);
    }

    /**
     * Fonction récupérant les déplacements théoriques possibles simples d'un Cavalier, c'est à dire qui prend en compte les
     * paramètres suivants :
     * <ul>
     *     <li>Case vide ou non</li>
     *     <li>Couleur du pion si dans une case non vide</li>
     *     <li>Déplacement théorique du pion</li>
     * </ul>
     * @param posPiece position du Cavalier
     * @return <code>ArrayListe {@linkplain Case}</code> liste contenant toutes les cases sur lesquelles peut
     * se déplacer le cavalier
     * @see PlateauDeJeu#updateListeDeplacements(Boolean)
     * @author melissa, yohan
     */
    public ArrayList<Case> getDeplacementsPossiblesCavaliers(Case posPiece) {

        ArrayList<Case> listeCases = new ArrayList<>();

        int x = posPiece.x;
        int y = posPiece.y;

        if (x - 1 > 0 && y + 2 <= 8) {
            if (grille.getCase(x - 1, y + 2).estVide()) {
                listeCases.add(this.grille.getCase(x - 1, y + 2));
            } else {
                if (!grille.getCase(x - 1, y + 2).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x - 1, y + 2));
                }
            }
        }
        if (x - 2 > 0 && y + 1 <= 8) {
            if (grille.getCase(x - 2, y + 1).estVide()) {
                listeCases.add(this.grille.getCase(x - 2, y + 1));
            } else {
                if (!grille.getCase(x - 2, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x - 2, y + 1));
                }
            }
        }
        if (x - 2 > 0 && y - 1 > 0) {
            if (grille.getCase(x - 2, y - 1).estVide()) {
                listeCases.add(this.grille.getCase(x - 2, y - 1));
            } else {
                if (!grille.getCase(x - 2, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x - 2, y - 1));
                }
            }
        }
        if (x - 1 > 0 && y - 2 > 0) {
            if (grille.getCase(x - 1, y - 2).estVide()) {
                listeCases.add(this.grille.getCase(x - 1, y - 2));
            } else {
                if (!grille.getCase(x - 1, y - 2).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x - 1, y - 2));
                }
            }
        }
        if (x + 1 <= 8 && y - 2 > 0) {
            if (grille.getCase(x + 1, y - 2).estVide()) {
                listeCases.add(this.grille.getCase(x + 1, y - 2));
            } else {
                if (!grille.getCase(x + 1, y - 2).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x + 1, y - 2));
                }
            }
        }
        if (x + 2 <= 8 && y - 1 > 0) {
            if (grille.getCase(x + 2, y - 1).estVide()) {
                listeCases.add(this.grille.getCase(x + 2, y - 1));
            } else {
                if (!grille.getCase(x + 2, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x + 2, y - 1));
                }
            }
        }
        if (x + 1 <= 8 && y + 2 <= 8) {
            if (grille.getCase(x + 1, y + 2).estVide()) {
                listeCases.add(this.grille.getCase(x + 1, y + 2));
            } else {
                if (!grille.getCase(x + 1, y + 2).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x + 1, y + 2));
                }
            }
        }
        if (x + 2 <= 8 && y + 1 <= 8) {
            if (grille.getCase(x + 2, y + 1).estVide()) {
                listeCases.add(this.grille.getCase(x + 2, y + 1));
            } else {
                if (!grille.getCase(x + 2, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x + 2, y + 1));
                }
            }
        }
        return listeCases;
    }

    /**
     * Fonction récupérant les déplacements théoriques possibles simples d'un fou, c'est à dire qui prend en compte les
     * paramètres suivants :
     * <ul>
     *     <li>Case vide ou non</li>
     *     <li>Couleur du pion si dans une case non vide</li>
     *     <li>Déplacement théorique du pion</li>
     * </ul>
     * @param posPiece position du fou
     * @return <code>ArrayListe {@linkplain Case}</code> liste contenant toutes les cases sur lesquelles peut
     * se déplacer le fou
     * @see PlateauDeJeu#updateListeDeplacements(Boolean)
     * @author melissa, yohan
     */
    public ArrayList<Case> getDeplacementsPossiblesFou(Case posPiece, boolean prevoirPrise, boolean continuer) {

        ArrayList<Case> listeCases = new ArrayList<>();

        int x = posPiece.x;
        int y = posPiece.y;

        int a = x + 1;
        int b = y + 1;
        while (a <= 8 && b <= 8) {
            if (grille.getCase(a, b).estVide()) {
                listeCases.add(this.grille.getCase(a, b));
            } else {
                if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(a, b));
                }
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
            }
            a++;
            b++;
        }

        a = x + 1;
        b = y - 1;
        while (a <= 8 && b > 0) {
            if (grille.getCase(a, b).estVide()) {
                listeCases.add(this.grille.getCase(a, b));
            } else {
                if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(a, b));
                }
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
            }
            a++;
            b--;
        }

        a = x - 1;
        b = y + 1;
        while (a > 0 && b <= 8) {
            if (grille.getCase(a, b).estVide()) {
                listeCases.add(this.grille.getCase(a, b));
            } else {
                if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(a, b));
                }
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
            }
            a--;
            b++;
        }

        a = x - 1;
        b = y - 1;
        while (a > 0 && b > 0) {
            if (grille.getCase(a, b).estVide()) {
                listeCases.add(this.grille.getCase(a, b));
            } else {
                if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(a, b));
                }
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
            }
            a--;
            b--;

        }
        return listeCases;
    }

    /**
     * Fonction récupérant les déplacements théoriques possibles simples d'une reine, c'est à dire qui prend en compte les
     * paramètres suivants :
     * <ul>
     *     <li>Case vide ou non</li>
     *     <li>Couleur du pion si dans une case non vide</li>
     *     <li>Déplacement théorique du pion</li>
     * </ul>
     * @param posPiece position de la reine
     * @return <code>ArrayListe {@linkplain Case}</code> liste contenant toutes les cases sur lesquelles peut
     * se déplacer la reine
     * @see PlateauDeJeu#updateListeDeplacements(Boolean)
     * @author melissa, yohan
     */
    public ArrayList<Case> getDeplacementsPossiblesReine(Case posPiece, boolean prevoirPrise, boolean continuer) {

        ArrayList<Case> listeCases = new ArrayList<>();

        int x = posPiece.x;
        int y = posPiece.y;

        for (int i = x - 1; i > 0; i--) {
            if (grille.getCase(i, y).estVide()) {
                listeCases.add(this.grille.getCase(i, y));
            } else {
                if (!grille.getCase(i, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(i, y));
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(i, y));
                    }
                }
                if (!continuer) break;
            }
        }
        for (int i = x + 1; i <= 8; i++) {
            if (grille.getCase(i, y).estVide()) {
                listeCases.add(this.grille.getCase(i, y));
            } else {
                if (!grille.getCase(i, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(i, y));
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(i, y));
                    }
                }
                if (!continuer) break;
            }
        }
        for (int i = y - 1; i > 0; i--) {
            if (grille.getCase(x, i).estVide()) {
                listeCases.add(this.grille.getCase(x, i));
            } else {
                if (!grille.getCase(x, i).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x, i));
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(x, i));
                    }
                }
                if (!continuer) break;
            }
        }
        for (int i = y + 1; i <= 8; i++) {
            if (grille.getCase(x, i).estVide()) {
                listeCases.add(this.grille.getCase(x, i));
            } else {
                if (!grille.getCase(x, i).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x, i));
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(x, i));
                    }
                }
                if (!continuer) break;
            }
        }

        int a = x + 1;
        int b = y + 1;
        while (a <= 8 && b <= 8) {
            if (grille.getCase(a, b).estVide()) {
                listeCases.add(this.grille.getCase(a, b));
            } else {
                if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(a, b));
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
            }
            a++;
            b++;
        }

        a = x + 1;
        b = y - 1;
        while (a <= 8 && b > 0) {
            if (grille.getCase(a, b).estVide()) {
                listeCases.add(this.grille.getCase(a, b));
            } else {
                if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(a, b));
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
            }
            a++;
            b--;
        }

        a = x - 1;
        b = y + 1;
        while (a > 0 && b <= 8) {
            if (grille.getCase(a, b).estVide()) {
                listeCases.add(this.grille.getCase(a, b));
            } else {
                if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(a, b));
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
            }
            a--;
            b++;
        }

        a = x - 1;
        b = y - 1;
        while (a > 0 && b > 0) {
            if (grille.getCase(a, b).estVide()) {
                listeCases.add(this.grille.getCase(a, b));
            } else {
                if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(a, b));
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
            }
            a--;
            b--;
        }
        return listeCases;
    }

    /**
     * Fonction récupérant les déplacements théoriques possibles simples d'une tour, c'est à dire qui prend en compte les
     * paramètres suivants :
     * <ul>
     *     <li>Case vide ou non</li>
     *     <li>Couleur du pion si dans une case non vide</li>
     *     <li>Déplacement théorique du pion</li>
     * </ul>
     * @param posPiece position de la tour
     * @return <code>ArrayListe {@linkplain Case}</code> liste contenant toutes les cases sur lesquelles peut
     * se déplacer la tour
     * @see PlateauDeJeu#updateListeDeplacements(Boolean)
     * @author melissa, yohan
     */
    public ArrayList<Case> getDeplacementsPossiblesTours(Case posPiece, boolean prevoirPrise, boolean continuer) {

        ArrayList<Case> listeCases = new ArrayList<>();

        int x = posPiece.x;
        int y = posPiece.y;

        for (int i = x - 1; i > 0; i--) {
            if (grille.getCase(i, y).estVide()) {
                listeCases.add(this.grille.getCase(i, y));
            } else {
                if (!grille.getCase(i, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(i, y));
                }
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(i, y));
                    }
                }
                if (!continuer) break;
            }
        }
        for (int i = x + 1; i <= 8; i++) {
            if (grille.getCase(i, y).estVide()) {
                listeCases.add(this.grille.getCase(i, y));
            } else {
                if (!grille.getCase(i, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(i, y));
                }
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(i, y));
                    }
                }
                if (!continuer) break;
            }
        }
        for (int i = y - 1; i > 0; i--) {
            if (grille.getCase(x, i).estVide()) {
                listeCases.add(this.grille.getCase(x, i));
            } else {
                if (!grille.getCase(x, i).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x, i));
                }
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(x, y));
                    }
                }
                if (!continuer) break;
            }
        }
        for (int i = y + 1; i <= 8; i++) {
            if (grille.getCase(x, i).estVide()) {
                listeCases.add(this.grille.getCase(x, i));
            } else {
                if (!grille.getCase(x, i).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x, i));
                }
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(x, y));
                    }
                }
                if (!continuer) break;
            }
        }
        return listeCases;
    }

    /**
     * Fonction récupérant les déplacements théoriques possibles simples d'un pion, c'est à dire qui prend en compte les
     * paramètres suivants :
     * <ul>
     *     <li>Case vide ou non</li>
     *     <li>Couleur du pion si dans une case non vide</li>
     *     <li>Déplacement théorique du pion</li>
     * </ul>
     * @param posPiece position du pion
     * @return <code>ArrayListe {@linkplain Case}</code> liste contenant toutes les cases sur lesquelles peut
     * se déplacer le pion
     * @see PlateauDeJeu#updateListeDeplacements(Boolean)
     * @author melissa, yohan
     */
    public ArrayList<Case> getDeplacementsPossiblesPions(Case posPiece) {

        ArrayList<Case> listeCases = new ArrayList<>();

        int x = posPiece.x;
        int y = posPiece.y;

        String classePiece = grille.getCase(posPiece).piece.getClassePiece();
        String couleurPiece = grille.getCase(posPiece).piece.getCouleur();

            if (classePiece.equalsIgnoreCase("Pion")) {
            if (couleurPiece.equalsIgnoreCase("Blanc")) {
                if (x <= 8 && x > 0 && y + 1 <= 8) {
                    if (grille.getCase(x, y + 1).estVide()) {
                        listeCases.add(this.grille.getCase(x, y + 1));
                    }
                }
                if (y == 2) {
                    if (grille.getCase(x, y + 2).estVide()) {
                        listeCases.add(this.grille.getCase(x, y + 2));
                    }
                }
                if (x + 1 <= 8 && y + 1 <= 8) {
                    if (grille.getCase(x + 1, y + 1).piece != null) {
                        if (!grille.getCase(x + 1, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                            listeCases.add(this.grille.getCase(x + 1, y + 1));
                        }
                    }
                }
                if (x - 1 > 0 && y + 1 <= 8) {
                    if (grille.getCase(x - 1, y + 1).piece != null) {
                        if (!grille.getCase(x - 1, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                            listeCases.add(this.grille.getCase(x - 1, y + 1));
                        }
                    }
                }
            }
            if (couleurPiece.equalsIgnoreCase("Noir")) {
                if ( x > 0 && x <= 8 && y - 1 > 0) {
                    if (grille.getCase(x, y - 1).estVide()) {
                        listeCases.add(this.grille.getCase(x, y - 1));
                    }
                }
                if (y == 7) {
                    if (grille.getCase(x, y - 2).estVide()) {
                        listeCases.add(this.grille.getCase(x, y - 2));
                    }
                }
                if (x + 1 <= 8 && y - 1 > 0) {
                    if (grille.getCase(x + 1, y - 1).piece != null) {
                        if (!grille.getCase(x + 1, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                            listeCases.add(this.grille.getCase(x + 1, y - 1));
                        }
                    }
                }
                if (x - 1 > 0 && y - 1 > 0) {
                    if (grille.getCase(x - 1, y - 1).piece != null) {
                        if (!grille.getCase(x - 1, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                            listeCases.add(this.grille.getCase(x - 1, y - 1));
                        }
                    }
                }
            }
        }
            return listeCases;
    }

    /**
     * Fonction récupérant les déplacements avancés d'un roi, c'est à dire qui prend en compte les
     * paramètres suivants :
     * <ul>
     *     <li>Case vide ou non</li>
     *     <li>Couleur du roi si dans une case non vide</li>
     *     <li>Déplacement théorique du roi</li>
     *     <li>La possibilité de se mettre en echec à cause de ce mouvement</li>
     * </ul>
     * @param posPiece position du roi
     * @return <code>ArrayListe {@linkplain Case}</code> liste contenant toutes les cases sur lesquelles peut
     * se déplacer le roi
     * @see PlateauDeJeu#updateListeDeplacements(Boolean)
     * @author melissa, yohan
     */
    public ArrayList<Case> getDeplacementsPossiblesRoi(Case posPiece, boolean prevoirPrise) {

        ArrayList<Case> listeCases = new ArrayList<>();

        int x = posPiece.x;
        int y = posPiece.y;

        if (x + 1 <= 8 && y + 1 <= 8) {
            if (grille.getCase(x + 1, y + 1).estVide()) {
                if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x + 1, y + 1)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x + 1, y + 1));
                }
            } else {
                if (!grille.getCase(x + 1, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                   if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x + 1, y + 1), true).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x + 1, y + 1));
                    }
                }
            }
        }
        if (x - 1 > 0 && y + 1 <= 8) {
            if (grille.getCase(x - 1, y + 1).estVide()) {
                if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x - 1, y + 1)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x - 1, y + 1));
                }
            } else {
                if (!grille.getCase(x - 1, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x - 1, y + 1)).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x - 1, y + 1));
                    }
                }
            }
        }
        //x, y + 1
        if (y + 1 <= 8) {
            if (grille.getCase(x, y + 1).estVide()) {
                if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x, y + 1)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x, y + 1));
                }
            } else {
                if (!grille.getCase(x, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x, y + 1)).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x, y + 1));
                    }
                }
            }
        }
        //x, y - 1
        if (y - 1 > 0) {
            if (grille.getCase(x, y - 1).estVide()) {
                if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x, y - 1)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x, y - 1));
                }
            } else {
                if (!grille.getCase(x, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x, y - 1)).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x, y - 1));
                    }
                }
            }
        }
        //x + 1, y
        if (x + 1 <= 8) {
            if (grille.getCase(x + 1, y).estVide()) {
                if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x + 1, y)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x + 1, y));
                }
            } else {
                if (!grille.getCase(x + 1, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x + 1, y)).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x + 1, y));
                    }
                }
            }
        }
        //x - 1, y
        if (x - 1 > 0 ) {
            if (grille.getCase(x - 1, y).estVide()) {
                if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x - 1, y)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x - 1, y));
                }
            } else {
                if (!grille.getCase(x - 1, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x - 1, y)).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x - 1, y));
                    }
                }
            }
        }
        //x - 1, y - 1
        if (x - 1 > 0 && y - 1 > 0) {
            if (grille.getCase(x - 1, y - 1).estVide()) {
                if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x - 1, y - 1)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x - 1, y - 1));
                }
            } else {
                if (!grille.getCase(x - 1, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x - 1, y - 1)).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x - 1, y - 1));
                    }
                }
            }
        }
        //x + 1, y - 1
        if (x + 1 <= 8 && y - 1 > 0) {
            if (grille.getCase(x + 1, y - 1).estVide()) {
                if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x + 1, y - 1)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x + 1, y - 1));
                }
            } else {
                if (!grille.getCase(x + 1, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    if (detecterEchec(grille.getCase(posPiece).piece, grille.getCase(x + 1, y - 1)).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x + 1, y - 1));
                    }
                }
            }
        }
        return listeCases;
    }

    /**
     * Fonction mettant à jour :
     * <ul>
     *     <li>{@linkplain PlateauDeJeu#listeDeplacementBlancs}</li>
     *     <li>{@linkplain PlateauDeJeu#listeDeplacementNoirs}</li>
     * </ul>
     * @param pionAvanceOnly Boolean permettant de préciser si l'on doit compter comme mouvement pour les pions
     *                       leurs prises, ou leurs déplacements, par exemple un pion ne peut pas avancer en diagonal
     *                       si la case est vide.
     * @author melissa, yohan
     */
    public void updateListeDeplacements(Boolean pionAvanceOnly) {
        listeDeplacementBlancs = this.generateListeDeplacementsBlancs(pionAvanceOnly, false, false);
        listeDeplacementNoirs = this.generateListeDeplacementsNoirs(pionAvanceOnly, false, false);
    }

    /**
     * Fonction générant la liste des {@linkplain Deplacement} possibles des blancs
     * @param pionAvanceOnly Boolean permettant de préciser si l'on doit compter comme mouvement pour les pions
     *                       leurs prises, ou leurs déplacements, par exemple un pion ne peut pas avancer en diagonal
     *                       si la case est vide.
     * @param prevoirPrise Boolean déterminant si un pion doit considérer une case contenant un de ses allié au cas où cette dernier
     *                     viendrait à être prise, dans quel cas cette case contenant initialement son allié, pourrait alors
     *                     contenir un ennemi
     * @param continuer Boolean indiquant si une pièce à projection linéaire doit ignorer les pions sur son passage et prendre
     *                  toutes les lignes comme déplacement possible malgré leur contenu. Cela permet de considérer une case
     *                  habituellement cachée par un roi met tout de même dans l'alignement avec la pièce. Case dans laquelle
     *                  dès lors le roi ne pourrait plus se déplacer car considérer dans les déplacements possibles de la pièce
     *                  grâce à ce paramètre
     * @return Liste des {@linkplain Deplacement} des blancs
     * @see Deplacement
     */
    public ArrayList<Deplacement> generateListeDeplacementsBlancs(Boolean pionAvanceOnly, Boolean prevoirPrise, Boolean continuer) {
        ArrayList<Case> listeBlancs = this.grille.getListePieceCouleur("blanc");
        ArrayList<Deplacement> listeDeplacementBlancs = new ArrayList<>();
        ArrayList<Case> l;

        for (Case c : listeBlancs) {
            if (c.piece.getClassePiece().equalsIgnoreCase("pion")) {
                l = new ArrayList<>();
                if (pionAvanceOnly) {
                    if (this.grille.getCase(c.x, c.y + 1).estVide()) {
                        l.add(this.grille.getCase(c.x, c.y + 1));
                    }
                    if (this.grille.getCase(c.x, c.y + 2).estVide() && c.y == 2) {
                        l.add(this.grille.getCase(c.x, c.y + 2));
                    }
                } else {
                    if (c.x +1 <= 8 && c.y + 1 <= 8) {
                        if (!this.grille.getCase(c.x + 1, c.y + 1).estVide()) {
                            l.add(this.grille.getCase(c.x + 1, c.y + 1));
                        }
                    }
                    if (c.x - 1 > 0 && c.y + 1 <= 8) {
                        if (!this.grille.getCase(c.x - 1, c.y + 1).estVide()) {
                            l.add(this.grille.getCase(c.x - 1, c.y + 1));
                        }
                    }
                }
                listeDeplacementBlancs.add(new Deplacement(c, l));
            } else if (c.piece.getClassePiece().equalsIgnoreCase("roi")) {
                l = new ArrayList<>();
                if (c.y + 1 <= 8) {
                    if (!this.grille.getCase(c.x, c.y + 1).estVide()) {
                        if (!this.grille.getCase(c.x, c.y + 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                            l.add(this.grille.getCase(c.x, c.y + 1));
                        }
                    } else l.add(this.grille.getCase(c.x, c.y + 1));
                }
                if (c.y - 1 > 0) {
                    if (!this.grille.getCase(c.x, c.y - 1).estVide()) {
                        if (!this.grille.getCase(c.x, c.y - 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                            l.add(this.grille.getCase(c.x, c.y - 1));
                        }
                    } else l.add(this.grille.getCase(c.x, c.y - 1));
                }
                listeDeplacementBlancs.add(new Deplacement(c, l));

                l = new ArrayList<>();
                if (c.x + 1 <= 8) {
                    if (c.y + 1 <= 8) {
                        if (!this.grille.getCase(c.x + 1, c.y + 1).estVide()) {
                            if (!this.grille.getCase(c.x + 1, c.y + 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                                l.add(this.grille.getCase(c.x + 1, c.y + 1));
                            }
                        } else l.add(this.grille.getCase(c.x + 1, c.y + 1));;
                    }
                    if (c.y - 1 > 0) {
                        if (!this.grille.getCase(c.x + 1, c.y - 1).estVide()) {
                            if (!this.grille.getCase(c.x + 1, c.y - 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                                l.add(this.grille.getCase(c.x + 1, c.y - 1));
                            }
                        } else l.add(this.grille.getCase(c.x + 1, c.y - 1));
                    }
                    if (!this.grille.getCase(c.x + 1, c.y).estVide()) {
                        if (!this.grille.getCase(c.x + 1, c.y).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                            l.add(this.grille.getCase(c.x + 1, c.y));
                        }
                    } else l.add(this.grille.getCase(c.x + 1, c.y));
                }
                listeDeplacementBlancs.add(new Deplacement(c, l));

                l = new ArrayList<>();
                if (c.x - 1 > 0) {
                    if (c.y + 1 <= 8) {
                        if (!this.grille.getCase(c.x - 1, c.y + 1).estVide()) {
                            if (!this.grille.getCase(c.x - 1, c.y + 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                                l.add(this.grille.getCase(c.x - 1, c.y + 1));
                            }
                        } else l.add(this.grille.getCase(c.x - 1, c.y + 1));
                    }
                    if (c.y - 1 > 0) {
                        if (!this.grille.getCase(c.x - 1, c.y - 1).estVide()) {
                            if (!this.grille.getCase(c.x - 1, c.y - 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                                l.add(this.grille.getCase(c.x - 1, c.y - 1));
                            }
                        } else l.add(this.grille.getCase(c.x - 1, c.y - 1));
                    }
                    if (!this.grille.getCase(c.x - 1, c.y).estVide()) {
                        if (!this.grille.getCase(c.x - 1, c.y).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                            l.add(this.grille.getCase(c.x - 1, c.y));
                        }
                    } else l.add(this.grille.getCase(c.x - 1, c.y - 1));
                }
                listeDeplacementBlancs.add(new Deplacement(c, l));
            } else {
                l = new ArrayList<>();
                for (Case d : this.deplacementsPossible(c, false, continuer)) {
                    l.add(d);
                }
                listeDeplacementBlancs.add(new Deplacement(c, l));
            }
        }
        ArrayList<Deplacement> temp = new ArrayList<>();
        for (Deplacement d : listeDeplacementBlancs) {
            if (d.hasDeplacement()) {
                temp.add(d);
            }
        }
        return temp;
    }

    /**
     * Fonction générant la liste des {@linkplain Deplacement} possibles des noirs
     * @param pionAvanceOnly Boolean permettant de préciser si l'on doit compter comme mouvement pour les pions
     *                       leurs prises, ou leurs déplacements, par exemple un pion ne peut pas avancer en diagonal
     *                       si la case est vide.
     * @param prevoirPrise Boolean déterminant si un pion doit considérer une case contenant un de ses allié au cas où cette dernier
     *                     viendrait à être prise, dans quel cas cette case contenant initialement son allié, pourrait alors
     *                     contenir un ennemi
     * @param continuer Boolean indiquant si une pièce à projection linéaire doit ignorer les pions sur son passage et prendre
     *                  toutes les lignes comme déplacement possible malgré leur contenu. Cela permet de considérer une case
     *                  habituellement cachée par un roi met tout de même dans l'alignement avec la pièce. Case dans laquelle
     *                  dès lors le roi ne pourrait plus se déplacer car considérer dans les déplacements possibles de la pièce
     *                  grâce à ce paramètre
     * @return Liste des {@linkplain Deplacement} des noirs
     * @see Deplacement
     */
    public ArrayList<Deplacement> generateListeDeplacementsNoirs(Boolean pionAvanceOnly, boolean prevoirPrise, boolean continuer) {
        ArrayList<Case> listeNoirs;
        ArrayList<Deplacement> listeDeplacementNoirs = new ArrayList<>();
        ArrayList<Case> l;
        listeNoirs = this.grille.getListePieceCouleur("noir");

        for (Case c : listeNoirs) {
            if (c.piece.getClassePiece().equalsIgnoreCase("pion")) {
                l = new ArrayList<>();
                if (pionAvanceOnly) {
                    if (this.grille.getCase(c.x, c.y - 1).estVide()) {
                        l.add(this.grille.getCase(c.x, c.y - 1));
                    }
                    if (this.grille.getCase(c.x, c.y - 2).estVide() && c.y == 7) {
                        l.add(this.grille.getCase(c.x, c.y - 2));
                    }
                } else {
                    if (c.x + 1 <= 8 && c.y - 1 > 0) {
                        if (!this.grille.getCase(c.x + 1, c.y - 1).estVide()) {
                            l.add(this.grille.getCase(c.x + 1, c.y - 1));
                        }
                    }
                    if (c.x - 1 > 0 && c.y - 1 > 0) {
                        if (!this.grille.getCase(c.x - 1, c.y - 1).estVide()) {
                            l.add(this.grille.getCase(c.x - 1, c.y - 1));
                        }
                    }
                }
                listeDeplacementNoirs.add(new Deplacement(c, l));
            } else if (c.piece.getClassePiece().equalsIgnoreCase("roi")) {
                l = new ArrayList<>();
                if (c.y + 1 <= 8) {
                    if (!this.grille.getCase(c.x, c.y + 1).estVide()) {
                        if (!this.grille.getCase(c.x, c.y + 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                            l.add(this.grille.getCase(c.x, c.y + 1));
                        }
                    } else l.add(this.grille.getCase(c.x, c.y + 1));
                }
                if (c.y - 1 > 0) {
                    if (!this.grille.getCase(c.x, c.y - 1).estVide()) {
                        if (!this.grille.getCase(c.x, c.y - 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                            l.add(this.grille.getCase(c.x, c.y - 1));
                        }
                    } else l.add(this.grille.getCase(c.x, c.y - 1));
                }
                listeDeplacementNoirs.add(new Deplacement(c, l));

                l = new ArrayList<>();
                if (c.x + 1 <= 8) {
                    if (c.y + 1 <= 8) {
                        if (!this.grille.getCase(c.x + 1, c.y + 1).estVide()) {
                            if (!this.grille.getCase(c.x + 1, c.y + 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                                l.add(this.grille.getCase(c.x + 1, c.y + 1));
                            }
                        } else l.add(this.grille.getCase(c.x + 1, c.y + 1));;
                    }
                    if (c.y - 1 > 0) {
                        if (!this.grille.getCase(c.x + 1, c.y - 1).estVide()) {
                            if (!this.grille.getCase(c.x + 1, c.y - 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                                l.add(this.grille.getCase(c.x + 1, c.y - 1));
                            }
                        } else l.add(this.grille.getCase(c.x + 1, c.y - 1));
                    }
                    if (!this.grille.getCase(c.x + 1, c.y).estVide()) {
                        if (!this.grille.getCase(c.x + 1, c.y).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                            l.add(this.grille.getCase(c.x + 1, c.y));
                        }
                    } else l.add(this.grille.getCase(c.x + 1, c.y));
                }
                listeDeplacementNoirs.add(new Deplacement(c, l));

                l = new ArrayList<>();
                if (c.x - 1 > 0) {
                    if (c.y + 1 <= 8) {
                        if (!this.grille.getCase(c.x - 1, c.y + 1).estVide()) {
                            if (!this.grille.getCase(c.x - 1, c.y + 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                                l.add(this.grille.getCase(c.x - 1, c.y + 1));
                            }
                        } else l.add(this.grille.getCase(c.x - 1, c.y + 1));
                    }
                    if (c.y - 1 > 0) {
                        if (!this.grille.getCase(c.x - 1, c.y - 1).estVide()) {
                            if (!this.grille.getCase(c.x - 1, c.y - 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                                l.add(this.grille.getCase(c.x - 1, c.y - 1));
                            }
                        } else l.add(this.grille.getCase(c.x - 1, c.y - 1));
                    }
                    if (!this.grille.getCase(c.x - 1, c.y).estVide()) {
                        if (!this.grille.getCase(c.x - 1, c.y).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                            l.add(this.grille.getCase(c.x - 1, c.y));
                        }
                    } else l.add(this.grille.getCase(c.x - 1, c.y - 1));
                }
                listeDeplacementNoirs.add(new Deplacement(c, l));
            } else {
                l = new ArrayList<>();
                for (Case d : this.deplacementsPossible(c, false, continuer)) {
                    l.add(d);
                }
                listeDeplacementNoirs.add(new Deplacement(c, l));
            }
        }
        ArrayList<Deplacement> temp = new ArrayList<>();
        for (Deplacement d : listeDeplacementNoirs) {
            if (d.hasDeplacement()) {
                temp.add(d);
            }
        }
        return temp;
    }

    /**
     * Fonction déterminant si la case donnée met en echec le roi donnée
     * @param pieceRoi Roi potentiellement en echec
     * @param posRoi Case où il peut potientiellement se mettre en situation d'échec
     * @return {@linkplain Echec}
     * @see EchecApplication
     * @see Echec
     * @author yohan
     */
    public Echec detecterEchec(Piece pieceRoi, Case posRoi) {
        return detecterEchec(pieceRoi, posRoi, false);
    }

    /**
     * Fonction déterminant si la case donnée met en echec le roi donnée
     * @param pieceRoi Roi potentiellement en echec
     * @param posRoi Case où il peut potientiellement se mettre en situation d'échec
     * @param prevoirPrise Boolean déterminant si un pion doit considérer une case contenant un de ses allié au cas où cette dernier
     *                     viendrait à être prise, dans quel cas cette case contenant initialement son allié, pourrait alors
     *                     contenir un ennemi
     * @return {@linkplain Echec}
     * @see EchecApplication
     * @see Echec
     * @author yohan
     */
    public Echec detecterEchec(Piece pieceRoi, Case posRoi, boolean prevoirPrise) {

        String couleurEnnemie = (pieceRoi.getCouleur() == "noir") ? "blanc" : "noir";
        ArrayList<Deplacement> listeDeplacement = new ArrayList<>();

        if (couleurEnnemie.equals("noir")) {
            for (Deplacement d : generateListeDeplacementsNoirs(false, true, false)) {
                if (d.contains(posRoi)) {
                    listeDeplacement.add(d);
                }
            }
        }
        if (couleurEnnemie.equals("blanc")) {
            for (Deplacement d : generateListeDeplacementsBlancs(false, true, false)) {
                if (d.contains(posRoi)) {
                    listeDeplacement.add(d);
                }
            }
        }

        Echec echec = new Echec(this, posRoi, pieceRoi.getCouleur(), listeDeplacement);
        this.echec = echec;
        return echec;
    }

    /**
     * Fonction permettant de choisir des déplacements basant sur le score des pièces qu'il peut
     * prendre
     * @author Melissa
     * @author Yohan
     */
    public void IA() {
        // ArrayList contenant les scores des pièces qu'on peut prendre.
        ArrayList<Integer> scores = new ArrayList<Integer>();
        // ArrayList contenant les cases d'origine des pièces qui peuvent prendre des pièces de l'adversaire.
        ArrayList<Case> casesOrigine = new ArrayList<>();
        // ArrayList contenant les cases où se trouvent les pièces qu'on peut prendre.
        ArrayList<Case> casesDestination = new ArrayList<>();
        // ArrayList contenant les cases d'origine des pièces qui ne peuvent que déplacer.
        ArrayList<Case> casesOrigine0 = new ArrayList<>();
        // ArrayList contenant les cases où on peut déplacer.
        ArrayList<Case> casesDestination0 = new ArrayList<>();
        // Variable contenant la case d'origine de la pièce qu'on bougera.
        Case caseOrigine;
        // Variable contenant la case où on va bouger notre pièce.
        Case caseDestination;

        // Verifier pour chaque pièce noire s'il y a possibilité de prendre une pièce blanche.
        for (int i = 1 ; i <= 8 ; i++) {
            for (int j = 1; j <= 8 ; j++) {
                if (grille.getCase(i, j).piece != null) {

                    // Verifier si Pion_noir_1 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Pion_noir_1.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_1")) {
                        ArrayList<Case> deplacementsPion1 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion1.size() ; a++) {
                            if (deplacementsPion1.get(a).piece != null) {
                                scores.add(deplacementsPion1.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion1.get(a)));
                            }
                            if (deplacementsPion1.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion1.get(a)));
                            }
                        }
                    }

                    // Verifier si Pion_noir_2 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Pion_noir_2.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_2")) {
                        ArrayList<Case> deplacementsPion2 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion2.size() ; a++) {
                            if (deplacementsPion2.get(a).piece != null) {
                                scores.add(deplacementsPion2.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion2.get(a)));
                            }
                            if (deplacementsPion2.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion2.get(a)));
                            }
                        }
                    }

                    // Verifier si Pion_noir_3 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Pion_noir_3.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_3")) {
                        ArrayList<Case> deplacementsPion3 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion3.size() ; a++) {
                            if (deplacementsPion3.get(a).piece != null) {
                                scores.add(deplacementsPion3.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion3.get(a)));
                            }
                            if (deplacementsPion3.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion3.get(a)));
                            }
                        }
                    }

                    // Verifier si Pion_noir_4 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Pion_noir_4.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_4")) {
                        ArrayList<Case> deplacementsPion4 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion4.size() ; a++) {
                            if (deplacementsPion4.get(a).piece != null) {
                                scores.add(deplacementsPion4.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion4.get(a)));
                            }
                            if (deplacementsPion4.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion4.get(a)));
                            }
                        }
                    }

                    // Verifier si Pion_noir_5 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Pion_noir_5.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_5")) {
                        ArrayList<Case> deplacementsPion5 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion5.size() ; a++) {
                            if (deplacementsPion5.get(a).piece != null) {
                                scores.add(deplacementsPion5.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion5.get(a)));
                            }
                            if (deplacementsPion5.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion5.get(a)));
                            }
                        }
                    }

                    // Verifier si Pion_noir_6 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Pion_noir_6.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_6")) {
                        ArrayList<Case> deplacementsPion6 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion6.size() ; a++) {
                            if (deplacementsPion6.get(a).piece != null) {
                                scores.add(deplacementsPion6.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion6.get(a)));
                            }
                            if (deplacementsPion6.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion6.get(a)));
                            }
                        }
                    }

                    // Verifier si Pion_noir_7 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Pion_noir_7.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_7")) {
                        ArrayList<Case> deplacementsPion7 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion7.size() ; a++) {
                            if (deplacementsPion7.get(a).piece != null) {
                                scores.add(deplacementsPion7.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion7.get(a)));
                            }
                            if (deplacementsPion7.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion7.get(a)));
                            }
                        }
                    }

                    // Verifier si Pion_noir_7 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Pion_noir_7.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_8")) {
                        ArrayList<Case> deplacementsPion8 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion8.size() ; a++) {
                            if (deplacementsPion8.get(a).piece != null) {
                                scores.add(deplacementsPion8.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion8.get(a)));
                            }
                            if (deplacementsPion8.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion8.get(a)));
                            }
                        }
                    }

                    // Verifier si Cavalier_noir_1 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Cavalier_noir_1.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Cavalier_noir_1")) {
                        ArrayList<Case> deplacementsCavalier1 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsCavalier1.size() ; a++) {
                            if (deplacementsCavalier1.get(a).piece != null) {
                                scores.add(deplacementsCavalier1.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsCavalier1.get(a)));
                            }
                            if (deplacementsCavalier1.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsCavalier1.get(a)));
                            }
                        }
                    }

                    // Verifier si Cavalier_noir_2 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Cavalier_noir_2.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Cavalier_noir_2")) {
                        ArrayList<Case> deplacementsCavalier2 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsCavalier2.size() ; a++) {
                            if (deplacementsCavalier2.get(a).piece != null) {
                                scores.add(deplacementsCavalier2.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsCavalier2.get(a)));
                            }
                            if (deplacementsCavalier2.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsCavalier2.get(a)));
                            }
                        }
                    }

                    // Verifier si Fou_noir_1 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Fou_noir_1.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Fou_noir_1")) {
                        ArrayList<Case> deplacementsFou1 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsFou1.size() ; a++) {
                            if (deplacementsFou1.get(a).piece != null) {
                                scores.add(deplacementsFou1.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsFou1.get(a)));
                            }
                            if (deplacementsFou1.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsFou1.get(a)));
                            }
                        }
                    }

                    // Verifier si Fou_noir_2 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Fou_noir_2.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Fou_noir_2")) {
                        ArrayList<Case> deplacementsFou2 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsFou2.size() ; a++) {
                            if (deplacementsFou2.get(a).piece != null) {
                                scores.add(deplacementsFou2.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsFou2.get(a)));
                            }
                            if (deplacementsFou2.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsFou2.get(a)));
                            }
                        }
                    }

                    // Verifier si Tour_noir_1 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Tour_noir_1.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Tour_noir_1")) {
                        ArrayList<Case> deplacementsTour1 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsTour1.size() ; a++) {
                            if (deplacementsTour1.get(a).piece != null) {
                                scores.add(deplacementsTour1.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsTour1.get(a)));
                            }
                            if (deplacementsTour1.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsTour1.get(a)));
                            }
                        }
                    }

                    // Verifier si Tour_noir_2 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Tour_noir_2.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Tour_noir_2")) {
                        ArrayList<Case> deplacementsTour2 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsTour2.size() ; a++) {
                            if (deplacementsTour2.get(a).piece != null) {
                                scores.add(deplacementsTour2.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsTour2.get(a)));
                            }
                            if (deplacementsTour2.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsTour2.get(a)));
                            }
                        }
                    }

                    // Verifier si Reine_noir_1 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Reine_noir_1.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Reine_noir_1")) {
                        ArrayList<Case> deplacementsReine = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsReine.size() ; a++) {
                            if (deplacementsReine.get(a).piece != null) {
                                scores.add(deplacementsReine.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsReine.get(a)));
                            }
                            if (deplacementsReine.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsReine.get(a)));
                            }
                        }
                    }

                    // Verifier si Roi_noir_1 peut prendre des pièces.
                    // Si oui, sauvergarder les score et les cases des pièces qu'on peut prendre.
                    // Si non, sauvegarder les cases des déplacements possibles de Roi_noir_1.
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Roi_noir_1")) {
                        ArrayList<Case> deplacementsRoi = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsRoi.size() ; a++) {
                            if (deplacementsRoi.get(a).piece != null) {
                                scores.add(deplacementsRoi.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsRoi.get(a)));
                            }
                            if (deplacementsRoi.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsRoi.get(a)));
                            }
                        }
                    }
                }

            }
        }

        // Si aucune des pièces noires ne peut prendre une pièce blanche, on choisit un déplacement aléatoirement.
        // Si non, on prend la pièce blanche qui a le score le plus grand.
        if (scores.isEmpty()) {
            Random rand = new Random();
            int index = rand.nextInt(casesOrigine0.size());
            caseOrigine = casesOrigine0.get(index);
            caseDestination = casesDestination0.get(index);
            deplacerPiece(caseOrigine, caseDestination);
        } else {
            Integer maxScore = scores.get(0);
            for (int i = 1 ; i < scores.size() ; i++) {
                if (scores.get(i) > maxScore) maxScore = scores.get(i);
            }
            int index = scores.indexOf(maxScore);
            caseOrigine = casesOrigine.get(index);
            caseDestination = casesDestination.get(index);
            prendrePiece(caseOrigine, caseDestination);
        }

    }

    /**
     * Fonction gérant les echecs pour l'IA, et jouant seulement les coups possibles qui la sauverait d'un echec
     * @param echecApplication {@linkplain EchecApplication} Classe Main contenant toutes les informations nécéssaires
     * pour l'IA pour déterminer ses meilleurs options possibles
     * @author yohan
     */
    public void IAEchec(EchecApplication echecApplication) {

        // On récupère tous les déplacements possibles des sauveurs du Jeu
        ArrayList<Deplacement> listeDeplacementsPossibles = echecApplication.getJeu().plateau.getEchec().getSauveur();

        // On sélectionne une origine random dans la liste
        Random rand = new Random();
        Integer choix = rand.nextInt(listeDeplacementsPossibles.size());

        // Dans cette origine on sélectionne un déplacement random
        Integer choixDeplacement = rand.nextInt(listeDeplacementsPossibles.get(choix).getDeplacement().size());
 
        // On format en Case les résultats pour qu'ils soient utilisables
        Case origine = listeDeplacementsPossibles.get(choix).getOrigine();
        Case destination = listeDeplacementsPossibles.get(choix).getDeplacement().get(choixDeplacement);

        // Si la destination est pleine alors c'est une prise, sinon c'est un déplacement
        if (destination.piece == null) {
            deplacerPiece(origine, destination);
        }
        else
        {
            prendrePiece(origine, destination);
        }

    }
}
