package br.com.gustavo.simuladorfs;

import java.time.LocalDateTime;

// TODO: Implement
public class Journal {
    public void log(String operation) {
        System.out.println("[JOURNAL] " + LocalDateTime.now() + " - " + operation);
    }
}
