package br.com.code.simulador.managers;

import java.text.SimpleDateFormat;
import java.util.List;

import br.com.code.simulador.file_system_entries.FileSystemEntry;

public class PrintManager {

    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_MAGENTA = "\u001B[35m";
    public static final String ANSI_BOLD = "\033[1m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static void printConsolePath(String path) {
        System.out.println(ANSI_MAGENTA + "* " + path + ANSI_RESET);
    }

    public static void printInfo(String msg) {
        System.out.println(ANSI_YELLOW + "[INFO] " + msg + ANSI_RESET);
    }

    public static void printError(String msg, String origen) {
        System.out.println(ANSI_RED + "[ERROR] " + msg + ANSI_RESET);
    }

    // Print to the terminal all subdirectories and files sorted in alphabetic
    // order, starting with the subdirectories, then the files
    public static void printDirectoryContent(String path, List<FileSystemEntry> sortedEntries) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
        int modeColumnWidth = 14;
        int timeColumnWidth = 20;
        int lengthColumnWidth = 10;

        String format = "%-" + modeColumnWidth +
                "s %" + timeColumnWidth +
                "s %" + lengthColumnWidth +
                "s  %s%n";

        System.out.println("\nDiretório: " + path + "\n");
        System.out.printf(format, "Type", "LastWriteTime", "Length", "Name");
        System.out.printf(format, "----", "-------------", "------", "----");

        for (FileSystemEntry entry : sortedEntries) {
            String type = entry.getType();
            String time = sdf.format(entry.getLastWrittenTime());
            // String lengthStr = entry.isDirectory() ? "" : String.valueOf("");
            String lengthStr = "1200";
            String name = entry.getName();
            System.out.printf(format, type, time, lengthStr, name);
        }
        System.out.println("");
    }

    public static void printBanner() {
        String ANSI_CYAN = PrintManager.ANSI_CYAN;
        String ANSI_BOLD = PrintManager.ANSI_BOLD;
        String ANSI_RESET = PrintManager.ANSI_RESET;

        System.out.println("\n" + ANSI_CYAN + ANSI_BOLD + "\n" +
                "╔═════════════════════════════════════╗" + "\n" +
                "║     Gerenciado de Arquivos S.O.     ║" + "\n" +
                "╚═════════════════════════════════════╝" + "\n" +
                ANSI_RESET);
    }

    public static void clearTerminal() {
        try {
            String os = System.getProperty("os.name");

            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception ignored) {
        }
    }

}
