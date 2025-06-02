package br.com.code.simulador.managers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import br.com.code.simulador.file_system_entries.Directory;

public class PersistanceManager {

    // Store the root directory in the project directory
    public static boolean save(Directory root) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("data/root.so"))) {
            oos.writeObject(root);
            return true;
        } catch (IOException e) {
            PrintManager.printError("Não foi possível salvar o diretório raiz (root)", "PersistanceManager.save()");
            return false;
        }
    }

    // Load and restore the root directory from the project directory
    public static Directory load() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("data/root.so"))) {
            return (Directory) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

}
