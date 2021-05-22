package com.echec.game;

import com.echec.Tools;
import com.echec.pieces.Piece;
import org.json.simple.JSONObject;

import java.time.LocalDateTime;

public class Evenement {

    private String id;
    private LocalDateTime dateEtHeure;
    private String contenu;
    private String type;
    private Case caseOrigine;
    private Case caseDestination;
    private Piece pieceOrigine;
    private Piece pieceDestination;

    /**
     * @param id Identifiant de l'event
     * @param type Type de l'évènement
     * <ul><li>Création</li><li>Déplacement</li><li>Prise</li></ul>
     * @param origine Case originelle qui permettra de récupérer la position et la pièce
     * @param destination Case de destintation qui permettra de récupérer la position et la pièce
     */
    public Evenement(String id, String type, Case origine, Case destination) {
        this.id = id;
        this.dateEtHeure = LocalDateTime.now();
        this.type = type;
        this.caseOrigine = origine;
        if (caseOrigine != null) this.pieceOrigine = caseOrigine.piece;
        else this.pieceOrigine = null;
        
        if (type.equalsIgnoreCase("déplacement"))  {
            this.caseDestination = destination;
            this.contenu = Tools.deplacementToNotationEchec(origine, destination);
        }
        if (type.equalsIgnoreCase("prise")) {
            this.caseDestination = destination;
            this.pieceDestination = destination.piece;
            this.contenu = Tools.priseToNotationEchec(origine, destination);
        }
        if (type.equalsIgnoreCase("undo")) {
            this.caseDestination = destination;
            this.contenu = Tools.deplacementToNotationEchec(origine, destination);
        }
    }


    public Evenement(Evenement e) {
        this.id = e.getId();
        this.dateEtHeure = e.getDateEtHeure();
        this.contenu = e.getContenu();
        this.type = e.getType();
        this.caseOrigine = e.getCaseOrigine();
        this.pieceOrigine = e.getPieceOrigine();
        this.caseDestination = e.getCaseDestination();
        this.pieceDestination = e.getPieceDestination();
    }

    public Evenement(JSONObject jsonObject) {
        this.id = (String) jsonObject.get("id");
        this.dateEtHeure = Tools.getLocalDateTimeFromFormatDate((String) jsonObject.get("dateEtHeure"));
        this.contenu = (String) jsonObject.get("contenu");
        this.type = (String) jsonObject.get("type");
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDateEtHeure(LocalDateTime dateEtHeure) {
        this.dateEtHeure = dateEtHeure;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public String getId() {
        return id;
    }

    public LocalDateTime getDateEtHeure() {
        return dateEtHeure;
    }

    public String getContenu() {
        return contenu;
    }

    public Case getCaseOrigine() {
        return caseOrigine;
    }

    public void setCaseOrigine(Case caseOrigine) {
        this.caseOrigine = caseOrigine;
    }

    public Case getCaseDestination() {
        return caseDestination;
    }

    public void setCaseDestination(Case caseDestination) {
        this.caseDestination = caseDestination;
    }

    public Piece getPieceOrigine() {
        return pieceOrigine;
    }

    public void setPieceOrigine(Piece pieceOrigine) {
        this.pieceOrigine = pieceOrigine;
    }

    public Piece getPieceDestination() {
        return pieceDestination;
    }

    public void setPieceDestination(Piece pieceDestination) {
        this.pieceDestination = pieceDestination;
    }

    public String toString() {
        String str = "@[%s]";
        return String.format("%-15s : %-13s | %-8s | %-4s | %-4s | %s",
                this.id, this.type, Tools.dateDateTimeMagnify(this.dateEtHeure),
                caseOrigine != null ? this.caseOrigine.x + " " + this.caseOrigine.y : "",
                caseDestination != null ? this.caseDestination.x + " " + this.caseDestination.y : "",
                this.contenu);
    }

    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", this.id);
        jsonObject.put("dateEtHeure", Tools.getFormatDate(this.dateEtHeure));
        jsonObject.put("type", this.type);
        jsonObject.put("contenu", this.contenu);
        return jsonObject;
    }

}
