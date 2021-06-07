package com.echec.game;

import java.util.ArrayList;

/**
 * Classe simulant un test d'échec, avec s:
 * <ul>
 *     <li>Son roi</li>
 *     <li>Son résultat de test</li>
 *     <li>Les pièces attaquants le roi <i>généralement à 1</i></li>
 *     <li>Les déplacements alliées pouvant sauver le roi de cet echec</li>
 * </ul>
 * @author yohan
 * @see Deplacement
 * @see com.echec.ui.EchecApplication
 */
public class Echec {
    /**
     * Case contenant le roi
     */
    private final Case roi;

    /**
     * Lien vers le plateau de Jeu
     */
    private final PlateauDeJeu plateau;

    /**
     * Couleur du roi
     */
    private final String couleurRoi;

    /**
     * Liste des déplacements pouvant sauver le roi de son échec
     */
    private ArrayList<Deplacement> sauveur = new ArrayList<>();

    /**
     * Liste des pions attanquants le roi ayant causés cet échec.
     * <i>Généralement ne contenant qu'une seule pièce</i>
     */
    private final ArrayList<Deplacement> attaquants;

    /**
     * Constructeur champ à champ de l'échec
     * @param plateau {@linkplain PlateauDeJeu}
     * @param roi {@linkplain Case}
     * @param couleur <code>String</code>
     * @param attaquants ArrayList {@linkplain Deplacement}
     * @author yohan
     * @see Deplacement
     * @see com.echec.ui.EchecApplication
     * @see PlateauDeJeu
     */
    public Echec (PlateauDeJeu plateau, Case roi, String couleur, ArrayList<Deplacement> attaquants) {
        this.plateau = plateau;
        this.roi = roi;
        this.couleurRoi = couleur;
        this.attaquants = attaquants;
        this.trouverSauveur();
    }

    /**
     * Getter de la liste des sauveurs
     * @return ArrayList {@linkplain Deplacement}
     * @author yohan
     */
    public ArrayList<Deplacement> getSauveur() {
        return sauveur;
    }

    /**
     * Fonction retournant la présence de sauveurs ou non pour cet echec
     * @return <code>Boolean</code>
     * @author yohan
     */
    public boolean hasSauveurs() {
        return this.sauveur.size() != 0;
    }

    /**
     * Fonction retournant le type d'échec
     * <ul><li><code>echec</code> : Si il y a echec mais possibilité de bouger le roi ou le protéger</li>
     * <li><code>mat</code> : Si il y a échec et que rien ne peut sauver le roi</li>
     * <li><code>no-echec</code> : Si le roi n'est pas en echec</li></ul>
     * @return <code>String</code> : le cas d'echec
     * @author yohan
     * @see Echec#trouverSauveur() 
     * @see com.echec.ui.EchecApplication#Echec(Case)
     */
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

    /**
     * Fonction déterminant les pions pouvant sauver le roi de cet echec
     * @author yohan
     * @see PlateauDeJeu
     * @see com.echec.ui.EchecApplication
     * @see Deplacement
     */
    public void trouverSauveur() {
        // Liste des déplacements des pions de chaque couleurs
        ArrayList<Deplacement> listeDeplacementsNoirs =  this.plateau.generateListeDeplacementsNoirs(false, true, false);
        ArrayList<Deplacement> listeDeplacementsBlancs =  this.plateau.generateListeDeplacementsBlancs(false, true, false);

        // Liste des cases sur lesquelles peuvent bouger les pions de chaque couleurs
        ArrayList<Case> listeCasesDeplacementsNoirs = new ArrayList<>();
        ArrayList<Case> listeCasesDeplacementsBlancs= new ArrayList<>();

        // On récupère toutes les cases de déplacements des noirs
        for (Deplacement d : listeDeplacementsNoirs) {
            for (Case c : d.getDeplacement()) {
                if (!listeCasesDeplacementsNoirs.contains(c)) {
                    listeCasesDeplacementsNoirs.add(c);
                }
            }
        }

        // On récupère toutes les cases de déplacements des blancs
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
        // On met à jour la liste des sauveurs avec la nouvelle liste
        sauveur = newSauveur;
    }

    /**
     * Réécriture de la fonction {@linkplain Object#toString()}
     * @return <code>String</code> Visualisation textuelle de l'échec
     */
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
