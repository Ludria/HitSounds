package dev.ludria.hitsound;

import dev.ludria.hitsound.config.ConfigHandler;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class SoundManager {
    public static final Identifier HIT_SOUND_ID = Identifier.fromNamespaceAndPath("hitsound", "hit");
    public static SoundEvent HIT_SOUND_EVENT;

    public void registerSounds() {
        // MC 26.x: built-in registries are already frozen when mod initializers run,
        // so the SoundEvent is created unregistered. Client playback resolves the id
        // via assets/hitsound/sounds.json ("hit" -> "default" -> default.ogg).
        HIT_SOUND_EVENT = SoundEvent.createVariableRangeEvent(HIT_SOUND_ID);
    }

    /**
     * Decides whether a hit on the given target should play the sound, according
     * to the per-target-type toggles in the config. Players count as "player",
     * monsters as "hostile", and animals/ambient/water mobs as "passive".
     * Neutral support was removed in 26.2+: MISC mobs (golems, armor stands,
     * ...) and non-mob living entities never play the hit sound.
     */
    public boolean shouldPlayFor(LivingEntity target) {
        if (ConfigHandler.configData == null) {
            return true;
        }
        if (target instanceof Player) {
            return ConfigHandler.configData.playForPlayers;
        }
        if (target instanceof Mob mob) {
            return switch (mob.getType().getCategory()) {
                case MONSTER -> ConfigHandler.configData.playForHostile;
                // Neutral removed: MISC mobs are always silent.
                case MISC -> false;
                default -> ConfigHandler.configData.playForPassive;
            };
        }
        // Neutral removed: non-mob living entities are always silent.
        return false;
    }

    /**
     * Plays the hit sound for the given target, but only if its type is enabled.
     */
    public void playHitSound(LivingEntity target) {
        if (shouldPlayFor(target)) {
            playHitSound();
        }
    }

    public void playHitSound() {
        // Only the physical client has a sound engine.
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.CLIENT) {
            return;
        }
        Minecraft client = Minecraft.getInstance();
        if (client == null) {
            return;
        }
        // Play by raw id: the SoundEngine resolves the id through sounds.json
        // (mod assets or vanilla), so no registered SoundEvent is ever touched.
        float volume = ConfigHandler.configData != null ? ConfigHandler.configData.volume : 1.0F;
        float pitch = ConfigHandler.configData != null ? ConfigHandler.configData.pitch : 1.0F;
        Identifier soundId = HIT_SOUND_ID;
        if (ConfigHandler.configData != null && ConfigHandler.configData.selectedSound != null) {
            Identifier configured = Identifier.tryParse(ConfigHandler.configData.selectedSound);
            if (configured != null) {
                soundId = configured;
            }
        }
        client.getSoundManager().play(new SimpleSoundInstance(
                soundId,
                SoundSource.PLAYERS,
                volume,
                pitch,
                SoundInstance.createUnseededRandom(),
                false,
                0,
                SoundInstance.Attenuation.NONE,
                0.0D, 0.0D, 0.0D,
                true));
    }
}
