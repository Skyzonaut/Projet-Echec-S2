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
     * @see Historique
     */
    private String id;

    /**
     * Date et heure à laquelle l'évènement a été créé
     * @see Historique
     */
    private LocalDateTime dateEtHeure;

    /**
     * Contenu de l'évènement, représentation textuelle de l'évènement, dans le cas d'un <br>
     * <code>déplacement, prise</code> ou <code>undo</code> contient la représentation textuelle conventionnelle
     * du coup.
     * <p>
     * <i>Voir :</i>
     * <a href=" https://leconsdechecspourdebutants.com/notions_supplementaires/lecon-6-notation-algebrique-echecs.htm">Notation conventionnel des coups d'échecs</a>
     * @see Historique
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
     * @see Historique
     * @author yohan
     */
    private String type;

    /**
     * {@linkplain Case} contenant la case à l'origine du déplacement ou de la prise
     * <p> sinon la variable est <code>null</code>
     * @see Historique
     * @author yohan
     */
    private Case caseOrigine;

    /**
     * {@linkplain Case} contenant la case à la destination du déplacement ou de la prise
     * <p> sinon la variable est <code>null</code>
     * @see Historique
     * @author yohan
     */
    private Case caseDestination;

    /**
     * {@linkplain Piece} contenue dans la case d'origine, correspond à la pièce que le joueur
     * a voulu bouger
     * @see Evenement#caseOrigine
     * @see Historique
     * @author yohan
     */
    private Piece pieceOrigine;

    /**
     * {@linkplain Piece} contenue dans la case de destination, correspond à la pièce que le joueur
     * a voulu prendre.
     * <b>Uniquement dans le cas d'une</b> <code>Prise</code>
     * @see Evenement#caseOrigine
     * @see Historique
     * @author yohan
     */
    private Piece pieceDestination;

    /**
     * <code>String</code> contenant l'origine de l'évènement au format international d'échec
     * <p>Sera utilisé par l'application JavaFx {@linkplain com.echec.ui.EchecApplicationFx} pour afficher
     * le contenu de l'évènement dans la table d'historique.
     * @see Evenement#caseOrigine
     * @see Historique
     * @see com.echec.ui.UiHistorique
     * @see com.echec.ui.UiEvent
     * @author yohan
     */
    private String contenuOrigineEchecNotation;

    /**
     * <code>String</code> contenant la destination de l'évènement au format international d'échec
     * <p>Sera utilisé par l'application JavaFx {@linkplain com.echec.ui.EchecApplicationFx} pour afficher
     * le contenu de l'évènement dans la table d'historique.
     * @see Evenement#caseOrigine
     * @see Historique
     * @see com.echec.ui.UiHistorique
     * @see com.echec.ui.UiEvent
     * @author yohan
     */
    private String contenuDestinationEchecNotation;

    /**
     * Constructeur champ à champ d'un évènement
     * @param id Identifiant de l'event
     * @param type Type de l'évènement
     * <ul><li>Création</li><li>Déplacement</li><li>Prise</li></ul>
     * @param origine Case originelle qui permettra de récupérer la position et la pièce
     * @param destination Case de destintation qui permettra de récupérer la position et la pièce
     * @see Evenement#Evenement(Evenement) e
     * @see Evenement#Evenement(JSONObject)
     * @see Historique
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
            this.contenuOrigineEchecNotation = this.contenu.split(" ")[0];
            this.contenuDestinationEchecNotation = this.contenu.split("")[0] + this.contenu.split(" ")[1];
        }
        if (type.equalsIgnoreCase("prise")) {
            this.caseDestination = destination;
            this.pieceDestination = destination.piece;
            this.contenu = Tools.priseToNotationEchec(origine, destination);
            this.contenuOrigineEchecNotation = this.contenu;
            this.contenuDestinationEchecNotation = this.contenu;
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
     * @see Historique
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
     * @see Historique
     * @author yohan
     */
    public Evenement(JSONObject jsonObject) {
        this.id = (String) jsonObject.get("id");
        this.dateEtHeure = Tools.getLocalDateTimeFromFormatDate((String) jsonObject.get("dateEtHeure"));
        this.contenu = (String) jsonObject.get("contenu");
        this.type = (String) jsonObject.get("type");
    }

    /**
     * Getter retournant le type de l'évènement
     * @return {@linkplain Evenement#type}
     * @see Historique
     * @author yohan
     */
    public String getType() {
        return type;
    }

    /**
     * Getter retournant l'identifiant de l'évènement
     * @return <code>String</code> : {@linkplain Evenement#id}
     * @see Historique
     * @author yohan
     */
    public String getId() {
        return id;
    }

    /**
     * Getter retournant la date et heure de création de l'évènement
     * @return <code>LocalDateTime</code> : {@linkplain Evenement#dateEtHeure}
     * @see Historique
     * @author yohan
     */
    public LocalDateTime getDateEtHeure() {
        return dateEtHeure;
    }

    /**
     * Getter retournant le contenu de l'évènement
     * @return <code>String</code> : {@linkplain Evenement#contenu}
     * @see Historique
     * @author yohan
     */
    public String getContenu() {
        return contenu;
    }

    /**
     * Getter retournant la case d'origine de l'évènement
     * @return <code>{@linkplain Case}</code> : {@linkplain Evenement#caseOrigine}
     * @see Historique
     * @author yohan
     */
    public Case getCaseOrigine() {
        return caseOrigine;
    }

    /**
     * Getter retournant la case de destination de l'évènement
     * @return <code>{@linkplain Case}</code> : {@linkplain Evenement#caseDestination}
     * @see Historique
     * @author yohan
     */
    public Case getCaseDestination() {
        return caseDestination;
    }

    /**
     * Getter retournant la pièce d'origine de l'évènement
     * @return <code>{@linkplain Piece}</code> : {@linkplain Evenement#pieceOrigine}
     * @see Historique
     * @author yohan
     */
    public Piece getPieceOrigine() {
        return pieceOrigine;
    }

    /**
     * Getter retournant la pièce de destination de l'évènement
     * @return <code>{@linkplain Piece}</code> : {@linkplain Evenement#pieceDestination}
     * @see Historique
     * @author yohan
     */
    public Piece getPieceDestination() {
        return pieceDestination;
    }

    /**
     * Getter retournant du contenu aux notations echecs pour la case d'origine
     * @return <code>{@linkplain Piece}</code> : {@linkplain Evenement#pieceDestination}
     * @see Historique
     * @author yohan
     */
    public String getContenuOrigineEchecNotation() {
        return contenuOrigineEchecNotation;
    }

    /**
     * Getter retournant du contenu aux notations echecs pour la case de destination
     * @return <code>{@linkplain Piece}</code> : {@linkplain Evenement#pieceDestination}
     * @see Historique
     * @author yohan
     */
    public String getContenuDestinationEchecNotation() {
        return contenuDestinationEchecNotation;
    }
    /**
     * Setter modifiant le type de l'évènement
     * @param type <code>String</code> : Nouveau type
     * @see Evenement#type
     * @see Historique
     * @author yohan
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Setter modifiant l'id de l'évènement
     * @param id <code>String</code> : Nouvel id
     * @see Evenement#id
     * @see Historique
     * @author yohan
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Setter modifiant la date et heure de création de l'évènement
     * @param dateEtHeure <code>LocalDateTime</code> : Nouvel id
     * @see Evenement#dateEtHeure
     * @see Historique
     * @author yohan
     */
    public void setDateEtHeure(LocalDateTime dateEtHeure) {
        this.dateEtHeure = dateEtHeure;
    }

    /**
     * Setter modifiant le contenu de l'évènement
     * @param contenu <code>String</code> : Nouveau contenu
     * @see Evenement#contenu
     * @see Historique
     * @author yohan
     */
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    /**
     * Setter modifiant la case d'origine de l'évènement
     * @param caseOrigine <code>{@linkplain Case}</code> : Nouvelle case d'origine
     * @see Evenement#caseOrigine
     * @see Historique
     * @author yohan
     */
    public void setCaseOrigine(Case caseOrigine) {
        this.caseOrigine = caseOrigine;
    }

    /**
     * Setter modifiant la case de destination de l'évènement
     * @param caseDestination <code>{@linkplain Case}</code> : Nouvelle case de destination
     * @see Evenement#caseDestination
     * @see Historique
     * @author yohan
     */
    public void setCaseDestination(Case caseDestination) {
        this.caseDestination = caseDestination;
    }

    /**
     * Setter modifiant la pièce d'origine de l'évènement
     * @param pieceOrigine <code>{@linkplain Piece}</code> : Nouvelle pièce d'origine
     * @see Evenement#pieceOrigine
     * @see Historique
     * @author yohan
     */
    public void setPieceOrigine(Piece pieceOrigine) {
        this.pieceOrigine = pieceOrigine;
    }

    /**
     * Setter modifiant la pièce de destination de l'évènement
     * @param pieceDestination <code>{@linkplain Piece}</code> : Nouvelle pièce de destination
     * @see Evenement#pieceDestination
     * @see Historique
     * @author yohan
     */
    public void setPieceDestination(Piece pieceDestination) {
        this.pieceDestination = pieceDestination;
    }

    /**
     * Setter modifiant le contenu de la case d'origine de l'évènement
     * @param contenuOrigineEchecNotation <code>{@linkplain Piece}</code> : Nouvelle pièce de destination
     * @see Evenement#pieceDestination
     * @see Historique
     * @author yohan
     */
    public void setContenuOrigineEchecNotation(String contenuOrigineEchecNotation) {
        this.contenuOrigineEchecNotation = contenuOrigineEchecNotation;
    }

    /**
     * Setter modifiant le contenu de la case de destination de l'évènement
     * @param contenuDestinationEchecNotation <code>{@linkplain Piece}</code> : Nouvelle pièce de destination
     * @see Evenement#pieceDestination
     * @see Historique
     * @author yohan
     */
    public void setContenuDestinationEchecNotation(String contenuDestinationEchecNotation) {
        this.contenuDestinationEchecNotation = contenuDestinationEchecNotation;
    }

    /**
     * Override de la méthode toString() de la classe {@linkplain Object#toString()}
     * <p>
     *     Retourne la grille sous le format suivant : <br>
     *         <ul></li>{@linkplain Evenement#id} : {@linkplain Evenement#type}
     *         | {@linkplain Evenement#dateEtHeure} | {@linkplain Evenement#caseOrigine}
     *         | {@linkplain Evenement#caseDestination} | {@linkplain Evenement#contenu}</li></ul>
     * </p>
     * @return <code>String</code> : Représentation textuelle de l'évènement et de ses attributs
     * @see Historique
     * @author yohan
     */
    public String toString() {
        return String.format("%-15s : %-13s | %-8s | %-4s | %-4s | %s | %s | %s",
                this.id, this.type, Tools.dateDateTimeMagnify(this.dateEtHeure),
                caseOrigine != null ? this.caseOrigine.x + " " + this.caseOrigine.y : "",
                caseDestination != null ? this.caseDestination.x + " " + this.caseDestination.y : "",
                this.contenu, this.contenuOrigineEchecNotation, this.contenuDestinationEchecNotation);
    }

    /**
     * Fonction permettant de sauvegarder les attributs de l'évènement dans un objet JSON,
     * et qui sera ensuite intégrée à la sauvegarde avec les informations sur le Jeu.
     * @return <code>JSONObject</code> : L'évènement sous format JSON
     * @see Grille#getJSONObject()
     * @see Case#getJSONObject()
     * @see Historique#getJSONObject()
     * @author yohan
     */
    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", this.id);
        jsonObject.put("dateEtHeure", Tools.getFormatDate(this.dateEtHeure));
        jsonObject.put("type", this.type);
        jsonObject.put("contenu", this.contenu);
        return jsonObject;
    }

}
