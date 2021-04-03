package com.projectEchec.classes;

import java.util.Date;
import java.util.*;
import classes;

public class Historique {

    private String id;

    private LocalDateTime dateEtHeure;

    private String contenu;

    public String getId() {
        return id;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public LocalDateTime getDateEtHeure() {
        return dateEtHeure;
    }

    public String toString() {
        return String.format("-------------------------------" +
                "\n@ %s" +
                "\n| %s" +
                "\n| %s", this.id, this.dateEtHeure, this.contenu);
    }

    public String afficherUnElHistorique() {
        System.out.println(String.format("-------------------------------" +
                "\n@ %s" +
                "\n| %s" +
                "\n| %s", this.id, this.dateEtHeure, this.contenu));
    }
}
