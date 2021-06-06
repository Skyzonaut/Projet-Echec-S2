package com.echec.game;

import java.util.ArrayList;

public class Echec {
    private final Case roi;
    private final PlateauDeJeu plateau;
    private final String couleurRoi;
    private ArrayList<Deplacement> sauveur = new ArrayList<>();
    private final ArrayList<Deplacement> attaquants;

    public Echec (PlateauDeJeu plateau, Case roi, String couleur, ArrayList<Deplacement> attaquants) {
        this.plateau = plateau;
        this.roi = roi;
        this.couleurRoi = couleur;
        this.attaquants = attaquants;
        this.trouverSauveur();
    }

    public ArrayList<Deplacement> getSauveur() {
        return sauveur;
    }

    public boolean hasSauveurs() {
        return this.sauveur.size() != 0;
    }

    public String isEchec() {
        if (this.attaquants.size() != 0 && this.sauveur.size() != 0)
        {
            return "echec";
        }
        else if (this.attaquants.size() == 0)
        {
            return "no-echec";
        }
        else if (this.attaquants.size() != 0 && this.sauveur.size() == 0)
        {
            return "mat";
        }
        return "no-echec";
    }
    public void trouverSauveur() {

        // Liste des déplacements des pions de chaque couleurs
        ArrayList<Deplacement> listeDeplacementsNoirs =  this.plateau.generateListeDeplacementsNoirs(false, true, false);
        ArrayList<Deplacement> listeDeplacementsBlancs =  this.plateau.generateListeDeplacementsBlancs(false, true, false);

        // Liste des cases sur lesquelles peuvent bouger les pions de chaque couleurs
        ArrayList<Case> listeCasesDeplacementsNoirs = new ArrayList<>();
        ArrayList<Case> listeCasesDeplacementsBlancs= new ArrayList<>();

        for (Deplacement d : listeDeplacementsNoirs) {
            for (Case c : d.getDeplacement()) {
                if (!listeCasesDeplacementsNoirs.contains(c)) {
                    listeCasesDeplacementsNoirs.add(c);
                }
            }
        }
        for (Deplacement d : listeDeplacementsBlancs) {
            for (Case c : d.getDeplacement()) {
                if (!listeCasesDeplacementsBlancs.contains(c)) {
                    listeCasesDeplacementsBlancs.add(c);
                }
            }
        }
        // Gérer prise de l'attaquant
        if (couleurRoi.equalsIgnoreCase("blanc")) {
            // On prend les attaquants
            for (Deplacement d : attaquants) {
                // Et pour chaque déplacement alliés
                for (Deplacement s : listeDeplacementsBlancs) {
                    // Si le pion allié est le roi
                    if (s.getOrigine().equals(roi)) {
                        // S'il peut bouger
                        if (s.hasDeplacement()) {
                            // On va vérifier chacun de ses mouvements pour prévenir c'un autre echec
                            Deplacement SafeS = s.getSafeDeplacement(listeCasesDeplacementsNoirs, d, this.plateau);
                            // Et s'il peut bien bouger sans danger on enregistre le déplacement
                            if (SafeS.hasDeplacement()) {
                                if (!sauveur.contains(SafeS)) {
                                    sauveur.add(SafeS);
                                }
                            }
                        }
                    }
                    // Si le pion allié n'est pas le roi, et qu'il peut prendre l'attaquant, on enregistre la piece
                    else if (s.getDeplacement().contains(d.getOrigine())) {
                        if (!sauveur.contains(s)) {
                            sauveur.add(s);
                        }
                    }
                }
                // Pour chaque case entre le roi et l'attaquant
                for (Case c : this.plateau.getCheminToRoi(d.getOrigine(), this.roi)) {
                    // On regarde si un des pions alliés peut s'y rendre
                    for (Deplacement s : listeDeplacementsBlancs) {
                        // Si un pion peut s'y rendre et qu'il n'est pas le roi on ajoute à la liste des sauveurs
                        if (s.contains(c) && !s.getOrigine().piece.getClassePiece().equals("Roi")) {
                            // On retire le roi des déplacements possibles du pions
                            s.retirerRoi(this.roi);
                            // Si le pion peut bouger
                            if (s.hasDeplacement()) {
                                // Si me déplacement n'est pas déjà enregistré
                                if (!sauveur.contains(s)) {
                                    sauveur.add(s);
                                }
                            }
                        }
                    }
                }
            }
        }
        else
        {
            // On prend chaque pion attaquant
            for (Deplacement d : attaquants) {
                // Et pour chaque déplacement alliés
                for (Deplacement s : listeDeplacementsNoirs) {
                    // Si le pion allié est le roi
                    if (s.getOrigine().equals(roi)) {
                        // S'il peut bouger
                        if (s.hasDeplacement()) {
                            // On va vérifier chacun de ses mouvements pour prévenir c'un autre echec
                            Deplacement SafeS = s.getSafeDeplacement(listeCasesDeplacementsBlancs, d, this.plateau);
                            // Et s'il peut bien bouger sans danger on enregistre le déplacement
                            if (SafeS.hasDeplacement()) {
                                if (!sauveur.contains(SafeS)) {
                                    sauveur.add(SafeS);
                                }
                            }
                        }
                    }
                    // Si le pion allié n'est pas le roi, on l'ajoute à la liste des sauveurs
                    else if (s.getDeplacement().contains(d.getOrigine())) {
                        if (!sauveur.contains(s)) {
                            sauveur.add(s);
                        }
                    }
                }
                // Pour chaque case entre le roi et l'attaquant
                for (Case c : this.plateau.getCheminToRoi(d.getOrigine(), this.roi)) {
                    // On regarde si un des pions alliés peut s'y rendre
                    for (Deplacement s : listeDeplacementsNoirs) {
                        // Si un pion peut s'y rendre et qu'il n'est pas le roi on ajoute à la liste des sauveurs
                        if (s.contains(c) && !s.getOrigine().piece.getClassePiece().equals("Roi")) {
                            // On retire le roi des déplacements possibles du pions
                            s.retirerRoi(this.roi);
                            // Si le pion peut bouger
                            if (s.hasDeplacement()) {
                                // Si me déplacement n'est pas déjà enregistré
                                if (!sauveur.contains(s)) {
                                    sauveur.add(s);
                                }
                            }
                        }
                    }
                }
            }
        }
        // On retire les doublons éventuels [2e sécurité]
        ArrayList<Case> c = new ArrayList<>();
        ArrayList<Deplacement> newSauveur = new ArrayList<>();
        for (Deplacement d : sauveur) {
            if (!c.contains(d.getOrigine())) {
                c.add(d.getOrigine());
                newSauveur.add(d);
            }
        }
        sauveur = newSauveur;
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[@ROI] \n");
        str.append(this.roi).append("\n");
        str.append("[@ATTAQUANT] \n");
        for (Deplacement d : this.attaquants) {
            str.append(d).append("\n");
        }
        str.append("[@SAUVEURS] \n");
        for (Deplacement d : this.sauveur) {
            str.append(d).append("\n");
        }
        return str.toString();
    }
}
