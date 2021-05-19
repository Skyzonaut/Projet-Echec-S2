package com.echec.game;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class Historique {

    private String id;
    private int index = 0;
    public final LinkedHashMap<Integer, Evenement> historique = new LinkedHashMap<>();
    private LocalDateTime dateEtHeure;

    public Historique() {
        this.addEvenement("Création", "Création initiale de l'historique");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Evenement getEvenementParIndex(int indexVoulu) {
        return this.historique.get(indexVoulu);
    }

    public ArrayList<Evenement> getEvenementsParType(String type) {
        ArrayList<Evenement> listeEvenement = new ArrayList<>();
        for (Map.Entry<Integer, Evenement> entry : this.historique.entrySet()) {
            if (entry.getValue().getType().equalsIgnoreCase(type)) {
                listeEvenement.add(entry.getValue());
            }
        } return listeEvenement;
    }

    public void addEvenement(String type, String contenu) {
        Evenement e = new Evenement(type + "_" + this.index, type, contenu);
        this.historique.put(index, e);
        index++;
    }

    public void addEvenement(Evenement e) {
        this.addEvenement(e.getType(), e.getContenu());
    }

    public String deleteEvenement(String idVoulu) {
        for (Map.Entry<Integer, Evenement> entry : this.historique.entrySet()) {
            if (entry.getValue().getId().equalsIgnoreCase(idVoulu)) {
                this.historique.remove(entry.getKey());
                return "Evenement supprimé";
            }
        } return "Evenement introuvable ou inexistant";
    }

    public String deleteEvenement(int indexVoulu) {
        if (this.historique.get(indexVoulu) != null) {
            this.historique.remove(indexVoulu);
            return "Evenement supprimé";
        } else {
            return "Evenement introuvable ou inexistant";
        }
    }

    public String deleteDernierElement() {
        if (this.historique.get(this.index-1) != null) {
            this.historique.remove(this.index-1);
            this.index--;
            return "Evenement supprimé";
        } else {
            return "Evenement introuvable ou inexistant";
        }
    }

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("-".repeat(80) + "\n");
        for (Map.Entry<Integer, Evenement> entry : this.historique.entrySet()) {
            str.append(entry.getValue()).append("\n");
        }
        str.append("-".repeat(80) + "\n");
        return str.toString();
    }

    public void afficher() {
        System.out.println(this);
    }
}
