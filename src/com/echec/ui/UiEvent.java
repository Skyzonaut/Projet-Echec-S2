package com.echec.ui;

import com.echec.game.Case;

public class UiEvent {

    private String componentOriginId;
    private Case caseOrigine;
    private boolean alreadyClicked;

    public String getComponentOriginId() {
        return componentOriginId;
    }

    public void setComponentOriginId(String componentOriginId) {
        this.componentOriginId = componentOriginId;
    }

    public Case getCaseOrigine() {
        return caseOrigine;
    }

    public boolean hasPiece() {
        return caseOrigine.piece != null;
    }

    public void setPieceOrigine(Case caseOrigine) {
        this.caseOrigine = caseOrigine;
    }

    public boolean isAlreadyClicked() {
        return alreadyClicked;
    }

    public void setAlreadyClicked(boolean alreadyClicked) {
        this.alreadyClicked = alreadyClicked;
    }

    public UiEvent(String componentOrigin, Case caseOrigine, boolean isClicked) {
        this.componentOriginId = componentOrigin;
        this.caseOrigine = caseOrigine;
        this.alreadyClicked = isClicked;
    }

    public String toString() {
        return componentOriginId + " | " + caseOrigine + " | " + alreadyClicked;
    }
}
