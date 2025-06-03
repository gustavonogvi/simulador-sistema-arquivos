package br.com.code.simulador;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.code.simulador.enums.OperationStatus;
import br.com.code.simulador.file_system_entries.Directory;
import br.com.code.simulador.file_system_entries.File;
import br.com.code.simulador.file_system_entries.FileSystemEntry;
import br.com.code.simulador.managers.PersistanceManager;
import br.com.code.simulador.managers.PrintManager;
import br.com.code.simulador.utils.Utils;

public class FileSystemSimulator {
    private Directory root;
    private Directory currentDir;

    public static final String clearTerminalCmd = "cls";
    public static final String navigationCmd = "cd";
    public static final String listCmd = "ls";
    public static final String helpCmd = "help";

    public static final String copyFileCmd = "cpf";
    public static final String createFileCmd = "mkf";
    public static final String removeFileCmd = "rmf";
    public static final String renameFileCmd = "rnf";

    public static final String copyDirCmd = "cpd";
    public static final String createDirCmd = "mkd";
    public static final String removeDirCmd = "rmd";
    public static final String renameDirCmd = "rnd";
    public static final String moveCmd = "mv";


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

    public void move(String sourcePath, String destPath) {
    FileSystemEntry sourceEntry = resolvePath(sourcePath);
    if (sourceEntry == null) {
        System.out.println("mv: origem não encontrada.");
        return;
    }

    Directory sourceParent = sourceEntry.getParent();
    if (sourceParent == null) {
        System.out.println("mv: não é possível mover o diretório raiz.");
        return;
    }

    Directory destDir = resolveDirectory(destPath);
    if (destDir == null) {
        System.out.println("mv: destino não encontrado ou não é um diretório.");
        return;
    }

    String name = sourceEntry.getName();

    // Verifica se já existe algo com o mesmo nome no destino
    if ((sourceEntry instanceof File && destDir.hasFile(name)) ||
        (sourceEntry instanceof Directory && destDir.hasDir(name))) {
        System.out.println("mv: já existe um item com esse nome no destino.");
        return;
    }

    // Remove da origem
    if (sourceEntry instanceof File) {
        sourceParent.removeFile(name);
        destDir.addFile((File) sourceEntry);
    } else {
        sourceParent.removeDir(name);
        destDir.addDir((Directory) sourceEntry);
    }

    sourceEntry.parent = destDir;

    System.out.println("mv: item movido com sucesso.");
}
    //
    private void clearTerminal(String comandName) {
        PrintManager.clearTerminal();
        Journal.writeLine(comandName, "", OperationStatus.SUCCESS, "terminal limpo");
    }

    // TODO:
    // Copy files - command: cp <origen> <destiny>
    private void copyDirectory(List<String> args, String commandName) {
        if (args == null) {
            PrintManager.printInfo("Uso correto: " + commandName + "<arquivo_ou_pasta>");
            return;
        }
    }

    // TODO:
    // Copy files - command: cp <origen> <destiny>
    private void copyFile(List<String> args, String commandName) {
        if (args == null) {
            PrintManager.printInfo("Uso correto: " + commandName + "<arquivo_ou_pasta>");
            return;
        }
    }

    // Create file - command: touch <file>
    private void createFile(List<String> args, String commandName) {
        if (args == null || args.isEmpty()) {
            PrintManager.printInfo("Uso correto: " + commandName + "<nome_do_arquivo.extensão>");
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
            PrintManager.printInfo("Uso correto: " + commandName + "<nome_da_pasta>");
            return;
        }

        if (args.size() > 1) {
            PrintManager.printInfo("No momento o comando aceita apenas um nome diretório");
            return;
        }

        String name = args.get(0);
        OperationStatus status = this.currentDir.addDir(name);

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

         private FileSystemEntry resolvePath(String path) {
        if (path == null || path.isBlank()) return null;

        Directory current = path.startsWith("/") ? root : currentDir;

        String[] parts = path.replaceAll("^/+", "").split("/");

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];

            if (i == parts.length - 1) {
                // Último elemento: pode ser diretório ou arquivo
                if (current.hasDir(part)) return current.getDir(part);
                if (current.hasFile(part)) return current.getFile(part);
                return null;
            }

            if (current.hasDir(part)) {
                current = current.getDir(part);
            } else {
                return null;
            }
        }

        return current;
    }

    private Directory resolveDirectory(String path) {
        FileSystemEntry entry = resolvePath(path);
        return (entry instanceof Directory) ? (Directory) entry : null;
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
private void moveEntry(List<String> args, String command) {
    if (args.size() < 2) {
        System.out.println("Uso: mv <origem> <destino>");
        return;
    }

    String origemPath = args.get(0);
    String destinoPath = args.get(1);

    FileSystemEntry origem = resolvePath(origemPath);
    Directory destino = resolveDirectory(destinoPath);

    if (origem == null) {
        System.out.println("Erro: entrada de origem não encontrada.");
        return;
    }

    if (destino == null) {
        System.out.println("Erro: diretório de destino não encontrado.");
        return;
    }

    origem.getParent().removeFile(origem.getName());

    if (origem instanceof File) {
        destino.addFile((File) origem);
        System.out.println("Arquivo movido com sucesso.");
    } else if (origem instanceof Directory) {
        origem.getParent().removeDir(origem.getName());
        destino.addDir((Directory) origem);
        System.out.println("Diretório movido com sucesso.");
    } else {
        System.out.println("Tipo de entrada desconhecido.");
    }

    origem.getParent().removeFile(origem.getName());
    origem.updateLastWriteTime();
}

    // Navegar entre diretórios
    private void navigate(List<String> args, String commandName) {
        if (args == null || args.isEmpty()) {
            PrintManager.printInfo("Uso correto: " + commandName + "<nome_diretório> ou cs ..");
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

        Directory dir = currentDir.getDir(arg);

        if (dir != null) {
            this.currentDir = dir;
            Journal.writeLine(commandName, getTargetPath(""), OperationStatus.SUCCESS, "navegação com sucesso");
            return;
        }

        PrintManager.printInfo("cd: " + arg + ": O diretório não existe");
        Journal.writeLine(commandName, getTargetPath(""), OperationStatus.FAILED, "diretório não axiste");
    }

    // Remove files or directory
    private void removeEntry(List<String> args, String commandName, boolean isFile) {
        if (args == null || args.isEmpty()) {
            PrintManager
                    .printInfo("Uso correto: " + commandName + " <nome_" + (isFile ? "arquivo" : "diretório") + ">");
            return;
        }

        if (args.size() != 1) {
            PrintManager
                    .printInfo("No momento o comando aceita apenas um nome de " + (isFile ? "arquivo" : "diretório"));
            return;
        }

        String path = args.get(0);
        FileSystemEntry entry = isFile ? currentDir.removeFile(path) : currentDir.removeDir(path);
        OperationStatus status = entry != null ? OperationStatus.SUCCESS : OperationStatus.FAILED;
        String tipo = isFile ? "arquivo" : "diretório";

        Journal.writeLine(commandName, getTargetPath(path), status,
                status == OperationStatus.SUCCESS ? tipo + " removido" : "falha ao remover " + tipo);

        if (status == OperationStatus.FAILED) {
            PrintManager.printInfo("falha ao remover " + tipo);
        }
    }

    // Remove Directory or File
    private void renameEntry(List<String> args, String commandName, boolean isFile) {
        if (args == null || args.size() != 2) {
            PrintManager.printInfo("Uso correto: " + commandName + " <nome_antigo> <nome_novo>");
            return;
        }

        String prefix = isFile ? "Arquivo" : "Diretório";
        String oldName = args.get(0);
        String newName = args.get(1);
        String formatedNewName = Utils.removeInvalidChars(newName);
        boolean hasFormatedEntry;
        FileSystemEntry entry;

        if (formatedNewName == null || formatedNewName.isBlank()) {
            Journal.writeLine(commandName, getTargetPath(oldName), OperationStatus.FAILED,
                    "novo nome inválido: " + newName);
            PrintManager.printInfo("Novo nome inválido");
            return;
        }

        if (isFile) {
            hasFormatedEntry = this.currentDir.hasFile(formatedNewName);
        } else {
            hasFormatedEntry = this.currentDir.hasDir(formatedNewName);
        }

        if (hasFormatedEntry) {
            Journal.writeLine(commandName, getTargetPath(oldName), OperationStatus.FAILED,
                    prefix + " com " + formatedNewName + " já existe");
            PrintManager.printInfo(prefix + " com " + formatedNewName + " já existe");
            return;
        }

        if (isFile) {
            entry = this.currentDir.removeFile(oldName);
        } else {
            entry = this.currentDir.removeDir(oldName);
        }

        if (entry == null) {
            Journal.writeLine(commandName, getTargetPath(oldName), OperationStatus.FAILED,
                    "não existe " + prefix + " com nome: " + oldName);
            PrintManager.printInfo(prefix + " com nome " + oldName + " não existe");
            return;
        }

        entry.setName(formatedNewName);
        OperationStatus status;

        if (isFile) {
            status = this.currentDir.addFile((File) entry);
        } else {
            status = this.currentDir.addDir((Directory) entry);
        }

        // I'm assuming that no error will occur when adding the new file
        if (status == OperationStatus.SUCCESS) {
            Journal.writeLine(commandName, getTargetPath(oldName), OperationStatus.SUCCESS,
                    prefix + " renomeado com sucesso");
            PrintManager.printInfo(prefix + " renomeado com sucesso");
        }
    }

    //
    private String getTargetPath(String targetName) {
        return this.getCurrentPath() + "/" + targetName;
    }

    // TODO: Atualizar
    private void printHelp() {
        System.out.println("""
                                Comandos disponíveis:
                    mkd <dir> ________________ Criar uma nova pasta
                    mkf <file> _______________ Criar um novo arquivo
                    ls _______________________ Listar o conteúdo de uma pasta
                    rmf <nome_arquvio> _______ Remover um arquivo
                    rmd <nome_dir> ___________ Remover uma pasta
                    cd <nome_dir> ____________ Ir para outro diretório
                    pwd ______________________ Mostrar o caminho atual
                    help _____________________ Mostrar esta ajuda
                    dmesg ____________________ Mostrar mensagens do sistema
                    sysctl ___________________ Exibir informações do sistema
                    cls ______________________ Limpar o terminal
                    cpf <velho> <novo> _______ Copiar um arquivo
                    cpd <velho> <novo> _______ Copiar uma pasta
                    rnf <velho> <novo> _______ Renomear um arquivo
                    rnd <velho> <novo> _______ Renomear uma pasta
                    exit _____________________ Sair do sistema
                """);
    }

    // TODO: Complete the list of commands and remove unused ones.
    public void processCommand(String input) {
        List<String> args = new ArrayList<>(Arrays.asList(input.trim().split("\\s+")));

        String command = args.removeFirst();

        if (command.isEmpty() || command.isBlank())
            return;

        switch (command) {
            case navigationCmd: // Navigation
                navigate(args, navigationCmd);
                break;
            case clearTerminalCmd: // Clear terminal
                clearTerminal(clearTerminalCmd);
                break;
            case copyDirCmd: // Copy directory
                copyDirectory(args, copyDirCmd);
                break;
            case copyFileCmd: // Copy file
                copyFile(args, copyFileCmd);
                break;
            case listCmd: // List directory
                listDirectories(args, listCmd);
                break;
            case createDirCmd: // Create directory
                createSubDirectory(args, createDirCmd);
                break;
            case removeFileCmd: // Remove file
                removeEntry(args, removeFileCmd, true);
                break;
            case removeDirCmd: // Remove directory
                removeEntry(args, removeDirCmd, false);
                break;
            case moveCmd:
                moveEntry(args, moveCmd);
                break;    
            case createFileCmd: // Create file
                createFile(args, createFileCmd);
                break;
            case renameDirCmd:
                renameEntry(args, renameDirCmd, false);
                break;
            case renameFileCmd:
                renameEntry(args, renameFileCmd, true);
                break;
            case "man":
            case "help":
                printHelp();
                break;
            default:
                System.err.println("Comando não reconhecido. Digite 'help' para ver os comandos disponíveis.");
        }

        // Pior canto, mas funciona :)
        PersistanceManager.save(root);
    }

}
