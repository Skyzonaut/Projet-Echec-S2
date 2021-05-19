package com.echec.game;

import com.echec.Tools;

import java.time.LocalDateTime;

public class Evenement {

    private String id;
    private LocalDateTime dateEtHeure;
    private String contenu;
    private String type;


    /**
     * @param id Identifiant de l'event
     * @param type Type de l'évènement
     *             <ul><li>Création</li><li>Déplacement</li><li>Prise</li></ul>
     * @param contenu Contenu de l'évènement
     */
    public Evenement(String id, String type, String contenu) {
        this.id = id;
        this.dateEtHeure = LocalDateTime.now();
        this.contenu = contenu;
        this.type = type;
    }

    public Evenement(Evenement e) {
        this.id = e.getId();
        this.dateEtHeure = e.getDateEtHeure();
        this.contenu = e.getContenu();
        this.type = e.getType();
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

    public String toString() {
        String str = "@[%s]";
        return String.format("%-15s : %-13s | %-8s | %s",
                this.id, this.type, Tools.dateDateTimeMagnify(this.dateEtHeure), this.contenu);
    }
}
