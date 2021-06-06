package com.echec.pieces;
import com.echec.game.Case;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Pion d'echec, hérité directement de {@linkplain Piece}
 * @see com.echec.pieces.Piece
 * @author yohan
 */
public class Pion extends Piece {

    /**
     * Constructeur champ à champ d'une pièce de Pion
     * @param id String : Identifiant du pion
     * @param couleur String : Noir ou Blanc, couleur de la pièce
     * @param etat Boolean : état de la pièce, est-elle prise ou non <b>Not in Use</b>
     * @see Pion#Pion(JSONObject)
     * @see Piece
     * @author yohan
     */
    public Pion(String id, String couleur, boolean etat) {
        this.id = id;
        this.couleur = couleur;
        this.etat = etat;
        this.pieceNoirUTF = "♟";
        this.pieceBlancheUTF = "♙";
        this.score = 1;
    }

    /**
     * Constructeur par recopie d'une pièce de Pion depuis une sauvegarde
     * @param jsonObject JSONObject : Objet Json contenant la pièce sauvegardée d'une autre partie, contenu dans
     *                   un fichier Json de sauvegarde
     * @see Pion#Pion(String, String, boolean)
     * @see Piece
     * @author yohan
     */
    public Pion(JSONObject jsonObject) {
        this.id = (String) jsonObject.get("id");
        this.couleur = (String) jsonObject.get("couleur");
        this.etat = (boolean) jsonObject.get("etat");
        this.pieceNoirUTF = "♙";
        this.pieceBlancheUTF = "♟";
        this.score = 1;
    }

    /**
     * <i>En développement</i>
     * @param mode String
     * @return Liste des cases où pourra se déplacer le pion
     */
    public ArrayList<Case> getDeplacement(String mode) {
        return new ArrayList<>();
    }
}
