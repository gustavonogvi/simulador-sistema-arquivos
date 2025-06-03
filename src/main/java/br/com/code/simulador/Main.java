package br.com.code.simulador;

import java.util.Scanner;

import br.com.code.simulador.managers.PrintManager;

public class Main {

    public static void main(String[] args) {
        // TODO: Remover daqui e colocar em uma classe. FileSystem talvez?
        Scanner scanner = new Scanner(System.in);
        FileSystemSimulator fs = new FileSystemSimulator();
        String input;

        PrintManager.clearTerminal();
        PrintManager.printBanner();
        fs.init();

        while (true) {
            PrintManager.printConsolePath(fs.getCurrentPath());

            input = scanConsole(scanner);

            if (input.equalsIgnoreCase("exit")) {
                PrintManager.printInfo("Shutting down. Goodbye.");
                break;
            }

            try {
                fs.processCommand(input);
            } catch (Exception e) {
                PrintManager.printError("error!!!!: " + e.getMessage(), "Main.main()");
            }
        }

        fs.finalise();
        scanner.close();
    }

    private static String scanConsole(Scanner sc) {
        System.out.print(PrintManager.ANSI_YELLOW + ">> ");
        String input = sc.nextLine().trim();
        System.out.print(PrintManager.ANSI_RESET);
        return input;
    }

}
