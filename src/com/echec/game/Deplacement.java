package com.echec.game;

import java.util.ArrayList;

/**
 * Classe représentant un déplacement de pion. Avec à savoir
 * <ul>
 *     <li>Son origine</li>
 *     <li>Ses destinations possibles</li>
 * </ul>
 * @see com.echec.ui.EchecApplication
 * @see Echec
 * @author yohan
 */
public class Deplacement {

    /**
     * Case contenant la pièce à l'origine de ce déplacement
     */
    private final Case origine;

    /**
     * Liste contenant toutes les destinations possibles pour {@linkplain Deplacement#origine}
     */
    private ArrayList<Case> deplacement;

    /**
     * Constructeur champ à champ de Déplacement
     * @param origine {@linkplain Deplacement#origine}
     * @param deplacement {@linkplain Deplacement#deplacement}
     * @author yohan
     */
    public Deplacement(Case origine, ArrayList<Case> deplacement) {
        this.origine = origine;
        this.deplacement = deplacement;
    }

    /**
     * Fonction retournant la présence ou non d'une case dans la listes des déplacements possibles.
     * <p>Sous entendu retourne <code>true</code> si l'origine de ce Déplacement peut se rendre sur la case donnée</p>
     * @param c Case de destination recherchée
     * @return Boolean, true si contenu dans le déplcament, false autrement
     * @author yohan
     */
    public boolean contains(Case c) {
        return deplacement.contains(c);
    }

    /**
     * Getter de {@linkplain Deplacement#origine}
     * @return {@linkplain Deplacement#origine}
     * @author yohan
     */
    public Case getOrigine() {
        return origine;
    }

    /**
     * Fonction indiquant si oui ou non il existe des case de destination possibles pour ce déplacement
     * @return true si il existe des cases de destinations pour ce déplacement, non autrement
     * @author yohan
     */
    public boolean hasDeplacement() {
        return this.deplacement.size() != 0;
    }

    /**
     * Getter de {@linkplain Deplacement#deplacement}
     * @return {@linkplain Deplacement#deplacement}
     * @author yohan
     */
    public ArrayList<Case> getDeplacement() {
        return deplacement;
    }

    /**
     * Fonction retirant tout déplacement qui serait en danger de prise. Cette fonction n'est utilisée que par les Roi
     * pour double checker la présence ou non d'échec sur leurs possibles futurs déplacements
     * @param listeCasesAttaquants Liste des case de la pièce possiblement attaquants le roi
     * @param attaquant La pièce qui pourrait attaquer le roi
     * @param plateau Le plateau qui sert de modèle de donnée
     * @return Renvoie ce déplacement avec les déplacements dangereux qui lui auront été retirés
     * @author yohan
     */
    public Deplacement getSafeDeplacement(ArrayList<Case> listeCasesAttaquants, Deplacement attaquant, PlateauDeJeu plateau) {
        ArrayList<Case> newDeplacement = new ArrayList<>();

        for (int i = 0; i < deplacement.size(); i++) {
            if (!listeCasesAttaquants.contains(deplacement.get(i)) && !plateau.getCheminToRoi(attaquant.getOrigine(), deplacement.get(i)).contains(deplacement.get(i))) {
                newDeplacement.add(deplacement.get(i));
            }
        }
        this.deplacement = newDeplacement;
        return this;
    }

    /**
     * Fonction retirant le roi des cases de destination de ce déplacement
     * @param c Case contenant le roi
     * @author yohan
     */
    public void retirerRoi(Case c) {
        ArrayList<Case> newListe = new ArrayList<>();
        for (Case d : this.deplacement) {
            if(!c.equals(d)) newListe.add(d);
        }
        this.deplacement = newListe;
    }

    /**
     * Fonction réécrite de {@linkplain Object#toString()}
     * @return retourne une visualisation textuelle du Déplacement
     * @author yohan
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("| Origine : ").append(origine).append("\n");
        for (Case c : deplacement) {
            str.append("\t\t").append(c).append("\n");
        }
        return str.toString();
    }
}
