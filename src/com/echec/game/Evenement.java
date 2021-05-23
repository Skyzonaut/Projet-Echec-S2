package com.echec.game;

import com.echec.Tools;
import com.echec.pieces.Piece;
import org.json.simple.JSONObject;

import java.time.LocalDateTime;

/**
 * Classe représentant l'enregistrement d'un mouvement, coup ou une quelconque action liée au jeu
 * <ul>
 *     <li><code>Déplacement</code> : déplacement d'un pion</li>
 *     <li><code>Prise</code> : prise d'un pion par un autre</li>
 *     <li><code>Undo</code> : retour en arrière sur un coup (si le niveau de difficulté l'autorise</li>
 *     <li><code>Création</code> : création de l'échiquier</li>
 * </ul>
 * Contient des informations sur l'évènement, qui permetteront de décrire le coup.
 * <p>Sera intégré dans un historique qui les manipulera.
 * @see Historique
 * @author yohan
 */
public class Evenement {

    /**
     * Idenfitiant de l'évènement
     */
    private String id;

    /**
     * Date et heure à laquelle l'évènement a été créé
     */
    private LocalDateTime dateEtHeure;

    /**
     * Contenu de l'évènement, représentation textuelle de l'évènement, dans le cas d'un <br>
     * <code>déplacement, prise</code> ou <code>undo</code> contient la représentation textuelle conventionnelle
     * du coup.
     * <p>
     * <i>Voir :</i>
     * <a href=" https://leconsdechecspourdebutants.com/notions_supplementaires/lecon-6-notation-algebrique-echecs.htm">Notation conventionnel des coups d'échecs</a>
     * @author yohan
     */
    private String contenu;

    /**
     * Type de l'évènement
     * <ul>
     *     <li><code>Déplacement</code> : déplacement d'un pion</li>
     *     <li><code>Prise</code> : prise d'un pion par un autre</li>
     *     <li><code>Undo</code> : retour en arrière sur un coup (si le niveau de difficulté l'autorise</li>
     *     <li><code>Création</code> : création de l'échiquier</li>
     * </ul>
     * @author yohan
     */
    private String type;

    /**
     * {@linkplain Case} contenant la case à l'origine du déplacement ou de la prise
     * <p> sinon la variable est <code>null</code>
     * @author yohan
     */
    private Case caseOrigine;

    /**
     * {@linkplain Case} contenant la case à la destination du déplacement ou de la prise
     * <p> sinon la variable est <code>null</code>
     * @author yohan
     */
    private Case caseDestination;

    /**
     * {@linkplain Piece} contenue dans la case d'origine, correspond à la pièce que le joueur
     * a voulu bouger
     * @see Evenement#caseOrigine
     * @author yohan
     */
    private Piece pieceOrigine;

    /**
     * {@linkplain Piece} contenue dans la case de destination, correspond à la pièce que le joueur
     * a voulu prendre.
     * <b>Uniquement dans le cas d'une</b> <code>Prise</code>
     * @see Evenement#caseOrigine
     * @author yohan
     */
    private Piece pieceDestination;

    /**
     * Constructeur champ à champ d'un évènement
     * @param id Identifiant de l'event
     * @param type Type de l'évènement
     * <ul><li>Création</li><li>Déplacement</li><li>Prise</li></ul>
     * @param origine Case originelle qui permettra de récupérer la position et la pièce
     * @param destination Case de destintation qui permettra de récupérer la position et la pièce
     * @see Evenement#Evenement(Evenement) e
     * @see Evenement#Evenement(JSONObject)
     * @author yohan
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

    /**
     * Constructeur par recopie d'un évènement depuis un évènement déjà existant en paramètre
     * @param e <code>{@linkplain Evenement}</code> : Evenement depuis lequel copier le nouvel évènement
     * @see Evenement#Evenement(String, String, Case, Case)
     * @see Evenement#Evenement(JSONObject)
     * @author yohan
     */
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

    /**
     * Constructeur par recopie d'un évènement depuis un évènement déjà existant en paramètre
     * @param e <code>{@linkplain Evenement}</code> : Evenement depuis lequel copier le nouvel évènement
     * @see Evenement#Evenement(String, String, Case, Case)
     * @see Evenement#Evenement(JSONObject)
     * @author yohan
     */
    public Evenement(JSONObject jsonObject) {
        this.id = (String) jsonObject.get("id");
        this.dateEtHeure = Tools.getLocalDateTimeFromFormatDate((String) jsonObject.get("dateEtHeure"));
        this.contenu = (String) jsonObject.get("contenu");
        this.type = (String) jsonObject.get("type");
    }

    /**
     *
     * @return
     */
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
