package com.echec.game;

import com.echec.pieces.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class Grille {

    private String id;

    private ArrayList<Case> grilleCases = new ArrayList<Case>();


    public Grille() {
        boolean noir = true;

        for (int ligne = 1; ligne <= 8; ligne++) {
            noir = !noir;
            for (int colonne = 8; colonne >= 0; colonne --) {
                grilleCases.add(new Case(ligne, colonne, noir ? "noir" : "blanc"));
                noir = !noir;
            }
        }
    }

    public void initialiserGrille() {

        // Blanc
        this.getCase(1, 1).ajouterPiece(new Tour("Tour_Noir_1", "noir", true));
        this.getCase(2, 1).ajouterPiece(new Fou("Fou_Noir_1", "noir", true));
        this.getCase(3, 1).ajouterPiece(new Cavalier("Cavalier_Noir_1", "noir", true));
        this.getCase(4, 1).ajouterPiece(new Reine("Reine_Noir_1", "noir", true));
        this.getCase(5, 1).ajouterPiece(new Roi("Roi_Noir_1", "noir", true));
        this.getCase(6, 1).ajouterPiece(new Fou("Fou_Noir_2", "noir", true));
        this.getCase(7, 1).ajouterPiece(new Cavalier("Cavalier_Noir_2", "noir", true));
        this.getCase(8, 1).ajouterPiece(new Tour("Tour_Noir_2", "noir", true));

        // Pions blancs
        for (int x = 1; x <= 8; x++) {
            this.getCase(x, 2).ajouterPiece(new Pion(String.format("Pion_Noir_%s", x),
                    "noir", true));
        }

        // Noir
        this.getCase(1, 8).ajouterPiece(new Tour("Tour_blanc_1", "blanc", true));
        this.getCase(2, 8).ajouterPiece(new Fou("Fou_blanc_1", "blanc", true));
        this.getCase(3, 8).ajouterPiece(new Cavalier("Cavalier_blanc_1", "blanc", true));
        this.getCase(4, 8).ajouterPiece(new Reine("Reine_blanc_1", "blanc", true));
        this.getCase(5, 8).ajouterPiece(new Roi("Roi_blanc_1", "blanc", true));
        this.getCase(6, 8).ajouterPiece(new Fou("Fou_blanc_2", "blanc", true));
        this.getCase(7, 8).ajouterPiece(new Cavalier("Cavalier_blanc_2", "blanc", true));
        this.getCase(8, 8).ajouterPiece(new Tour("Tour_blanc_2", "blanc", true));


        // Pions noirs
        for (int x = 1; x <= 8; x++) {
            this.getCase(x, 7).ajouterPiece(new Pion(String.format("Pion_Blanc_%s", x),
                    "blanc", true));
        }
    }

    public String getId() {
        return this.id;
    }

    void setId(String id) {
        this.id = id;
    }

    public Case getCase(int x, int y) {
        for (Case uneCase : this.grilleCases) {
//            System.out.println(uneCase);
            if (uneCase.x == x && uneCase.y == y) {
                return uneCase;
            }
        } return null;
    }

    public void printGrilleInfo() {
        for (int colonne = 1; colonne <= 8; colonne ++) {
            System.out.println(String.format("[Colonne: %d]", colonne));
            for (int ligne = 1; ligne <= 8; ligne++) {
                System.out.println(this.getCase(ligne, colonne));
            }
        }
    }

    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", this.id);
        JSONArray jsonArray = new JSONArray();
        for (Case c : this.grilleCases) {
            jsonArray.add(c.getJSONObject());
        }
        jsonObject.put("grilleCases", jsonArray);
        return jsonObject;
    }

}
