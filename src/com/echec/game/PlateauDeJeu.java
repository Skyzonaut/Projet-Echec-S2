package com.echec.game;
import org.json.simple.JSONObject;
import com.echec.Tools;

public class PlateauDeJeu {

    private String id;
    private final Grille grille;
    public Historique historique = new Historique();

    public PlateauDeJeu() {
        this.id = "plateau " + Tools.getFormatDate();
        this.grille = new Grille();
        this.initPlateau();
    }

    public PlateauDeJeu(JSONObject jsonObject) {
        this.id = (String) jsonObject.get("id");
        this.grille = new Grille((JSONObject) jsonObject.get("grille"));
        this.historique = new Historique((JSONObject) jsonObject.get("historique"));
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

        for (int colonne = 1; colonne <= 8; colonne++)
        {
            for (int j = 0; j < largeur ; j++)
            {
                if (j == (largeur / 2))
                {
                    dessinPlateau.append(Tools.getLettreColonne(colonne) + " ");
                }
                else
                {
                    dessinPlateau.append(" ");
                }
            }
        }

        dessinPlateau.append("\n-");

        for (int i = 0; i < 8; i++)
        {
            dessinPlateau.append("-".repeat(largeur));
            dessinPlateau.append("-");
        }
        dessinPlateau.append("\n");

        int ligneCompte = 8;

        for (int ligne = 1; ligne < hauteur * 8; ligne++)
        {
            String contenu = " ";
            dessinPlateau.append("|");

            for (int colonne = 1; colonne <= 8; colonne++)
            {
                if (this.grille.getCase(colonne, ligneCompte).piece != null)
                {
                    contenu = this.grille.getCase(colonne, ligneCompte).piece.utfString();
                }

                for (int j = 0; j < largeur ; j++)
                {
                    if (j == (largeur / 2))
                    {
                        if (!contenu.equals(" "))
                        {
                            dessinPlateau.append(ligne % hauteur == 0 ? " " : contenu);
                        }
                        else
                        {
                            dessinPlateau.append(" ");
                        }
                    }
                    else
                    {
                        dessinPlateau.append(ligne % hauteur == 0 ? "-" : " ");
                    }
                }
                contenu = " ";
                dessinPlateau.append(ligne % hauteur == 0 ? "+" : "|");

            }
            dessinPlateau.append(ligne % hauteur == 0 ? " " : " " + ligneCompte);
            dessinPlateau.append("\n");

            if (ligne % hauteur == 0)  ligneCompte --;
        }
        dessinPlateau.append("-");

        for (int i = 0; i < 8; i++)
        {
            dessinPlateau.append("-".repeat(Math.max(0, largeur)));
            dessinPlateau.append("-");
        }
        dessinPlateau.append("\n");

        return dessinPlateau.toString();
    }

    public Grille getGrille() {
        return this.grille;
    }

    public void initPlateau() {
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


    public boolean testerDeplacement(Case origin, Case destination) {
        int x = origin.x;
        int y = origin.y;
        int dx = destination.x;
        int dy = destination.y;
        String classe = origin.piece.getClassePiece();
        if (classe.equalsIgnoreCase("Reine")) {
            if (dx - x == dy - y) {
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
        return false;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getId() {
        return this.id;
    }

    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", this.id);
        jsonObject.put("grille", this.grille.getJSONObject());
        jsonObject.put("historique", this.historique.getJSONObject());
        return jsonObject;
    }
}
