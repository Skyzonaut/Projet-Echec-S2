package com.echec.pieces;
import com.echec.game.Case;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public abstract class Piece {

    protected String id;

    protected String couleur;

    protected boolean etat;

    protected String pieceNoirUTF;

    protected String pieceBlancheUTF;

    public String getId() {
        return this.id;
    }

    void setId(String value) {
        this.id = value;
    }

    public String getCouleur() {
        return this.couleur;
    }

    public void setCouleur(String value) {
        this.couleur = value;
    }

    public boolean isEtat() {
        return this.etat;
    }

    public void setEtat(boolean value) {
        this.etat = value;
    }

    public String utfString() {
        return this.couleur.equals("noir") ? this.pieceNoirUTF : this.pieceBlancheUTF;
    }

    public String toString() {
        return String.format("$[ Pièce : %-8s , Id : %-16s , Couleur : %-4s , Etat : %-4b , UTF : %s ]",
                this.getClassePiece(), this.id, this.couleur, this.etat, this.utfString());
    }

    public String getClassePiece() {
        String[] listePathClassePiece = this.getClass().getName().split("\\.");
        return listePathClassePiece[listePathClassePiece.length-1];
    }

    public abstract ArrayList<Case> getDeplacement(String mode);

    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", this.id);
        jsonObject.put("couleur", this.couleur);
        jsonObject.put("etat", this.etat);
        return jsonObject;
    }
}
