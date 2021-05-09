package com.echec;

import com.echec.game.*;

public class Jeu {

    public PlateauDeJeu jouer() {

        PlateauDeJeu plateau = new PlateauDeJeu();
        return plateau;
    }

    public void updateHistorique() {
    }

    public void chargerJeu() {
    }


    public static void main(String[] args) {

        Jeu jeu = new Jeu();
        PlateauDeJeu plateau = jeu.jouer();
        plateau.afficher();
        plateau.deplacerPiece(plateau.getGrille().getCase(2, 2), plateau.getGrille().getCase(2, 4));
        plateau.afficher();
        plateau.prendrePiece(plateau.getGrille().getCase(2, 3), plateau.getGrille().getCase(2, 1));
        plateau.afficher();


    }

}
