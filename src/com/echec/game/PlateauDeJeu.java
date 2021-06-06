package com.echec.game;

import com.echec.Tools;
import com.echec.pieces.Piece;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Random;

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

    public Echec getEchec() {
        return  this.echec;
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
                this.updateListeDeplacements(false);
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


    public ArrayList<Case> getCheminToRoi(Case origine, Case roi) {
        ArrayList<Case> chemin = new ArrayList<>();
        if (origine.piece.getClassePiece().equalsIgnoreCase("tour")) {
            if (origine.y == roi.y) {
                if (origine.x > roi.x) {
                    for (int x = 1; x <= origine.x - roi.x; x++) {
                        chemin.add(this.grille.getCase(origine.x - x, origine.y));
                    }
                } else {
                    for (int x = 1; x <= roi.x - origine.x; x++) {
                        chemin.add(this.grille.getCase(origine.x + x, origine.y));
                    }
                }
            } else if (origine.x == roi.x) {
                if (origine.y > roi.y) {
                    for (int y = 1; y <= origine.y - roi.y; y++) {
                        chemin.add(this.grille.getCase(origine.x, origine.y - y));
                    }
                } else {
                    for (int y = 1; y <= roi.y - origine.y; y++) {
                        chemin.add(this.grille.getCase(origine.x, origine.y - y));
                    }
                }
            }
        }
        if (origine.piece.getClassePiece().equalsIgnoreCase("reine")) {
            if (origine.y == roi.y) {
                if (origine.x > roi.x) {
                    for (int x = 1; x <= origine.x - roi.x; x++) {
                        chemin.add(this.grille.getCase(origine.x - x, origine.y));
                    }
                } else {
                    for (int x = 1; x <= roi.x - origine.x; x++) {
                        chemin.add(this.grille.getCase(origine.x + x, origine.y));
                    }
                }
            } else if (origine.x == roi.x) {
                if (origine.y > roi.y) {
                    for (int y = 1; y <= origine.y - roi.y; y++) {
                        chemin.add(this.grille.getCase(origine.x, origine.y - y));
                    }
                } else {
                    for (int y = 1; y <= roi.y - origine.y; y++) {
                        chemin.add(this.grille.getCase(origine.x, origine.y + y));
                    }
                }
            }
            if (origine.x > roi.x && origine.y > roi.y) {
                for (int i = 1; i <= origine.x - roi.x; i++) {
                    chemin.add(this.grille.getCase(origine.x - i, origine.y - i));
                }
            }
            if (origine.x > roi.x && origine.y < roi.y) {
                for (int i = 1; i <= origine.x - roi.x; i++) {
                    chemin.add(this.grille.getCase(origine.x - i, origine.y + i));
                }
            }
            if (origine.x < roi.x && origine.y > roi.y) {
                for (int i = 1; i <= roi.x - origine.x; i++) {
                    chemin.add(this.grille.getCase(origine.x + i, origine.y - i));
                }
            }
            if (origine.x < roi.x && origine.y < roi.y) {
                for (int i = 1; i <= roi.x - origine.x; i++) {
                    chemin.add(this.grille.getCase(origine.x + i, origine.y + i));
                }
            }
        }
        if (origine.piece.getClassePiece().equalsIgnoreCase("fou")) {
            if (origine.x > roi.x && origine.y > roi.y) {
                for (int i = 1; i <= origine.x - roi.x; i++) {
                    chemin.add(this.grille.getCase(origine.x - i, origine.y - i));
                }
            }
            if (origine.x > roi.x && origine.y < roi.y) {
                for (int i = 1; i <= origine.x - roi.x; i++) {
                    chemin.add(this.grille.getCase(origine.x - i, origine.y + i));
                }
            }
            if (origine.x < roi.x && origine.y > roi.y) {
                for (int i = 1; i <= roi.x - origine.x; i++) {
                    chemin.add(this.grille.getCase(origine.x + i, origine.y - i));
                }
            }
            if (origine.x < roi.x && origine.y < roi.y) {
                for (int i = 1; i <= roi.x - origine.x; i++) {
                    chemin.add(this.grille.getCase(origine.x + i, origine.y + i));
                }
            }
        }
        return chemin;
    }


    /**
     * Fonction permettant d'avoir les déplacements possibles d'une piece
     * @param posPiece <code>{@linkplain Case}</code> : Case de la piece dont on veut avoir les déplacements
     * @return <code>ArrayList<{@linkplain Case}></code> : Retourne une liste de cases des déplacements possibles
     * @see Case
     * @author melissa
     */
    public ArrayList<Case> deplacementsPossible (Case posPiece, boolean prevoirPrise, boolean continuer) {
        ArrayList<Case> listeCases = new ArrayList<>();

        int x = posPiece.x;
        int y = posPiece.y;

        String classePiece = grille.getCase(posPiece).piece.getClassePiece();

        if (classePiece.equalsIgnoreCase("Pion"))
        {
            listeCases = getDeplacementsPossiblesPions(posPiece);
        }
        if (classePiece.equalsIgnoreCase("Tour"))
        {
            listeCases = getDeplacementsPossiblesTours(posPiece, prevoirPrise, continuer);
        }
        if (classePiece.equalsIgnoreCase("Reine"))
        {
            listeCases = getDeplacementsPossiblesReine(posPiece, prevoirPrise, continuer);
        }
        if (classePiece.equalsIgnoreCase("Fou"))
        {
            listeCases = getDeplacementsPossiblesFou(posPiece, prevoirPrise, continuer);
        }
        if (classePiece.equalsIgnoreCase("Cavalier"))
        {
            listeCases = getDeplacementsPossiblesCavaliers(posPiece);
        }
        if (classePiece.equalsIgnoreCase("Roi"))
        {
            listeCases = getDeplacementsPossiblesRoi(posPiece, prevoirPrise);
        }
        return listeCases;
    }

    public ArrayList<Case> deplacementsPossible (Case posPiece) {
        return deplacementsPossible(posPiece, false, false);
    }

    public ArrayList<Case> getDeplacementsPossiblesCavaliers(Case posPiece) {

        ArrayList<Case> listeCases = new ArrayList<>();

        int x = posPiece.x;
        int y = posPiece.y;

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
        return listeCases;
    }

    public ArrayList<Case> getDeplacementsPossiblesFou(Case posPiece, boolean prevoirPrise, boolean continuer) {

        ArrayList<Case> listeCases = new ArrayList<>();

        int x = posPiece.x;
        int y = posPiece.y;

        int a = x + 1;
        int b = y + 1;
        while (a <= 8 && b <= 8) {
            if (grille.getCase(a, b).estVide()) {
                listeCases.add(this.grille.getCase(a, b));
            } else {
                if (!grille.getCase(a, b).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(a, b));
                }
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
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
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
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
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
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
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
            }
            a--;
            b--;

        }
        return listeCases;
    }

    public ArrayList<Case> getDeplacementsPossiblesReine(Case posPiece, boolean prevoirPrise, boolean continuer) {

        ArrayList<Case> listeCases = new ArrayList<>();

        int x = posPiece.x;
        int y = posPiece.y;

        for (int i = x - 1; i > 0; i--) {
            if (grille.getCase(i, y).estVide()) {
                listeCases.add(this.grille.getCase(i, y));
            } else {
                if (!grille.getCase(i, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(i, y));
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(i, y));
                    }
                }
                if (!continuer) break;
            }
        }
        for (int i = x + 1; i <= 8; i++) {
            if (grille.getCase(i, y).estVide()) {
                listeCases.add(this.grille.getCase(i, y));
            } else {
                if (!grille.getCase(i, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(i, y));
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(i, y));
                    }
                }
                if (!continuer) break;
            }
        }
        for (int i = y - 1; i > 0; i--) {
            if (grille.getCase(x, i).estVide()) {
                listeCases.add(this.grille.getCase(x, i));
            } else {
                if (!grille.getCase(x, i).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x, i));
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(x, i));
                    }
                }
                if (!continuer) break;
            }
        }
        for (int i = y + 1; i <= 8; i++) {
            if (grille.getCase(x, i).estVide()) {
                listeCases.add(this.grille.getCase(x, i));
            } else {
                if (!grille.getCase(x, i).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x, i));
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(x, i));
                    }
                }
                if (!continuer) break;
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
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
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
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
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
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
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
                } else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(a, b));
                    }
                }
                if (!continuer) break;
            }
            a--;
            b--;
        }
        return listeCases;
    }

    public ArrayList<Case> getDeplacementsPossiblesTours(Case posPiece, boolean prevoirPrise, boolean continuer) {

        ArrayList<Case> listeCases = new ArrayList<>();

        int x = posPiece.x;
        int y = posPiece.y;

        for (int i = x - 1; i > 0; i--) {
            if (grille.getCase(i, y).estVide()) {
                listeCases.add(this.grille.getCase(i, y));
            } else {
                if (!grille.getCase(i, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(i, y));
                }
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(i, y));
                    }
                }
                if (!continuer) break;
            }
        }
        for (int i = x + 1; i <= 8; i++) {
            if (grille.getCase(i, y).estVide()) {
                listeCases.add(this.grille.getCase(i, y));
            } else {
                if (!grille.getCase(i, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(i, y));
                }
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(i, y));
                    }
                }
                if (!continuer) break;
            }
        }
        for (int i = y - 1; i > 0; i--) {
            if (grille.getCase(x, i).estVide()) {
                listeCases.add(this.grille.getCase(x, i));
            } else {
                if (!grille.getCase(x, i).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x, i));
                }
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(x, y));
                    }
                }
                if (!continuer) break;
            }
        }
        for (int i = y + 1; i <= 8; i++) {
            if (grille.getCase(x, i).estVide()) {
                listeCases.add(this.grille.getCase(x, i));
            } else {
                if (!grille.getCase(x, i).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    listeCases.add(this.grille.getCase(x, i));
                }
                else {
                    if (prevoirPrise) {
                        listeCases.add(this.grille.getCase(x, y));
                    }
                }
                if (!continuer) break;
            }
        }
        return listeCases;
    }

    public ArrayList<Case> getDeplacementsPossiblesPions(Case posPiece) {

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
            return listeCases;
    }

    public ArrayList<Case> getDeplacementsPossiblesRoi(Case posPiece, boolean prevoirPrise) {

        ArrayList<Case> listeCases = new ArrayList<>();

        int x = posPiece.x;
        int y = posPiece.y;

        if (x + 1 <= 8 && y + 1 <= 8) {
            if (grille.getCase(x + 1, y + 1).estVide()) {
                if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x + 1, y + 1)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x + 1, y + 1));
                }
            } else {
                if (!grille.getCase(x + 1, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                   if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x + 1, y + 1), true).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x + 1, y + 1));
                    }
                }
            }
        }
        if (x - 1 > 0 && y + 1 <= 8) {
            if (grille.getCase(x - 1, y + 1).estVide()) {
                if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x - 1, y + 1)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x - 1, y + 1));
                }
            } else {
                if (!grille.getCase(x - 1, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x - 1, y + 1)).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x - 1, y + 1));
                    }
                }
            }
        }
        //x, y + 1
        if (y + 1 <= 8) {
            if (grille.getCase(x, y + 1).estVide()) {
                if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x, y + 1)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x, y + 1));
                }
            } else {
                if (!grille.getCase(x, y + 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x, y + 1)).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x, y + 1));
                    }
                }
            }
        }
        //x, y - 1
        if (y - 1 > 0) {
            if (grille.getCase(x, y - 1).estVide()) {
                if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x, y - 1)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x, y - 1));
                }
            } else {
                if (!grille.getCase(x, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x, y - 1)).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x, y - 1));
                    }
                }
            }
        }
        //x + 1, y
        if (x + 1 <= 8) {
            if (grille.getCase(x + 1, y).estVide()) {
                if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x + 1, y)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x + 1, y));
                }
            } else {
                if (!grille.getCase(x + 1, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x + 1, y)).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x + 1, y));
                    }
                }
            }
        }
        //x - 1, y
        if (x - 1 > 0 ) {
            if (grille.getCase(x - 1, y).estVide()) {
                if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x - 1, y)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x - 1, y));
                }
            } else {
                if (!grille.getCase(x - 1, y).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x - 1, y)).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x - 1, y));
                    }
                }
            }
        }
        //x - 1, y - 1
        if (x - 1 > 0 && y - 1 > 0) {
            if (grille.getCase(x - 1, y - 1).estVide()) {
                if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x - 1, y - 1)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x - 1, y - 1));
                }
            } else {
                if (!grille.getCase(x - 1, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x - 1, y - 1)).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x - 1, y - 1));
                    }
                }
            }
        }
        //x + 1, y - 1
        if (x + 1 <= 8 && y - 1 > 0) {
            if (grille.getCase(x + 1, y - 1).estVide()) {
                if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x + 1, y - 1)).isEchec().equals("no-echec")) {
                    listeCases.add(this.grille.getCase(x + 1, y - 1));
                }
            } else {
                if (!grille.getCase(x + 1, y - 1).piece.getCouleur().equalsIgnoreCase(posPiece.piece.getCouleur())) {
                    if (detecterEchec2(grille.getCase(posPiece).piece, grille.getCase(x + 1, y - 1)).isEchec().equals("no-echec")) {
                        listeCases.add(this.grille.getCase(x + 1, y - 1));
                    }
                }
            }
        }
        return listeCases;
    }

    public void updateListeDeplacements(Boolean pionAvanceOnly) {
        listeDeplacementBlancs = this.generateListeDeplacementsBlancs(pionAvanceOnly, false, false);
        listeDeplacementNoirs = this.generateListeDeplacementsNoirs(pionAvanceOnly, false, false);
    }

    public ArrayList<Deplacement> generateListeDeplacementsBlancs(Boolean pionAvanceOnly, Boolean prevoirPrise, Boolean continuer) {
        ArrayList<Case> listeBlancs = this.grille.getListePieceCouleur("blanc");
        ArrayList<Deplacement> listeDeplacementBlancs = new ArrayList<>();
        ArrayList<Case> l;

        for (Case c : listeBlancs) {
            if (c.piece.getClassePiece().equalsIgnoreCase("pion")) {
                l = new ArrayList<>();
                if (pionAvanceOnly) {
                    if (this.grille.getCase(c.x, c.y + 1).estVide()) {
                        l.add(this.grille.getCase(c.x, c.y + 1));
                    }
                    if (this.grille.getCase(c.x, c.y + 2).estVide() && c.y == 2) {
                        l.add(this.grille.getCase(c.x, c.y + 2));
                    }
                } else {
                    if (c.x +1 <= 8 && c.y + 1 <= 8) {
                        if (!this.grille.getCase(c.x + 1, c.y + 1).estVide()) {
                            l.add(this.grille.getCase(c.x + 1, c.y + 1));
                        }
                    }
                    if (c.x - 1 > 0 && c.y + 1 <= 8) {
                        if (!this.grille.getCase(c.x - 1, c.y + 1).estVide()) {
                            l.add(this.grille.getCase(c.x - 1, c.y + 1));
                        }
                    }
                }
                listeDeplacementBlancs.add(new Deplacement(c, l));
            } else if (c.piece.getClassePiece().equalsIgnoreCase("roi")) {
                l = new ArrayList<>();
                if (c.y + 1 <= 8) {
                    if (!this.grille.getCase(c.x, c.y + 1).estVide()) {
                        if (!this.grille.getCase(c.x, c.y + 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                            l.add(this.grille.getCase(c.x, c.y + 1));
                        }
                    } else l.add(this.grille.getCase(c.x, c.y + 1));
                }
                if (c.y - 1 > 0) {
                    if (!this.grille.getCase(c.x, c.y - 1).estVide()) {
                        if (!this.grille.getCase(c.x, c.y - 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                            l.add(this.grille.getCase(c.x, c.y - 1));
                        }
                    } else l.add(this.grille.getCase(c.x, c.y - 1));
                }
                listeDeplacementBlancs.add(new Deplacement(c, l));

                l = new ArrayList<>();
                if (c.x + 1 <= 8) {
                    if (c.y + 1 <= 8) {
                        if (!this.grille.getCase(c.x + 1, c.y + 1).estVide()) {
                            if (!this.grille.getCase(c.x + 1, c.y + 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                                l.add(this.grille.getCase(c.x + 1, c.y + 1));
                            }
                        } else l.add(this.grille.getCase(c.x + 1, c.y + 1));;
                    }
                    if (c.y - 1 > 0) {
                        if (!this.grille.getCase(c.x + 1, c.y - 1).estVide()) {
                            if (!this.grille.getCase(c.x + 1, c.y - 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                                l.add(this.grille.getCase(c.x + 1, c.y - 1));
                            }
                        } else l.add(this.grille.getCase(c.x + 1, c.y - 1));
                    }
                    if (!this.grille.getCase(c.x + 1, c.y).estVide()) {
                        if (!this.grille.getCase(c.x + 1, c.y).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                            l.add(this.grille.getCase(c.x + 1, c.y));
                        }
                    } else l.add(this.grille.getCase(c.x + 1, c.y));
                }
                listeDeplacementBlancs.add(new Deplacement(c, l));

                l = new ArrayList<>();
                if (c.x - 1 > 0) {
                    if (c.y + 1 <= 8) {
                        if (!this.grille.getCase(c.x - 1, c.y + 1).estVide()) {
                            if (!this.grille.getCase(c.x - 1, c.y + 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                                l.add(this.grille.getCase(c.x - 1, c.y + 1));
                            }
                        } else l.add(this.grille.getCase(c.x - 1, c.y + 1));
                    }
                    if (c.y - 1 > 0) {
                        if (!this.grille.getCase(c.x - 1, c.y - 1).estVide()) {
                            if (!this.grille.getCase(c.x - 1, c.y - 1).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                                l.add(this.grille.getCase(c.x - 1, c.y - 1));
                            }
                        } else l.add(this.grille.getCase(c.x - 1, c.y - 1));
                    }
                    if (!this.grille.getCase(c.x - 1, c.y).estVide()) {
                        if (!this.grille.getCase(c.x - 1, c.y).piece.getCouleur().equalsIgnoreCase(c.piece.getCouleur())) {
                            l.add(this.grille.getCase(c.x - 1, c.y));
                        }
                    } else l.add(this.grille.getCase(c.x - 1, c.y - 1));
                }
                listeDeplacementBlancs.add(new Deplacement(c, l));
            } else {
                l = new ArrayList<>();
                for (Case d : this.deplacementsPossible(c, false, continuer)) {
                    l.add(d);
                }
                listeDeplacementBlancs.add(new Deplacement(c, l));
            }
        }
        ArrayList<Deplacement> temp = new ArrayList<>();
        for (Deplacement d : listeDeplacementBlancs) {
            if (d.hasDeplacement()) {
                temp.add(d);
            }
        }
        return temp;
    }

    public ArrayList<Deplacement> generateListeDeplacementsNoirs(Boolean pionAvanceOnly, boolean prevoirPrise, boolean continuer) {
        ArrayList<Case> listeNoirs;
        ArrayList<Deplacement> listeDeplacementNoirs = new ArrayList<>();
        ArrayList<Case> l;
        listeNoirs = this.grille.getListePieceCouleur("noir");

        for (Case c : listeNoirs) {
            if (c.piece.getClassePiece().equalsIgnoreCase("pion")) {
                l = new ArrayList<>();
                if (pionAvanceOnly) {
                    if (this.grille.getCase(c.x, c.y - 1).estVide()) {
                        l.add(this.grille.getCase(c.x, c.y - 1));
                    }
                    if (this.grille.getCase(c.x, c.y - 2).estVide() && c.y == 7) {
                        l.add(this.grille.getCase(c.x, c.y - 2));
                    }
                } else {
                    if (c.x + 1 <= 8 && c.y - 1 > 0) {
                        if (!this.grille.getCase(c.x + 1, c.y - 1).estVide()) {
                            l.add(this.grille.getCase(c.x + 1, c.y - 1));
                        }
                    }
                    if (c.x - 1 > 0 && c.y - 1 > 0) {
                        if (!this.grille.getCase(c.x - 1, c.y - 1).estVide()) {
                            l.add(this.grille.getCase(c.x - 1, c.y - 1));
                        }
                    }
                }
                listeDeplacementNoirs.add(new Deplacement(c, l));
            } else if (c.piece.getClassePiece().equalsIgnoreCase("roi")) {
                l = new ArrayList<>();
                if (c.y + 1 <= 8) {
                    if (this.grille.getCase(c.x, c.y + 1).estVide()) l.add(this.grille.getCase(c.x, c.y + 1));
                }
                if (c.y - 1 > 0) {
                    if (this.grille.getCase(c.x, c.y - 1).estVide()) l.add(this.grille.getCase(c.x, c.y - 1));
                }
                listeDeplacementNoirs.add(new Deplacement(c, l));

                l = new ArrayList<>();
                if (c.x + 1 <= 8) {
                    if (c.y + 1 <= 8) {
                        if (this.grille.getCase(c.x + 1, c.y + 1).estVide()) l.add(this.grille.getCase(c.x + 1, c.y + 1));
                    }
                    if (c.y - 1 > 0) {
                        if (this.grille.getCase(c.x + 1, c.y - 1).estVide()) l.add(this.grille.getCase(c.x + 1, c.y - 1));
                    }
                    if (this.grille.getCase(c.x + 1, c.y).estVide()) l.add(this.grille.getCase(c.x + 1, c.y));
                }
                listeDeplacementNoirs.add(new Deplacement(c, l));

                l = new ArrayList<>();
                if (c.x - 1 > 0) {
                    if (c.y + 1 <= 8) {
                        if (this.grille.getCase(c.x - 1, c.y + 1).estVide()) l.add(this.grille.getCase(c.x - 1, c.y + 1));
                    }
                    if (c.y - 1 > 0) {
                        if (this.grille.getCase(c.x - 1, c.y - 1).estVide()) l.add(this.grille.getCase(c.x - 1, c.y - 1));
                    }
                    if (this.grille.getCase(c.x - 1, c.y).estVide()) l.add(this.grille.getCase(c.x - 1, c.y));
                }
                listeDeplacementNoirs.add(new Deplacement(c, l));
            } else {
                l = new ArrayList<>();
                for (Case d : this.deplacementsPossible(c, prevoirPrise, continuer)) {
                    l.add(d);
                }
                listeDeplacementNoirs.add(new Deplacement(c, l));
            }
        }
        ArrayList<Deplacement> temp = new ArrayList<>();
        for (Deplacement d : listeDeplacementNoirs) {
            if (d.hasDeplacement()) {
                temp.add(d);
            }
        }
        return temp;
    }

    public Echec detecterEchec2 (Piece pieceRoi, Case posRoi) {
        return detecterEchec2(pieceRoi, posRoi, false);
    }

    public Echec detecterEchec2 (Piece pieceRoi, Case posRoi, boolean prevoirPrise) {

        String couleurEnnemie = (pieceRoi.getCouleur() == "noir") ? "blanc" : "noir";
        ArrayList<Deplacement> listeDeplacement = new ArrayList<>();

        if (couleurEnnemie.equals("noir")) {
            for (Deplacement d : generateListeDeplacementsNoirs(false, true, false)) {
                if (d.contains(posRoi)) {
                    listeDeplacement.add(d);
                }
            }
        }
        if (couleurEnnemie.equals("blanc")) {
            for (Deplacement d : generateListeDeplacementsBlancs(false, true, false)) {
                if (d.contains(posRoi)) {
                    listeDeplacement.add(d);
                }
            }
        }

        Echec echec = new Echec(this, posRoi, pieceRoi.getCouleur(), listeDeplacement);
        this.echec = echec;
        return echec;
    }

    public void IA() {
        System.out.println("IA");
        ArrayList<Integer> scores = new ArrayList<Integer>();
        ArrayList<Case> casesOrigine = new ArrayList<>();
        ArrayList<Case> casesDestination = new ArrayList<>();

        ArrayList<Case> casesOrigine0 = new ArrayList<>();
        ArrayList<Case> casesDestination0 = new ArrayList<>();

        Case caseOrigine;
        Case caseDestination;

        for (int i = 1 ; i <= 8 ; i++) {
            for (int j = 1; j <= 8 ; j++) {
                if (grille.getCase(i, j).piece != null) {
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_1")) {
                        ArrayList<Case> deplacementsPion1 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion1.size() ; a++) {
                            if (deplacementsPion1.get(a).piece != null) {
                                scores.add(deplacementsPion1.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion1.get(a)));
                            }
                            if (deplacementsPion1.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion1.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_2")) {
                        ArrayList<Case> deplacementsPion2 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion2.size() ; a++) {
                            if (deplacementsPion2.get(a).piece != null) {
                                scores.add(deplacementsPion2.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion2.get(a)));
                            }
                            if (deplacementsPion2.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion2.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_3")) {
                        ArrayList<Case> deplacementsPion3 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion3.size() ; a++) {
                            if (deplacementsPion3.get(a).piece != null) {
                                scores.add(deplacementsPion3.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion3.get(a)));
                            }
                            if (deplacementsPion3.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion3.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_4")) {
                        ArrayList<Case> deplacementsPion4 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion4.size() ; a++) {
                            if (deplacementsPion4.get(a).piece != null) {
                                scores.add(deplacementsPion4.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion4.get(a)));
                            }
                            if (deplacementsPion4.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion4.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_5")) {
                        ArrayList<Case> deplacementsPion5 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion5.size() ; a++) {
                            if (deplacementsPion5.get(a).piece != null) {
                                scores.add(deplacementsPion5.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion5.get(a)));
                            }
                            if (deplacementsPion5.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion5.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_6")) {
                        ArrayList<Case> deplacementsPion6 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion6.size() ; a++) {
                            if (deplacementsPion6.get(a).piece != null) {
                                scores.add(deplacementsPion6.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion6.get(a)));
                            }
                            if (deplacementsPion6.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion6.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_7")) {
                        ArrayList<Case> deplacementsPion7 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion7.size() ; a++) {
                            if (deplacementsPion7.get(a).piece != null) {
                                scores.add(deplacementsPion7.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion7.get(a)));
                            }
                            if (deplacementsPion7.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion7.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Pion_noir_8")) {
                        ArrayList<Case> deplacementsPion8 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsPion8.size() ; a++) {
                            if (deplacementsPion8.get(a).piece != null) {
                                scores.add(deplacementsPion8.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsPion8.get(a)));
                            }
                            if (deplacementsPion8.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsPion8.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Cavalier_noir_1")) {
                        ArrayList<Case> deplacementsCavalier1 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsCavalier1.size() ; a++) {
                            if (deplacementsCavalier1.get(a).piece != null) {
                                scores.add(deplacementsCavalier1.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsCavalier1.get(a)));
                            }
                            if (deplacementsCavalier1.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsCavalier1.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Cavalier_noir_2")) {
                        ArrayList<Case> deplacementsCavalier2 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsCavalier2.size() ; a++) {
                            if (deplacementsCavalier2.get(a).piece != null) {
                                scores.add(deplacementsCavalier2.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsCavalier2.get(a)));
                            }
                            if (deplacementsCavalier2.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsCavalier2.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Fou_noir_1")) {
                        ArrayList<Case> deplacementsFou1 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsFou1.size() ; a++) {
                            if (deplacementsFou1.get(a).piece != null) {
                                scores.add(deplacementsFou1.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsFou1.get(a)));
                            }
                            if (deplacementsFou1.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsFou1.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Fou_noir_2")) {
                        ArrayList<Case> deplacementsFou2 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsFou2.size() ; a++) {
                            if (deplacementsFou2.get(a).piece != null) {
                                scores.add(deplacementsFou2.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsFou2.get(a)));
                            }
                            if (deplacementsFou2.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsFou2.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Tour_noir_1")) {
                        ArrayList<Case> deplacementsTour1 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsTour1.size() ; a++) {
                            if (deplacementsTour1.get(a).piece != null) {
                                scores.add(deplacementsTour1.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsTour1.get(a)));
                            }
                            if (deplacementsTour1.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsTour1.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Tour_noir_2")) {
                        ArrayList<Case> deplacementsTour2 = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsTour2.size() ; a++) {
                            if (deplacementsTour2.get(a).piece != null) {
                                scores.add(deplacementsTour2.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsTour2.get(a)));
                            }
                            if (deplacementsTour2.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsTour2.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Reine_noir_1")) {
                        ArrayList<Case> deplacementsReine = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsReine.size() ; a++) {
                            if (deplacementsReine.get(a).piece != null) {
                                scores.add(deplacementsReine.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsReine.get(a)));
                            }
                            if (deplacementsReine.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsReine.get(a)));
                            }
                        }
                    }
                    if (grille.getCase(i, j).piece.getId().equalsIgnoreCase("Roi_noir_1")) {
                        ArrayList<Case> deplacementsRoi = deplacementsPossible(grille.getCase(i, j));
                        for (int a = 0 ; a < deplacementsRoi.size() ; a++) {
                            if (deplacementsRoi.get(a).piece != null) {
                                scores.add(deplacementsRoi.get(a).piece.getScore());
                                casesOrigine.add(grille.getCase(i, j));
                                casesDestination.add(grille.getCase(deplacementsRoi.get(a)));
                            }
                            if (deplacementsRoi.get(a).piece == null) {
                                casesOrigine0.add(grille.getCase(i, j));
                                casesDestination0.add(grille.getCase(deplacementsRoi.get(a)));
                            }
                        }
                    }
                }

            }
        }

        if (scores.isEmpty()) {
            Random rand = new Random();
            int index = rand.nextInt(casesOrigine0.size());
            caseOrigine = casesOrigine0.get(index);
            caseDestination = casesDestination0.get(index);
            deplacerPiece(caseOrigine, caseDestination);
        } else {
            Integer maxScore = scores.get(0);
            for (int i = 1 ; i < scores.size() ; i++) {
                if (scores.get(i) > maxScore) maxScore = scores.get(i);
            }
            int index = scores.indexOf(maxScore);
            caseOrigine = casesOrigine.get(index);
            caseDestination = casesDestination.get(index);
            prendrePiece(caseOrigine, caseDestination);
        }
    }

    public void IAEchec() {

    }
}
