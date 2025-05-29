package br.com.gustavo.simuladorfs;

import java.io.File;
import java.util.List;

public class BootManager {

    private static final List<String> ROOT_DIRS = List.of("bin", "home", "etc", "usr", "var", "dev", "tmp");

    public static void initialize(File root) {
        if (!root.exists()) {
            root.mkdirs();
            System.out.println("[BOOT] Creating root structure in: " + root.getAbsolutePath());
            ROOT_DIRS.forEach(sub -> {
                File subDir = new File(root, sub);
                if (subDir.mkdirs()) {
                    System.out.println("[BOOT] /" + sub + " initialized");
                }
            });
        }
    }
}
