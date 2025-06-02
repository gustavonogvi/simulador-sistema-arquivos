package br.com.gustavo.simuladorfs.file_system_entries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import br.com.gustavo.simuladorfs.managers.PrintManager;

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
    public boolean addSubDir(String name) {
        if (this.subDirectories.containsKey(name)) {
            return false;
        }

        this.subDirectories.put(name, new Directory(name, this));
        return true;
    }

    // Add a file to the subdirectories list.
    // If the file already exists, update lastWriteTime
    public void addFile(File file) {
        File innerFile = this.files.get(file.getName());

        if (innerFile != null) {
            file.setLastWriteTime(getCurrentTimestamp());
        } else {
            this.files.put(file.getName(), file);
        }
    }

    public void removeFile(String name) {

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
