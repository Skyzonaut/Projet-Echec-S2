package com.echec.game;

import java.util.ArrayList;
import java.util.HashMap;

public class Echec {
    private Case roi;
    private PlateauDeJeu plateau;
    private ArrayList<Deplacement> sauveur = new ArrayList<>();
    private ArrayList<Deplacement> attaquants = new ArrayList<>();

    public Echec (PlateauDeJeu plateau, Case roi) {
        this.plateau = plateau;
        this.roi = roi;
    }

    public void addAttaquant(Deplacement d) {
        this.attaquants.add(d);
    }

    public boolean isEchec() {
        return this.attaquants.size() != 0;
    }

    public void trouverSauveur() {
        for (Deplacement d : sauveur) {

        }
    }
}
