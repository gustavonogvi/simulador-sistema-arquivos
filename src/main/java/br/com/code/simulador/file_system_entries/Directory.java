package br.com.code.simulador.file_system_entries;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import br.com.code.simulador.enums.EntryType;
import br.com.code.simulador.enums.OperationStatus;

public class Directory extends FileSystemEntry {

    public HashMap<String, Directory> directories;
    public HashMap<String, File> files;

    public Directory(String name, Directory parent) {
        super(name, parent);
        this.directories = new HashMap<>();
        this.files = new HashMap<>();
        this.type = EntryType.DIRECTORY;
    }

    // Construtor especial. Vai ser usado apenas para criar o root
    protected Directory() {
        super();
        this.directories = new HashMap<>();
        this.files = new HashMap<>();
    }

    //
    public Directory getDir(String name) {
        return this.directories.get(name);
    }
    public File getFile(String name) {
    return this.files.get(name);
}

    //
    public boolean hasDir(String name) {
        return this.directories.containsKey(name);
    }

    //
    public boolean hasFile(String name) {
        return this.files.containsKey(name);
    }

    // Create a directory that dont have a parent directory
    public static Directory createRoot() {
        return new Directory();
    }

    // Add a Directory to the directories list
    public OperationStatus addDir(String name) {
        if (this.directories.containsKey(name)) {
            return OperationStatus.FAILED;
        }

        this.directories.put(name, new Directory(name, this));
        return OperationStatus.CREATED;
    }

    //
    public OperationStatus addDir(Directory dir) {
        String name = dir.getName();

        if (this.directories.containsKey(name)) {
            return OperationStatus.FAILED;
        }

        this.directories.put(name, dir);

        return OperationStatus.CREATED;
    }

    //
    public Directory removeDir(String name) {
        return this.directories.remove(name);
    }

    // Add a file to the directories list.
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

    //
    public OperationStatus addFile(File file) {
        String name = file.getName();

        if (this.files.containsKey(name)) {
            return OperationStatus.FAILED;
        }

        this.files.put(name, file);

        return OperationStatus.CREATED;
    }

    // Remover a file from files list
    public File removeFile(String name) {
        return this.files.remove(name);
    }

    // TODO:
    public void copy(String sourcePath, String destPath) {

    }

    // TODO: Precisa?
    private Directory deepCopy() {
        return null;
    }

    // Gemini -
    public List<FileSystemEntry> getSortedEntries() {
        List<FileSystemEntry> entryList = new ArrayList<>();

        // Adiciona diretórios ordenados
        List<Directory> dirs = new ArrayList<>(directories.values());
        dirs.sort(Comparator.comparing(dir -> dir.getName().toLowerCase()));
        entryList.addAll(dirs);

        // Adiciona arquivos ordenados
        List<File> fs = new ArrayList<>(files.values());
        fs.sort(Comparator.comparing(file -> file.getName().toLowerCase()));
        entryList.addAll(fs);

        return entryList;
    }

}
