package com.echec;

import com.echec.game.Case;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Tools {
    private static final int CONSTANT = 5;
    public static final HashMap<Integer, String> notationEchecMapLignes = new HashMap<>();
    static {
        notationEchecMapLignes.put(1, "A");
        notationEchecMapLignes.put(2, "B");
        notationEchecMapLignes.put(3, "C");
        notationEchecMapLignes.put(4, "D");
        notationEchecMapLignes.put(5, "E");
        notationEchecMapLignes.put(6, "F");
        notationEchecMapLignes.put(7, "G");
        notationEchecMapLignes.put(8, "H");
    }

    private Tools() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }


    public static boolean estString(String s)
    {
        try {
            Integer.parseInt(s);
            return false;
        } catch (NumberFormatException ex) {
            return true;
        }
    }

    public static String getOuiNon() {
        System.out.println("[oui/non]");
        Scanner scannerEcraserOuSave = new Scanner(System.in);
        String ecraserOuSave = scannerEcraserOuSave.nextLine();
        while (!ecraserOuSave.equalsIgnoreCase("non") && !ecraserOuSave.equalsIgnoreCase("oui")) {
            System.out.println("Entrez [oui/non]");
            ecraserOuSave = scannerEcraserOuSave.nextLine();
        }
        return ecraserOuSave;
    }

    public static String getFormatDate() {
        LocalDateTime dateEtHeure = LocalDateTime.now();
        DateTimeFormatter formatDateEtHeure = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        return formatDateEtHeure.format(dateEtHeure);
    }

    public static LocalDateTime getLocalDateTimeFromFormatDate(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyMMdd-HHmmss");
        return LocalDateTime.parse(dateTime, formatter);
    }

    public static String getFormatDate(LocalDateTime dateEtHeure) {
        DateTimeFormatter formatDateEtHeure = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
        return formatDateEtHeure.format(dateEtHeure);
    }

    public static String dateDateTimeMagnify(LocalDateTime e) {
        DateTimeFormatter formatDateEtHeure = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        return formatDateEtHeure.format(e);
    }

    public static String coordToChessNotation(Case c) {
        String str = "";
        str += String.valueOf(notationEchecMapLignes.get(c.x));
        str += String.valueOf(c.y);
        return str;
    }

    public static String deplacementToNotationEchec(Case origine, Case destination) {
        String str = origine.piece.utfString() + coordToChessNotation(origine);
        str += " " + coordToChessNotation(destination);
        return str;
    }

    public static String priseToNotationEchec(Case origine, Case destination) {
        String str = origine.piece.utfString() + coordToChessNotation(origine);
        str += "x";
        str += destination.piece.utfString() + coordToChessNotation(destination);
        return str;
    }

    public static String getCoordonneeBonFormat(Scanner scanner) {
        String coord = scanner.nextLine();
        String[] coordSplit = coord.split("");
        while (!coord.matches("^\\d \\d$|^\\d\\d$") && !coord.matches("|^[a-gA-g] \\d$|^[a-gA-G]\\d$")) {
            System.out.println("Le format est [x y] ou [xy] ou [a y] ou [ay]");
            coord = scanner.nextLine();
        }
        if (coordSplit[0].matches("\\d")) {
            return coord;
        } else if (coordSplit[0].matches("[a-gA-G]")) {
            for (Map.Entry<Integer, String> entry : notationEchecMapLignes.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(coordSplit[0])) {
                    return entry.getKey().toString() + coordSplit[coordSplit.length-1];
                }
            }
        }
        return coord;
    }

    public static String getNomFichierSauvegarde() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("[Rentrez le nom de sauvegarde, ne rentrez rien pour une génération automatique]");
        String input = scanner.nextLine();
        if (!input.trim().equals("")) {
            while (input.matches("[^a-zA-Z0-9_-]")) {
                System.out.println("Le nom ne doit pas contenir les caractères suivants : ? < > : \" / \\ | ? *");
                input = scanner.nextLine();
            } return input + ".json";
        } else {
            return Tools.getFormatDate() + ".json";
        }
    }

    public static String getLettreColonne(int colonne) {
        return notationEchecMapLignes.get(colonne);
    }
}
