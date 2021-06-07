package com.echec.ui;

import com.echec.game.Case;
import java.util.ArrayList;

/**
 * Classe contenant les évènements de l'interface graphique.
 * Cette classe est censée correspondre avec l'historique du Jeu. Du moins il en est la partie visible par l'utilisateur
 * @see UiEvent
 * @see com.echec.game.Historique
 * @see com.echec.game.Evenement
 * @author yohan
 */
public class UiHistorique {

    /**
     * Listes des {@linkplain UiEvent} de cette UiHistorique
     */
    private ArrayList<UiEvent> uiEvents = new ArrayList<>();

    /**
     * Boolean présisant si le dernier évènement était le premier clic sur une case ou non
     */
    private boolean alreadyClicked;

    /**
     * Constructeur par défaut de l'UiHistorique.
     * Un premier évènement de type "CréationUiHistorique" sans autres valeurs est systématiquement créé pour initialiser
     * le début de l'UiHistorique
     * @author yohan
     */
    public UiHistorique() {
       alreadyClicked  = false;
       this.uiEvents.add(new UiEvent("CréationUiHistorique", null, false));
    }

    /**
     * Fonction Changeant l'état de {@linkplain UiHistorique#alreadyClicked}
     * @author yohan
     */
    public void clicked() {
        alreadyClicked = !alreadyClicked;
    }

    /**
     * Getter retournat {@linkplain UiHistorique#alreadyClicked}
     * @return {@linkplain UiHistorique#alreadyClicked}
     * @author yohan
     */
    public boolean isClicked() {
        return this.alreadyClicked;
    }

    /**
     * Fonction ajoutant un nouvel {@linkplain UiEvent} à {@linkplain UiHistorique}
     * @param componentOrigin L'id de l'imageView d'origine, Exemple : v54
     * @param caseOrigin La case d'orgine
     * @author yohan
     */
    public void addUiEvent(String componentOrigin, Case caseOrigin) {
        this.uiEvents.add(new UiEvent(componentOrigin, caseOrigin, this.alreadyClicked));
    }

    /**
     * Fonction retournant le dernier {@linkplain UiEvent} dans {@linkplain UiHistorique#uiEvents}
     * @return dernier {@linkplain UiEvent} dans {@linkplain UiHistorique#uiEvents}
     * @author yohan
     */
    public UiEvent getLastUiEvent() {
        return this.uiEvents.get(this.uiEvents.size()-1);
    }

    /**
     * Fonction retournant l'{@linkplain UiEvent} dans {@linkplain UiHistorique#uiEvents} à l'index donné
     * @param i index
     * @return l'{@linkplain UiEvent} dans {@linkplain UiHistorique#uiEvents} à l'index donné
     * @author yohan
     */
    public UiEvent getUiEventByIndex(int i) {
        return this.uiEvents.get(i);
    }

    /**
     * Getter retournat {@linkplain UiHistorique#uiEvents}
     * @return {@linkplain UiHistorique#uiEvents}
     * @author yohan
     */
    public ArrayList<UiEvent> getUiEvents() {
        return uiEvents;
    }

    /**
     * Fonction affichant l'UiHistorique sur la console.
     * <p><b>Sert UNIQUEMENT pour le débug</b></p>
     * @author yohan
     */
    public void afficher() {
        System.out.println("[UiHistorique]" + "-".repeat(60));
        for (UiEvent uiEvent : this.uiEvents) {
            System.out.println(uiEvent);
        }
        System.out.println("-".repeat(80));
    }
}
