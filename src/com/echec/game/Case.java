package com.echec.game;

import com.echec.pieces.*;
import org.json.simple.JSONObject;

/**
 * Objet représentant une case d'un échiquier. La case contient une pièce ou non, et est caractérisé par
 * sa colonne et ligne sur l'échuiquier. Ainsi que sa couleur.
 * <p>
 * 64 cases composerons unne {@linkplain Grille}, qui elle même sert de matrice au {@linkplain PlateauDeJeu}.
 * @see Grille
 * @see PlateauDeJeu
 * @see Piece
 * @author yohan
 */
public class Case {

    /**
     * Colonne de la grille de l'échiquier à laquelle appartient la case
     */
    public int x;

    /**
     * Ligne de la grille de l'échiquier à laquelle appartient la case
     */
    public int y;

    /**
     * Couleur de la case : noir ou blanc
     */
    public String couleur;

    /**
     * Contenu de la case, si le contenu est <code>null</code> alors la case est vide,
     * sinon sur la case se trouve une {@linkplain Piece}
     */
    public Piece piece;

    /**
     * Constructeur champ à champ de cases avec initialisation du contenu
     * @param x <code>int</code> : Colonne
     * @param y <code>int</code> : Ligne
     * @param couleur <code>Couleur</code> : noir ou blanc
     * @param piece <code>Piece</code> : vide si <code>null</code>, sinon contient la pièce présente sur la case
     * @see Piece
     * @author yohan
     */
    public Case(int x, int y, String couleur, Piece piece) {
        this.x = x;
        this.y = y;
        this.couleur = couleur;
        this.piece = piece;
    }

    /**
     * Constructeur champ à champ de case vides
     * @param x <code>int</code> : Colonne
     * @param y <code>int</code> : Ligne
     * @param couleur <code>Couleur</code> : noir ou blanc
     * @see Piece
     * @author yohan
     */
    public Case(int x, int y, String couleur) {
        this.x = x;
        this.y = y;
        this.couleur = couleur;
        this.piece = null;
    }

    /**
     * Constructeur par recopie d'une case en copiant une case déjà sauvegardée dans un objet json
     * @param jsonObject <code>JSONObject</code> : Sauvegarde d'une case au format Json
     * @see Piece
     * @see Grille#getJSONObject()
     * @see Case#getJSONObject()
     * @author yohan
     */
    public Case(JSONObject jsonObject) {
        this.x = ((Long) jsonObject.get("x")).intValue();
        this.y = ((Long) jsonObject.get("y")).intValue();
        this.couleur = (String) jsonObject.get("couleur");
        if (jsonObject.get("piece") != null) {
            JSONObject pieceJSON = (JSONObject) jsonObject.get("piece");
            String idPiece = (String) pieceJSON.get("id");
            String type = idPiece.split("_")[0].toLowerCase();
            switch (type) {
                case "cavalier" : this.piece = new Cavalier(pieceJSON); break;
                case "fou"      : this.piece = new Fou(pieceJSON);      break;
                case "pion"     : this.piece = new Pion(pieceJSON);     break;
                case "reine"    : this.piece = new Reine(pieceJSON);    break;
                case "roi"      : this.piece = new Roi(pieceJSON);      break;
                case "tour"     : this.piece = new Tour(pieceJSON);     break;
            }
        } else {
            this.piece = null;
        }
    }

    /**
     * Fonction permettant d'ajouter un objet de classe {@linkplain Piece} à la case
     * @param piece {@linkplain Piece} : Pièce à ajouter à la case
     * @author yohan
     * @see Piece
     */
    public void ajouterPiece(Piece piece) {
        this.piece = piece;
    }

    /**
     * Fonction permettant de retirer le contenu d'une case
     * @see Piece
     * @author yohan
     */
    public void vider() {
        this.piece = null;
    }

    /**
     * Fonction permettant de vérifier si une case est vide.
     * @return <code>Boolean</code> :
     * <ul>
     *     <li><code>true</code> si la case contient une pièce</li>
     *     <li><code>false</code> si la case ne contient aucune pièce</li>
     * </ul>
     * @see Piece
     * @author yohan
     */
    public boolean estVide() {
        return this.piece == null;
    }

    /**
     * Override de la méthode toString() de la classe {@linkplain Object#toString()}
     * <p>
     *     Retourne la case sous le format suivant : <br>
     *         [{@linkplain Case#x}, {@linkplain Case#y}] | Couleur : {@linkplain Case#couleur} | {@linkplain Piece}
     * </p>
     * @return <code>String</code> : Représentation textuelle de la case et de ses attributs
     * @see Piece
     * @author yohan
     */
    public String toString() {
        return String.format("[%d, %d] | Couleur : %-5s | %s",
                this.x,this.y,this.couleur,this.piece);
    }

    /**
     * Fonction permettant de sauvegarder les attributs de la case dans un objet JSON, qui sera ensuite
     * intégré dans un JSON avec toutes les autres pièces, et informations sur le Jeu. Qui servira de fichier de
     * sauvegarde au jeu.
     * @return <code>JSONObject</code> : La case sous format JSON
     * @see Grille#getJSONObject()
     * @author yohan
     */
    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("x", this.x);
        jsonObject.put("y", this.y);
        jsonObject.put("couleur", this.couleur);
        if (this.piece != null) {
            jsonObject.put("piece", this.piece.getJSONObject());
        } else {
            jsonObject.put("piece", null);
        }
        return jsonObject;
    }

}
