

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        FileSystemSimulator fs = new FileSystemSimulator();
        Scanner scanner = new Scanner(System.in);

        System.out.println("MiniBSD v1.0 - Simulador de Sistema de Arquivos");
        System.out.println("Digite comandos como mkdir, touch, ls, rm, cp, rename, exit\n");

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("exit")) {
                System.out.println("Encerrando MiniBSD...");
                break;
            }

            fs.processCommand(input);
        }

        scanner.close();
    }
}
