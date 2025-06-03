package br.com.code.simulador.file_system_entries;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

import br.com.code.simulador.enums.EntryType;
import br.com.code.simulador.utils.Utils;

public abstract class FileSystemEntry implements Serializable {
    protected String name;
    public Directory parent;
    protected EntryType type;
    protected int length;

    protected Timestamp creationTime;
    protected Timestamp lastWriteTime;

    private final String invalidChars = "[\\\\/|*?<>]";

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

    public int getLength() {
        return length;
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
            return "root/";
        }

        return parent.getPath() + this.name + "/";
    }

    public EntryType getType() {
        return this.type;
    }

    public void setName(String newName) {
        name = newName;
    }

    public boolean isRoot() {
        return this.parent == null;
    }

    public void setLastWriteTime(Timestamp time) {
        this.lastWriteTime = time;
    }

    public void updateLastWriteTime() {
        this.lastWriteTime = getCurrentTimestamp();
    }

    // TODO: Better put on Utils.java?
    protected Timestamp getCurrentTimestamp() {
        Date dataAtual = new Date();
        return new Timestamp(dataAtual.getTime());
    }

}