package br.com.gustavo.simuladorfs;

import java.time.LocalDateTime;

public class Journal {
    public void log(String operation) {
        System.out.println("[JOURNAL] " + LocalDateTime.now() + " - " + operation);
    }
}
