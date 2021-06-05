package com.echec.game;

import java.util.ArrayList;
import java.util.HashMap;

public class Echec {
    private Case roi;
    private String couleur;
    private PlateauDeJeu plateau;
    private String couleurRoi;
    private ArrayList<Deplacement> sauveur = new ArrayList<>();
    private ArrayList<Deplacement> attaquants = new ArrayList<>();

    public Echec (PlateauDeJeu plateau, Case roi, String couleur) {
        this.plateau = plateau;
        this.roi = roi;
        this.couleurRoi = couleur;
    }

    public void addAttaquant(Deplacement d) {
        this.attaquants.add(d);
    }

    public boolean isEchec() {
        return this.attaquants.size() != 0;
    }

    public void trouverSauveur() {
//        String couleurEnnemie = roi.piece.getCouleur().equals("noir") ? "blanc" : "noir";
        System.out.println("---------------------------------------------------------------------------");
        if (couleurRoi.equalsIgnoreCase("blanc")) {
            for (Deplacement d : attaquants) {
                for (Case c : this.plateau.getCheminToRoi(d.getOrigine(), this.roi)) {
                    for (Deplacement s : this.plateau.listeDeplacementBlancs) {
                        if (s.contains(c)) {
                            if (!sauveur.contains(s)) {
                                sauveur.add(s);
                            }
                        }
                    }
                }
            }
        }
        else {
            for (Deplacement d : attaquants) {
                for (Case c : this.plateau.getCheminToRoi(d.getOrigine(), this.roi)) {
                    for (Deplacement s : this.plateau.listeDeplacementNoirs) {
                        if (s.contains(c)) {
                            if (!sauveur.contains(s)) {
                                sauveur.add(s);
                            }
                        }
                    }
                }
            }
        }
        for (Deplacement d : sauveur) {
            System.out.println(d);
        }
    }
}
