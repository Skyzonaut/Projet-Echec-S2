package com.echec.game;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlateauDeJeu {

    private String id;


    private Grille grille;


    public List<Historique> historique = new ArrayList<Historique> ();


    public PlateauDeJeu() {
        this.id = LocalDateTime.now().toString();
        this.grille = new Grille();
        this.grille.initialiserGrille();
//        this.matrice.printGrilleInfo();
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

    public void save() {
    }

    public void initPlateau() {
    }


    public void deplacerPiece(Case origin, Case destination) {
        destination.piece = origin.piece;
        origin.vider();
    }


    public void prendrePiece(Case origin, Case destination) {
        destination.piece.setEtat(false);
        destination.vider();

        deplacerPiece(origin, destination);
    }


    public boolean testerDeplacement(Case origin, Case destination) {
        int x = origin.x;
        int y = origin.y;
        int dx = destination.x;
        int dy = destination.y;
        String nom = this.grille.getCase(x, y).piece.getId();
        if (nom.equals("Reine_Noir_1")) {
            if (dx - x == dy - y) {
                return true;
            }
            if (dx == x) {
                return true;
            }
            if (dy == y) {
                return true;
            }
            return false;
        }
        if (nom.equals("Tour_Noir_1")) {
            if (dx == x) {
                return true;
            }
            if (dy == y) {
                return true;
            }
            return false;
        }
        if (nom.equals("Fou_Noir_1")) {
            if (dx - x == dy -y) {
                return true;
            }
            return false;
        }
        if (nom.equals("Cavalier_Noir_1")) {
            if (((dx == x + 2) || (dx == x - 2)) && ((dy == y + 1) || (dy == y - 1))) {
                return true;
            }
            if (((dx == x + 1) || (dx == x - 1)) && ((dy == y + 2) || (dy == y - 2))) {
                return true;
            }
            return false;
        }

        return true;
    }


    void setId(String value) {
        this.id = value;
    }


    String getId() {
        return this.id;
    }

}
