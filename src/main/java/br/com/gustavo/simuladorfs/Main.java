package br.com.gustavo.simuladorfs;

import java.util.Scanner;

public class Main {

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_BOLD = "\033[1m";

    public static void main(String[] args) {
        clearScreen();
        printBanner();

        Scanner scanner = new Scanner(System.in);
        FileSystemSimulator fs = new FileSystemSimulator();

        while (true) {
            System.out.print(ANSI_BOLD + ANSI_CYAN + "MiniBSD > " + ANSI_RESET);
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) {
                printInfo("Shutting down. Goodbye.");
                break;
            }
            try {
                fs.processCommand(input);
            } catch (Exception e) {
                printError("error: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void printBanner() {
        System.out.println(ANSI_CYAN + ANSI_BOLD);
        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║               MiniBSD v1.0 Filesystem             ║");
        System.out.println("║        Academic Java 21 Simulation Project        ║");
        System.out.println("╚═══════════════════════════════════════════════════╝" + ANSI_RESET);
        System.out.println(ANSI_GREEN + "Available commands:");
        System.out.println("  mkdir <dir>");
        System.out.println("  touch <file>");
        System.out.println("  ls <dir>");
        System.out.println("  rm <file|dir>");
        System.out.println("  rename <path> <new_name>");
        System.out.println("  cp <source> <dest>");
        System.out.println("  cd <path>");
        System.out.println("  pwd");
        System.out.println("  exit" + ANSI_RESET);
        System.out.println();
}
    private static void printInfo(String msg) {
        System.out.println(ANSI_GREEN + "[INFO] " + msg + ANSI_RESET);
    }

    private static void printError(String msg) {
        System.out.println(ANSI_RED + "[ERROR] " + msg + ANSI_RESET);
    }

    private static void clearScreen() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception ignored) {}
    }
}
