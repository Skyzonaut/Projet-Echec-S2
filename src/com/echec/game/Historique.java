package com.echec.game;

import com.echec.Tools;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Historique {

    private String id;
    private int index = 0;
    public LinkedHashMap<Integer, Evenement> historique = new LinkedHashMap<>();
    private LocalDateTime dateEtHeure;

    // ----------------------------------------------------------------------------------------------------------------
    // Constructeurs
    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Classe stockant des  {@linkplain Evenement} et offrant des fonctions de stockage, lecture et modification.
     * <p>Crée par défaut un évènement de création de type "création" pour initialiser l'historique</p>
     */
    public Historique() {
        Evenement e = new Evenement("creation_0", "Création", null, null);
        e.setContenu("Création intiale de l'historique");
        this.addEvenement(e);
        this.id = Tools.getFormatDate();
    }

    /**
     * Constructeur de chargement de sauvegarde de {@linkplain Historique#Historique()}.
     * <p>Permettant de créer un Historique à partir d'une sauvegarde copiant cette dernière.</p>
     * @param jsonObject Historique sauvegardé préalablement au format Json
     */
    public Historique(JSONObject jsonObject) {
        this.id = (String) jsonObject.get("idHistorique");
        JSONArray jsonArray = (JSONArray) jsonObject.get("listeEvenements");
        Iterator<JSONObject> iterator = jsonArray.iterator();
        this.historique = new LinkedHashMap<>();
        this.index = 0;
        while (iterator.hasNext()) {
            JSONObject pieceJsonObject = iterator.next();
            this.historique.put(this.index, new Evenement(pieceJsonObject));
            this.index++;
        }
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Getter & Setter & Fonction add / delete
    // ----------------------------------------------------------------------------------------------------------------

    /**
     * Ajoute un évènement à l'historique
     * @param type String : Type de l'évènement
     * @param origine {@linkplain Case} : Case contenant l'origine du mouvement
     * @param destination {@linkplain Case} : Case contenant la destination du mouvement
     */
    public void addEvenement(String type, Case origine, Case destination) {
        Evenement e = new Evenement((type + "_" + this.index), type, origine, destination);
        this.historique.put(index, e);
        index++;
    }

    /**
     * Ajoute un nouvel évènement à l'historique à partir d'un évènement pré-éxistant.
     * @param e {@linkplain Evenement}
     */
    public void addEvenement(Evenement e) {
        this.historique.put(index, e);
        index++;
    }

    /**
     * Getter {@linkplain Historique#id}
     * @return {@linkplain Historique#id}
     */
    public String getId() {
        return id;
    }

    /**
     * Setter de {@linkplain Historique#id}
     * @param id String
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Récupère le dernier évènement de l'historique.
     * @return {@linkplain Evenement}
     */
    public Evenement getDernierEvenement() {
        return this.historique.get(this.historique.size() -1);
    }

    /**
     * Récupère le dernier évènement qui n'est pas un retour en arrière. Permet de revenir en arrière plusieurs
     * fois en récupérant toujours le dernier évènement non "undo" afin d'éviter d'"undo" un "undo".
     * @param typeAEviter String : Type à éviter
     * @return {@linkplain Evenement} Dernier évènement qui n'est pas de type "Undo"
     */
    public Evenement getDernierEvenement(String typeAEviter) {
        for (int i = this.historique.size()-1; i >= 0 ; i--) {
            if (!this.historique.get(i).getType().equalsIgnoreCase("undo")) {
                return this.historique.get(i);
            }
        }
        return null;
    }

    /**
     * Récupère l'évènement à l'index voulu.
     * @param indexVoulu int
     * @return {@linkplain Evenement}
     */
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

    // ----------------------------------------------------------------------------------------------------------------
    // Fonction d'affichage
    // ----------------------------------------------------------------------------------------------------------------

    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("-".repeat(80)).append("\n");
        for (Map.Entry<Integer, Evenement> entry : this.historique.entrySet()) {
            str.append(entry.getValue()).append("\n");
        }
        str.append("-".repeat(80)).append("\n");
        return str.toString();
    }

    public void afficher() {
        System.out.println(this);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // Fonction de sauvegarde
    // ----------------------------------------------------------------------------------------------------------------

    public JSONObject getJSONObject() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("idHistorique", this.id);
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<Integer, Evenement> entry : this.historique.entrySet()) {
            jsonArray.add(entry.getValue().getJSONObject());
        }
        jsonObject.put("listeEvenements", jsonArray);
        return jsonObject;
    }
}
