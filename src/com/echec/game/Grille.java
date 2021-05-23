package com.echec.game;

import com.echec.Tools;
import com.echec.pieces.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Matrice d'un échuiquier, représente un ensemble de 64 {@linkplain Case}, avec chacune une coordonnée
 * x : colonne, et y : ligne. Cette classe sert d'intermediaire entre le {@linkplain PlateauDeJeu} et ses Cases.
 * En offrant des fonctions de lecture des cases selon differentes méthodes.
 * @see Grille#getCase(int, int)
 * @see Grille#getCase(Case)
 * @see Piece
 * @see PlateauDeJeu
 * @author yohan
 */
public class Grille {

    /**
     * Identifiant de la grille, par défaut sa date de création au format <code>yyyyMMdd-HHmmss</code>
     */
    private String id;

    /**
     * {@linkplain ArrayList} contenant la liste des {@linkplain Case}, c'est dans cette liste que la grille
     * ira chercher les cases.
     * @see Grille#getCase(int, int)
     * @see Grille#getCase(Case)
     * @see Piece
     * @see PlateauDeJeu
     */
    private ArrayList<Case> grilleCases = new ArrayList<>();

    /**
     * Constructeur par défaut d'une {@linkplain Grille}, créé 8 lignes et 8 colonnes numérotées de
     * 1 à 8. Seulement ensuite les numéros de colonnes seront traduis en lettres selon les normes
     * d'eches
     * @see Tools#getLettreColonne(int)
     * @see Case
     * @author yohan
     */
    public Grille() {
        boolean noir = true;
        this.id = "grille " + Tools.getFormatDate();
        for (int ligne = 1; ligne <= 8; ligne++) {
            noir = !noir;
            for (int colonne = 8; colonne > 0; colonne --) {
                grilleCases.add(new Case(ligne, colonne, noir ? "noir" : "blanc"));
                noir = !noir;
            }
        }
    }

    /**
     * Constructeur par recopie d'une {@linkplain Grille} en copiant une grille
     * déjà sauvegardée dans un objet json
     * @param jsonObject <code>JSONObject</code> : Sauvegarde d'une grille au format Json
     * @see Grille
     * @see Piece#getJSONObject()
     * @see Case#getJSONObject()
     * @see PlateauDeJeu#getJSONObject()
     * @author yohan
     */
    public Grille(JSONObject jsonObject) {
        this.id = (String) jsonObject.get("idGrille");
        JSONArray jsonArray = (JSONArray) jsonObject.get("grilleCases");
        Iterator<JSONObject> iterator = jsonArray.iterator();
        this.grilleCases = new ArrayList<>();
        while (iterator.hasNext()) {
            JSONObject pieceJsonObject = iterator.next();
            this.grilleCases.add(new Case(pieceJsonObject));
        }
    }

    /**
     * Fonction initialisant la {@linkplain Grille} avec les pièces d'un échiquier sur leurs cases attitrées,
     * avec toujours les blancs en ligne 1.
     * @see Case
     * @see Piece
     * @author yohan
     */
    public void initialiserGrille() {

        // Noirs en ligne 8
        this.getCase(1, 8).ajouterPiece(new Tour("Tour_Noir_1", "noir", true));
        this.getCase(2, 8).ajouterPiece(new Cavalier("Cavalier_Noir_1", "noir", true));
        this.getCase(3, 8).ajouterPiece(new Fou("Fou_Noir_1", "noir", true));
        this.getCase(4, 8).ajouterPiece(new Reine("Reine_Noir_1", "noir", true));
        this.getCase(5, 8).ajouterPiece(new Roi("Roi_Noir_1", "noir", true));
        this.getCase(6, 8).ajouterPiece(new Fou("Fou_Noir_2", "noir", true));
        this.getCase(7, 8).ajouterPiece(new Cavalier("Cavalier_Noir_2", "noir", true));
        this.getCase(8, 8).ajouterPiece(new Tour("Tour_Noir_2", "noir", true));

        // Pions noirs en ligne 7
        for (int x = 1; x <= 8; x++) {
            this.getCase(x, 7).ajouterPiece(new Pion(String.format("Pion_Noir_%s", x),
                    "noir", true));
        }

        // Blancs en ligne 1
        this.getCase(1, 1).ajouterPiece(new Tour("Tour_blanc_1", "blanc", true));
        this.getCase(2, 1).ajouterPiece(new Cavalier("Cavalier_blanc_1", "blanc", true));
        this.getCase(3, 1).ajouterPiece(new Fou("Fou_blanc_1", "blanc", true));
        this.getCase(4, 1).ajouterPiece(new Reine("Reine_blanc_1", "blanc", true));
        this.getCase(5, 1).ajouterPiece(new Roi("Roi_blanc_1", "blanc", true));
        this.getCase(6, 1).ajouterPiece(new Fou("Fou_blanc_2", "blanc", true));
        this.getCase(7, 1).ajouterPiece(new Cavalier("Cavalier_blanc_2", "blanc", true));
        this.getCase(8, 1).ajouterPiece(new Tour("Tour_blanc_2", "blanc", true));


        // Pions blancs en ligne 8
        for (int x = 1; x <= 8; x++) {
            this.getCase(x, 2).ajouterPiece(new Pion(String.format("Pion_Blanc_%s", x),
                    "blanc", true));
        }
    }

    /**
     * Getter retournant l'identifiant de la grille
     * @return {@linkplain Grille#id}
     * @author yohan
     */
    public String getId() {
        return this.id;
    }

    /**
     * Setter modifiant l'identifiant de la grille
     * @param id String : Nouvel identifiant
     * @see Grille#id
     * @author yohan
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Getter retournant la Case à ces coordonnées de la grille
     * @param x <code>int</code> : colonne de la case recherchée
     * @param y <code>int</code> : ligne de la case recherchée
     * @return {@linkplain Case} : Case contenue aux coordonnées en paramètres de la méthode
     * @see Case
     * @author yohan
     */
    public Case getCase(int x, int y) {
        for (Case uneCase : this.grilleCases) {
            if (uneCase.x == x && uneCase.y == y) {
                return uneCase;
            }
        } return null;
    }
    /**
     * Getter retournant la Case de la grille égale à celle en paramètre de la méthode
     * @param c <code>{@linkplain Case}</code> : Case recherchée dans la grille
     * @return {@linkplain Case} : Case contenue égale à la case recherchée
     * @see Case
     * @author yohan
     */
    public Case getCase(Case c) {
        for (Case uneCase : this.grilleCases) {
            if (c.equals(uneCase)) {
                return uneCase;
            }
        }
        return null;
    }

    /**
     * <code>Outils de DEBUGGAGE</code> : Fonction affichant les informations de la grille
     * <p><i>Not in use</i>
     * @deprecated
     * @see PlateauDeJeu#afficher()
     * @author yohan
     */
    public void printGrilleInfo() {
        for (int colonne = 1; colonne <= 8; colonne ++) {
            System.out.printf("[Colonne: %d]%n", colonne);
            for (int ligne = 1; ligne <= 8; ligne++) {
                System.out.println(this.getCase(ligne, colonne));
            }
        }
    }

    /**
     * Override de la méthode toString() de la classe {@linkplain Object#toString()}
     * <p>
     *     Retourne la grille sous le format suivant : <br><pre>
     *         [Colonne1]
     *         [Case1]
     *         [Case2]
     *         [Colonne2]
     *         [Case1]
     *         ...</pre>
     * </p>
     * @return <code>String</code> : Représentation textuelle de la case et de ses attributs
     * @see Case
     * @see Piece
     * @author yohan
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int colonne = 1; colonne <= 8; colonne ++) {
            str.append(String.format("[Colonne: %d]", colonne)).append("\n");
            for (int ligne = 1; ligne <= 8; ligne++) {
                str.append(this.getCase(ligne, colonne)).append("\n");
            }
        }
        return str.toString();
    }

    /**
     * Fonction permettant de sauvegarder les attributs de la grille dans un objet JSON, ainsi que les cases
     * qu'elle contient elles aussi au format JSON, et qui sera ensuite
     * intégrée à la sauvegarde avec les informations sur le Jeu.
     * @return <code>JSONObject</code> : La Grille sous format JSON
     * @see Piece#getJSONObject()
     * @see PlateauDeJeu#getJSONObject()
     * @author yohan
     */
    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("idGrille", this.id);
        JSONArray jsonArray = new JSONArray();
        for (Case c : this.grilleCases) {
            jsonArray.add(c.getJSONObject());
        }
        jsonObject.put("grilleCases", jsonArray);
        return jsonObject;
    }


}
