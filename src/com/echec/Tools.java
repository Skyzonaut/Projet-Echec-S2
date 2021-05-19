package com.echec;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Tools {
    private static final int CONSTANT = 5;

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
        String dateEtHeureString = formatDateEtHeure.format(dateEtHeure);
        return dateEtHeureString;
    }
}
