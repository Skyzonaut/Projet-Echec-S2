package com.echec.ui;

import com.echec.game.Case;

import java.util.ArrayList;

public class UiHistorique {
    private ArrayList<UiEvent> uiEvents = new ArrayList<>();
    private boolean alreadyClicked;

    public UiHistorique() {
       alreadyClicked  = false;
       this.uiEvents.add(new UiEvent("CréationUiHistorique", null, false));
    }

    public void clicked() {
        alreadyClicked = !alreadyClicked;
    }

    public boolean isClicked() {
        return this.alreadyClicked;
    }

    public void addUiEvent(String componentOrigin, Case caseOrigin) {
        this.uiEvents.add(new UiEvent(componentOrigin, caseOrigin, this.alreadyClicked));
    }

    public UiEvent getUiEventByIndex(int i) {
        return this.uiEvents.get(i);
    }

    public ArrayList<UiEvent> getUiEvents() {
        return uiEvents;
    }

    public void afficher() {
        System.out.println("[UiHistorique]" + "-".repeat(60));
        for (UiEvent uiEvent : this.uiEvents) {
            System.out.println(uiEvent);
        }
        System.out.println("-".repeat(80));
    }

    public UiEvent getLastUiEvent() {
        return this.uiEvents.get(this.uiEvents.size()-1);
    }
}
