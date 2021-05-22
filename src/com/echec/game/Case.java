package com.echec.game;

import com.echec.pieces.*;
import org.json.simple.JSONObject;

public class Case {

    public int x;

    public int y;

    public String couleur;

    public Piece piece;

    public Case(int x, int y, String couleur, Piece piece) {
        this.x = x;
        this.y = y;
        this.couleur = couleur;
        this.piece = piece;
    }

    public Case(int x, int y, String couleur) {
        this.x = x;
        this.y = y;
        this.couleur = couleur;
        this.piece = null;
    }
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

    public void retirerPiece() {
        this.piece = null;
    }

    public void ajouterPiece(Piece piece) {
        this.piece = piece;
    }

    public void vider() {
        this.piece = null;
    }

    public boolean estVide() {
        return this.piece == null;
    }
    public String toString() {
        return String.format("[%d, %d] | Couleur : %-5s | %s",
                this.x,this.y,this.couleur,this.piece);
    }

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
