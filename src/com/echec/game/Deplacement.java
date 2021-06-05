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

    public ArrayList<Case> getDeplacement() {
        return deplacement;
    }

    public String toString() {
        String str = "";
        str += origine +"\n";
        for (Case c : deplacement) {
            str += "\t\t" + c + "\n";
        }
        return str;
    }
}
