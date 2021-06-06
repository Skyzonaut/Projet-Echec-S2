package com.echec.ui;

import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.*;

public class PopupWindow {
    private String contenu;
    private int x;
    private int y;

    public PopupWindow(String contenu) {
        this.contenu = contenu;
        this.x = 250;
        this.y = 150;
    }
    public PopupWindow(String contenu, int x, int y) {
        this.contenu = contenu;
        this.x = x;
        this.y = y;
    }


    public void display()
    {
        Stage popupwindow=new Stage();

        popupwindow.initModality(Modality.NONE);
        popupwindow.setTitle("Attention");

        Label label1 = new Label(this.contenu);
        label1.setFont(new Font("Arial", 18));

        Button button1= new Button("Fermer");
        button1.setOnAction(e -> popupwindow.close());

        VBox layout= new VBox(10);
        layout.getChildren().addAll(label1, button1);
        layout.setAlignment(Pos.CENTER);

        Scene scene1= new Scene(layout, x, y);
        popupwindow.setScene(scene1);
        popupwindow.showAndWait();

    }

}