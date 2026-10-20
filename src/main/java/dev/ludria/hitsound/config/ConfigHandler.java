package dev.ludria.hitsound.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class ConfigHandler {

    public static ConfigData configData;
    private static final File CONFIG_FILE = new File("config/hitsound_config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void loadConfig() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                configData = GSON.fromJson(reader, ConfigData.class);
            } catch (Exception e) {
                // Malformed config must never crash the game - fall back to defaults.
                e.printStackTrace();
                configData = new ConfigData();
            }
        } else {
            configData = new ConfigData();
            saveConfig();
        }
        normalize(configData);
    }

    /**
     * Sanitizes values read from disk: migrates old selectedSound entries (file
     * names like "default.ogg") to the new sound-id scheme and clamps volume and
     * pitch into playable bounds. Fields missing from the file keep their
     * defaults because GSON fills them in from the field initializers.
     */
    private static void normalize(ConfigData data) {
        if (data == null) {
            configData = new ConfigData();
            data = configData;
        }
        if (data.selectedSound == null || !data.selectedSound.contains(":")) {
            data.selectedSound = ConfigData.DEFAULT_SOUND_ID;
        }
        data.volume = Math.max(0.0F, Math.min(data.volume, 1.0F));
        data.pitch = Math.max(0.5F, Math.min(data.pitch, 2.0F));
    }

    public static void saveConfig() {
        try {
            // The config directory does not exist on a fresh install. FileWriter
            // would throw FileNotFoundException (silently caught below) and every
            // setting would be lost on the next launch, so create it first.
            File parent = CONFIG_FILE.getAbsoluteFile().getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                System.err.println("Hitsounds: could not create config directory " + parent);
            }
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(configData, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getSoundFiles() {
        File dir = new File(FabricLoader.getInstance().getGameDir() + "hitsound");
        if (!dir.exists()) {
            dir.mkdirs();

            try {
                File defaultSound = new File(dir, "default.ogg");
                if (!defaultSound.exists()) {
                    defaultSound.createNewFile();

                    try (InputStream in = ConfigHandler.class.getResourceAsStream("/assets/hitsound/sounds/default.ogg");
                         FileOutputStream out = new FileOutputStream(defaultSound)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = in.read(buffer)) != -1) {
                            out.write(buffer, 0, bytesRead);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Arrays.asList(dir.list((dir1, name) -> name.endsWith(".ogg")));
    }

    public static class ConfigData {
        // Sound event id played on hit: "hitsound:hit" (bundled default.ogg) or a
        // vanilla note block instrument such as "minecraft:block.note_block.harp".
        public static final String DEFAULT_SOUND_ID = "hitsound:hit";
        public String selectedSound = DEFAULT_SOUND_ID;
        public float volume = 1.0F;
        // Playback pitch multiplier; 1.0 is the instrument's neutral tone.
        public float pitch = 1.0F;
        // Per-target-type toggles: a hit only plays the sound when the type of
        // the entity being hit is enabled. All on by default. Neutral
        // support was removed in 26.2+: MISC and non-mob living targets
        // never play the hit sound and have no config option.
        public boolean playForPlayers = true;
        public boolean playForHostile = true;
        public boolean playForPassive = true;
    }
}
