package br.com.gustavo.simuladorfs.file_system_entries;

public class File extends FileSystemEntry {

    public File(String name, Directory parent) {
        super(name, parent);
    }

    @Override
    public boolean isDirectory() {
        return false;
    }
}
