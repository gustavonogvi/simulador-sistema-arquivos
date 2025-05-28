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

    private DirectoryEntry getRoot() {
        DirectoryEntry dir = this;
        while (dir.parent != null) {
            dir = dir.parent;
        }
        return dir;
    }

    private DirectoryEntry getDirectory(String path) {
        String[] parts = path.split("/");
        DirectoryEntry current = path.startsWith("/") ? getRoot() : this;

        for (String part : parts) {
            if (part.isEmpty() || part.equals(".")) continue;
            if (part.equals("..")) {
                if (current.parent != null) current = current.parent;
            } else {
                current = current.subdirs.get(part);
            }

            if (current == null) return null;
        }
        return current;
    }

    public void createDirectory(String path) {
        String[] parts = path.split("/");
        DirectoryEntry current = path.startsWith("/") ? getRoot() : this;

        for (String part : parts) {
            if (part.isEmpty() || part.equals(".")) continue;

            if (part.equals("..")) {
                if (current.parent != null) current = current.parent;
            } else {
                current.subdirs.putIfAbsent(part, new DirectoryEntry(part, current));
                current = current.subdirs.get(part);
            }
        }

        System.out.println("[OK] directory created: " + path);
    }

    public void createFile(String path) {
        int idx = path.lastIndexOf('/');
        String dirPath = idx == -1 ? "." : path.substring(0, idx);
        String fileName = idx == -1 ? path : path.substring(idx + 1);

        DirectoryEntry dir = getDirectory(dirPath);
        if (dir == null) {
            System.out.println("error: directory not found.");
            return;
        }

        dir.files.put(fileName, new FileEntry(fileName));
        System.out.println("[OK] file created: " + path);
    }

    public void list(String path) {
        DirectoryEntry dir = getDirectory(path);
        if (dir == null) {
            System.out.println("error: directory not found.");
            return;
        }

        System.out.println("Contents of " + dir.getFullPath() + ":");
        for (String d : dir.subdirs.keySet()) {
            System.out.println("[DIR]  " + d);
        }
        for (String f : dir.files.keySet()) {
            System.out.println("[FILE] " + f);
        }
    }

    public void remove(String path) {
        int idx = path.lastIndexOf('/');
        String dirPath = idx == -1 ? "." : path.substring(0, idx);
        String name = idx == -1 ? path : path.substring(idx + 1);

        DirectoryEntry dir = getDirectory(dirPath);
        if (dir == null) {
            System.out.println("error: directory not found.");
            return;
        }

        if (dir.files.remove(name) != null || dir.subdirs.remove(name) != null) {
            System.out.println("[OK] removed: " + path);
        } else {
            System.out.println("error: file or directory not found.");
        }
    }

    public void rename(String path, String newName) {
        int idx = path.lastIndexOf('/');
        String dirPath = idx == -1 ? "." : path.substring(0, idx);
        String name = idx == -1 ? path : path.substring(idx + 1);

        DirectoryEntry dir = getDirectory(dirPath);
        if (dir == null) {
            System.out.println("error: directory not found.");
            return;
        }

        if (dir.files.containsKey(name)) {
            FileEntry file = dir.files.remove(name);
            file.setName(newName);
            dir.files.put(newName, file);
            System.out.println("[OK] file renamed.");
        } else if (dir.subdirs.containsKey(name)) {
            DirectoryEntry subdir = dir.subdirs.remove(name);
            dir.subdirs.put(newName, subdir);
            System.out.println("[OK] directory renamed.");
        } else {
            System.out.println("error: file or directory not found.");
        }
    }

    public void copy(String sourcePath, String destPath) {
    DirectoryEntry source = getDirectory(sourcePath);
    DirectoryEntry destination = getDirectory(destPath);

    if (source == null || destination == null) {
        System.out.println("error: source or destination not found.");
        return;
    }

    DirectoryEntry copy = deepCopy(source, destination);
    destination.subdirs.put(copy.name, copy);

    System.out.println("[OK] copied from " + sourcePath + " to " + destPath);
}

private DirectoryEntry deepCopy(DirectoryEntry original, DirectoryEntry newParent) {
    DirectoryEntry copy = new DirectoryEntry(original.name, newParent);
    for (Map.Entry<String, FileEntry> entry : original.files.entrySet()) {
        copy.files.put(entry.getKey(), new FileEntry(entry.getKey())); // simples
    }
    for (Map.Entry<String, DirectoryEntry> entry : original.subdirs.entrySet()) {
        copy.subdirs.put(entry.getKey(), deepCopy(entry.getValue(), copy));
    }
    return copy;
}


    public DirectoryEntry changeDirectory(String path) {
    if (path.equals("..") && this.parent == null) {
        System.out.println("error: already at root.");
        return this;
    }

    DirectoryEntry target = getDirectory(path);
    if (target == null) {
        System.out.println("cd: no such directory: " + path);
        return null;
    }

    return target;
}



    public String getFullPath() {
        if (parent == null) return "/";
        StringBuilder path = new StringBuilder();
        DirectoryEntry current = this;

        while (current.parent != null) {
            path.insert(0, "/" + current.name);
            current = current.parent;
        }

        return path.toString();
    }
}
