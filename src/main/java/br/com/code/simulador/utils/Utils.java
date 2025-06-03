package br.com.code.simulador.utils;

public class Utils {
    public static final String invalidChars = "[\\\\/|*?<>]";

    public static boolean hasInvalidChars(String text) {
        return text.matches(".*" + invalidChars + ".*");
    }

    public static String removeInvalidChars(String name) {
        if (name == null)
            return null;

        return name.replaceAll(invalidChars, "");
    }

}
