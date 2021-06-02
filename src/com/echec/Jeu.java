package com.echec;

import com.echec.game.*;
import com.echec.pieces.Piece;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Jeu {

    public PlateauDeJeu plateau;
    public static final String[] arrayDesCommandes = {"deplacer", "prendre", "save", "charger", "undo", "quitter", "abandonner", "help", "historique"};
    public static final String[] arrayDesCommandesRaccourci = {"d", "p", "s", "c", "u", "q", "a", "h", "n"};
    public static final List<String> listeDesCommandes = Arrays.asList(arrayDesCommandes);
    public static final List<String> listeDesCommandesRaccourci = Arrays.asList(arrayDesCommandesRaccourci);
    private String tour;
    private int niveauDeDifficulte;
    private static FileWriter fw;
    private Boolean jeuEnCours = true;


    public Jeu() {
        this.plateau = new PlateauDeJeu();
    }

    public void updateHistorique() {
    }

    public static void main(String[] args) {
        Jeu jeu = new Jeu();
        jeu.jouer();
    }

    public void jouer() {

        while (jeuEnCours) {

            this.tour = "blanc";
            int[] listeParam = lancerPartie();
            niveauDeDifficulte = listeParam[0];
            boolean partieEnCours = true;

            while (partieEnCours) {

                this.plateau.afficher();

                System.out.format("> C'est au tour des %ss\n\n", this.tour);

                String commande = this.getCommandeInput().trim();

                if (listeDesCommandes.contains(commande) || listeDesCommandesRaccourci.contains(commande)) {

                    switch (commande) {
                        case "prendre":
                        case "p":
                            this.prendrePiece();
                            break;

                        case "deplacer":
                        case "d":
                            this.deplacerPiece();
                            break;

                        case "save":
                        case "s":
                            System.out.println(this.save());
                            break;

                        case "charger":
                        case "c":
                            System.out.println(this.charger());
                            break;

                        case "undo":
                        case "u":
                            if (niveauDeDifficulte == 2) {
                                this.undo();
                            } else {
                                System.out.println("Vous ne pouvez pas revenir en arrière dans ce niveau de difficulté!");
                            }
                            break;

                        case "quitter" :
                        case "q" :
                            this.quitterJeu();
                            break;

                        case "abandonner" :
                        case "a" :
                            partieEnCours = this.finirPartie();
                            break;

                        case "help" :
                        case "h" :
                            this.help();

                        case "historique" :
                        case "n" :
                            this.plateau.historique.afficher();

                        default:
                            System.out.println("Commande non reconnu");
                    }
                } else {
                    System.out.println("Commande incompréhensible");
                }
//                this.plateau.historique.afficher();
            }
        }
    }

    public void prendrePiece() {
        Case origine = this.getCaseOrigineDepuisCoordonneeInput();
        Case destination = this.getCaseDestinationDepuisCoordonneeInput();
        this.plateau.prendrePiece(origine, destination);
        this.tour = (this.tour.equals("blanc")) ? "noir" : "blanc";
    }

    /**
     * Fonction permettant d'avoir les déplacements possibles d'une piece
     * @param posPiece <code>{@linkplain Case}</code> : Case de la piece dont on veut avoir les déplacements
     * @return <code>ArrayList<{@linkplain Case}></code> : Retourne une liste de cases des déplacements possibles
     * @see Case
     * @author melissa
     */
    public ArrayList<Case> deplacementsPossible(Case posPiece) {
        ArrayList<Case> listeCases = new ArrayList<Case>();

        int x = posPiece.x;
        int y = posPiece.y;

        String classePiece = plateau.getGrille().getCase(posPiece).piece.getClassePiece();
        String couleurPiece = plateau.getGrille().getCase(posPiece).piece.getCouleur();

        if (classePiece.equalsIgnoreCase("Pion")) {
            if (couleurPiece.equalsIgnoreCase("Blanc")) {
                if (plateau.getGrille().getCase(x, y + 1).piece == null) {
                    listeCases.add(new Case(x, y + 1, couleurPiece));
                }
                if (y == 2) {
                    if (plateau.getGrille().getCase(x, y + 2).piece == null) {
                        listeCases.add(new Case(x, y + 2, couleurPiece));
                    }
                }
            }
            if (couleurPiece.equalsIgnoreCase("Noir")) {
                if (plateau.getGrille().getCase(x, y - 1).piece == null) {
                    listeCases.add(new Case(x, y - 1, couleurPiece));
                }
                if (y == 7) {
                    if (plateau.getGrille().getCase(x, y - 2).piece == null) {
                        listeCases.add(new Case(x, y - 2, couleurPiece));
                    }
                }
            }
        }
        if (classePiece.equalsIgnoreCase("Tour")) {
            for (int i = x - 1 ; i > 0 ; i--) {
                do {
                    listeCases.add(new Case(i, y, couleurPiece));
                } while(plateau.getGrille().getCase(i, y).piece == null);
            }
            for (int i = x + 1 ; i <= 8 ; i++) {
                do {
                    listeCases.add(new Case(i, y, couleurPiece));
                } while(plateau.getGrille().getCase(i, y).piece == null);
            }
            for (int i = y - 1 ; i > 0 ; i--) {
                do {
                    listeCases.add(new Case(x, i, couleurPiece));
                } while(plateau.getGrille().getCase(x, i).piece == null);
            }
            for (int i = y + 1 ; i <= 8 ; i++) {
                do {
                    listeCases.add(new Case(x, i, couleurPiece));
                } while(plateau.getGrille().getCase(x, i).piece == null);
            }
        }
        if (classePiece.equalsIgnoreCase("Reine")) {
            for (int i = x - 1 ; i > 0 ; i--) {
                do {
                    listeCases.add(new Case(i, y, couleurPiece));
                } while(plateau.getGrille().getCase(i, y).piece == null);
            }
            for (int i = x + 1 ; i <= 8 ; i++) {
                do {
                    listeCases.add(new Case(i, y, couleurPiece));
                } while(plateau.getGrille().getCase(i, y).piece == null);
            }
            for (int i = y - 1 ; i > 0 ; i--) {
                do {
                    listeCases.add(new Case(x, i, couleurPiece));
                } while(plateau.getGrille().getCase(x, i).piece == null);
            }
            for (int i = y + 1 ; i <= 8 ; i++) {
                do {
                    listeCases.add(new Case(x, i, couleurPiece));
                } while(plateau.getGrille().getCase(x, i).piece == null);
            }
            int a = x + 1;
            int b = y + 1;
            do {
                listeCases.add(new Case(a, b, couleurPiece));
                a++;
                b++;
            } while(plateau.getGrille().getCase(a, b).piece == null && a <= 8 && b <= 8);
            a = x + 1;
            b = y - 1;
            do {
                listeCases.add(new Case(a, b, couleurPiece));
                a++;
                b--;
            } while(plateau.getGrille().getCase(a, b).piece == null && a <= 8 && b > 0);
            a = x - 1;
            b = y + 1;
            do {
                listeCases.add(new Case(a, b, couleurPiece));
                a--;
                b++;
            } while(plateau.getGrille().getCase(a, b).piece == null && a > 0 && b <= 8);
            a = x - 1;
            b = y - 1;
            do {
                listeCases.add(new Case(a, b, couleurPiece));
                a--;
                b--;
            } while(plateau.getGrille().getCase(a, b).piece == null && a > 0 && b > 0);
        }
        if (classePiece.equalsIgnoreCase("Fou")) {
            int a = x + 1;
            int b = y + 1;
            do {
                listeCases.add(new Case(a, b, couleurPiece));
                a++;
                b++;
            } while(plateau.getGrille().getCase(a, b).piece == null && a <= 8 && b <= 8);
            a = x + 1;
            b = y - 1;
            do {
                listeCases.add(new Case(a, b, couleurPiece));
                a++;
                b--;
            } while(plateau.getGrille().getCase(a, b).piece == null && a <= 8 && b > 0);
            a = x - 1;
            b = y + 1;
            do {
                listeCases.add(new Case(a, b, couleurPiece));
                a--;
                b++;
            } while(plateau.getGrille().getCase(a, b).piece == null && a > 0 && b <= 8);
            a = x - 1;
            b = y - 1;
            do {
                listeCases.add(new Case(a, b, couleurPiece));
                a--;
                b--;
            } while(plateau.getGrille().getCase(a, b).piece == null && a > 0 && b > 0);
        }
        if (classePiece.equalsIgnoreCase("Cavalier")) {
            if (plateau.getGrille().getCase(x - 1, y + 2).piece == null) {
                listeCases.add(new Case(x - 1, y + 2, couleurPiece));
            }
            if (plateau.getGrille().getCase(x - 2, y + 1).piece == null) {
                listeCases.add(new Case(x - 2, y + 1, couleurPiece));
            }
            if (plateau.getGrille().getCase(x - 2, y - 1).piece == null) {
                listeCases.add(new Case(x - 2, y - 1, couleurPiece));
            }
            if (plateau.getGrille().getCase(x - 1, y - 2).piece == null) {
                listeCases.add(new Case(x - 1, y - 2, couleurPiece));
            }
            if (plateau.getGrille().getCase(x + 1, y - 2).piece == null) {
                listeCases.add(new Case(x + 1, y - 2, couleurPiece));
            }
            if (plateau.getGrille().getCase(x + 2, y - 1).piece == null) {
                listeCases.add(new Case(x + 2, y - 1, couleurPiece));
            }
            if (plateau.getGrille().getCase(x + 1, y + 2).piece == null) {
                listeCases.add(new Case(x + 1, y + 2, couleurPiece));
            }
            if (plateau.getGrille().getCase(x + 2, y + 1).piece == null) {
                listeCases.add(new Case(x + 2, y + 1, couleurPiece));
            }
        }
        if (classePiece.equalsIgnoreCase("Roi")) {
            if (plateau.getGrille().getCase(x + 1, y + 1).piece == null
                    && !detecterEchec(plateau.getGrille().getCase(posPiece).piece, plateau.getGrille().getCase(x + 1, y + 1))) {
                listeCases.add(new Case(x + 1, y + 1, couleurPiece));
            }
            if (plateau.getGrille().getCase(x - 1, y + 1).piece == null
                    && !detecterEchec(plateau.getGrille().getCase(posPiece).piece, plateau.getGrille().getCase(x - 1, y + 1))) {
                listeCases.add(new Case(x - 1, y + 1, couleurPiece));
            }
            if (plateau.getGrille().getCase(x, y + 1).piece == null
                    && !detecterEchec(plateau.getGrille().getCase(posPiece).piece, plateau.getGrille().getCase(x, y + 1))) {
                listeCases.add(new Case(x, y + 1, couleurPiece));
            }
            if (plateau.getGrille().getCase(x, y - 1).piece == null
                    && !detecterEchec(plateau.getGrille().getCase(posPiece).piece, plateau.getGrille().getCase(x, y - 1))) {
                listeCases.add(new Case(x, y - 1, couleurPiece));
            }
            if (plateau.getGrille().getCase(x + 1, y).piece == null
                    && !detecterEchec(plateau.getGrille().getCase(posPiece).piece, plateau.getGrille().getCase(x + 1, y))) {
                listeCases.add(new Case(x + 1, y, couleurPiece));
            }
            if (plateau.getGrille().getCase(x - 1, y).piece == null
                    && !detecterEchec(plateau.getGrille().getCase(posPiece).piece, plateau.getGrille().getCase(x - 1, y))) {
                listeCases.add(new Case(x - 1, y, couleurPiece));
            }
            if (plateau.getGrille().getCase(x - 1, y - 1).piece == null
                    && !detecterEchec(plateau.getGrille().getCase(posPiece).piece, plateau.getGrille().getCase(x - 1, y - 1))) {
                listeCases.add(new Case(x - 1, y - 1, couleurPiece));
            }
            if (plateau.getGrille().getCase(x + 1, y - 1).piece == null
                    && !detecterEchec(plateau.getGrille().getCase(posPiece).piece, plateau.getGrille().getCase(x + 1, y - 1))) {
                listeCases.add(new Case(x + 1, y - 1, couleurPiece));
            }
        }
        return listeCases;
    }

    /**
     * Fonction permettant de detecter les menaces directes sur le roi
     * @param pieceRoi <code>{@linkplain Piece}</code> : La piece roi dont on veut vérifier l'existence d'un échec
     * @param posRoi <code>{@linkplain Case}</code> : Case du roi dont on veut vérifier l'existence d'un échec
     * @return <code>boolean</code> : true en cas d'échec
     * @see Piece
     * @see Case
     * @author melissa
     */
    public boolean detecterEchec(Piece pieceRoi, Case posRoi) {
        int x = posRoi.x;
        int y = posRoi.y;

        int i;
        int j;

        int supX = posRoi.x + 1;
        int infX = posRoi.x - 1;
        int supY = posRoi.y + 1;
        int infY = posRoi.y - 1;

        String couleur = pieceRoi.getCouleur();

        for (i = infX ; i > 0 ; i--) {
            if (plateau.getGrille().getCase(i,y).piece != null) {
                if (!plateau.getGrille().getCase(i, y).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (plateau.getGrille().getCase(i, y).piece.getClassePiece().equalsIgnoreCase("Reine") || plateau.getGrille().getCase(i, y).piece.getClassePiece().equalsIgnoreCase("Tour")) {
                        for (j = infX ; j > i ; j++) {
                            if (plateau.getGrille().getCase(j, y).piece != null) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        for (i = supX ; i <= 8 ; i++) {
            if (plateau.getGrille().getCase(i,y).piece != null) {
                if (!plateau.getGrille().getCase(i, y).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (plateau.getGrille().getCase(i, y).piece.getClassePiece().equalsIgnoreCase("Reine") || plateau.getGrille().getCase(i, y).piece.getClassePiece().equalsIgnoreCase("Tour")) {
                        for (j = supX ; j < i ; j++) {
                            if (plateau.getGrille().getCase(j, y).piece != null) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        for (i = infY ; i > 0 ; i--) {
            if (plateau.getGrille().getCase(x,i).piece != null) {
                if (!plateau.getGrille().getCase(x, i).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (plateau.getGrille().getCase(x, i).piece.getClassePiece().equalsIgnoreCase("Reine") || plateau.getGrille().getCase(x, i).piece.getClassePiece().equalsIgnoreCase("Tour")) {
                        for (j = infY ; j > i ; j++) {
                            if (plateau.getGrille().getCase(x, j).piece != null) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }
        for (i = supY ; i <= 8 ; i++) {
            if (plateau.getGrille().getCase(x,i).piece != null) {
                if (!plateau.getGrille().getCase(x, i).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (plateau.getGrille().getCase(x, i).piece.getClassePiece().equalsIgnoreCase("Reine") || plateau.getGrille().getCase(x, i).piece.getClassePiece().equalsIgnoreCase("Tour")) {
                        for (j = supY ; j < i ; j++) {
                            if (plateau.getGrille().getCase(x, j).piece != null) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }

        i = supX; j = supY;
        while (i <= 8 && j <= 8) {
            if (plateau.getGrille().getCase(i,j).piece != null) {
                if (!plateau.getGrille().getCase(i, j).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (plateau.getGrille().getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Reine") || plateau.getGrille().getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Fou")) {
                        int a = supX;
                        int b = supY;
                        while (a < i && b < j) {
                            if (plateau.getGrille().getCase(a, b).piece != null)
                                return false;
                            a++;
                            b++;
                        }
                        i++;
                        j++;
                    }
                }
            }
        }

        i = infX; j = supY;
        while (i > 0 && j <= 8) {
            if (plateau.getGrille().getCase(i,j).piece != null) {
                if (!plateau.getGrille().getCase(i, j).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (plateau.getGrille().getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Reine") || plateau.getGrille().getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Fou")) {
                        int a = infX;
                        int b = supY;
                        while (a > i && b < j) {
                            if (plateau.getGrille().getCase(a, b).piece != null)
                                return false;
                            a--;
                            b++;
                        }
                        i--;
                        j++;
                    }
                }
            }
        }

        i = supX; j = infY;
        while (i <= 8 && j > 0) {
            if (plateau.getGrille().getCase(i,j).piece != null) {
                if (!plateau.getGrille().getCase(i, j).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (plateau.getGrille().getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Reine") || plateau.getGrille().getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Fou")) {
                        int a = supX;
                        int b = infY;
                        while (a < i && b > j) {
                            if (plateau.getGrille().getCase(a, b).piece != null)
                                return false;
                            a++;
                            b--;
                        }
                        i++;
                        j--;
                    }
                }
            }
        }

        i = infX; j = infY;
        while (i > 0 && j > 0) {
            if (plateau.getGrille().getCase(i,j).piece != null) {
                if (!plateau.getGrille().getCase(i, j).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (plateau.getGrille().getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Reine") || plateau.getGrille().getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Fou")) {
                        int a = infX;
                        int b = infY;
                        while (a > i && b > j) {
                            if (plateau.getGrille().getCase(i, j).piece != null)
                                return false;
                            a--;
                            b--;
                        }
                        i--;
                        j--;
                    }
                }
            }
        }

        if (plateau.getGrille().getCase(x - 1, y + 2).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                && !plateau.getGrille().getCase(x - 1, y + 2).piece.getCouleur().equalsIgnoreCase(couleur))
            return true;
        if (plateau.getGrille().getCase(x - 2, y + 1).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                && !plateau.getGrille().getCase(x - 2, y + 1).piece.getCouleur().equalsIgnoreCase(couleur))
            return true;
        if (plateau.getGrille().getCase(x - 2, y - 1).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                && !plateau.getGrille().getCase(x - 1, y + 2).piece.getCouleur().equalsIgnoreCase(couleur))
            return true;
        if (plateau.getGrille().getCase(x - 1, y - 2).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                && !plateau.getGrille().getCase(x - 1, y - 2).piece.getCouleur().equalsIgnoreCase(couleur))
            return true;
        if (plateau.getGrille().getCase(x + 1, y - 2).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                && !plateau.getGrille().getCase(x + 1, y - 2).piece.getCouleur().equalsIgnoreCase(couleur))
            return true;
        if (plateau.getGrille().getCase(x + 2, y - 1).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                && !plateau.getGrille().getCase(x + 2, y - 1).piece.getCouleur().equalsIgnoreCase(couleur))
            return true;
        if (plateau.getGrille().getCase(x + 1, y + 2).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                && !plateau.getGrille().getCase(x + 1, y + 2).piece.getCouleur().equalsIgnoreCase(couleur))
            return true;
        if (plateau.getGrille().getCase(x + 2, y + 1).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                && !plateau.getGrille().getCase(x + 2, y + 1).piece.getCouleur().equalsIgnoreCase(couleur))
            return true;

        if (couleur.equalsIgnoreCase("Noir")) {
            if ((plateau.getGrille().getCase(infX, infY).piece.getClassePiece().equalsIgnoreCase("Pion")
                    && plateau.getGrille().getCase(infX, infY).piece.getClassePiece().equalsIgnoreCase("Blanc")))
                return true;
            if (plateau.getGrille().getCase(supX, infY).piece.getClassePiece().equalsIgnoreCase("Pion")
                    && (plateau.getGrille().getCase(supX, infY).piece.getCouleur().equalsIgnoreCase("Blanc")))
                return true;
            if (plateau.getGrille().getCase(x, infY).piece.getClassePiece().equalsIgnoreCase("Roi")
                    && plateau.getGrille().getCase(x, infY).piece.getCouleur().equalsIgnoreCase("Blanc"))
                return true;
            if (plateau.getGrille().getCase(x, supY).piece.getClassePiece().equalsIgnoreCase("Roi")
                    && plateau.getGrille().getCase(x, supY).piece.getCouleur().equalsIgnoreCase("Blanc"))
                return true;
            if (plateau.getGrille().getCase(infX, y).piece.getClassePiece().equalsIgnoreCase("Roi")
                    && plateau.getGrille().getCase(infX, y).piece.getCouleur().equalsIgnoreCase("Blanc"))
                return true;
            if (plateau.getGrille().getCase(supX, y).piece.getClassePiece().equalsIgnoreCase("Roi")
                    && plateau.getGrille().getCase(supX, y).piece.getCouleur().equalsIgnoreCase("Blanc"))
                return true;
            if (plateau.getGrille().getCase(infX, supY).piece.getClassePiece().equalsIgnoreCase("Roi")
                    && plateau.getGrille().getCase(infX, supY).piece.getCouleur().equalsIgnoreCase("Blanc"))
                return true;
            if (plateau.getGrille().getCase(supX, supY).piece.getClassePiece().equalsIgnoreCase("Roi")
                    && plateau.getGrille().getCase(supX, supY).piece.getCouleur().equalsIgnoreCase("Blanc"))
                return true;
        }
        if (couleur.equalsIgnoreCase("Blanc")) {
            if ((plateau.getGrille().getCase(infX, supY).piece.getClassePiece().equalsIgnoreCase("Pion")
                    && plateau.getGrille().getCase(infX, supY).piece.getClassePiece().equalsIgnoreCase("Noir")))
                return true;
            if (plateau.getGrille().getCase(supX, supY).piece.getClassePiece().equalsIgnoreCase("Pion")
                    && (plateau.getGrille().getCase(supX, supY).piece.getCouleur().equalsIgnoreCase("Noir")))
                return true;
            if (plateau.getGrille().getCase(x, infY).piece.getClassePiece().equalsIgnoreCase("Roi")
                    && plateau.getGrille().getCase(x, infY).piece.getCouleur().equalsIgnoreCase("Noir"))
                return true;
            if (plateau.getGrille().getCase(x, supY).piece.getClassePiece().equalsIgnoreCase("Noir")
                    && plateau.getGrille().getCase(x, supY).piece.getCouleur().equalsIgnoreCase("Noir"))
                return true;
            if (plateau.getGrille().getCase(infX, y).piece.getClassePiece().equalsIgnoreCase("Roi")
                    && plateau.getGrille().getCase(infX, y).piece.getCouleur().equalsIgnoreCase("Noir"))
                return true;
            if (plateau.getGrille().getCase(supX, y).piece.getClassePiece().equalsIgnoreCase("Roi")
                    && plateau.getGrille().getCase(supX, y).piece.getCouleur().equalsIgnoreCase("Noir"))
                return true;
            if (plateau.getGrille().getCase(infX, supY).piece.getClassePiece().equalsIgnoreCase("Roi")
                    && plateau.getGrille().getCase(infX, supY).piece.getCouleur().equalsIgnoreCase("Noir"))
                return true;
            if (plateau.getGrille().getCase(supX, supY).piece.getClassePiece().equalsIgnoreCase("Roi")
                    && plateau.getGrille().getCase(supX, supY).piece.getCouleur().equalsIgnoreCase("Noir"))
                return true;
        }

        return false;
    }

    /**
     * Fonction permettant de vérifier l'existence de collisions entre les pieces en cas de déplacement
     * @param origin <code>{@linkplain Case}</code> : Case originale de la piece qu'on veut déplacer
     * @param destination <code>{@linkplain Case}</code> : Case de la destination où on veut déplacer la piece
     * @return <code>boolean</code> : true en cas de collision possible
     * @see Case
     * @author melissa
     */
    public boolean testerCollisions(Case origin, Case destination) {
        int x = origin.x;
        int y = origin.y;
        int dx = destination.x;
        int dy = destination.y;
        int i;
        int j;

        String classeOrigin = origin.piece.getClassePiece();
        String couleur = origin.piece.getCouleur();

        if (classeOrigin.equalsIgnoreCase("Reine")) {
            if (Math.abs(dx - x) == Math.abs(dy - y)) {
                if (dy > y && dx > x) {
                    i = x + 1;
                    j = y + 1;
                    while (i < dx && j < dy) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        i++;
                        j++;
                    }
                }
                if (dy < y && dx < x) {
                    i = x - 1;
                    j = y - 1;
                    while (dx < i && dy < j) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        i--;
                        j--;
                    }
                }
                if (dy > y && dx < x) {
                    i = x - 1;
                    j = y + 1;
                    while (dx < i && j < dy) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        j++;
                        i--;
                    }
                }
                if (dy < y && dx > x) {
                    i = x + 1;
                    j = y - 1;
                    while (i < dx && dy > j) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        j--;
                        i++;
                    }
                }
            }
            if (dx == x) {
                if (dy > y) {
                    i = x;
                    j = y + 1;
                    while (j < dy) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        j++;
                    }
                }
                if (dy < y) {
                    i = x;
                    j = y - 1;
                    while (dy < j) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        j--;
                    }
                }
            }
            if (dy == y) {
                if (dx > x) {
                    i = x + 1;
                    j = y;
                    while (i < dx) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        i++;
                    }
                }
                if (dx < x) {
                    i = x - 1;
                    j = y - 1;
                    while (dx < i) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        j--;
                    }
                }
            }
        }
        if (classeOrigin.equalsIgnoreCase("Tour")) {
            if (dx == x) {
                if (dy > y) {
                    i = x;
                    j = y + 1;
                    while (j < dy) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        j++;
                    }
                }
                if (dy < y) {
                    i = x;
                    j = y - 1;
                    while (dy < j) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        j--;
                    }
                }
            }
            if (dy == y) {
                if (dx > x) {
                    i = x + 1;
                    j = y;
                    while (i < dx) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        i++;
                    }
                }
                if (dx < x) {
                    i = x - 1;
                    j = y - 1;
                    while (dx < i) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        j--;
                    }
                }
            }
        }
        if (classeOrigin.equalsIgnoreCase("Fou")) {
            if (Math.abs(dx - x) == Math.abs(dy - y)) {
                if (dy > y && dx > x) {
                    i = x + 1;
                    j = y + 1;
                    while (i < dx && j < dy) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        i++;
                        j++;
                    }
                }
                if (dy < y && dx < x) {
                    i = x - 1;
                    j = y - 1;
                    while (dx < i && dy < j) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        i--;
                        j--;
                    }
                }
                if (dy > y && dx < x) {
                    i = x - 1;
                    j = y + 1;
                    while (dx < i && j < dy) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        j++;
                        i--;
                    }
                }
                if (dy < y && dx > x) {
                    i = x + 1;
                    j = y - 1;
                    while (i < dx && dy > j) {
                        if (plateau.getGrille().getCase(i, j).piece != null) {
                            return false;
                        }
                        j--;
                        i++;
                    }
                }
            }
        }
        if (classeOrigin.equalsIgnoreCase("Roi")) {
            if (plateau.getGrille().getCase(destination).piece == null
                    || (plateau.getGrille().getCase(destination).piece != null
                    && !plateau.getGrille().getCase(destination).piece.getCouleur().equalsIgnoreCase(couleur)
                    && !plateau.getGrille().getCase(destination).piece.getClassePiece().equalsIgnoreCase("Roi"))) {
                if (!detecterEchec(plateau.getGrille().getCase(origin).piece, plateau.getGrille().getCase(destination)))
                    return true;
                else return false;
            }

        }
        if (classeOrigin.equalsIgnoreCase("Pion")) {
            if (couleur.equalsIgnoreCase("Blanc")) {
                if (dy == y + 1 && plateau.getGrille().getCase(x, y + 1).piece == null) {
                    return true;
                }
                if (dy == y + 2 && y == 2 && plateau.getGrille().getCase(x, y + 2).piece == null) {
                    return true;
                }
                if (dy == y + 1 && (dx == x + 1 || dx == x - 1) && !(plateau.getGrille().getCase(x, y + 1).piece == null)) {
                    return true;
                }
            }
            if (couleur.equalsIgnoreCase("Noir")) {
                if (dy == y - 1 && plateau.getGrille().getCase(x, y - 1).piece == null) {
                    return true;
                }
                if (dy == y - 2 && y == 7 && plateau.getGrille().getCase(x, y - 2).piece == null) {
                    return true;
                }
                if (dy == y - 1 && (dx == x + 1 || dx == x - 1) && !(plateau.getGrille().getCase(x, y - 1).piece == null)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /**
     * Fonction permettant de vérifier la légalité des déplacements
     * @param origin <code>{@linkplain Case}</code> : Case originale de la piece qu'on veut déplacer
     * @param destination <code>{@linkplain Case}</code> : Case de la destination où on veut déplacer la piece
     * @return <code>boolean</code> : true quand le déplacement est légal
     * @see Case
     * @author melissa
     */
    public boolean testerDeplacement (Case origin, Case destination){
        int x = origin.x;
        int y = origin.y;

        int dx = destination.x;
        int dy = destination.y;

        String classe = origin.piece.getClassePiece();
        String couleur = origin.piece.getCouleur();

        if (classe.equalsIgnoreCase("Reine")) {
            if (Math.abs(dx - x) == Math.abs(dy - y)) {
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
        if (classe.equalsIgnoreCase("Tour")) {
            if (dx == x) {
                return true;
            }
            if (dy == y) {
                return true;
            }
            return false;
        }
        if (classe.equalsIgnoreCase("Fou")) {
            if (Math.abs(dx - x) == Math.abs(dy - y)) {
                return true;
            }
            return false;
        }
        if (classe.equalsIgnoreCase("Cavalier")) {
            if (((dx == x + 2) || (dx == x - 2)) && ((dy == y + 1) || (dy == y - 1))) {
                return true;
            }
            if (((dx == x + 1) || (dx == x - 1)) && ((dy == y + 2) || (dy == y - 2))) {
                return true;
            }
            return false;
        }
        if (classe.equalsIgnoreCase("Roi")) {
            if (dx == x && (dy == y - 1 || dy == y + 1)) {
                return true;
            }
            if ((dx == x - 1 || dx == x + 1) && (dy == y - 1 || dy == y + 1)) {
                return true;
            }
            return false;
        }
        if (classe.equalsIgnoreCase("Pion")) {
            if (couleur.equalsIgnoreCase("Blanc")) {
                if (dy == y + 1) {
                    return true;
                }
                if (dy == y + 2 && y == 2) {
                    return true;
                }
            }
            if (couleur.equalsIgnoreCase("Noir")) {
                if (dy == y - 1) {
                    return true;
                }
                if (dy == y - 2 && y == 7) {
                    return true;
                }
            }

        }
        return false;
    }

    public void deplacerPiece() {
        Case origine = this.getCaseOrigineDepuisCoordonneeInput();
        Case destination = this.getCaseDestinationDepuisCoordonneeInput();
        String retour = this.plateau.deplacerPiece(origine, destination);
        if (retour.equals("ok")) this.tour = (this.tour.equals("blanc")) ? "noir" : "blanc";
    }

    public void undo() {
        System.out.println("undo done");
        Evenement dernierEvenement = this.plateau.historique.getDernierEvenement("undo");
        Case origine = dernierEvenement.getCaseOrigine();
        Piece pieceOrigine = dernierEvenement.getPieceOrigine();
        Case destination = dernierEvenement.getCaseDestination();
        String typeEvenement = dernierEvenement.getType();

        this.plateau.historique.addEvenement("Undo", destination, origine);

        if (typeEvenement.equalsIgnoreCase("déplacement")) {
            this.plateau.deplacerPiece(destination, origine, false);
        }
        else if (typeEvenement.equalsIgnoreCase("Prise")) {
            Piece pieceDestination = dernierEvenement.getPieceDestination();
            this.plateau.deplacerPiece(destination, origine, false);
            this.plateau.getGrille().getCase(destination).ajouterPiece(pieceDestination);
        }
        this.tour = (this.tour.equals("blanc")) ? "noir" : "blanc";
    }

    public String save() {
        String dossierSauvegardeChemin = "../save/";
        File repertoire = new File(dossierSauvegardeChemin);
        JSONObject jsonObject = this.plateau.getJSONObject();
        jsonObject.put("tour", this.tour);
        jsonObject.put("difficulte", this.niveauDeDifficulte);

        String nouveauFichierChemin = dossierSauvegardeChemin + Tools.getNomFichierSauvegarde();

        if (repertoire.isDirectory()) {
            try {
                fw = new FileWriter(nouveauFichierChemin);
                fw.write(jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fw.flush();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "Partie sauvegardé : " + nouveauFichierChemin;
    }

    public String charger() {

        // On demande si l'utilisateur veut sauvegarder sa partie avant
        System.out.println("Vous allez écraser votre partie - Voulez vous la sauvegarder ?");
        String ouiNon = Tools.getOuiNon();
        if (ouiNon.equalsIgnoreCase("oui")) {
            System.out.println(this.save() + "\n");
        }

        String stringRetour;
        String dossierSauvegardeChemin = "../save/";
        File repertoire = new File(dossierSauvegardeChemin);

        // On affiche la liste des sauvegardes pour sélection
        System.out.println("Sélectionnez la sauvegarde que vous souhaitez utiliser :\n");

        if (repertoire.isDirectory()) {
            File[] listeFiles = repertoire.listFiles();
            Arrays.sort(listeFiles, Comparator.comparingLong(File::lastModified));
            if (listeFiles.length != 0) {
                int indexFile = 0;
                for (File file : listeFiles) {
                    System.out.println(indexFile + " - " + getSaveFileName(file.getName()));
                    indexFile++;
                }

                // On demande à l'utilisateur de choisir quel sauvegarde prendre
                Scanner scannerSelectionFile = new Scanner(System.in);
                int sauvegardeChoisie = scannerSelectionFile.nextInt();


                // On charge le fichier sélectionné et on le transforme en json
                try {
                    Object obj = new JSONParser().parse(new FileReader(listeFiles[sauvegardeChoisie]));
                    JSONObject jsonObject = (JSONObject) obj;
                    this.tour = (String) jsonObject.get("tour");
                    this.niveauDeDifficulte = ((Long) jsonObject.get("difficulte")).intValue();

                    this.plateau = new PlateauDeJeu((JSONObject) obj);
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                stringRetour = "Aucune sauvegarde n'a été trouvée";
            }
        } else { stringRetour = "Dossier de sauvegarde non trouvé"; }
        stringRetour = "Partie chargée";
        return stringRetour;
    }

    public int[] lancerPartie(){
        int[] listeParam = new int[3];
        Scanner scanner = new Scanner(System.in);
        System.out.println("Pour démarrer une partie tapez : [jouer]\nPuis sélectionner le niveau de difficulté");
        System.out.println("\n1 - Normal : Règles standards\n2 - Apprenti : Possibilité de retour en arrière");
        listeParam[0] = scanner.nextInt();
        while (listeParam[0] > 2 ||  listeParam[0] < 1) {
            System.out.println("Choisissez une difficult disponible!");
            listeParam[0] = scanner.nextInt();
        }
        System.out.println("-------- Début de la partie --------");
        return listeParam;
    }

    public Boolean finirPartie() {
        System.out.println("[Voulez vous sauvegarder votre partie ?]");
        if (Tools.getOuiNon().equalsIgnoreCase("oui")) this.save();
        return false;
    }

    public void quitterJeu() {
        System.out.println("[Voulez vous sauvegarder votre partie ?]");
        if (Tools.getOuiNon().equalsIgnoreCase("oui")) this.save();
        System.exit(0);
    }

    public void help() {
        System.out.println("[Déplacer] " + "-".repeat(40) +
                "| Entrez : deplacer ou d\n" +
                "| Commande permettant de déplacer une pièce sur l'échiquier\n" +
                "| Premier entrée  : Coordonnées de la case contenant le pion que vous souhaitez déplacer" +
                "\n|\t\t Entrez au format numérique : [xy] ou [x y]; Exemple : a5 ou g 7" +
                "\n|\t\t Entrez au format échec     : [ay] ou [x y]; Exemple : a5 ou g 7\n" +
                "| Deuxième entrée : Coordonnées de la case où vous souhaitez déplacer votre pion" +
                "\n|\t\t Entrez au format numérique : [xy] ou [x y]; Exemple : a5 ou g 7" +
                "\n|\t\t Entrez au format échec     : [ay] ou [x y]; Exemple : a5 ou g 7\n");

        System.out.println("[Prendre] " + "-".repeat(40) +
                "| Entrez : prendre ou p\n" +
                "| Commande permettant de prendre une pièce sur l'échiquier\n" +
                "| Premier entrée : Coordonnées de la case contenant le pion que vous souhaitez déplacer" +
                "\n|\t\t Entrez au format numérique : [xy] ou [x y]; Exemple : a5 ou g 7" +
                "\n|\t\t Entrez au format échec     : [ay] ou [x y]; Exemple : a5 ou g 7\n" +
                "| Premier entrée : Coordonnées de la case où vous souhaitez déplacer votre pion et y prendre le pion présent" +
                "\n|\t\t Entrez au format numérique : [xy] ou [x y]; Exemple : a5 ou g 7" +
                "\n|\t\t Entrez au format échec     : [ay] ou [x y]; Exemple : a5 ou g 7\n");

        System.out.println("[Save] " + "-".repeat(43) +
                "| Entrez : save ou s\n" +
                "| Commande permettant de sauvegarder l'état actuel de votre partie dans un fichier de sauvegarde\n" +
                "| pour pouvoir la recharger plus tard\n" +
                "| Premier entrée : Indiquez le nom que vous souhaitez donner à votre sauvegarde\n" +
                "| Par défaut (c'est à dire si vous ne renseignez aucun nom de sauvegarde)\n" +
                "| la sauvergarde sera enregistré avec comme nom la date et heure au format : YYYYMMJJ-hhmmss\n");

        System.out.println("[Charger] " + "-".repeat(40) +
                "| Entrez : charger ou c\n" +
                "| Commande permettant de charger une ancienne sauvegarde et d'y reprendre la partie sauvegardée\n" +
                "| Premier entrée  : Indiquez si vous voulez sauvegarder votre partie actuelle" +
                "\n|\t\t Entrez au format oui/non : [oui] ou [non]" +
                "| Deuxième entrée : Sélectionnez la sauvegarde à charger\n");

        System.out.println("[Undo] " + "-".repeat(43) +
                "| Entrez : undo ou u\n" +
                "| Commande permettant de revenir sur les coups précédents [UNIQUEMENT SI EN MODE APPRENTI]\n");

        System.out.println("[Quitter] " + "-".repeat(41) +
                "| Entrez : undo ou u\n" +
                "| Commande permettant de quitter le jeu et d'éteindre le programme\n" +
                "| Premier entrée  : Indiquez si vous voulez sauvegarder votre partie actuelle" +
                "\n|\t\t Entrez au format oui/non : [oui] ou [non]\n");

        System.out.println("[Abandonner] " + "-".repeat(37) +
                "| Entrez : undo ou u\n" +
                "| Commande permettant d'abandonner votre partie actuelle, et de revenir à l'écran de sélection\n" +
                "| de niveau de difficulté de parties\n" +
                "| Premier entrée  : Indiquez si vous voulez sauvegarder votre partie actuelle" +
                "\n|\t\t Entrez au format oui/non : [oui] ou [non]\n");

        System.out.println("[Abandonner] " + "-".repeat(37) +
                "| Entrez : undo ou u\n" +
                "| Commande permettant d'afficher les indications sur les commandes disponibles\n");

        System.out.println("[Historique] " + "-".repeat(37) +
                "| Entrez : historique ou n\n" +
                "| Commande permettant d'afficher l'historique des coups et undo\n");
    }
    public String getCommandeInput() {
        printListeCommande();
        Scanner inputCommandeScanner = new Scanner(System.in);
        String commande = inputCommandeScanner.nextLine();
        return commande;
    }

    public void printListeCommande() {
        String strCommande = "[";
        for (String commande : arrayDesCommandes) {strCommande += commande + "/";}
        strCommande += "] ou leurs raccourcis : [";
        for (String commande : arrayDesCommandesRaccourci) {strCommande += commande + "/";}
        System.out.println(strCommande + "]");
    }

    public Case getCaseOrigineDepuisCoordonneeInput() {
        Scanner inputCoordDestinataireScanner = new Scanner(System.in);
        System.out.println("Origine : [xy ou x y] ou [ay ou a y]");
        String coordDestination = Tools.getCoordonneeBonFormat(inputCoordDestinataireScanner);
        Case destination = convertirCaseDepuisInput(coordDestination, this.plateau.getGrille());
        return destination;
    }

    public Case getCaseDestinationDepuisCoordonneeInput() {

        Scanner inputCoordDestinataireScanner = new Scanner(System.in);
        System.out.println("Destination : [xy ou x y] ou [ay ou a y]");
        String coordDestination = Tools.getCoordonneeBonFormat(inputCoordDestinataireScanner);
        Case destination = convertirCaseDepuisInput(coordDestination, this.plateau.getGrille());
        return destination;
    }

    public Case convertirCaseDepuisInput(String inputString, Grille grile) {
        int x;
        int y;
        if (inputString.contains(" ")) {
            x = Integer.parseInt(inputString.split(" ")[0]);
            y = Integer.parseInt(inputString.split(" ")[1]);
        } else {
            x = Integer.parseInt(inputString.split("")[0]);
            y = Integer.parseInt(inputString.split("")[1]);
        }
        return grile.getCase(x, y);
    }

    public static String getSaveFileName(String fileName) {
        if (fileName.matches("[0-9-]")) {
            String parsedSaveName = "";
            String[] fileNameArray = fileName.split("");
            for (int a = 0; a < 4; a++) {
                parsedSaveName += fileNameArray[a];
            }  parsedSaveName += "/";
            for (int m = 4; m < 6; m++) {
                parsedSaveName += fileNameArray[m];
            }  parsedSaveName += "/";
            for (int j = 6; j < 8; j++) {
                parsedSaveName += fileNameArray[j];
            }  parsedSaveName += " ";
            for (int h = 9; h < 11; h++) {
                parsedSaveName += fileNameArray[h];
            }  parsedSaveName += ":";
            for (int m = 11; m < 13; m++) {
                parsedSaveName += fileNameArray[m];
            }  parsedSaveName += ":";
            for (int s = 13; s < 15; s++) {
                parsedSaveName += fileNameArray[s];
            }  parsedSaveName += "";
            return parsedSaveName;
        } else return fileName;
    }
}
