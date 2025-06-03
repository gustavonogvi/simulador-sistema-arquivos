package br.com.code.simulador.utils;

import java.util.Random;

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

    public static int randInt(int inf, int upper) {
        return new Random().nextInt(upper - inf + 1) + inf;
    }

}
