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
            case "pwd" -> System.out.println(getRelativePath(currentPath));
            case "cls" -> Main.clearScreen();
            case "man", "help" -> printHelp();
            case "dmesg" -> System.out.println("[KERNEL] " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME) + " - boot ok");
            case "sysctl" -> System.out.println("kern.ostype=MiniBSD\nkern.osrelease=1.0\nkern.hostname=localhost");
            default -> System.err.println("Comando desconhecido. Digite 'help' para ver os comandos disponíveis.");
        }
    }

    private void mkdir(String name) {
        if (name == null) {
            System.err.println("Uso: mkdir <nome_da_pasta>");
            return;
        }
        try {
            Path dirPath = resolvePath(name);
            Files.createDirectories(dirPath);
            System.out.println("✔ Diretório criado com sucesso: " + name);
        } catch (FileAlreadyExistsException e) {
            System.err.println("A pasta já existe.");
        } catch (IOException | SecurityException e) {
            System.err.println("Erro ao criar diretório: " + e.getMessage());
        }
    }

    private void touch(String name) {
        if (name == null) {
            System.err.println("Uso: touch <nome_do_arquivo>");
            return;
        }
        try {
            Path filePath = resolvePath(name);
            Files.createDirectories(filePath.getParent());
            Files.createFile(filePath);
            System.out.println("✔ Arquivo criado: " + name);
        } catch (FileAlreadyExistsException e) {
            System.err.println("Esse arquivo já existe.");
        } catch (IOException | SecurityException e) {
            System.err.println("Erro ao criar arquivo: " + e.getMessage());
        }
    }

    private void ls(String pathArg) {
        try {
            Path path = resolvePath(pathArg != null ? pathArg : ".");
            if (Files.isDirectory(path)) {
                System.out.println("Conteúdo de " + getRelativePath(path) + ":");
                Files.list(path).map(p -> " - " + p.getFileName()).forEach(System.out::println);
            } else {
                System.err.println("Isso não é um diretório.");
            }
        } catch (IOException | SecurityException e) {
            System.err.println("Erro ao listar: " + e.getMessage());
        }
    }

    private void rm(String name) {
        if (name == null) {
            System.err.println("Uso: rm <arquivo_ou_pasta>");
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
                                System.err.println("Erro ao deletar: " + p);
                            }
                        });
            } else {
                Files.deleteIfExists(target);
            }
            System.out.println("✔ Removido: " + name);
        } catch (IOException | SecurityException e) {
            System.err.println("Erro ao remover: " + e.getMessage());
        }
    }

    private void cd(String dir) {
        if (dir == null) {
            System.err.println("Uso: cd <caminho>");
            return;
        }
        try {
            Path newPath = resolvePath(dir).normalize();
            if (Files.isDirectory(newPath)) {
                currentPath = newPath;
                System.out.println("✔ Agora em: " + getRelativePath(currentPath));
            } else {
                System.err.println("Esse caminho não é um diretório.");
            }
        } catch (SecurityException e) {
            System.err.println("Acesso negado.");
        }
    }

    private Path resolvePath(String inputPath) {
        Path resolved = currentPath.resolve(inputPath).normalize();
        if (!resolved.startsWith(ROOT_FOLDER.toPath())) {
            throw new SecurityException("Acesso negado: você não pode sair do diretório raiz do sistema.");
        }
        return resolved;
    }

    private void printHelp() {
        System.out.println("""
                Comandos disponíveis:
                  mkdir <dir>      - Criar uma nova pasta
                  touch <file>     - Criar um novo arquivo
                  ls [dir]         - Listar o conteúdo de uma pasta
                  rm <file|dir>    - Remover arquivo ou pasta
                  cd <dir>         - Navegar para outro diretório
                  pwd              - Mostrar caminho atual
                  man | help       - Mostrar esta ajuda
                  dmesg            - Mensagens do kernel
                  sysctl           - Informações do sistema
                  cls              - Limpar a tela
                  exit             - Sair do MiniBSD
                """);
    }

    private String getRelativePath(Path path) {
        return path.toString().replace(ROOT_FOLDER.getAbsolutePath(), "");
    }
}
