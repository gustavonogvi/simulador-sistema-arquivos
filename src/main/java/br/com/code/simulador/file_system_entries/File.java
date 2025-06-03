package br.com.code.simulador.file_system_entries;

import br.com.code.simulador.enums.EntryType;
import br.com.code.simulador.utils.Utils;

public class File extends FileSystemEntry {

    public File(String name, Directory parent) {
        super(name, parent);
        this.length = Utils.randInt(100, 99999);
        this.type = EntryType.FILE;
    }
}
