package com.echec.pieces;
import com.echec.game.Case;
import com.echec.game.Grille;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Classe représentant une pièce d'échec, avec ses caractéristiques :
 * <ul>
 *     <li>{@linkplain Piece#id}</li>
 *     <li>{@linkplain Piece#couleur}</li>
 *     <li>{@linkplain Piece#etat}</li>
 *     <li>{@linkplain Piece#pieceNoirUTF}</li>
 *     <li>{@linkplain Piece#pieceBlancheUTF}</li>
 * </ul>
 * La classe a pour but d'être manipulée par le jeu et ses différents composants afin de permettre au joueur de
 * visualiser ses pièces, et de jouer.
 * @see Pion
 * @see Cavalier
 * @see Tour
 * @see Fou
 * @see Reine
 * @see Roi
 */
public abstract class Piece {

    /**
     * Identifiant de la pièce, qui sera composé de 'type_couleur_index'
     */
    protected String id;

    /**
     * Couleur de la pièce : Noire ou Blanche (écrit et stocké : noir ou blanc)
     */
    protected String couleur;

    /**
     * État de la pièce, servira à savoir si la pièce est hors du jeu ou non.
     * <i>Not in use</i>
     */
    protected boolean etat;

    /**
     * Variable qui contiendra pour chaque instance des classes enfantes de Pièce, le caractère noir UTF-8 de leur pièce.
     */
    protected String pieceNoirUTF;

    /**
     * Variable qui contiendra pour chaque instance des classes enfantes de Pièce, le caractère blanc UTF-8 de leur pièce.
     */
    protected String pieceBlancheUTF;

    /**
     * Coùt de la pièce, sert à classer les pièces par valeur. Il servira à l'IA pour déterminer quelle pièce il vaut
     * mieux prendre
     */
    protected int score;

    /**
     * Getter retournant l'id de la pièce
     * @return {@linkplain Piece#id}
     * @author yohan
     */
    public String getId() {
        return this.id;
    }

    /**
     * Setter modifiant l'id de la pièce
     * @param value <code>String</code> : Nouvel id
     * @see Piece#id
     * @author yohan
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Getter retournant la couleur de la pièce
     * @return {@linkplain Piece#couleur}
     * @author yohan
     */
    public String getCouleur() {
        return this.couleur;
    }

    /**
     * Setter modifiant la couleur de la pièce
     * @param value <code>String</code> : Nouvelle couleur
     * @see Piece#couleur
     * @author yohan
     */
    public void setCouleur(String value) {
        this.couleur = value;
    }

    /**
     * Getter retournant l'état' de la pièce
     * @return {@linkplain Piece#etat}
     * @author yohan
     */
    public boolean isEtat() {
        return this.etat;
    }

    /**
     * Setter modifiant l'état de la pièce
     * @param value <code>String</code> : Nouvel état
     * @see Piece#etat
     * @author yohan
     */
    public void setEtat(boolean value) {
        this.etat = value;
    }

    /**
     * Fonction récupérant le caractère UTF-8 représentant la pièce en fonction de sa classe
     * et de sa couleur
     * @return String : Représentation UTF-8 de la pièce
     * @see Piece#pieceBlancheUTF
     * @see Piece#pieceNoirUTF
     * @see Pion
     * @see Cavalier
     * @see Tour
     * @see Fou
     * @see Reine
     * @see Roi
     * @author yohan
     */
    public String utfString() {
        return this.couleur.equals("noir") ? this.pieceNoirUTF : this.pieceBlancheUTF;
    }

    /**
     * Override de la méthode toString() de la classe {@linkplain Object#toString()}
     * <p>
     *     Retourne la pièce sous le format suivant : <br>
     *         $[ Pièce : {@linkplain Piece#getClass()} , Id : {@linkplain Piece#id} ,
     *         Couleur : {@linkplain Piece#couleur}, Etat : {@linkplain Piece#etat} , UTF : {@linkplain Piece#utfString()}} ]
     * </p>
     * @return String : Représentation textuelle de la pièce et de ses attributs
     * @see Pion
     * @see Cavalier
     * @see Tour
     * @see Fou
     * @see Reine
     * @see Roi
     * @author yohan
     */
    public String toString() {
        return String.format("$[%s]", this.id);
    }

    /**
     * récupère la classe du pion, cette méthode étant héritée par les classes enfantes, elle permettra de récupérer
     * les classes précises de chaque pièce, et donc leur type.
     * @return {@linkplain Piece#getClass()}
     * @see Pion
     * @see Cavalier
     * @see Tour
     * @see Fou
     * @see Reine
     * @see Roi
     * @author yohan
     */
    public String getClassePiece() {
        String[] listePathClassePiece = this.getClass().getName().split("\\.");
        return listePathClassePiece[listePathClassePiece.length-1];
    }

    /**
     * @param mode String
     * @return
     * <i>Not in Use</i>
     */
    public abstract ArrayList<Case> getDeplacement(String mode);

    /**
     * Fonction permettant de sauvegarder les attributs de la pièce dans un objet JSON, qui sera ensuite
     * intégrée dans un JSON avec toutes les autres pièces, et informations sur le Jeu. Qui servira de fichier de
     * sauvegarde au jeu.
     * @return <code>JSONObject</code> : La pièce sous format JSON
     * @see Grille#getJSONObject()
     * @author yohan
     */
    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", this.id);
        jsonObject.put("couleur", this.couleur);
        jsonObject.put("etat", this.etat);
        return jsonObject;
    }

    public Integer getScore() {
        return this.score;
    };
}
