package br.com.gustavo.simuladorfs;

public class FileSystemSimulator {
    private final DirectoryEntry root = new DirectoryEntry("/", null);
    private final Journal journal = new Journal();

    public void processCommand(String input) {
        journal.log(input);

        String[] parts = input.split(" ");
        if (parts.length < 2) {
            System.out.println("Uso: comando caminho [novo_nome|destino]");
            return;
        }

        String command = parts[0];
        String arg1 = parts[1];
        String arg2 = (parts.length > 2) ? parts[2] : null;

        switch (command) {
            case "mkdir" -> root.createDirectory(arg1);
            case "touch" -> root.createFile(arg1);
            case "ls" -> root.list(arg1);
            case "rm" -> root.remove(arg1);
            case "rename" -> {
                if (arg2 == null) {
                    System.out.println("Uso: rename caminho novo_nome");
                } else {
                    root.rename(arg1, arg2);
                }
            }
            case "cp" -> {
                if (arg2 == null) {
                    System.out.println("Uso: cp origem destino");
                } else {
                    root.copy(arg1, arg2);
                }
            }
            default -> System.out.println("Comando não reconhecido.");
        }
    }
}
