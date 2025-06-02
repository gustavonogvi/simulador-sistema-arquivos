package br.com.code.simulador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.code.simulador.constants.Constants;
import br.com.code.simulador.enums.OperationStatus;
import br.com.code.simulador.file_system_entries.Directory;
import br.com.code.simulador.file_system_entries.FileSystemEntry;
import br.com.code.simulador.managers.PersistanceManager;
import br.com.code.simulador.managers.PrintManager;

public class FileSystem {
    private Directory root;
    private Directory currentDir;

    // Initialize the File System
    public void init() {
        this.root = PersistanceManager.load();

        if (this.root == null) {
            PrintManager.printError("Não foi possível carregar o diretório raiz (root)", "FileSystem.init()");
            PrintManager.printInfo("Criando arquivo Root...");
            this.root = Directory.createRoot();
            PrintManager.printInfo("Root criado.");
            PrintManager.printInfo("Salvando Root...");
            boolean status = PersistanceManager.save(root);

            if (status) {
                PrintManager.printInfo("Root salvo com sucesso.");
            } else {
                PrintManager.printError("Erro ao salvar Root. Encerrando programa.", "FileSystem.init()");
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

    //
    private void clearTerminal(String comandName) {
        PrintManager.clearTerminal();
        Journal.writeLine(comandName, "", OperationStatus.SUCCESS, "terminal limpo");
    }

    // TODO:
    // Copy files - command: cp <origen> <destiny>
    private void copyDirectory(String name) {
        if (name == null) {
            PrintManager.printInfo("Uso correto: rm <arquivo_ou_pasta>");
            return;
        }
    }

    // TODO:
    // Copy files - command: cp <origen> <destiny>
    private void copyFile(String name) {
        if (name == null) {
            PrintManager.printInfo("Uso correto: rm <arquivo_ou_pasta>");
            return;
        }
    }

    // Create file - command: touch <file>
    private void createFile(List<String> args, String commandName) {
        if (args == null || args.isEmpty()) {
            PrintManager.printInfo("Uso correto: touch <nome_do_arquivo.extensão>");
            return;
        }

        if (args.size() != 1) {
            PrintManager.printInfo("No momento o comando aceita apenas um nome de arquivo");
            return;
        }

        String name = args.get(0);
        OperationStatus status = currentDir.addFile(name);

        if (status == OperationStatus.CREATED) {
            Journal.writeLine(commandName, getTargetPath(name), status, "arquivo criado");
            return;
        }

        if (status == OperationStatus.UPDATED) {
            Journal.writeLine(commandName, getTargetPath(name), status, "timestamp atualizado");
            return;
        }
    }

    // Create directorys
    private void createSubDirectory(List<String> args, String commandName) {

        if (args == null || args.isEmpty()) {
            PrintManager.printInfo("Uso correto: mkdir <nome_da_pasta>");
            return;
        }

        // TODO: Remove when aceppt more than one argument
        if (args.size() > 1) {
            PrintManager.printInfo("No momento o comando aceita apenas um nome diretório");
            return;
        }

        String name = args.get(0).replaceAll(Constants.prohibitedCharacters, "");
        OperationStatus status = this.currentDir.addSubDir(name);

        if (status == OperationStatus.FAILED) {
            PrintManager.printInfo("mkdir: Não foi possível criar o diretório " + name + ": o arquivo já existe");
            Journal.writeLine(commandName, getTargetPath(name), OperationStatus.FAILED,
                    "arquvio já existe");
            return;
        }

        if (status == OperationStatus.CREATED) {
            Journal.writeLine(commandName, getTargetPath(name), OperationStatus.CREATED, "diretório criado");
        }
    }

    // Listar arquivos de um diretório
    private void listDirectories(List<String> args, String commandName) {
        if (args.size() != 0) {
            PrintManager.printInfo("No momento o comando aceita apenas o nome do próprio comando");
            return;
        }

        List<FileSystemEntry> sortedEntries = currentDir.getSortedEntries();
        PrintManager.printDirectoryContent(getCurrentPath(), sortedEntries);
        Journal.writeLine(commandName, getTargetPath(""), OperationStatus.SUCCESS, "listagem da pasta");
    }

    // Navegar entre diretórios
    private void navigate(List<String> args, String commandName) {
        if (args == null || args.isEmpty()) {
            PrintManager.printInfo("Uso correto: cd <nome_diretório> ou cs ..");
            return;
        }
        if (args.size() > 2 || args.get(0).isBlank()) {
            PrintManager.printInfo("No momento o comando aceita apenas um nome de diretório ou ..");
            return;
        }

        String arg = args.get(0);
        Directory parent = this.currentDir.getParent();

        if (arg.equals("..")) {
            if (parent == null)
                return;

            this.currentDir = parent;
            Journal.writeLine(commandName, getTargetPath(""), OperationStatus.SUCCESS, "navegação com sucesso");
            return;
        }

        Directory dir = currentDir.getSubDirectory(arg);

        if (dir != null) {
            this.currentDir = dir;
            Journal.writeLine(commandName, getTargetPath(""), OperationStatus.SUCCESS, "navegação com sucesso");
            return;
        }

        PrintManager.printInfo("cd: " + arg + ": O diretório não existe");
        Journal.writeLine(commandName, getTargetPath(""), OperationStatus.FAILED, "diretório não axiste");
    }

    // Remove directory
    // [x] command: rm -r <name>
    // [TODO:] command: rm -r <path>
    private void removeDirectory(List<String> args, String commandName) {
        if (args == null || args.isEmpty()) {
            PrintManager.printInfo("Uso correto: rm -r <nome_arquivo>");
            return;
        }

        if (args.size() != 1) {
            PrintManager.printInfo("No momento o comando aceita apenas um nome de diretório");
            return;
        }

        String path = args.get(0);
        OperationStatus status = this.currentDir.removeSubDir(path);

        if (status == OperationStatus.CREATED) {
            Journal.writeLine(commandName, getTargetPath(path), status, "diretório removido");
            return;
        }

        if (status == OperationStatus.FAILED) {
            Journal.writeLine(commandName, getTargetPath(path), status, "falha ao remover diretório");
            return;
        }
    }

    // [x] command: rm <name>
    // [TODO:] command: rm <path>
    private void removeFile(List<String> args, String commandName) {
        if (args == null || args.isEmpty()) {
            PrintManager.printInfo("Uso correto: rm <nome_arquivo>");
            return;
        }

        if (args.size() != 1) {
            PrintManager.printInfo("No momento o comando aceita apenas um nome de arquivo");
            return;
        }

        String path = args.get(0);
        OperationStatus status = this.currentDir.removeFile(path);

        if (status == OperationStatus.DELETED) {
            Journal.writeLine(commandName, getTargetPath(path), status, "arquivo removido");
            return;
        }

        if (status == OperationStatus.FAILED) {
            Journal.writeLine(commandName, getTargetPath(path), status, "falha ao remover arquivo");
            return;
        }
    }

    // TODO:
    // Rename directory - command: mv <current_name> <new_name>
    private void renameDirectory(String path) {
    }

    // TODO:
    // Rename file - command: mv <current_name> <new_name>
    private void renameFile(String path) {
    }

    //
    private String getTargetPath(String targetName) {
        return this.getCurrentPath() + "/" + targetName;
    }

    //
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

    // TODO: Complete the list of commands and remove unused ones.
    public void processCommand(String input) {
        List<String> args = new ArrayList<>(Arrays.asList(input.trim().split("\\s+")));

        String command = args.removeFirst();

        if (command.isEmpty() || command.isBlank())
            return;

        switch (command) {
            case "cd":
                navigate(args, "cd");
                break;
            case "cls":
                clearTerminal("cls");
                break;
            case "dir":
                listDirectories(args, "dir");
                break;
            case "ls":
                listDirectories(args, "ls");
                break;
            case "mkdir":
                createSubDirectory(args, "mkdir");
                break;
            case "rm":
                removeFile(args, "rm");
                break;
            case "rm -r":
                removeFile(args, "rm");
                break;
            case "touch":
                createFile(args, "touch");
                break;
            case "man":
            case "help":
                printHelp();
                break;
            default:
                System.err.println("Comando não reconhecido. Digite 'help' para ver os comandos disponíveis.");
        }
    }

}
