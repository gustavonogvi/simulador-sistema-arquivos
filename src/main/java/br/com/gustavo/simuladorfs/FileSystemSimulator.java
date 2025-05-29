package br.com.gustavo.simuladorfs;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public class FileSystemSimulator {
    public static final File ROOT_FOLDER = new File(System.getProperty("user.home"), ".miniBSD");
    private Path currentPath = ROOT_FOLDER.toPath();
    private final Journal journal = new Journal();

    public FileSystemSimulator() {
        BootManager.initialize(ROOT_FOLDER);
    }

    public void startShell() {
        while (true) {
            System.out.print("MiniBSD > ");
            String input = System.console().readLine();
            if (input == null || input.equalsIgnoreCase("exit")) break;
            processCommand(input);
        }
    }

    public void processCommand(String input) {
        journal.log(input);

        String[] parts = input.trim().split("\\s+");
        if (parts.length == 0 || parts[0].isBlank()) return;

        String command = parts[0];
        String arg1 = parts.length > 1 ? parts[1] : null;
        String arg2 = parts.length > 2 ? parts[2] : null;

        switch (command) {
            case "mkdir" -> mkdir(arg1);
            case "touch" -> touch(arg1);
            case "ls" -> ls(arg1);
            case "rm" -> rm(arg1);
            case "cd" -> cd(arg1);
            case "pwd" -> System.out.println(currentPath.toString().replace(ROOT_FOLDER.getAbsolutePath(), ""));
            case "cls" -> Main.clearScreen();
            case "man", "help" -> printHelp();
            case "dmesg" -> System.out.println("[KERNEL] " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + " - boot ok");
            case "sysctl" -> System.out.println("kern.ostype=MiniBSD\nkern.osrelease=1.0\nkern.hostname=localhost");
            default -> System.err.println("error: unknown command");
        }
    }

    private void mkdir(String name) {
        if (name == null) {
            System.err.println("usage: mkdir <dir>");
            return;
        }
        try {
            Path dirPath = resolvePath(name);
            Files.createDirectories(dirPath);
            System.out.println("[OK] directory created: " + name);
        } catch (FileAlreadyExistsException e) {
            System.err.println("error: directory already exists.");
        } catch (IOException | SecurityException e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    private void touch(String name) {
        if (name == null) {
            System.err.println("usage: touch <file>");
            return;
        }
        try {
            Path filePath = resolvePath(name);
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
            System.out.println("[OK] file created: " + name);
        } catch (FileAlreadyExistsException e) {
            System.err.println("error: file already exists.");
        } catch (IOException | SecurityException e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    private void ls(String pathArg) {
        try {
            Path path = resolvePath(pathArg != null ? pathArg : ".");
            if (Files.isDirectory(path)) {
                System.out.println("Contents of " + currentPath.toString().replace(ROOT_FOLDER.getAbsolutePath(), "") + ":");
                Files.list(path).map(p -> " - " + p.getFileName()).forEach(System.out::println);
            } else {
                System.err.println("error: not a directory.");
            }
        } catch (IOException | SecurityException e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    private void rm(String name) {
        if (name == null) {
            System.err.println("usage: rm <file|dir>");
            return;
        }
        try {
            Path target = resolvePath(name);
            if (Files.isDirectory(target)) {
                Files.walk(target)
                        .sorted(Comparator.reverseOrder())
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                System.err.println("error deleting: " + p);
                            }
                        });
            } else {
                Files.deleteIfExists(target);
            }
            System.out.println("[OK] removed: " + name);
        } catch (IOException | SecurityException e) {
            System.err.println("error: " + e.getMessage());
        }
    }

    private void cd(String dir) {
        if (dir == null) {
            System.err.println("usage: cd <path>");
            return;
        }
        try {
            Path newPath = resolvePath(dir).normalize();
            if (Files.isDirectory(newPath)) {
                currentPath = newPath;
                System.out.println("[OK] changed directory to: " + currentPath.toString().replace(ROOT_FOLDER.getAbsolutePath(), ""));
            } else {
                System.err.println("error: not a directory.");
            }
        } catch (SecurityException e) {
            System.err.println("error: access denied.");
        }
    }

    private Path resolvePath(String inputPath) {
        Path resolved = currentPath.resolve(inputPath).normalize();
        if (!resolved.startsWith(ROOT_FOLDER.toPath())) {
            throw new SecurityException("Access denied: cannot escape the root directory.");
        }
        return resolved;
    }

    private void printHelp() {
        System.out.println("""
                Available commands:
                  mkdir <dir>      - Create a directory
                  touch <file>     - Create a file
                  ls [dir]         - List directory contents
                  rm <file|dir>    - Remove file or directory
                  cd <dir>         - Change directory
                  pwd              - Show current path
                  man | help       - Show help
                  dmesg            - Kernel logs
                  sysctl           - System info
                  cls              - Clear screen
                  exit             - Exit shell
                """);
    }
}
