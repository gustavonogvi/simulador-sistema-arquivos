package br.com.code.simulador.file_system_entries;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public abstract class FileSystemEntry implements Serializable {
    protected String name;
    protected String path;
    protected Directory parent;

    protected Timestamp creationTime;
    protected Timestamp lastWriteTime;

    protected FileSystemEntry(String name, Directory parent) {
        // O nome não pode ser vazio, nem expaços em branco, nem conter /
        if (name == null || name.trim().isEmpty() || name.contains("/")) {
            throw new IllegalArgumentException("Nome inválido: " + name);
        }

        // O pai não pode ser nulo
        if (parent == null) {
            throw new IllegalArgumentException("Diretório pai não pode ser nulo para uma entrada não raiz.");
        }

        this.name = name;
        this.parent = parent;
        this.path = parent.getPath() + "/" + this.name;
        this.creationTime = getCurrentTimestamp();
        this.lastWriteTime = this.creationTime;
    }

    // Construtor especial. Vai ser usado apenas para criar o root
    protected FileSystemEntry() {
        this.name = "root";
        this.parent = null;
        this.path = this.name;
    }

    public String getName() {
        return name;
    }

    public Directory getParent() {
        return parent;
    }

    public Timestamp getLastWrittenTime() {
        return lastWriteTime;
    }

    public Timestamp getCreationTime() {
        return creationTime;
    }

    public String getPath() {
        return path;
    }

    public String getType() {
        return isDirectory() ? "directory" : "file";
    }

    public boolean isFile() {
        return !isDirectory();
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public void setLastWriteTime(Timestamp time) {
        this.lastWriteTime = time;
    }

    protected Timestamp getCurrentTimestamp() {
        Date dataAtual = new Date();
        return new Timestamp(dataAtual.getTime());
    }

    public abstract boolean isDirectory();
}