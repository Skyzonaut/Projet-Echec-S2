package com.projectEchec.classes;

public class Piece {

    private String id;

    private String couleur;

    private String type;

    private boolean etat;

    public String toString() {
    }

    public Piece() {
    }

    public String getId() {
        return this.id;
    }

    void setId(String value) {
        this.id = value;
    }

    public String getCouleur() {
        return this.couleur;
    }

    void setCouleur(String value) {
        this.couleur = value;
    }

    String getType() {
        return this.type;
    }

    void setType(String value) {
        this.type = value;
    }

    boolean isEtat() {
        return this.etat;
    }

    void setEtat(boolean value) {
        this.etat = value;
    }

}
