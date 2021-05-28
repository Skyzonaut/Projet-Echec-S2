package com.echec.pieces;
import com.echec.game.Case;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Tour d'echec, héritée directement de {@linkplain Piece}
 * @see com.echec.pieces.Piece
 * @author yohan
 */
public class Tour extends Piece {

    /**
     * Constructeur champ à champ d'une pièce de Tour
     * @param id String : Identifiant du Tour
     * @param couleur String : Noir ou Blanc, couleur de la pièce
     * @param etat Boolean : état de la pièce, est-elle prise ou non <b>Not in Use</b>
     * @see Tour#Tour(JSONObject)
     * @see Piece
     * @author yohan
     */
    public Tour(String id, String couleur, boolean etat) {
        this.id = id;
        this.couleur = couleur;
        this.etat = etat;
        this.pieceNoirUTF = "♜";
        this.pieceBlancheUTF = "♖";
//        this.pieceNoirUTF = "♖";
//        this.pieceBlancheUTF = "♜";
    }

    /**
     * Constructeur par recopie d'une pièce de Tour depuis une sauvegarde
     * @param jsonObject JSONObject : Objet Json contenant la pièce sauvegardée d'une autre partie, contenu dans
     *                   un fichier Json de sauvegarde
     * @see Tour#Tour(String, String, boolean)
     * @see Piece
     * @author yohan
     */
    public Tour(JSONObject jsonObject) {
        this.id = (String) jsonObject.get("id");
        this.couleur = (String) jsonObject.get("couleur");
        this.etat = (boolean) jsonObject.get("etat");
        this.pieceNoirUTF = "♖";
        this.pieceBlancheUTF = "♜";
    }

    /**
     * <i>En développement</i>
     * @param mode String
     * @return Liste des cases où pourra se déplacer la tour
     */
    public ArrayList<Case> getDeplacement(String mode) {
        return new ArrayList<>();
    }

    public static void main(String[] args) {
        System.out.println("oui");
    }
}