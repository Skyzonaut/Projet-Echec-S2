package com.echec.game;

import com.echec.Tools;
import com.echec.pieces.Piece;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Jeu {

    public PlateauDeJeu plateau;
    public static final String[] arrayDesCommandes = {"deplacer", "prendre", "save", "charger", "undo", "quitter", "abandonner", "help", "historique"};
    public static final String[] arrayDesCommandesRaccourci = {"d", "p", "s", "c", "u", "q", "a", "h", "n"};
    public static final List<String> listeDesCommandes = Arrays.asList(arrayDesCommandes);
    public static final List<String> listeDesCommandesRaccourci = Arrays.asList(arrayDesCommandesRaccourci);
    private String tour = "blanc";

    /**
     * 1 : difficile
     * 2 : facile
     */
    private int niveauDeDifficulte;
    private static FileWriter fw;
    private Boolean jeuEnCours = true;

    public Jeu() {
        this.plateau = new PlateauDeJeu();
    }

//    public static void main(String[] args) throws Exception {
//        Jeu jeu = new Jeu();
//        jeu.jouer();
//    }

    public void jouer() throws Exception {

        while (jeuEnCours) {

            this.tour = "blanc";
            int[] listeParam = lancerPartie();
            niveauDeDifficulte = listeParam[0];
            boolean partieEnCours = true;

            while (partieEnCours) {

                this.plateau.afficher();

                System.out.format("> C'est au tour des %ss\n\n", this.tour);

                String commande = this.getCommandeInput().trim();

                if (listeDesCommandes.contains(commande) || listeDesCommandesRaccourci.contains(commande)) {

                    switch (commande) {
                        case "prendre":
                        case "p":
                            this.prendrePiece();
                            break;

                        case "deplacer":
                        case "d":
                            this.deplacerPiece();
                            break;

                        case "save":
                        case "s":
                            System.out.println(this.save());
                            break;

                        case "charger":
                        case "c":
                            System.out.println(this.charger());
                            break;

                        case "undo":
                        case "u":
                            if (niveauDeDifficulte == 2) {
                                this.undo();
                            } else {
                                System.out.println("Vous ne pouvez pas revenir en arrière dans ce niveau de difficulté!");
                            }
                            break;

                        case "quitter" :
                        case "q" :
                            this.quitterJeu();
                            break;

                        case "abandonner" :
                        case "a" :
                            partieEnCours = this.finirPartie();
                            break;

                        case "help" :
                        case "h" :
                            this.help();

                        case "historique" :
                        case "n" :
                            this.plateau.historique.afficher();

                        default:
                            System.out.println("Commande non reconnu");
                    }
                } else {
                    System.out.println("Commande incompréhensible");
                }
//                this.plateau.historique.afficher();
            }
        }
    }

    public String prendrePiece() {
        Case origine = this.getCaseOrigineDepuisCoordonneeInput();
        Case destination = this.getCaseDestinationDepuisCoordonneeInput();
        String retour = this.plateau.prendrePiece(origine, destination);
        if (retour.equals("ok")) {
            this.tour = (this.tour.equals("blanc")) ? "noir" : "blanc";
            return "ok";
        } else {
            return "nok";
        }
    }

    public String deplacerPiece() {
        Case origine = this.getCaseOrigineDepuisCoordonneeInput();
        Case destination = this.getCaseDestinationDepuisCoordonneeInput();
        String retour = this.plateau.deplacerPiece(origine, destination);
        if (retour.equals("ok")) {
            this.tour = (this.tour.equals("blanc")) ? "noir" : "blanc";
            return "ok";
        } else {
            return "nok";
        }
    }

    public void undo() {
        System.out.println("undo done");
        Evenement dernierEvenement = this.plateau.historique.getDernierEvenement("undo");
        Case origine = dernierEvenement.getCaseOrigine();
        Piece pieceOrigine = dernierEvenement.getPieceOrigine();
        Case destination = dernierEvenement.getCaseDestination();
        String typeEvenement = dernierEvenement.getType();

        this.plateau.historique.addEvenement("Undo", destination, origine);

        if (typeEvenement.equalsIgnoreCase("déplacement")) {
            this.plateau.deplacerPiece(destination, origine, false);
        }
        else if (typeEvenement.equalsIgnoreCase("Prise")) {
            Piece pieceDestination = dernierEvenement.getPieceDestination();
            this.plateau.deplacerPiece(destination, origine, false);
            this.plateau.getGrille().getCase(destination).ajouterPiece(pieceDestination);
        }
        this.tour = (this.tour.equals("blanc")) ? "noir" : "blanc";
    }

    public String save() {
        String dossierSauvegardeChemin = "../save/";
        File repertoire = new File(dossierSauvegardeChemin);
        JSONObject jsonObject = this.plateau.getJSONObject();
        jsonObject.put("tour", this.tour);
        jsonObject.put("difficulte", this.niveauDeDifficulte);

        String nouveauFichierChemin = dossierSauvegardeChemin + Tools.getNomFichierSauvegarde();

        if (repertoire.exists()) {
            if (repertoire.isDirectory()) {
                writeFile(nouveauFichierChemin, jsonObject);
            }
        } else {
            try {
                Path path = Paths.get("../save/");
                Files.createDirectories(path);
                writeFile(nouveauFichierChemin, jsonObject);
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

    public void saveFromUi(File file) {
        JSONObject jsonObject = this.plateau.getJSONObject();
        jsonObject.put("tour", this.tour);
        jsonObject.put("difficulte", this.niveauDeDifficulte);
        writeFile(file.getPath(), jsonObject);
    }

    public void writeFile(String filePath, JSONObject jsonObject) {
        try {
            fw = new FileWriter(filePath);
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

    public String charger() {

        // On demande si l'utilisateur veut sauvegarder sa partie avant
        System.out.println("Vous allez écraser votre partie - Voulez vous la sauvegarder ?");
        String ouiNon = Tools.getOuiNon();
        if (ouiNon.equalsIgnoreCase("oui")) {
            System.out.println(this.save() + "\n");
        }

        String stringRetour;
        String dossierSauvegardeChemin = "../save/";
        File repertoire = new File(dossierSauvegardeChemin);

        // On affiche la liste des sauvegardes pour sélection
        System.out.println("Sélectionnez la sauvegarde que vous souhaitez utiliser :\n");

        if (repertoire.isDirectory()) {
            File[] listeFiles = repertoire.listFiles();
            Arrays.sort(listeFiles, Comparator.comparingLong(File::lastModified));
            if (listeFiles.length != 0) {
                int indexFile = 0;
                for (File file : listeFiles) {
                    System.out.println(indexFile + " - " + getSaveFileName(file.getName()));
                    indexFile++;
                }

                // On demande à l'utilisateur de choisir quel sauvegarde prendre
                Scanner scannerSelectionFile = new Scanner(System.in);
                int sauvegardeChoisie = scannerSelectionFile.nextInt();

                // On charge le fichier sélectionné et on le transforme en json
                this.chargerJeuFromFile(listeFiles[sauvegardeChoisie]);

            } else {
                stringRetour = "Aucune sauvegarde n'a été trouvée";
            }
        } else { stringRetour = "Dossier de sauvegarde non trouvé"; }
        stringRetour = "Partie chargée";
        return stringRetour;
    }

    public void chargerJeuFromFile(File file) {
        try {
            Object obj = new JSONParser().parse(new FileReader(file));
            JSONObject jsonObject = (JSONObject) obj;
            this.tour = (String) jsonObject.get("tour");
            this.niveauDeDifficulte = ((Long) jsonObject.get("difficulte")).intValue();

            this.plateau = new PlateauDeJeu((JSONObject) obj);
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public int[] lancerPartie() {
        int[] listeParam = new int[3];
        Scanner scanner = new Scanner(System.in);
        System.out.println("Sélectionnez le niveau de difficulté");
        System.out.println("\n1 - Normal : Règles standards\n2 - Apprenti : Possibilité de retour en arrière");
        listeParam[0] = scanner.nextInt();
        while (listeParam[0] > 2 ||  listeParam[0] < 1) {
            System.out.println("Choisissez une difficult disponible!");
            listeParam[0] = scanner.nextInt();
        }
        System.out.println("-------- Début de la partie --------");
        return listeParam;
    }

    public Boolean finirPartie() {
        System.out.println("[Voulez vous sauvegarder votre partie ?]");
        if (Tools.getOuiNon().equalsIgnoreCase("oui")) this.save();
        return false;
    }

    public void quitterJeu() {
        System.out.println("[Voulez vous sauvegarder votre partie ?]");
        if (Tools.getOuiNon().equalsIgnoreCase("oui")) this.save();
        System.exit(0);
    }

    public void help() {
        System.out.println("[Déplacer] " + "-".repeat(40) +
                "| Entrez : deplacer ou d\n" +
                "| Commande permettant de déplacer une pièce sur l'échiquier\n" +
                "| Premier entrée  : Coordonnées de la case contenant le pion que vous souhaitez déplacer" +
                "\n|\t\t Entrez au format numérique : [xy] ou [x y]; Exemple : a5 ou g 7" +
                "\n|\t\t Entrez au format échec     : [ay] ou [x y]; Exemple : a5 ou g 7\n" +
                "| Deuxième entrée : Coordonnées de la case où vous souhaitez déplacer votre pion" +
                "\n|\t\t Entrez au format numérique : [xy] ou [x y]; Exemple : a5 ou g 7" +
                "\n|\t\t Entrez au format échec     : [ay] ou [x y]; Exemple : a5 ou g 7\n");

        System.out.println("[Prendre] " + "-".repeat(40) +
                "| Entrez : prendre ou p\n" +
                "| Commande permettant de prendre une pièce sur l'échiquier\n" +
                "| Premier entrée : Coordonnées de la case contenant le pion que vous souhaitez déplacer" +
                "\n|\t\t Entrez au format numérique : [xy] ou [x y]; Exemple : a5 ou g 7" +
                "\n|\t\t Entrez au format échec     : [ay] ou [x y]; Exemple : a5 ou g 7\n" +
                "| Premier entrée : Coordonnées de la case où vous souhaitez déplacer votre pion et y prendre le pion présent" +
                "\n|\t\t Entrez au format numérique : [xy] ou [x y]; Exemple : a5 ou g 7" +
                "\n|\t\t Entrez au format échec     : [ay] ou [x y]; Exemple : a5 ou g 7\n");

        System.out.println("[Save] " + "-".repeat(43) +
                "| Entrez : save ou s\n" +
                "| Commande permettant de sauvegarder l'état actuel de votre partie dans un fichier de sauvegarde\n" +
                "| pour pouvoir la recharger plus tard\n" +
                "| Premier entrée : Indiquez le nom que vous souhaitez donner à votre sauvegarde\n" +
                "| Par défaut (c'est à dire si vous ne renseignez aucun nom de sauvegarde)\n" +
                "| la sauvergarde sera enregistré avec comme nom la date et heure au format : YYYYMMJJ-hhmmss\n");

        System.out.println("[Charger] " + "-".repeat(40) +
                "| Entrez : charger ou c\n" +
                "| Commande permettant de charger une ancienne sauvegarde et d'y reprendre la partie sauvegardée\n" +
                "| Premier entrée  : Indiquez si vous voulez sauvegarder votre partie actuelle" +
                "\n|\t\t Entrez au format oui/non : [oui] ou [non]" +
                "| Deuxième entrée : Sélectionnez la sauvegarde à charger\n");

        System.out.println("[Undo] " + "-".repeat(43) +
                "| Entrez : undo ou u\n" +
                "| Commande permettant de revenir sur les coups précédents [UNIQUEMENT SI EN MODE APPRENTI]\n");

        System.out.println("[Quitter] " + "-".repeat(41) +
                "| Entrez : undo ou u\n" +
                "| Commande permettant de quitter le jeu et d'éteindre le programme\n" +
                "| Premier entrée  : Indiquez si vous voulez sauvegarder votre partie actuelle" +
                "\n|\t\t Entrez au format oui/non : [oui] ou [non]\n");

        System.out.println("[Abandonner] " + "-".repeat(37) +
                "| Entrez : undo ou u\n" +
                "| Commande permettant d'abandonner votre partie actuelle, et de revenir à l'écran de sélection\n" +
                "| de niveau de difficulté de parties\n" +
                "| Premier entrée  : Indiquez si vous voulez sauvegarder votre partie actuelle" +
                "\n|\t\t Entrez au format oui/non : [oui] ou [non]\n");

        System.out.println("[Abandonner] " + "-".repeat(37) +
                "| Entrez : undo ou u\n" +
                "| Commande permettant d'afficher les indications sur les commandes disponibles\n");

        System.out.println("[Historique] " + "-".repeat(37) +
                "| Entrez : historique ou n\n" +
                "| Commande permettant d'afficher l'historique des coups et undo\n");
    }
    public String getCommandeInput() {
        printListeCommande();
        Scanner inputCommandeScanner = new Scanner(System.in);
        String commande = inputCommandeScanner.nextLine();
        return commande;
    }

    public void printListeCommande() {
        String strCommande = "[";
        for (String commande : arrayDesCommandes) {strCommande += commande + "/";}
        strCommande += "] ou leurs raccourcis : [";
        for (String commande : arrayDesCommandesRaccourci) {strCommande += commande + "/";}
        System.out.println(strCommande + "]");
    }

    public Case getCaseOrigineDepuisCoordonneeInput() {
        Scanner inputCoordDestinataireScanner = new Scanner(System.in);
        System.out.println("Origine : [xy ou x y] ou [ay ou a y]");
        String coordDestination = Tools.getCoordonneeBonFormat(inputCoordDestinataireScanner);
        Case destination = convertirCaseDepuisInput(coordDestination, this.plateau.getGrille());
        return destination;
    }

    public Case getCaseDestinationDepuisCoordonneeInput() {

        Scanner inputCoordDestinataireScanner = new Scanner(System.in);
        System.out.println("Destination : [xy ou x y] ou [ay ou a y]");
        String coordDestination = Tools.getCoordonneeBonFormat(inputCoordDestinataireScanner);
        Case destination = convertirCaseDepuisInput(coordDestination, this.plateau.getGrille());
        return destination;
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

    public void changerCouleurTour() {
        if (this.tour.equals("blanc")) {
            this.tour = "noir";
        } else {
            this.tour = "blanc";
        }
    }

    public String getTour() {
        return tour;
    }

    public void setTour(String string) {
        this.tour = string;
    }

    public static String getSaveFileName(String fileName) {
        if (fileName.matches("[0-9-]")) {
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
        } else return fileName;
    }

    public void setNiveauDeDifficulte(int niveauDeDifficulte) {
        this.niveauDeDifficulte = niveauDeDifficulte;
    }

    public int getNiveauDeDifficulte() {
        return niveauDeDifficulte;
    }


}
