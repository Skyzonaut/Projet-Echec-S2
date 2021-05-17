package com.echec.game;
import org.json.simple.JSONObject;
import com.echec.Tools;

import java.util.ArrayList;
import java.util.List;

public class PlateauDeJeu {

    private String id;
    private Grille grille;
    public List<Historique> historique = new ArrayList<Historique> ();
    public PlateauDeJeu() {

        this.id = "plateau " + Tools.getFormatDate();
        this.grille = new Grille();
        this.initPlateau();
//        this.matrice.printGrilleInfo();
    }

    public PlateauDeJeu(JSONObject jsonObject) {
        this.id = (String) jsonObject.get("id");
        this.grille = new Grille((JSONObject) jsonObject.get("grille"));
    }

    public void afficher(int hauteur, int largeur) {
        System.out.println(this.toString(hauteur, largeur));
    }

    public void afficher() {
        this.afficher(2, 5);
    }

    public String toString() {
        return this.toString(2, 5);
    }

    public String toString(int hauteur, int largeur) {

        String dessinPlateau = "-";

        for (int i = 0; i < 8 + 1; i++) {
            for (int j = 0; j < largeur; j++) {
                dessinPlateau += "-";
            } dessinPlateau += "-";
        } dessinPlateau += "\n";

        int ligneCompte = 1;

        for (int ligne = 1; ligne < hauteur * 8; ligne++) {

            String contenu = " ";
            dessinPlateau += "|";

            for (int colonne = 1; colonne <= 8; colonne++) {
                if (this.grille.getCase(colonne, ligneCompte).piece != null) {
                    contenu = this.grille.getCase(colonne, ligneCompte).piece.utfString();
                }

                for (int j = 0; j < largeur ; j++) {
                    if (j == (largeur / 2)) {
                        if (contenu != " ") {
                            dessinPlateau += ligne % hauteur == 0 ? " " : contenu;
                        } else {
                            dessinPlateau += " ";
                        }
                    } else {
                        dessinPlateau += ligne % hauteur == 0 ? "-" : " ";
                    }
                }

                contenu = " ";

                dessinPlateau += ligne % hauteur == 0 ? "+" : "|";
            } dessinPlateau += "\n";

            if (ligne % hauteur == 0)  ligneCompte ++;
        }

        dessinPlateau += "-";
        for (int i = 0; i < 8 + 1; i++) {
            for (int j = 0; j < largeur; j++) {
                dessinPlateau += "-";
            } dessinPlateau += "-";
        } dessinPlateau += "\n";

        return dessinPlateau;
    }

    public Grille getGrille() {
        return this.grille;
    }

    public void update() {
    }


    public void initPlateau() {
        this.grille.initialiserGrille();
    }


    public void deplacerPiece(Case origin, Case destination) {
        if (destination.estVide()) {
            destination.piece = origin.piece;
            origin.vider();
        } else {
            System.out.println("La destination n'est pas vide, veuillez utiliser la commande [prendre]");
        }
    }

    public void prendrePiece(Case origin, Case destination) {
        if (!destination.estVide()) {
            destination.piece.setEtat(false);
            destination.vider();
            deplacerPiece(origin, destination);
        } else {
            System.out.println("La destination est pas vide, veuillez utiliser la commande [déplacer]");
        }
    }

    public boolean testerDeplacement() {
        return true;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getId() {
        return this.id;
    }

    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", this.id);
        jsonObject.put("grille", this.grille.getJSONObject());
        return jsonObject;
    }
}
