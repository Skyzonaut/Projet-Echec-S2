package com.echec.pieces;
import com.echec.game.Case;

import java.util.ArrayList;
import java.util.List;

public class Tour extends Piece {

    public Tour(String id, String couleur, boolean etat) {
        this.id = id;
        this.couleur = couleur;
        this.etat = etat;
        this.pieceNoirUTF = "♖";
        this.pieceBlancheUTF = "♜";
    }

    public ArrayList<Case> getDeplacement(String mode) {
        ArrayList<Case> listeCasesDeplacement = new ArrayList<Case>();

        return listeCasesDeplacement;
    }


}
