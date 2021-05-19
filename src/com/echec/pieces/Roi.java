package com.echec.pieces;
import com.echec.game.Case;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Roi extends Piece {

    public Roi(String id, String couleur, boolean etat) {
        this.id = id;
        this.couleur = couleur;
        this.etat = etat;
        this.pieceNoirUTF = "♔";
        this.pieceBlancheUTF = "♚";
    }

    public Roi(JSONObject jsonObject) {
        this.id = (String) jsonObject.get("id");
        this.couleur = (String) jsonObject.get("couleur");
        this.etat = (boolean) jsonObject.get("etat");
        this.pieceNoirUTF = "♔";
        this.pieceBlancheUTF = "♚";
    }

    public ArrayList<Case> getDeplacement(String mode) {
        ArrayList<Case> listeCasesDeplacement = new ArrayList<Case>();

        return listeCasesDeplacement;
    }


    public Roi() {
    }

}
