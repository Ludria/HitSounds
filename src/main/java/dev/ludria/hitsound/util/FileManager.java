package dev.ludria.hitsound.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileManager {

    private static final File HITSOUND_FOLDER = new File(System.getProperty("user.dir"), "hitsound");

    public static void initialize() {
        if (!HITSOUND_FOLDER.exists()) {
            HITSOUND_FOLDER.mkdirs();

            try {
                File defaultSound = new File(HITSOUND_FOLDER, "default.ogg");
                if (!defaultSound.exists()) {
                    defaultSound.createNewFile();
                    try (InputStream in = FileManager.class.getResourceAsStream("/assets/hitsound/sounds/default.ogg");
                         FileOutputStream out = new FileOutputStream(defaultSound)) {
                        if (in == null) {
                            System.err.println("default.ogg didn't found!");
                        } else {
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = in.read(buffer)) != -1) {
                                out.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> getSoundFiles() {
        try (Stream<File> files = Files.list(HITSOUND_FOLDER.toPath()).map(java.nio.file.Path::toFile)) {
            return files.filter(file -> file.getName().endsWith(".ogg"))
                    .map(File::getName)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return List.of();
    }
}
