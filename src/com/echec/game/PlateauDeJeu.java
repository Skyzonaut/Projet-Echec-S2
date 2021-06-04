package com.echec.game;

import com.echec.Tools;
import com.echec.pieces.Piece;
import org.json.simple.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlateauDeJeu {

    private String id;
    private Grille grille;
    public Historique historique = new Historique();
    public ArrayList<Deplacement> listeDeplacementNoirs = new ArrayList<>();
    public ArrayList<Deplacement> listeDeplacementBlancs = new ArrayList<>();
    private boolean enEchec = false;
    private Echec echec;

    public PlateauDeJeu() {
        this.id = "plateau " + Tools.getFormatDate();
        this.grille = new Grille();
        this.initGrille();
    }

    public PlateauDeJeu(JSONObject jsonObject) {
        this.id = (String) jsonObject.get("id");
        this.grille = new Grille((JSONObject) jsonObject.get("grille"));
        this.historique = new Historique((JSONObject) jsonObject.get("historique"));
        this.enEchec = (Boolean) jsonObject.get("enEchec");
    }

    public void init() {
        this.id = "plateau " + Tools.getFormatDate();
        this.grille = new Grille();
        this.historique = new Historique();
        this.initGrille();
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

        StringBuilder dessinPlateau = new StringBuilder(" ");

        for (int colonne = 1; colonne <= 8; colonne++) {
            for (int j = 0; j < largeur; j++) {
                if (j == (largeur / 2)) {
                    dessinPlateau.append(Tools.getLettreColonne(colonne)).append(" ");
                } else {
                    dessinPlateau.append(" ");
                }
            }
        }

        dessinPlateau.append("\n-");

        for (int i = 0; i < 8; i++) {
            dessinPlateau.append("-".repeat(largeur));
            dessinPlateau.append("-");
        }
        dessinPlateau.append("\n");

        int ligneCompte = 8;

        for (int ligne = 1; ligne < hauteur * 8; ligne++) {
            String contenu = " ";
            dessinPlateau.append("|");

            for (int colonne = 1; colonne <= 8; colonne++) {
                if (this.grille.getCase(colonne, ligneCompte).piece != null) {
                    contenu = this.grille.getCase(colonne, ligneCompte).piece.utfString();
                }

                for (int j = 0; j < largeur; j++) {
                    if (j == (largeur / 2)) {
                        if (!contenu.equals(" ")) {
                            dessinPlateau.append(ligne % hauteur == 0 ? " " : contenu);
                        } else {
                            dessinPlateau.append(" ");
                        }
                    } else {
                        dessinPlateau.append(ligne % hauteur == 0 ? "-" : " ");
                    }
                }
                contenu = " ";
                dessinPlateau.append(ligne % hauteur == 0 ? "+" : "|");

            }
            dessinPlateau.append(ligne % hauteur == 0 ? " " : " " + ligneCompte);
            dessinPlateau.append("\n");

            if (ligne % hauteur == 0) ligneCompte--;
        }
        dessinPlateau.append("-");

        for (int i = 0; i < 8; i++) {
            dessinPlateau.append("-".repeat(Math.max(0, largeur)));
            dessinPlateau.append("-");
        }
        dessinPlateau.append("\n");

        return dessinPlateau.toString();
    }

    public Grille getGrille() {
        return this.grille;
    }

    public void initGrille() {
        this.grille.initialiserGrille();
    }

    public String deplacerPiece(Case origine, Case destination) {
        return deplacerPiece(origine, destination, true);
    }

    public String deplacerPiece(Case origine, Case destination, boolean updateHistorique) {
        if (!origine.estVide()) {
            if (destination.estVide()) {
                destination.piece = origine.piece;
                if (updateHistorique) {
                    this.historique.addEvenement("Déplacement", origine, destination);
                }
                origine.vider();
                this.updateListeDeplacements();
                return "ok";
            } else {
                System.out.println("La destination n'est pas vide, veuillez utiliser la commande [prendre]");
                return "nok";
            }
        } else {
            System.out.println("L'origine est vide!");
            return "nok";
        }
    }

    public String prendrePiece(Case origine, Case destination) {
        if (!destination.estVide()) {
            destination.piece.setEtat(false);
            this.historique.addEvenement("Prise", origine, destination);
            destination.vider();
            deplacerPiece(origine, destination, false);
            return "ok";
        } else {
            System.out.println("La destination est pas vide, veuillez utiliser la commande [déplacer]");
            return "nok";
        }
    }


    public void setId(String value) {
        this.id = value;
    }

    public String getId() {
        return this.id;
    }

    public boolean isEnEchec() {
        return enEchec;
    }

    public void setEnEchec(boolean enEchec) {
        this.enEchec = enEchec;
    }

    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", this.id);
        jsonObject.put("grille", this.grille.getJSONObject());
        jsonObject.put("historique", this.historique.getJSONObject());
        jsonObject.put("enEchec",this.enEchec );
        return jsonObject;
    }

    /**
     * Fonction permettant d'avoir les déplacements possibles d'une piece
     * @param posPiece <code>{@linkplain Case}</code> : Case de la piece dont on veut avoir les déplacements
     * @return <code>ArrayList<{@linkplain Case}></code> : Retourne une liste de cases des déplacements possibles
     * @see Case
     * @author melissa
     */
    public ArrayList<Case> deplacementsPossible (Case posPiece) {
        ArrayList<Case> listeCases = new ArrayList<>();

        int x = posPiece.x;
        int y = posPiece.y;

        String classePiece = grille.getCase(posPiece).piece.getClassePiece();
        String couleurPiece = grille.getCase(posPiece).piece.getCouleur();

        if (classePiece.equalsIgnoreCase("Pion")) {
            if (couleurPiece.equalsIgnoreCase("Blanc")) {
                if (x <= 8 && x > 0 && y + 1 <= 8) {
                    if (grille.getCase(x, y + 1).estVide()) {
                        listeCases.add(this.grille.getCase(x, y + 1));
                    }
                }
                if (y == 2) {
                    if (grille.getCase(x, y + 2).estVide()) {
                        listeCases.add(this.grille.getCase(x, y + 2));
                    }
                }
                if (x + 1 <= 8 && y + 1 <= 8) {
                    if (grille.getCase(x + 1, y + 1).piece != null) {
                        if (!grille.getCase(x + 1, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                            listeCases.add(this.grille.getCase(x + 1, y + 1));
                        }
                    }
                }
                if (x - 1 > 0 && y + 1 <= 8) {
                    if (grille.getCase(x - 1, y + 1).piece != null) {
                        if (!grille.getCase(x - 1, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                            listeCases.add(this.grille.getCase(x - 1, y + 1));
                        }
                    }
                }
            }
            if (couleurPiece.equalsIgnoreCase("Noir")) {
                if ( x > 0 && x <= 8 && y - 1 > 0) {
                    if (grille.getCase(x, y - 1).estVide()) {
                        listeCases.add(this.grille.getCase(x, y - 1));
                    }
                }
                if (y == 7) {
                    if (grille.getCase(x, y - 2).estVide()) {
                        listeCases.add(this.grille.getCase(x, y - 2));
                    }
                }
                if (x + 1 <= 8 && y - 1 > 0) {
                    if (grille.getCase(x + 1, y - 1).piece != null) {
                        if (!grille.getCase(x + 1, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                            listeCases.add(this.grille.getCase(x + 1, y - 1));
                        }
                    }
                }
                if (x - 1 > 0 && y - 1 > 0) {
                    if (grille.getCase(x - 1, y - 1).piece != null) {
                        if (!grille.getCase(x - 1, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                            listeCases.add(this.grille.getCase(x - 1, y - 1));
                        }
                    }
                }
            }
        }
        if (classePiece.equalsIgnoreCase("Tour")) {
            for (int i = x - 1; i > 0; i--) {
                if (grille.getCase(i, y).estVide()) {
                    listeCases.add(this.grille.getCase(i, y));
                } else {
                    if (!grille.getCase(i, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(i, y));
                    }
                    break;
                }
            }
            for (int i = x + 1; i <= 8; i++) {
                if (grille.getCase(i, y).estVide()) {
                    listeCases.add(this.grille.getCase(i, y));
                } else {
                    if (!grille.getCase(i, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(i, y));
                    }
                    break;
                }
            }
            for (int i = y - 1; i > 0; i--) {
                if (grille.getCase(x, i).estVide()) {
                    listeCases.add(this.grille.getCase(x, i));
                } else {
                    if (!grille.getCase(x, i).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(x, i));
                    }
                    break;
                }
            }
            for (int i = y + 1; i <= 8; i++) {
                if (grille.getCase(x, i).estVide()) {
                    listeCases.add(this.grille.getCase(x, i));
                } else {
                    if (!grille.getCase(x, i).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(x, i));
                    }
                    break;
                }
            }
        }

        if (classePiece.equalsIgnoreCase("Reine")) {
            for (int i = x - 1; i > 0; i--) {
                if (grille.getCase(i, y).estVide()) {
                    listeCases.add(this.grille.getCase(i, y));
                } else {
                    if (!grille.getCase(i, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(i, y));
                    }
                    break;
                }
            }
            for (int i = x + 1; i <= 8; i++) {
                if (grille.getCase(i, y).estVide()) {
                    listeCases.add(this.grille.getCase(i, y));
                } else {
                    if (!grille.getCase(i, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(i, y));
                    }
                    break;
                }
            }
            for (int i = y - 1; i > 0; i--) {
                if (grille.getCase(x, i).estVide()) {
                    listeCases.add(this.grille.getCase(x, i));
                } else {
                    if (!grille.getCase(x, i).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(x, i));
                    }
                    break;
                }
            }
            for (int i = y + 1; i <= 8; i++) {
                if (grille.getCase(x, i).estVide()) {
                    listeCases.add(this.grille.getCase(x, i));
                } else {
                    if (!grille.getCase(x, i).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(x, i));
                    }
                    break;
                }
            }

            int a = x + 1;
            int b = y + 1;
            while (a <= 8 && b <= 8) {
                if (grille.getCase(a, b).estVide()) {
                    listeCases.add(this.grille.getCase(a, b));
                } else {
                    if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                    break;
                }
                a++;
                b++;
            }

            a = x + 1;
            b = y - 1;
            while (a <= 8 && b > 0) {
                if (grille.getCase(a, b).estVide()) {
                    listeCases.add(this.grille.getCase(a, b));
                } else {
                    if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                    break;
                }
                a++;
                b--;
            }

            a = x - 1;
            b = y + 1;
            while (a > 0 && b <= 8) {
                if (grille.getCase(a, b).estVide()) {
                    listeCases.add(this.grille.getCase(a, b));
                } else {
                    if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                    break;
                }
                a--;
                b++;
            }

            a = x - 1;
            b = y - 1;
            while (a > 0 && b > 0) {
                if (grille.getCase(a, b).estVide()) {
                    listeCases.add(this.grille.getCase(a, b));
                } else {
                    if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                    break;
                }
                a--;
                b--;
            }
        }

        if (classePiece.equalsIgnoreCase("Fou")) {
            int a = x + 1;
            int b = y + 1;
            while (a <= 8 && b <= 8) {
                if (grille.getCase(a, b).estVide()) {
                    listeCases.add(this.grille.getCase(a, b));
                } else {
                    if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                    break;
                }
                a++;
                b++;
            }

            a = x + 1;
            b = y - 1;
            while (a < 8 && b > 0) {
                if (grille.getCase(a, b).estVide()) {
                    listeCases.add(this.grille.getCase(a, b));
                } else {
                    if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                    break;
                }
                a++;
                b--;
            }

            a = x - 1;
            b = y + 1;
            while (a > 0 && b < 8) {
                if (grille.getCase(a, b).estVide()) {
                    listeCases.add(this.grille.getCase(a, b));
                } else {
                    if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                    break;
                }
                a--;
                b++;
            }

            a = x - 1;
            b = y - 1;
            while (a > 0 && b > 0) {
                if (grille.getCase(a, b).estVide()) {
                    listeCases.add(this.grille.getCase(a, b));
                } else {
                    if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                    break;
                }
                a--;
                b--;
            }
        }
        if (classePiece.equalsIgnoreCase("Cavalier")) {
            if (x - 1 > 0 && y + 2 <= 8) {
                if (grille.getCase(x - 1, y + 2).estVide()) {
                    listeCases.add(this.grille.getCase(x - 1, y + 2));
                } else {
                    if (!grille.getCase(x - 1, y + 2).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(x - 1, y + 2));
                    }
                }
            }
            if (x - 2 > 0 && y + 1 <= 8) {
                if (grille.getCase(x - 2, y + 1).estVide()) {
                    listeCases.add(this.grille.getCase(x - 2, y + 1));
                } else {
                    if (!grille.getCase(x - 2, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(x - 2, y + 1));
                    }
                }
            }
            if (x - 2 > 0 && y - 1 > 0) {
                if (grille.getCase(x - 2, y - 1).estVide()) {
                    listeCases.add(this.grille.getCase(x - 2, y - 1));
                } else {
                    if (!grille.getCase(x - 2, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(x - 2, y - 1));
                    }
                }
            }
            if (x - 1 > 0 && y - 2 > 0) {
                if (grille.getCase(x - 1, y - 2).estVide()) {
                    listeCases.add(this.grille.getCase(x - 1, y - 2));
                } else {
                    if (!grille.getCase(x - 1, y - 2).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(x - 1, y - 2));
                    }
                }
            }
            if (x + 1 <= 8 && y - 2 > 0) {
                if (grille.getCase(x + 1, y - 2).estVide()) {
                    listeCases.add(this.grille.getCase(x + 1, y - 2));
                } else {
                    if (!grille.getCase(x + 1, y - 2).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(x + 1, y - 2));
                    }
                }
            }
            if (x + 2 <= 8 && y - 1 > 0) {
                if (grille.getCase(x + 2, y - 1).estVide()) {
                    listeCases.add(this.grille.getCase(x + 2, y - 1));
                } else {
                    if (!grille.getCase(x + 2, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(x + 2, y - 1));
                    }
                }
            }
            if (x + 1 <= 8 && y + 2 <= 8) {
                if (grille.getCase(x + 1, y + 2).estVide()) {
                    listeCases.add(this.grille.getCase(x + 1, y + 2));
                } else {
                    if (!grille.getCase(x + 1, y + 2).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(x + 1, y + 2));
                    }
                }
            }
            if (x + 2 <= 8 && y + 1 <= 8) {
                if (grille.getCase(x + 2, y + 1).estVide()) {
                    listeCases.add(this.grille.getCase(x + 2, y + 1));
                } else {
                    if (!grille.getCase(x + 2, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        listeCases.add(this.grille.getCase(x + 2, y + 1));
                    }
                }
            }
        }
        if (classePiece.equalsIgnoreCase("Roi")) {
            if (x + 1 <= 8 && y + 1 <= 8) {
                if (grille.getCase(x + 1, y + 1).estVide()) {
                    if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x + 1, y + 1))) {
                        listeCases.add(this.grille.getCase(x + 1, y + 1));
                    }
                } else {
                    if (!grille.getCase(x + 1, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x + 1, y + 1))) {
                            listeCases.add(this.grille.getCase(x + 1, y + 1));
                        }
                    }
                }
            }
            if (x - 1 > 0 && y + 1 <= 8) {
                if (grille.getCase(x - 1, y + 1).estVide()) {
                    if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x - 1, y + 1))) {
                        listeCases.add(this.grille.getCase(x - 1, y + 1));
                    }
                } else {
                    if (!grille.getCase(x - 1, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x - 1, y + 1))) {
                            listeCases.add(this.grille.getCase(x - 1, y + 1));
                        }
                    }
                }
            }
            //x, y + 1
            if (y + 1 <= 8) {
                if (grille.getCase(x, y + 1).estVide()) {
                    if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x, y + 1))) {
                        listeCases.add(this.grille.getCase(x, y + 1));
                    }
                } else {
                    if (!grille.getCase(x, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x, y + 1))) {
                            listeCases.add(this.grille.getCase(x, y + 1));
                        }
                    }
                }
            }
            //x, y - 1
            if (y - 1 > 0) {
                if (grille.getCase(x, y - 1).estVide()) {
                    if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x, y - 1))) {
                        listeCases.add(this.grille.getCase(x, y - 1));
                    }
                } else {
                    if (!grille.getCase(x, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x, y - 1))) {
                            listeCases.add(this.grille.getCase(x, y - 1));
                        }
                    }
                }
            }
            //x + 1, y
            if (x + 1 <= 8) {
                if (grille.getCase(x + 1, y).estVide()) {
                    if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x + 1, y))) {
                        listeCases.add(this.grille.getCase(x + 1, y));
                    }
                } else {
                    if (!grille.getCase(x + 1, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x + 1, y))) {
                            listeCases.add(this.grille.getCase(x + 1, y));
                        }
                    }
                }
            }
            //x - 1, y
            if (x - 1 > 0 ) {
                if (grille.getCase(x - 1, y).estVide()) {
                    if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x - 1, y))) {
                        listeCases.add(this.grille.getCase(x - 1, y));
                    }
                } else {
                    if (!grille.getCase(x - 1, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x - 1, y))) {
                            listeCases.add(this.grille.getCase(x - 1, y));
                        }
                    }
                }
            }
            //x - 1, y - 1
            if (x - 1 > 0 && y - 1 > 0) {
                if (grille.getCase(x - 1, y - 1).estVide()) {
                    if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x - 1, y - 1))) {
                        listeCases.add(this.grille.getCase(x - 1, y - 1));
                    }
                } else {
                    if (!grille.getCase(x - 1, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x - 1, y - 1))) {
                            listeCases.add(this.grille.getCase(x - 1, y - 1));
                        }
                    }
                }
            }
            //x + 1, y - 1
            if (x + 1 <= 8 && y - 1 > 0) {
                if (grille.getCase(x + 1, y - 1).estVide()) {
                    if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x + 1, y - 1))) {
                        listeCases.add(this.grille.getCase(x + 1, y - 1));
                    }
                } else {
                    if (!grille.getCase(x + 1, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                        if (!detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x + 1, y - 1))) {
                            listeCases.add(this.grille.getCase(x + 1, y - 1));
                        }
                    }
                }
            }
        }
        return listeCases;
    }

    public void updateListeDeplacements() {
        ArrayList<Case> listeNoirs;
        ArrayList<Case> listeBlancs;
        ArrayList<Case> l = new ArrayList<>();
        listeNoirs = this.grille.getListePieceCouleur("noir");
        listeBlancs = this.grille.getListePieceCouleur("blanc");

        for (Case c : listeNoirs) {
            if (c.piece.getClassePiece().equalsIgnoreCase("pion")) {
                l = new ArrayList<>();
                l.add(this.grille.getCase(c.x + 1, c.y + 1));
                l.add(this.grille.getCase(c.x - 1, c.y + 1));
                this.listeDeplacementNoirs.add(new Deplacement(c, l));
            } else if (c.piece.getClassePiece().equalsIgnoreCase("roi")) {

                l = new ArrayList<>();
                l.add(this.grille.getCase(c.x, c.y + 1));
                l.add(this.grille.getCase(c.x, c.y - 1));
                this.listeDeplacementNoirs.add(new Deplacement(c, l));

                l = new ArrayList<>();
                l.add(this.grille.getCase(c.x + 1, c.y + 1));
                l.add(this.grille.getCase(c.x + 1, c.y ));
                l.add(this.grille.getCase(c.x + 1, c.y - 1));
                this.listeDeplacementNoirs.add(new Deplacement(c, l));

                l = new ArrayList<>();
                l.add(this.grille.getCase(c.x - 1, c.y + 1));
                l.add(this.grille.getCase(c.x - 1, c.y));
                l.add(this.grille.getCase(c.x - 1, c.y - 1));
                this.listeDeplacementNoirs.add(new Deplacement(c, l));

            } else {
                l = new ArrayList<>();
                for (Case d : this.deplacementsPossible(c)) {
                    l.add(d);
                }
                this.listeDeplacementNoirs.add(new Deplacement(c, l));
            }
        }

        for (Case c : listeBlancs) {
            if (c.piece.getClassePiece().equalsIgnoreCase("pion")) {
                l = new ArrayList<>();
                l.add(this.grille.getCase(c.x + 1, c.y + 1));
                l.add(this.grille.getCase(c.x - 1, c.y + 1));
                this.listeDeplacementBlancs.add(new Deplacement(c, l));
            } else if (c.piece.getClassePiece().equalsIgnoreCase("roi")) {

                l = new ArrayList<>();
                l.add(this.grille.getCase(c.x, c.y + 1));
                l.add(this.grille.getCase(c.x, c.y - 1));
                this.listeDeplacementBlancs.add(new Deplacement(c, l));

                l = new ArrayList<>();
                l.add(this.grille.getCase(c.x + 1, c.y + 1));
                l.add(this.grille.getCase(c.x + 1, c.y ));
                l.add(this.grille.getCase(c.x + 1, c.y - 1));
                this.listeDeplacementBlancs.add(new Deplacement(c, l));

                l = new ArrayList<>();
                l.add(this.grille.getCase(c.x - 1, c.y + 1));
                l.add(this.grille.getCase(c.x - 1, c.y));
                l.add(this.grille.getCase(c.x - 1, c.y - 1));
                this.listeDeplacementBlancs.add(new Deplacement(c, l));

            } else {
                l = new ArrayList<>();
                for (Case d : this.deplacementsPossible(c)) {
                    l.add(d);
                }
                this.listeDeplacementBlancs.add(new Deplacement(c, l));
            }
        }
    }

    public boolean detecterEchec2 (Piece pieceRoi, Case posRoi) {
        String couleurEnnemie = (pieceRoi.getCouleur() == "noir") ? "blanc" : "noir";
        Echec echec = new Echec(this, posRoi);
        if (couleurEnnemie.equals("noir")) {
            for (Deplacement d : this.listeDeplacementNoirs) {
                if (d.contains(posRoi)) {
                    echec.addAttaquant(d);
                }
            }
        }
        if (couleurEnnemie.equals("blanc")) {
            for (Deplacement d : this.listeDeplacementBlancs) {
                if (d.contains(posRoi)) {
                    echec.addAttaquant(d);
                }
            }
        }
        echec.trouverSauveur();
        this.echec = echec;
        return echec.isEchec();
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
    public boolean detecterEchec (Piece pieceRoi, Case posRoi){
        int x = posRoi.x;
        int y = posRoi.y;

        int i;
        int j;

//        int supX = posRoi.x + 1;
//        int infX = posRoi.x - 1;
//        int supY = posRoi.y + 1;
//        int infY = posRoi.y - 1;


        int supX = x;
        int infX = x;
        int supY = y;
        int infY = y;

        String couleur = pieceRoi.getCouleur();

        for (i = infX; i > 0; i--) {
            if (grille.getCase(i, y).piece != null) {
                if (!grille.getCase(i, y).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (grille.getCase(i, y).piece.getClassePiece().equalsIgnoreCase("Reine")
                            || grille.getCase(i, y).piece.getClassePiece().equalsIgnoreCase("Tour")) {
                        for (j = infX; j > i; j++) {
                            if (grille.getCase(j, y).piece != null) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            } else i--;
        }
        for (i = supX; i <= 8; i++) {
            if (grille.getCase(i, y).piece != null) {
                if (!grille.getCase(i, y).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (grille.getCase(i, y).piece.getClassePiece().equalsIgnoreCase("Reine") || grille.getCase(i, y).piece.getClassePiece().equalsIgnoreCase("Tour")) {
                        for (j = supX; j < i; j++) {
                            if (grille.getCase(j, y).piece != null) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            } else i++;
        }
        for (i = infY; i > 0; i--) {
            if (grille.getCase(x, i).piece != null) {
                if (!grille.getCase(x, i).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (grille.getCase(x, i).piece.getClassePiece().equalsIgnoreCase("Reine") || grille.getCase(x, i).piece.getClassePiece().equalsIgnoreCase("Tour")) {
                        for (j = infY; j > i; j++) {
                            if (grille.getCase(x, j).piece != null) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            } else i--;
        }
        for (i = supY; i <= 8; i++) {
            if (grille.getCase(x, i).piece != null) {
                if (!grille.getCase(x, i).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (grille.getCase(x, i).piece.getClassePiece().equalsIgnoreCase("Reine") || grille.getCase(x, i).piece.getClassePiece().equalsIgnoreCase("Tour")) {
                        for (j = supY; j < i; j++) {
                            if (grille.getCase(x, j).piece != null) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
            }
        }

        i = supX;
        j = supY;
        while (i <= 8 && j <= 8) {
            if (grille.getCase(i, j).piece != null) {
                if (!grille.getCase(i, j).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (grille.getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Reine") || grille.getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Fou")) {
                        int a = supX;
                        int b = supY;
                        while (a < i && b < j) {
                            if (grille.getCase(a, b).piece != null)
                                return false;
                            a++;
                            b++;
                        }
                    }
                }
            } else i++; j++;
        }

        i = infX;
        j = supY;
        while (i > 0 && j <= 8) {
            if (grille.getCase(i, j).piece != null) {
                if (!grille.getCase(i, j).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (grille.getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Reine") || grille.getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Fou")) {
                        int a = infX;
                        int b = supY;
                        while (a > i && b < j) {
                            if (grille.getCase(a, b).piece != null)
                                return false;
                            a--;
                            b++;
                        }
                    }
                }
            } j++; i--;
        }

        i = supX;
        j = infY;
        while (i <= 8 && j > 0) {
            if (grille.getCase(i, j).piece != null) {
                if (!grille.getCase(i, j).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (grille.getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Reine") || grille.getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Fou")) {
                        int a = supX;
                        int b = infY;
                        while (a < i && b > j) {
                            if (grille.getCase(a, b).piece != null)
                                return false;
                            a++;
                            b--;
                        }
                    }
                }
            } j--; i++;
        }

        i = infX;
        j = infY;
        while (i > 0 && j > 0) {
            if (grille.getCase(i, j).piece != null) {
                if (!grille.getCase(i, j).piece.getCouleur().equalsIgnoreCase(couleur)) {
                    if (grille.getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Reine") || grille.getCase(i, j).piece.getClassePiece().equalsIgnoreCase("Fou")) {
                        int a = infX;
                        int b = infY;
                        while (a > i && b > j) {
                            if (grille.getCase(i, j).piece != null)
                                return false;
                            a--;
                            b--;
                        }
                    }
                }
            } j--; i--;
        }

        if (x - 1 > 0 && y + 2 <= 8) {
            if (grille.getCase(x - 1, y + 2).piece != null) {
                if (grille.getCase(x - 1, y + 2).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                        && !grille.getCase(x - 1, y + 2).piece.getCouleur().equalsIgnoreCase(couleur))
                    return true;
            }
        }
        if (x - 2 > 0 && y + 1 <= 8) {
            if (grille.getCase(x - 2, y + 1).piece != null) {
                if (grille.getCase(x - 2, y + 1).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                        && !grille.getCase(x - 2, y + 1).piece.getCouleur().equalsIgnoreCase(couleur))
                    return true;
            }
        }
        if (x - 2 > 0 && y - 1 > 0) {
            if (grille.getCase(x - 2, y - 1).piece != null) {
                if (grille.getCase(x - 2, y - 1).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                        && !grille.getCase(x - 1, y + 2).piece.getCouleur().equalsIgnoreCase(couleur))
                    return true;
            }
        }
        if (x - 1 > 0 && y - 2 > 0) {
            if (grille.getCase(x - 1, y - 2).piece != null) {
                if (grille.getCase(x - 1, y - 2).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                        && !grille.getCase(x - 1, y - 2).piece.getCouleur().equalsIgnoreCase(couleur))
                    return true;
            }
        }
        if (x + 1 <= 8 && y - 2 > 0) {
            if (grille.getCase(x + 1, y - 2).piece != null) {
                if (grille.getCase(x + 1, y - 2).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                        && !grille.getCase(x + 1, y - 2).piece.getCouleur().equalsIgnoreCase(couleur))
                    return true;
            }
        }
        if (x + 2 <= 8 && y - 1 > 0) {
            if (grille.getCase(x + 2, y - 1).piece != null) {
                if (grille.getCase(x + 2, y - 1).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                        && !grille.getCase(x + 2, y - 1).piece.getCouleur().equalsIgnoreCase(couleur))
                    return true;
            }
        }
        if (x + 1 <= 8 && y + 2 <= 8) {
            if (grille.getCase(x + 1, y + 2).piece != null) {
                if (grille.getCase(x + 1, y + 2).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                        && !grille.getCase(x + 1, y + 2).piece.getCouleur().equalsIgnoreCase(couleur))
                    return true;
            }
        }
        if (x + 2 <= 8 && y + 1 <= 8) {
            if (grille.getCase(x + 2, y + 1).piece != null) {
                if (grille.getCase(x + 2, y + 1).piece.getClassePiece().equalsIgnoreCase("Cavalier")
                        && !grille.getCase(x + 2, y + 1).piece.getCouleur().equalsIgnoreCase(couleur))
                    return true;
            }
        }

        if (couleur.equalsIgnoreCase("Noir")) {
            if (grille.getCase(x-1, y-1).piece != null) {
                if ((grille.getCase(x-1, y-1).piece.getClassePiece().equalsIgnoreCase("Pion")
                        && grille.getCase(x-1, y-1).piece.getClassePiece().equalsIgnoreCase("Blanc")))
                    return true;
            }
            if (grille.getCase(x+1, y-1).piece != null) {
                if (grille.getCase(x+1, y-1).piece.getClassePiece().equalsIgnoreCase("Pion")
                        && (grille.getCase(x+1, y-1).piece.getCouleur().equalsIgnoreCase("Blanc")))
                    return true;
            }
        }
        if (couleur.equalsIgnoreCase("Blanc")) {

            if (grille.getCase(x+1, y+1).piece != null) {
                if ((grille.getCase(x+1, y+1).piece.getClassePiece().equalsIgnoreCase("Pion")
                        && grille.getCase(x+1, y+1).piece.getClassePiece().equalsIgnoreCase("Noir")))
                    return true;
            }

            if (grille.getCase(x-1, y+1).piece != null) {
                if (grille.getCase(x-1, y+1).piece.getClassePiece().equalsIgnoreCase("Pion")
                        && (grille.getCase(x-1, y+1).piece.getCouleur().equalsIgnoreCase("Noir")))
                    return true;
            }
        }

        String couleurEnnemie = (pieceRoi.getCouleur() == "noir") ? "blanc" : "noir";

        if (couleur.equalsIgnoreCase(couleurEnnemie)) {
            if (grille.getCase(x, infY).piece != null) {
                if (grille.getCase(x, infY).piece.getClassePiece().equalsIgnoreCase("Roi")
                        && grille.getCase(x, infY).piece.getCouleur().equalsIgnoreCase(couleurEnnemie))
                    return true;
            }
            if (grille.getCase(x, supY).piece != null) {
                if (grille.getCase(x, supY).piece.getClassePiece().equalsIgnoreCase("Roi")
                        && grille.getCase(x, supY).piece.getCouleur().equalsIgnoreCase(couleurEnnemie))
                    return true;
            }
            if (grille.getCase(infX, y).piece != null) {
                if (grille.getCase(infX, y).piece.getClassePiece().equalsIgnoreCase("Roi")
                        && grille.getCase(infX, y).piece.getCouleur().equalsIgnoreCase(couleurEnnemie))
                    return true;
            }
            if (grille.getCase(supX, y).piece != null) {
                if (grille.getCase(supX, y).piece.getClassePiece().equalsIgnoreCase("Roi")
                        && grille.getCase(supX, y).piece.getCouleur().equalsIgnoreCase(couleurEnnemie))
                    return true;
            }
            if (grille.getCase(infX, supY).piece != null) {
                if (grille.getCase(infX, supY).piece.getClassePiece().equalsIgnoreCase("Roi")
                        && grille.getCase(infX, supY).piece.getCouleur().equalsIgnoreCase(couleurEnnemie))
                    return true;
            }
            if (grille.getCase(supX, supY).piece != null) {
                if (grille.getCase(supX, supY).piece.getClassePiece().equalsIgnoreCase("Roi")
                        && grille.getCase(supX, supY).piece.getCouleur().equalsIgnoreCase(couleurEnnemie))
                    return true;
            }
        }

//        if (couleur.equalsIgnoreCase("Blanc")) {
//            if (grille.getCase(x, infY).piece.getClassePiece().equalsIgnoreCase("Roi")
//                    && grille.getCase(x, infY).piece.getCouleur().equalsIgnoreCase("Noir"))
//                return true;
//            if (grille.getCase(x, supY).piece.getClassePiece().equalsIgnoreCase("Noir")
//                    && grille.getCase(x, supY).piece.getCouleur().equalsIgnoreCase("Noir"))
//                return true;
//            if (grille.getCase(infX, y).piece.getClassePiece().equalsIgnoreCase("Roi")
//                    && grille.getCase(infX, y).piece.getCouleur().equalsIgnoreCase("Noir"))
//                return true;
//            if (grille.getCase(supX, y).piece.getClassePiece().equalsIgnoreCase("Roi")
//                    && grille.getCase(supX, y).piece.getCouleur().equalsIgnoreCase("Noir"))
//                return true;
//            if (grille.getCase(infX, supY).piece.getClassePiece().equalsIgnoreCase("Roi")
//                    && grille.getCase(infX, supY).piece.getCouleur().equalsIgnoreCase("Noir"))
//                return true;
//            if (grille.getCase(supX, supY).piece.getClassePiece().equalsIgnoreCase("Roi")
//                    && grille.getCase(supX, supY).piece.getCouleur().equalsIgnoreCase("Noir"))
//                return true;
//        }

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
    public boolean testerCollisions (Case origin, Case destination){
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
                        if (grille.getCase(i, j).piece != null) {
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
                        if (grille.getCase(i, j).piece != null) {
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
                        if (grille.getCase(i, j).piece != null) {
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
                        if (grille.getCase(i, j).piece != null) {
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
                        if (grille.getCase(i, j).piece != null) {
                            return false;
                        }
                        j++;
                    }
                }
                if (dy < y) {
                    i = x;
                    j = y - 1;
                    while (dy < j) {
                        if (grille.getCase(i, j).piece != null) {
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
                        if (grille.getCase(i, j).piece != null) {
                            return false;
                        }
                        i++;
                    }
                }
                if (dx < x) {
                    i = x - 1;
                    j = y - 1;
                    while (dx < i) {
                        if (grille.getCase(i, j).piece != null) {
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
                        if (grille.getCase(i, j).piece != null) {
                            return false;
                        }
                        j++;
                    }
                }
                if (dy < y) {
                    i = x;
                    j = y - 1;
                    while (dy < j) {
                        if (grille.getCase(i, j).piece != null) {
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
                        if (grille.getCase(i, j).piece != null) {
                            return false;
                        }
                        i++;
                    }
                }
                if (dx < x) {
                    i = x - 1;
                    j = y - 1;
                    while (dx < i) {
                        if (grille.getCase(i, j).piece != null) {
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
                        if (grille.getCase(i, j).piece != null) {
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
                        if (grille.getCase(i, j).piece != null) {
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
                        if (grille.getCase(i, j).piece != null) {
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
                        if (grille.getCase(i, j).piece != null) {
                            return false;
                        }
                        j--;
                        i++;
                    }
                }
            }
        }
        if (classeOrigin.equalsIgnoreCase("Roi")) {
            if (grille.getCase(destination).piece == null
                    || (grille.getCase(destination).piece != null
                    && !grille.getCase(destination).piece.getCouleur().equalsIgnoreCase(couleur)
                    && !grille.getCase(destination).piece.getClassePiece().equalsIgnoreCase("Roi"))) {
                if (!detecterEchec(grille.getCase(origin).piece, grille.getCase(destination)))
                    return true;
                else return false;
            }

        }
        if (classeOrigin.equalsIgnoreCase("Pion")) {
            if (couleur.equalsIgnoreCase("Blanc")) {
                if (dy == y + 1 && grille.getCase(x, y + 1).piece == null) {
                    return true;
                }
                if (dy == y + 2 && y == 2 && grille.getCase(x, y + 2).piece == null) {
                    return true;
                }
                if (dy == y + 1 && (dx == x + 1 || dx == x - 1) && !(grille.getCase(x, y + 1).piece == null)) {
                    return true;
                }
            }
            if (couleur.equalsIgnoreCase("Noir")) {
                if (dy == y - 1 && grille.getCase(x, y - 1).piece == null) {
                    return true;
                }
                if (dy == y - 2 && y == 7 && grille.getCase(x, y - 2).piece == null) {
                    return true;
                }
                if (dy == y - 1 && (dx == x + 1 || dx == x - 1) && !(grille.getCase(x, y - 1).piece == null)) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }


    /**
     * Fonction permettant de vérifier la légalité des déplacements
     *
     * @param origin      <code>{@linkplain Case}</code> : Case originale de la piece qu'on veut déplacer
     * @param destination <code>{@linkplain Case}</code> : Case de la destination où on veut déplacer la piece
     * @return <code>boolean</code> : true quand le déplacement est légal
     * @author melissa
     * @see Case
     */
    public boolean testerDeplacement(Case origin, Case destination) {
        int x = origin.x;
        int y = origin.y;

        int dx = destination.x;
        int dy = destination.y;

        String classe = origin.piece.getClassePiece();
        String couleur = origin.piece.getCouleur();

        if (classe.equalsIgnoreCase("Reine")) {
            if (dx - x == dy - y) {
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
                if (dx - x == dy - y) {
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
        }
        return false;
    }
}
