package dev.ludria.hitsound;

import dev.ludria.hitsound.util.FileManager;
import net.fabricmc.api.ModInitializer;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import dev.ludria.hitsound.config.ConfigHandler;

public class HitSound implements ModInitializer {
    public static final String MOD_ID = "hitsound";
    public static final Identifier HIT_SOUND_ID = Identifier.fromNamespaceAndPath(MOD_ID, "default");
    public static SoundEvent HIT_SOUND_EVENT = SoundEvent.createVariableRangeEvent(HIT_SOUND_ID);
    public static SoundManager soundManager;

    @Override
    public void onInitialize() {
        // MC 26.x freezes built-in registries before mod entrypoints run, so no
        // Registry.register here. Playback resolves via assets/hitsound/sounds.json.
        System.out.println("Hitsounds has been initialized!");

        FileManager.initialize();
        ConfigHandler.loadConfig();

        soundManager = new SoundManager();
        soundManager.registerSounds();
    }
}
