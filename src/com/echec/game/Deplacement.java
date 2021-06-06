package com.echec.game;

import java.util.ArrayList;

public class Deplacement {
    private Case origine;
    private ArrayList<Case> deplacement;

    public Deplacement(Case origine, ArrayList<Case> deplacement) {
        this.origine = origine;
        this.deplacement = deplacement;
    }

    public boolean contains(Case c) {
        return deplacement.contains(c);
    }
    public Case getOrigine() {
        return origine;
    }

    public boolean hasDeplacement() {
        return this.deplacement.size() != 0;
    }
    public ArrayList<Case> getDeplacement() {
        return deplacement;
    }

    public Deplacement getSafeDeplacement(ArrayList<Case> listeCasesAttaquants, Deplacement attaquant, PlateauDeJeu plateau) {
        ArrayList<Case> newDeplacement = new ArrayList<>();

        for (int i = 0; i < deplacement.size(); i++) {
            if (!listeCasesAttaquants.contains(deplacement.get(i)) && !plateau.getCheminToRoi(attaquant.getOrigine(), deplacement.get(i)).contains(deplacement.get(i))) {
                newDeplacement.add(deplacement.get(i));
            }
//            if (deplacement.get(i).equals(attaquant.getOrigine())) {
//                newDeplacement.add(deplacement.get(i));
//            }
        }
        this.deplacement = newDeplacement;
        return this;
    }

    public void retirerRoi(Case c) {
        ArrayList<Case> newListe = new ArrayList<>();
        for (Case d : this.deplacement) {
            if(!c.equals(d)) newListe.add(d);
        }
        this.deplacement = newListe;
    }

    public String toString() {
        String str = "";
        str += "| Origine : " + origine +"\n";
        for (Case c : deplacement) {
            str += "\t\t" + c + "\n";
        }
        return str;
    }
}
