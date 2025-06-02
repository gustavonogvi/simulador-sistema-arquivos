package br.com.code.simulador.constants;

public class Constants {
    public static final String prohibitedCharacters = "[\\\\/|*?<>]";

    public static boolean hasProhibitedChars(String text) {
        return text.matches(".*" + prohibitedCharacters + ".*");
    }
}
