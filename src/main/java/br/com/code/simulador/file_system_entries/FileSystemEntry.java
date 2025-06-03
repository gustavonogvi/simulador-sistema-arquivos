package br.com.code.simulador.file_system_entries;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import br.com.code.simulador.utils.Utils;

public abstract class FileSystemEntry implements Serializable {
    protected String name;
    protected Directory parent;

    protected Timestamp creationTime;
    protected Timestamp lastWriteTime;

    public String invalidChars = "[\\\\/|*?<>]";

    protected FileSystemEntry(String name, Directory parent) {
        name = Utils.removeInvalidChars(name.trim());

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome inválido: " + name);
        }

        if (parent == null) {
            throw new IllegalArgumentException("Diretório pai não pode ser nulo para uma entrada não raiz.");
        }

        this.name = name;
        this.parent = parent;
        this.creationTime = getCurrentTimestamp();
        this.lastWriteTime = this.creationTime;
    }

    // Construtor especial. Vai ser usado apenas para criar o root
    protected FileSystemEntry() {
        this.name = "root";
        this.parent = null;
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

    // Easier to call recursively than to modify the path
    public String getPath() {
        if (this.parent == null) {
            return "root";
        }

        return parent.getPath() + this.name + "/";
    }

    public String getType() {
        return isDirectory() ? "directory" : "file";
    }

    public void setName(String newName) {
        name = newName;
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

    // TODO: Better put on Utils.java?
    protected Timestamp getCurrentTimestamp() {
        Date dataAtual = new Date();
        return new Timestamp(dataAtual.getTime());
    }

    public abstract boolean isDirectory();
}