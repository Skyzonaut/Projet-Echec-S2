package com.echec.game;

import com.echec.pieces.Piece;

public class Case {

    public int x;

    public int y;

    public String couleur;

    public Piece piece;

    public Case(int x, int y, String couleur, Piece piece) {
        this.x = x;
        this.y = y;
        this.couleur = couleur;
        this.piece = piece;
    }

    public Case(int x, int y, String couleur) {
        this.x = x;
        this.y = y;
        this.couleur = couleur;
        this.piece = null;
    }

    public void retirerPiece() {
        this.piece = null;
    }

    public void ajouterPiece(Piece piece) {
        this.piece = piece;
    }

    public void vider() {
        this.piece = null;
    }
    public String toString() {
        return String.format("[%d, %d] | Couleur : %-5s | %s",
                this.x,this.y,this.couleur,this.piece);
    }

}
