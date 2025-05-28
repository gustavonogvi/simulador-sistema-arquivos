package br.com.gustavo.simuladorfs;

public class FileSystemSimulator {
    private DirectoryEntry current = new DirectoryEntry("/", null);
    private final DirectoryEntry root = current;
    private final Journal journal = new Journal();

    public void processCommand(String input) {
        journal.log(input);

        String[] parts = input.trim().split("\\s+");
        if (parts.length < 1) {
            System.err.println("usage: <command> <args>");
            return;
        }

        String command = parts[0];
        String arg1 = (parts.length > 1) ? parts[1] : null;
        String arg2 = (parts.length > 2) ? parts[2] : null;

        switch (command) {
            case "mkdir" -> {
                if (arg1 == null) System.err.println("usage: mkdir <dir>");
                else current.createDirectory(arg1);
            }
            case "touch" -> {
                if (arg1 == null) System.err.println("usage: touch <file>");
                else current.createFile(arg1);
            }
            case "ls" -> current.list(arg1 != null ? arg1 : ".");
            case "rm" -> {
                if (arg1 == null) System.err.println("usage: rm <file|dir>");
                else current.remove(arg1);
            }
            case "rename" -> {
                if (arg1 == null || arg2 == null) System.err.println("usage: rename <path> <new_name>");
                else current.rename(arg1, arg2);
            }
            case "cp" -> {
                if (arg1 == null || arg2 == null) System.err.println("usage: cp <source> <destination>");
                else current.copy(arg1, arg2);
            }
            case "cd" -> {
                if (arg1 == null) System.err.println("usage: cd <path>");
                else {
                    DirectoryEntry newDir = current.changeDirectory(arg1);
                    if (newDir != null) current = newDir;
                }
            }
            case "pwd" -> System.out.println(current.getFullPath());
            default -> System.err.println("error: unknown command");
        }
    }
}
