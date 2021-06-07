package com.echec.ui;

import com.echec.game.Case;

/**
 * Classe représentant un évènement graphique dans l'interface utilisateur. Et est censée correspondre aux {@linkplain com.echec.game.Evenement}.
 * Du moins ils en sont la face visible
 * @see UiHistorique
 * @see com.echec.game.Evenement
 * @author yohan
 */
public class UiEvent {

    /**
     * Id de l'imageView d'origine de l'UiEvent, Exemple : v54
     */
    private String componentOriginId;

    /**
     * Case d'origine de l'UiEvent
     */
    private Case caseOrigine;

    /**
     * Boolean précisant si l'UiEvent est un premier clic sur une case ou non
     */
    private boolean alreadyClicked;

    /**
     * Constructeur champ à champ de {@linkplain UiEvent}
     * @param componentOrigin
     * @param caseOrigine
     * @param isClicked
     * @see UiHistorique
     * @author yohan
     */
    public UiEvent(String componentOrigin, Case caseOrigine, boolean isClicked) {
        this.componentOriginId = componentOrigin;
        this.caseOrigine = caseOrigine;
        this.alreadyClicked = isClicked;
    }

    /**
     * Getter de {@linkplain UiEvent#componentOriginId}
     * @return {@linkplain UiEvent#componentOriginId}
     * @author yohan
     */
    public String getComponentOriginId() {
        return componentOriginId;
    }

    /**
     * Getter de {@linkplain UiEvent#caseOrigine}
     * @return {@linkplain UiEvent#caseOrigine}
     * @author yohan
     */
    public Case getCaseOrigine() {
        return caseOrigine;
    }

    /**
     * Fonction vérifiant si la {@linkplain UiEvent#caseOrigine} contient une pièce
     * @return {@linkplain UiEvent#caseOrigine}.piece
     * @author yohan
     */
    public boolean hasPiece() {
        return caseOrigine.piece != null;
    }

    /**
     * Réécriture de la fonction {@linkplain Object#toString()} retournant une chaîne de caractère
     * représentant l'UiEvent
     * <p><b>Sert UNIQUEMENT pour le débug</b></p>
     * @return <code>String</code> L'UiEvent au format texte
     * @author yohan
     */
    public String toString() {
        return componentOriginId + " | " + caseOrigine + " | " + alreadyClicked;
    }
}
