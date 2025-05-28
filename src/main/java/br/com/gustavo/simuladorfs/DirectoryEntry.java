package br.com.gustavo.simuladorfs;

import java.util.HashMap;
import java.util.Map;

public class DirectoryEntry {
    private final String name;
    private final DirectoryEntry parent;
    private final Map<String, DirectoryEntry> subdirs = new HashMap<>();
    private final Map<String, FileEntry> files = new HashMap<>();

    public DirectoryEntry(String name, DirectoryEntry parent) {
        this.name = name;
        this.parent = parent;
    }

    private DirectoryEntry getDirectory(String path) {
        String[] parts = path.split("/");
        DirectoryEntry current = this;
        for (String part : parts) {
            if (part.isEmpty()) continue;
            current = current.subdirs.get(part);
            if (current == null) return null;
        }
        return current;
    }

    public void createDirectory(String path) {
        String[] parts = path.split("/");
        DirectoryEntry current = this;
        for (String part : parts) {
            if (part.isEmpty()) continue;
            current.subdirs.putIfAbsent(part, new DirectoryEntry(part, current));
            current = current.subdirs.get(part);
        }
        System.out.println("[OK] Diretório criado: " + path);
    }

    public void createFile(String path) {
        int idx = path.lastIndexOf('/');
        String dirPath = path.substring(0, idx);
        String fileName = path.substring(idx + 1);
        DirectoryEntry dir = getDirectory(dirPath.isEmpty() ? "/" : dirPath);
        if (dir == null) {
            System.out.println("Diretório não encontrado.");
            return;
        }
        dir.files.put(fileName, new FileEntry(fileName));
        System.out.println("[OK] Arquivo criado: " + path);
    }

    public void list(String path) {
        DirectoryEntry dir = getDirectory(path);
        if (dir == null) {
            System.out.println("Diretório não encontrado.");
            return;
        }

        System.out.println("Conteúdo de " + path + ":");
        dir.subdirs.keySet().forEach(d -> System.out.println("[DIR]  " + d));
        dir.files.keySet().forEach(f -> System.out.println("[FILE] " + f));
    }

    public void remove(String path) {
        int idx = path.lastIndexOf('/');
        String dirPath = path.substring(0, idx);
        String name = path.substring(idx + 1);
        DirectoryEntry dir = getDirectory(dirPath.isEmpty() ? "/" : dirPath);
        if (dir == null) {
            System.out.println("Diretório não encontrado.");
            return;
        }
        if (dir.files.remove(name) != null || dir.subdirs.remove(name) != null) {
            System.out.println("[OK] Removido: " + path);
        } else {
            System.out.println("Arquivo/diretório não encontrado.");
        }
    }

    public void rename(String path, String newName) {
        int idx = path.lastIndexOf('/');
        String dirPath = path.substring(0, idx);
        String name = path.substring(idx + 1);
        DirectoryEntry dir = getDirectory(dirPath.isEmpty() ? "/" : dirPath);
        if (dir == null) {
            System.out.println("Diretório não encontrado.");
            return;
        }

        if (dir.files.containsKey(name)) {
            FileEntry file = dir.files.remove(name);
            file.setName(newName);
            dir.files.put(newName, file);
            System.out.println("[OK] Arquivo renomeado.");
        } else if (dir.subdirs.containsKey(name)) {
            DirectoryEntry subdir = dir.subdirs.remove(name);
            dir.subdirs.put(newName, subdir);
            System.out.println("[OK] Diretório renomeado.");
        } else {
            System.out.println("Arquivo ou diretório não encontrado.");
        }
    }

    public void copy(String sourcePath, String destPath) {
        System.out.println("[Simulação] Copiado de " + sourcePath + " para " + destPath);
    }
}
