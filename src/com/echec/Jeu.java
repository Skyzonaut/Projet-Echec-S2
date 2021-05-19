package com.echec;

import com.echec.game.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Jeu {

    PlateauDeJeu plateau;
    public static final String[] arrayDesCommandes = {"deplacer", "prendre", "save", "charger"};
    public static final List<String> listeDesCommandes = Arrays.asList(arrayDesCommandes);
    private String tour;
    private static FileWriter fw;


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
        jeu.jouer();
        jeu.plateau.afficher();

        boolean jeuEnCours = true;

        while (jeuEnCours)
        {

            String commande = jeu.getCommandeInput().trim();

            if (listeDesCommandes.contains(commande))
            {

                switch (commande)
                {
                    case "prendre" : jeu.prendrePiece();
                        break;
                    case "deplacer" : jeu.deplacerPiece();
                        break;
                    case "save" : System.out.println(jeu.save());
                        break;
                    case "charger" : System.out.println(jeu.charger());
                    break;
                    default: System.out.println("Commande non reconnu");
                }

            }
            else
            {
                System.out.println("Commande incompréhensible");
            }
            jeu.plateau.afficher();
        }
    }

    public void prendrePiece() {

        Case origine = this.getCaseDepuisCoordonneeInput();
        Case destination = this.getCaseDepuisCoordonneeInput();
        plateau.prendrePiece(origine, destination);
    }

    public void deplacerPiece() {

        Case origine = this.getCaseDepuisCoordonneeInput();
        Case destination = this.getCaseDepuisCoordonneeInput();
        plateau.deplacerPiece(origine, destination);
    }

    public String save() {
        String dossierSauvegardeChemin = "./save/";
        File repertoire = new File(dossierSauvegardeChemin);
        JSONObject jsonObject = this.plateau.getJSONObject();

        String nouveauFichierChemin = dossierSauvegardeChemin + Tools.getFormatDate() + ".json";

        if (repertoire.isDirectory()) {
            try {
                fw = new FileWriter(nouveauFichierChemin);
                fw.write(jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fw.flush();
                    fw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "Partie sauvegardé : " + nouveauFichierChemin;
    }

    public String charger() {
        // On demande si l'utilisateur veut sauvegarder sa partie avant
        System.out.println("Vous allez écraser votre partie - Voulez vous la sauvegarder ?");
        String ouiNon = Tools.getOuiNon();
        if (ouiNon.equalsIgnoreCase("oui")) {
            System.out.println(this.save() + "\n");
        }

        String stringRetour = "";
        String dossierSauvegardeChemin = "./save/";
        File repertoire = new File(dossierSauvegardeChemin);

        // On affiche la liste des sauvegardes pour sélection
        System.out.println("Sélectionnez la sauvegarde que vous souhaitez utiliser :\n");

        if (repertoire.isDirectory()) {
            File[] listeFiles = repertoire.listFiles();
            if (listeFiles.length != 0) {
                int indexFile = 0;
                for (File file : listeFiles) {
                    System.out.println(indexFile + " - " + parseSaveName(file.getName()));
                    indexFile++;
                }

                // On demande à l'utilisateur de choisir quel sauvegarde prendre
                Scanner scannerSelectionFile = new Scanner(System.in);
                int sauvegardeChoisie = scannerSelectionFile.nextInt();


                // On charge le fichier sélectionné et on le transforme en json
                try {
                    Object obj = new JSONParser().parse(new FileReader(listeFiles[sauvegardeChoisie]));
                    this.plateau = new PlateauDeJeu((JSONObject) obj);
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                }
            } else {
                stringRetour = "Aucune sauvegarde n'a été trouvée";
            }
        } else { stringRetour = "Dossier de sauvegardé non trouvé"; }
        stringRetour = "Partie chargée";
        return stringRetour;
    }

    public String getCommandeInput() {

        Scanner inputCommandeScanner = new Scanner(System.in);
        String commande = inputCommandeScanner.nextLine();
        return commande;
    }

    public void printListeCommande() {
        String strCommande = "[";
        for (String commande : arrayDesCommandes) {strCommande += commande + "/";};
        System.out.println(strCommande + "]");
    }

    public Case getCaseDepuisCoordonneeInput() {

        Scanner inputCoordDestinataireScanner = new Scanner(System.in);
        System.out.println("Destination : [xy ou x y]");
        String coordDestination = getCoordonneeBonFormat(inputCoordDestinataireScanner);
        Case destination = convertirCaseDepuisInput(coordDestination, this.plateau.getGrille());
        return destination;
    }

    public String getCoordonneeBonFormat(Scanner scanner) {

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

    public Case convertirCaseDepuisInput(String inputString, Grille grille) {
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

    public static String parseSaveName(String fileName) {
        String parsedSaveName = "";
        String[] fileNameArray = fileName.split("");
        for (int a = 0; a < 4; a++) {
            parsedSaveName += fileNameArray[a];
        }  parsedSaveName += "/";
        for (int m = 4; m < 6; m++) {
            parsedSaveName += fileNameArray[m];
        }  parsedSaveName += "/";
        for (int j = 6; j < 8; j++) {
            parsedSaveName += fileNameArray[j];
        }  parsedSaveName += " ";
        for (int h = 9; h < 11; h++) {
            parsedSaveName += fileNameArray[h];
        }  parsedSaveName += ":";
        for (int m = 11; m < 13; m++) {
            parsedSaveName += fileNameArray[m];
        }  parsedSaveName += ":";
        for (int s = 13; s < 15; s++) {
            parsedSaveName += fileNameArray[s];
        }  parsedSaveName += "";
        return parsedSaveName;
    }
}
