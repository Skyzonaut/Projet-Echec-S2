package com.echec.game;

import com.echec.Tools;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class Historique {

    /**
     * Idenfitiant de l'historique
     * @see Evenement
     */
    private String id;

    /**
     * Index de l'historique qui sert à numéroter ses évènements
     * @see Evenement
     */
    private int index = 0;

    /**
     * Map contenant les évènements et leur index :
     * <br><{@linkplain Historique#index}, {@linkplain Evenement}>
     * @see Evenement
     */
    public LinkedHashMap<Integer, Evenement> historique = new LinkedHashMap<>();

    /**
     * Classe stockant des {@linkplain Evenement} et offrant des fonctions de stockage, lecture et modification.
     * <p>Crée par défaut un évènement de création de type "création" pour initialiser l'historique</p>
     * @see Evenement
     * @see Evenement#Evenement(Evenement)
     * @see Evenement#Evenement(JSONObject)
     * @author yohan
     */
    public Historique() {
        Evenement e = new Evenement("creation_0", "Création", null, null);
        e.setContenu("Création intiale de l'historique");
        e.setContenuOrigineEchecNotation("Création");
        e.setContenuDestinationEchecNotation("Création");
        this.addEvenement(e);
        this.id = Tools.getFormatDate();
    }

    /**
     * Constructeur de chargement de sauvegarde de {@linkplain Historique#Historique()}.
     * <p>Permettant de créer un Historique à partir d'une sauvegarde copiant cette dernière.</p>
     * @param jsonObject <code>jsonObject</code> : Historique sauvegardé préalablement au format Json
     * @see Evenement
     * @see Evenement#Evenement(String, String, Case, Case)
     * @see Evenement#Evenement(JSONObject)
     * @author yohan
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

    /**
     * Ajoute un évènement à l'historique
     * @param type String : Type de l'évènement
     * @param origine {@linkplain Case} : Case contenant l'origine du mouvement
     * @param destination {@linkplain Case} : Case contenant la destination du mouvement
     * @see Evenement
     * @author yohan
     */
    public void addEvenement(String type, Case origine, Case destination) {
        Evenement e = new Evenement((type + "_" + this.index), type, origine, destination);
        this.historique.put(index, e);
        index++;
    }

    /**
     * Ajoute un nouvel évènement à l'historique à partir d'un évènement pré-éxistant.
     * @param e {@linkplain Evenement} : Evènement à ajouter
     * @see Evenement
     * @author yohan
     */
    public void addEvenement(Evenement e) {
        this.historique.put(index, e);
        index++;
    }

    /**
     * Getter retournant l'identifiant de l'historique
     * @return {@linkplain Historique#id}
     * @see Evenement
     * @author yohan
     */
    public String getId() {
        return id;
    }

    /**
     * Setter de {@linkplain Historique#id}
     * @param id String
     * @see Evenement
     * @author yohan
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Récupère le dernier évènement de l'historique.
     * @return {@linkplain Evenement}
     * @see Evenement
     * @author yohan
     */
    public Evenement getDernierEvenement() {
        return this.historique.get(this.historique.size() -1);
    }

    /**
     * Récupère la liste des évènements
     * @return {@linkplain Historique#historique}
     * @see Evenement
     * @author yohan
     */
    public LinkedHashMap<Integer, Evenement> getHistorique() {
        return historique;
    }

    /**
     * Récupère le dernier évènement qui n'est pas un retour en arrière. Permet de revenir en arrière plusieurs
     * fois en récupérant toujours le dernier évènement non "undo" afin d'éviter d'"undo" un "undo".
     * @param typeAEviter String : Type à éviter
     * @return {@linkplain Evenement} Dernier évènement qui n'est pas de type "Undo"
     * @see Evenement
     * @author yohan
     */
    public Evenement getDernierEvenement(String typeAEviter) {
        for (int i = this.historique.size()-1; i >= 0; i--) {
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
     * @see Evenement
     * @author yohan
     */
    public Evenement getEvenementParIndex(int indexVoulu) {
        return this.historique.get(indexVoulu);
    }

    /**
     * Fonction récupérant tous les évènements dans l'historique du type donné en paramètre
     * @param type <code>String</code> : Type des évènements recherchés
     * @return {@linkplain ArrayList} des évènements du type demandé
     * @see Evenement
     * @author yohan
     */
    public ArrayList<Evenement> getEvenementsParType(String type) {
        ArrayList<Evenement> listeEvenement = new ArrayList<>();
        for (Map.Entry<Integer, Evenement> entry : this.historique.entrySet()) {
            if (entry.getValue().getType().equalsIgnoreCase(type)) {
                listeEvenement.add(entry.getValue());
            }
        } return listeEvenement;
    }

    /**
     * Fonction supprimant l'évènement donné par son identifiant en paramètre
     * @param idVoulu <code>String</code> : Identifiant de l'évènement à supprimer
     * @return <code>String</code> : Chaîne retour
     * @see Evenement
     * @author yohan
     */
    public String deleteEvenement(String idVoulu) {
        for (Map.Entry<Integer, Evenement> entry : this.historique.entrySet()) {
            if (entry.getValue().getId().equalsIgnoreCase(idVoulu)) {
                this.historique.remove(entry.getKey());
                return "Evenement supprimé";
            }
        } return "Evenement introuvable ou inexistant";
    }

    /**
     * Fonction supprimant l'évènement donné par son index dans l'historique en paramètre
     * @param indexVoulu <code>int</code> : Index de l'évènement à supprimer
     * @return <code>String</code> : Chaîne retour
     * @see Evenement
     * @author yohan
     */
    public String deleteEvenement(int indexVoulu) {
        if (this.historique.get(indexVoulu) != null) {
            this.historique.remove(indexVoulu);
            return "Evenement supprimé";
        } else {
            return "Evenement introuvable ou inexistant";
        }
    }

    /**
     * Fonction supprimant le dernier évènement de l'historique
     * @return <code>String</code> : Chaîne retour
     * @see Evenement
     * @author yohan
     */
    public String deleteDernierElement() {
        if (this.historique.get(this.index-1) != null) {
            this.historique.remove(this.index-1);
            this.index--;
            return "Evenement supprimé";
        } else {
            return "Evenement introuvable ou inexistant";
        }
    }

    /**
     * Override de la méthode toString() de la classe {@linkplain Object#toString()}
     * <p>
     *     Retourne la grille sous le format suivant : <br><pre>
     *         [Evènement1]
     *         [Evènement2]
     *         ...</pre>
     * </p>
     * @return <code>String</code> : Représentation textuelle de l'historique
     * @see Evenement
     * @author yohan
     */
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("-".repeat(80)).append("\n");
        for (Map.Entry<Integer, Evenement> entry : this.historique.entrySet()) {
            str.append(entry.getValue()).append("\n");
        }
        str.append("-".repeat(80)).append("\n");
        return str.toString();
    }

    /**
     * Fonction affichant directement sur la console l'historique
     * @see Historique#toString()
     * @see Evenement
     * @author yohan
     */
    public void afficher() {
        System.out.println(this);
    }

    /**
     * Fonction permettant de sauvegarder les attributs de l'historique et
     * ses évènements dans un objet JSON, qui sera ensuite
     * intégrée dans un JSON avec toutes les autres pièces, et informations sur le Jeu.
     * Qui servira de fichier de sauvegarde au jeu.
     * @return <code>JSONObject</code> : L'historique sous format JSON
     * @see Evenement#getJSONObject()
     * @see Grille#getJSONObject()
     * @see Case#getJSONObject()
     * @see PlateauDeJeu#getJSONObject()
     * @author yohan
     */
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
