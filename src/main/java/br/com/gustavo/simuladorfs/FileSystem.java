package br.com.gustavo.simuladorfs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.gustavo.simuladorfs.file_system_entries.Directory;
import br.com.gustavo.simuladorfs.file_system_entries.File;
import br.com.gustavo.simuladorfs.file_system_entries.FileSystemEntry;
import br.com.gustavo.simuladorfs.managers.PersistanceManager;
import br.com.gustavo.simuladorfs.managers.PrintManager;

public class FileSystem {
    private Directory root;
    private Directory currentDir;

    // Initialize the File System
    public void init() {
        this.root = PersistanceManager.load();

        if (this.root == null) {
            PrintManager.printError("Não foi possível carregar o diretório raiz (root)");
            PrintManager.printInfo("Criando arquivo Root...");
            this.root = Directory.createRoot();
            PrintManager.printInfo("Root criado.");
            PrintManager.printInfo("Salvando Root...");
            boolean status = PersistanceManager.save(root);

            if (status) {
                PrintManager.printInfo("Root salvo com sucesso.");
            } else {
                PrintManager.printError("Erro ao salvar Root. Encerrando programa.");
            }
        }

        this.currentDir = root;
    }

    // Finalize the file system, i.e., save the root file
    public void finalise() {
        PersistanceManager.save(this.root);
    }

    // Return the path of the current directory
    public String getCurrentPath() {
        return this.currentDir.getPath();
    }

    // Create file - command: touch <file>
    private void createFile(List<String> args) {
        if (args == null || args.isEmpty()) {
            PrintManager.printInfo("Uso correto: touch <nome_do_arquivo.extensão>");
            return;
        }

        if (args.size() != 1) {
            PrintManager.printInfo("No momento o comando aceita apenas um nome de arquivo");
            return;
        }

        String[] components = args.get(0).split(".");

        if (components.length != 1) {
            PrintManager.printInfo("Uso correto: touch <nome_do_arquivo.extensão>");
            return;
        }

        File file = new File(args.get(0), currentDir);

        currentDir.addFile(file);
    }

    // Copy files - command: cp <origen> <destiny>
    private void copyFile(String name) {
        if (name == null) {
            PrintManager.printInfo("Uso correto: rm <arquivo_ou_pasta>");
            return;
        }

    }

    // Remove files - command: rm <path> or <name>
    private void removeEntry(List<String> args) {
        if (args == null || args.isEmpty()) {
            System.err.println("Uso correto: rm <nome_arquivo_ou_pasta>");
            return;
        }

        if (args.size() != 1) {
            PrintManager.printInfo("No momento o comando aceita apenas um nome de diretório ou arquivo");
            return;
        }
    }

    // Remove directory - command: rm -r <path>
    private void removeDirectory(String path) {
    }

    // Rename file - command: mv <current_name> <new_name>
    private void renameFile(String path) {
    }

    // Rename directory - command: mv <current_name> <new_name>
    private void renameDirectory(String path) {
    }

    // Create directorys
    // [x] mkdir <name> : create directory with name
    // [ ] mkdir <name> <name> ... : create directories
    private void createSubDirectory(List<String> args) {
        if (args == null || args.isEmpty()) {
            PrintManager.printInfo("Uso correto: mkdir <nome_da_pasta>");
            return;
        }

        if (args.size() > 1) {
            PrintManager.printInfo("No momento o comando aceita apenas um nome diretório");
            return;
        }

        String name = args.get(0);
        boolean created = this.currentDir.addSubDir(name);

        if (!created) {
            PrintManager.printInfo("mkdir: Não foi possível criar o diretório " + name + ": o arquivo já existe");
        }
    }

    // 7. Listar arquivos de um diretório
    // [OK] ls : List current directory files
    // [TODO:] ls <path> : Lsit path files
    private void listDirectories(List<String> args) {
        if (args.size() != 0) {
            PrintManager.printInfo("No momento o comando aceita apenas o nome do próprio comando");
            return;
        }

        List<FileSystemEntry> sortedEntries = currentDir.getSortedEntries();
        PrintManager.printDirectoryContent(getCurrentPath(), sortedEntries);
    }

    // Navegar entre diretórios
    // [OK] cd <dir_name> : go to directory
    // [OK] cs .. : go back on directory
    // [TODO:] cd <path> : go to directory
    private void navigate(List<String> args) {
        if (args == null || args.isEmpty()) {
            PrintManager.printInfo("Uso correto: cd <nome_diretório> ou cs ..");
            return;
        }

        if (args.size() > 1 || args.get(0).isBlank()) {
            PrintManager.printInfo("No momento o comando aceita apenas um nome de diretório ou ..");
            return;
        }

        String arg = args.get(0);
        Directory parent = this.currentDir.getParent();

        if (arg.equals("..") && parent != null) {
            this.currentDir = parent;
            return;
        }

        Directory dir = currentDir.getSubDirectory(arg);

        if (dir != null) {
            this.currentDir = dir;
            return;
        }

        PrintManager.printInfo("bash: cd: " + arg + ": No such file or directory");
    }

    // TODO: Complete the list of commands and remove unused ones.
    public void processCommand(String input) {
        List<String> parts = new ArrayList<>(Arrays.asList(input.trim().split("\\s+")));

        if (parts.isEmpty() || parts.get(0).isBlank())
            return;

        String command = parts.removeFirst();
        List<String> args = parts;

        switch (command) {
            case "mkdir":
                createSubDirectory(args);
                break;
            case "touch":
                createFile(args);
                break;
            case "dir":
            case "ls":
                listDirectories(args);
                break;
            case "rm":
                removeEntry(args);
                break;
            case "cd":
                navigate(args);
                break;
            case "pwd":
                // System.out.println(getRelativePath(currentPath));
                break;
            case "cls":
                PrintManager.clearTerminal();
                break;
            case "man":
            case "help":
                printHelp();
                break;
            case "dmesg":
                System.out.println("[KERNEL] " + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_TIME)
                        + " - sistema iniciado com sucesso");
                break;
            case "sysctl":
                System.out.println("Sistema: MiniBSD\nVersão: 1.0\nNome da máquina: localhost");
                break;
            default:
                System.err.println("Comando não reconhecido. Digite 'help' para ver os comandos disponíveis.");
        }
    }

    private void printHelp() {
        System.out.println("""
                Comandos disponíveis:
                  mkdir <dir>      - Criar uma nova pasta
                  touch <file>     - Criar um novo arquivo
                  ls [dir]         - Listar o conteúdo de uma pasta
                  rm <file|dir>    - Remover arquivo ou pasta
                  cd <dir>         - Ir para outro diretório
                  pwd              - Mostrar o caminho atual
                  man | help       - Mostrar esta ajuda
                  dmesg            - Mostrar mensagens do sistema
                  sysctl           - Exibir informações do sistema
                  cls              - Limpar a tela
                  exit             - Sair do sistema
                """);
    }

}
