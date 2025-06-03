package br.com.code.simulador;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.code.simulador.enums.OperationStatus;
import br.com.code.simulador.managers.PrintManager;

public class Journal {
    public static final String path = "data/journal.txt";

    public void log(String operation) {
        try {
            List<String> linhas = Files.readAllLines(Paths.get(path));
            for (String linha : linhas) {
                System.out.println(linha);
            }
        } catch (IOException e) {
            PrintManager.printInfo("Não foi possível ler o arquivo " + path);
            e.printStackTrace();
        }
    }

    // Modelo
    // [timestamp] [command] [target] [status] [info extra]
    // 2025-06-02T20:30:15 mkdir /docs SUCCESS Created directory
    // 2025-06-02T20:31:02 touch /docs/notes.txt SUCCESS Created file
    // 2025-06-02T20:32:18 rm /docs/notes.txt SUCCESS File deleted
    // 2025-06-02T20:33:00 cd /notfound FAIL Directory does not exist

    public static void writeLine(
            String command,
            String targetPath,
            OperationStatus status,
            String extaInfo) {

        int timeColumnWidth = 20;
        int commandColumnWidth = 9;
        int pathColumnWidth = 10;
        int statusColumnWidth = 10;
        int extraInfoColumnWidth = 11;
        String timestamp = getFormatedTimestamp();

        String lineformat = "%-" + timeColumnWidth +
                "s %-" + commandColumnWidth +
                "s %-" + statusColumnWidth +
                "s %-" + pathColumnWidth +
                "s %-" + extraInfoColumnWidth +
                "s%n";

        String header = String.format(
                lineformat,
                "Hora",
                "Comando",
                "Status",
                "Path",
                "Info");

        String divider = String.format(
                lineformat,
                "-".repeat(4),
                "-".repeat(7),
                "-".repeat(4),
                "-".repeat(5),
                "-".repeat(4));

        String line = String.format(lineformat, timestamp, command, status.name(), targetPath, extaInfo).toLowerCase();

        if (!fileExists()) {
            writeLine(header);
            writeLine(divider);
        }

        writeLine(line);
    }

    private static void writeLine(String line) {
        try (FileWriter writer = new FileWriter(path, true)) {
            writer.write(line);
        } catch (IOException e) {
            PrintManager.printInfo("Não foi possível escrever no arquivo " + path);
            e.printStackTrace();
        }
    }

    public static String getFormatedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm");
        Timestamp ts = new Timestamp(new Date().getTime());
        return sdf.format(ts);
    }

    public static boolean fileExists() {
        File file = new File(path);
        return file.exists() && file.isFile();
    }
}
