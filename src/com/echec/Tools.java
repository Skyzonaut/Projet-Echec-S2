package com.echec;

import com.echec.game.Case;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Scanner;

public class Tools {
    private static final int CONSTANT = 5;
    public static final HashMap<Integer, String> notationEchecMapLignes = new HashMap<>();
    public static final HashMap<Integer, String> notationEchecMapTypesPions = new HashMap<>();
    static {
        notationEchecMapLignes.put(1, "A");
        notationEchecMapLignes.put(2, "B");
        notationEchecMapLignes.put(3, "C");
        notationEchecMapLignes.put(4, "D");
        notationEchecMapLignes.put(5, "E");
        notationEchecMapLignes.put(6, "F");
        notationEchecMapLignes.put(7, "G");
        notationEchecMapLignes.put(8, "A");
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
        while (!estString(ecraserOuSave)) {
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

    public static String toNotationEchec(Case origine, Case destination) {
        String str = "";
        str += String.format("%s%s | [%d %d] -> [%d %d]",
                coordToChessNotation(origine), coordToChessNotation(destination),
                origine.x, origine.y, destination.x, destination.y);
        return str;
    }

    public static String deplacementToNotationEchec(Case origine, Case destination) {
        String str = origine.piece.utfString() + coordToChessNotation(origine);
        str += coordToChessNotation(destination);
        return str;
    }

    public static String priseToNotationEchec(Case origine, Case destination) {
        String str = origine.piece.utfString() + coordToChessNotation(origine);
        str += "x";
        str += destination.piece.utfString() + coordToChessNotation(destination);
        return str;
    }
}
