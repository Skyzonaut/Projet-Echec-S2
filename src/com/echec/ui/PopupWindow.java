package com.echec.ui;

import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.*;

/**
 * Fenêtre de Popup JavaFx personnalisable
 * @see EchecApplication
 * @see Popup
 * @author yohan
 */
public class PopupWindow {

    /**
     * Texte contenu dans la Popup
     */
    private final String contenu;

    /**
     * Largeur de la fenêtre de Popup
     */
    private final int x;

    /**
     * Hauteur de la fenêtre de Popup
     */
    private final int y;

    /**
     * Constructeur classique de la popup
     * @param contenu {@linkplain PopupWindow#contenu} Texte qui sera contenu dans la Popup
     * @author yohan
     * @see Popup
     */
    public PopupWindow(String contenu) {
        this.contenu = contenu;
        this.x = 250;
        this.y = 150;
    }

    /**
     * Constructeur champ à champ de la pop
     * @param contenu {@linkplain PopupWindow#contenu} Texte qui sera contenu dans la Popup
     * @param x {@linkplain PopupWindow#x} : Largeur de la fenêtre
     * @param y {@linkplain PopupWindow#y} : Hauteur de la fenêtre
     */
    public PopupWindow(String contenu, int x, int y) {
        this.contenu = contenu;
        this.x = x;
        this.y = y;
    }

    /**
     * Fonction créant et affichant la popup
     * @see Popup
     * @author yohan
     */
    public void display() {

        // On crée une nouvelle stage
        Stage popupwindow=new Stage();

        // On la rend secondaire, cad qu'elle ne bloque pas les autres fenêtres
        popupwindow.initModality(Modality.NONE);


        // On set le titre
        popupwindow.setTitle("Attention");

        // On y ajoute un label avec le contenu précisé
        Label label1 = new Label(this.contenu);
        label1.setFont(new Font("Arial", 18));

        // On y ajoute un bouton pour la fermer
        Button button1= new Button("Fermer");
        button1.setOnAction(e -> popupwindow.close());

        // On crée un conteneur verticalement redimensionnable
        VBox layout= new VBox(10);
        // Et on lui ajoute le texte et le bouton
        layout.getChildren().addAll(label1, button1);
        layout.setAlignment(Pos.CENTER);

        // Puis on crée une nouvelle scène à ce stage et on l'affiche
        Scene scene1= new Scene(layout, x, y);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();
    }
}