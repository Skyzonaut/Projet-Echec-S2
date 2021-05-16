package com.echec;

import com.echec.game.*;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Jeu {

    PlateauDeJeu plateau;
    public static final String[] arrayDesCommandes = {"deplacer", "prendre"};
    public static final List<String> listeDesCommandes = Arrays.asList(arrayDesCommandes);

    public Jeu() {
        this.plateau = new PlateauDeJeu();
    }

    public PlateauDeJeu jouer() {
        return plateau;
    }

    public void updateHistorique() {
    }

    public void chargerJeu() {
    }


    public static void main(String[] args)

    {
        Jeu jeu = new Jeu();
        PlateauDeJeu plateau = jeu.jouer();
        plateau.afficher();

        boolean jeuEnCours = true;

        while (jeuEnCours)
        {

            String commande = getCommandeInput();

            if (listeDesCommandes.contains(commande))
            {
                Case origine = jeu.getCaseDepuisCoordonneeInput();
                Case destination = jeu.getCaseDepuisCoordonneeInput();

                switch (commande)
                {
                    case "prendre" : plateau.prendrePiece(origine, destination);
                        break;
                    case "deplacer" : plateau.deplacerPiece(origine, destination);
                        break;
                    default: System.out.println("Commande non reconnu");
                }

            }
            else
            {
                System.out.println("Commande incompréhensible");
            }
            plateau.afficher();
        }
    }

    public static String getCommandeInput() {

        Scanner inputCommandeScanner = new Scanner(System.in);
        System.out.println("[deplacer/prendre]");
        String commande = inputCommandeScanner.nextLine();
        return commande;
    }

    public Case getCaseDepuisCoordonneeInput() {

        Scanner inputCoordDestinataireScanner = new Scanner(System.in);
        System.out.println("Destination : [xy ou x y]");
        String coordDestination = getCoordonneeBonFormat(inputCoordDestinataireScanner);
        Case destination = convertirCaseDepuisInput(coordDestination, this.plateau.getGrille());
        return destination;
    }

    public static String getCoordonneeBonFormat(Scanner scanner) {

        Pattern pattern = Pattern.compile("^\\d \\d$|^\\d\\d$");
        Matcher matcher;

        String coord = scanner.nextLine();
        matcher = pattern.matcher(coord);

        if (!matcher.find()) {
            System.out.println("Le format est [x y] ou [xy]");
            while (!matcher.find()) {
                coord = scanner.nextLine();
                matcher = pattern.matcher(coord);
            }
        }
        return coord;
    }

    public static Case convertirCaseDepuisInput(String inputString, Grille grille) {
        int x;
        int y;
        if (inputString.contains(" ")) {
            x = Integer.parseInt(inputString.split(" ")[0]);
            y = Integer.parseInt(inputString.split(" ")[1]);
        } else {
            x = Integer.parseInt(inputString.split("")[0]);
            y = Integer.parseInt(inputString.split("")[1]);
        }
        return grille.getCase(x, y);
    }
}
