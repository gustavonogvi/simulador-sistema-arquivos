package br.com.code.simulador.file_system_entries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import br.com.code.simulador.enums.OperationStatus;
import br.com.code.simulador.managers.PrintManager;

public class Directory extends FileSystemEntry {

    private HashMap<String, Directory> subDirectories;
    private HashMap<String, File> files;

    public Directory(String name, Directory parent) {
        super(name, parent);
        this.subDirectories = new HashMap<>();
        this.files = new HashMap<>();
    }

    // Construtor especial. Vai ser usado apenas para criar o root
    protected Directory() {
        super();
        this.subDirectories = new HashMap<>();
        this.files = new HashMap<>();
    }

    public Directory getSubDirectory(String name) {
        return this.subDirectories.get(name);
    }

    public static Directory createRoot() {
        return new Directory();
    }

    @Override
    public boolean isDirectory() {
        return true;
    }

    // Add a Directory to the subdirectories list
    public OperationStatus addSubDir(String name) {
        if (this.subDirectories.containsKey(name)) {
            return OperationStatus.FAILED;
        }

        this.subDirectories.put(name, new Directory(name, this));
        return OperationStatus.CREATED;
    }

    public OperationStatus removeSubDir(String name) {
        Directory dir = this.subDirectories.remove(name);

        if (dir != null) {
            return OperationStatus.DELETED;
        } else {
            return OperationStatus.FAILED;
        }
    }

    // Add a file to the subdirectories list.
    // If the file already exists, update lastWriteTime
    public OperationStatus addFile(String name) {
        File innerFile = this.files.get(name);

        if (innerFile != null) {
            innerFile.setLastWriteTime(getCurrentTimestamp());
            return OperationStatus.UPDATED;
        }

        File file = new File(name, this);
        this.files.put(name, file);

        return OperationStatus.CREATED;
    }

    // Remover a file from files list
    public OperationStatus removeFile(String name) {
        File file = this.files.remove(name);

        if (file != null) {
            return OperationStatus.DELETED;
        } else {
            return OperationStatus.FAILED;
        }
    }

    public void rename(String path, String newName) {

    }

    public void copy(String sourcePath, String destPath) {

    }

    private Directory deepCopy() {
        return null;
    }

    // Gemini -
    public List<FileSystemEntry> getSortedEntries() {
        List<FileSystemEntry> entryList = new ArrayList<>();

        // Adiciona diretórios ordenados
        List<Directory> dirs = new ArrayList<>(subDirectories.values());
        dirs.sort(Comparator.comparing(dir -> dir.getName().toLowerCase()));
        entryList.addAll(dirs);

        // Adiciona arquivos ordenados
        List<File> fs = new ArrayList<>(files.values());
        fs.sort(Comparator.comparing(file -> file.getName().toLowerCase()));
        entryList.addAll(fs);

        return entryList;
    }

}
