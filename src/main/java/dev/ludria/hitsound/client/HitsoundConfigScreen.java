package dev.ludria.hitsound.client;

import dev.ludria.hitsound.HitSound;
import dev.ludria.hitsound.config.ConfigHandler;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.CycleButton;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

/**
 * Standalone config screen (no Cloth Config / ModMenu required).
 * Sound picker + volume/pitch sliders + test button; saves to
 * config/hitsound_config.json on close.
 */
public class HitsoundConfigScreen extends Screen {

    private static final float MIN_PITCH = 0.5F;
    private static final float MAX_PITCH = 2.0F;

    /** One selectable hit sound: a sound event id plus its display name. */
    private record SoundOption(String id, Component label) {
        SoundOption(String id, String translationKey) {
            this(id, Component.translatable(translationKey));
        }
    }

    // The bundled custom sound first, then every vanilla note block instrument
    // (list verified against 26.1 assets). Each option plays at pitch 1.0 (the
    // neutral tone) and can be fine-tuned with the pitch slider.
    private static final List<SoundOption> SOUND_OPTIONS = List.of(
            new SoundOption("hitsound:hit", "hitsound.sound.custom"),
            new SoundOption("minecraft:block.note_block.banjo", "hitsound.sound.banjo"),
            new SoundOption("minecraft:block.note_block.basedrum", "hitsound.sound.basedrum"),
            new SoundOption("minecraft:block.note_block.bass", "hitsound.sound.bass"),
            new SoundOption("minecraft:block.note_block.bell", "hitsound.sound.bell"),
            new SoundOption("minecraft:block.note_block.bit", "hitsound.sound.bit"),
            new SoundOption("minecraft:block.note_block.chime", "hitsound.sound.chime"),
            new SoundOption("minecraft:block.note_block.cow_bell", "hitsound.sound.cow_bell"),
            new SoundOption("minecraft:block.note_block.didgeridoo", "hitsound.sound.didgeridoo"),
            new SoundOption("minecraft:block.note_block.flute", "hitsound.sound.flute"),
            new SoundOption("minecraft:block.note_block.guitar", "hitsound.sound.guitar"),
            new SoundOption("minecraft:block.note_block.harp", "hitsound.sound.harp"),
            new SoundOption("minecraft:block.note_block.hat", "hitsound.sound.hat"),
            new SoundOption("minecraft:block.note_block.iron_xylophone", "hitsound.sound.iron_xylophone"),
            new SoundOption("minecraft:block.note_block.pling", "hitsound.sound.pling"),
            new SoundOption("minecraft:block.note_block.snare", "hitsound.sound.snare"),
            new SoundOption("minecraft:block.note_block.trumpet", "hitsound.sound.trumpet"),
            new SoundOption("minecraft:block.note_block.trumpet_exposed", "hitsound.sound.trumpet_exposed"),
            new SoundOption("minecraft:block.note_block.trumpet_oxidized", "hitsound.sound.trumpet_oxidized"),
            new SoundOption("minecraft:block.note_block.trumpet_weathered", "hitsound.sound.trumpet_weathered"),
            new SoundOption("minecraft:block.note_block.xylophone", "hitsound.sound.xylophone"));

    public HitsoundConfigScreen() {
        super(Component.translatable("hitsound.screen.title"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        ConfigHandler.ConfigData config = ConfigHandler.configData;

        // Title (centered manually; 26.1 StringWidget has no alignCenter)
        int titleWidth = this.font.width(this.title);
        this.addRenderableWidget(new StringWidget(
                centerX - titleWidth / 2, 15, titleWidth, 10, this.title, this.font));

        // Volume slider (0-100%)
        float volume = config != null ? config.volume : 1.0F;
        this.addRenderableWidget(new AbstractSliderButton(
                centerX - 100, 40, 200, 20,
                Component.translatable("hitsound.screen.volume", Math.round(volume * 100.0F)),
                volume) {
            @Override
            protected void updateMessage() {
                this.setMessage(Component.translatable("hitsound.screen.volume", Math.round(this.value * 100.0)));
            }

            @Override
            protected void applyValue() {
                if (ConfigHandler.configData != null) {
                    ConfigHandler.configData.volume = (float) this.value;
                }
            }
        });

        // Sound picker (Custom + all vanilla note block instruments)
        SoundOption current = soundOptionFor(config != null ? config.selectedSound : null);
        this.addRenderableWidget(CycleButton.<SoundOption>builder(SoundOption::label, current)
                .withValues(SOUND_OPTIONS)
                .create(centerX - 100, 65, 200, 20,
                        Component.translatable("hitsound.screen.sound"),
                        (button, option) -> {
                            if (ConfigHandler.configData != null) {
                                ConfigHandler.configData.selectedSound = option.id();
                            }
                        }));

        // Pitch slider (0.5x - 2.0x, 1.0x = neutral tone)
        float pitch = config != null ? config.pitch : 1.0F;
        this.addRenderableWidget(new AbstractSliderButton(
                centerX - 100, 90, 200, 20,
                pitchLabel(pitch), pitchToSlider(pitch)) {
            @Override
            protected void updateMessage() {
                this.setMessage(pitchLabel(sliderToPitch(this.value)));
            }

            @Override
            protected void applyValue() {
                if (ConfigHandler.configData != null) {
                    ConfigHandler.configData.pitch = sliderToPitch(this.value);
                }
            }
        });

        // Per-target-type toggles; a hit only plays when its type is on.
        // Neutral support was removed in 26.2+: golems, armor stands and other
        // MISC/non-mob targets never play the hit sound.
        addToggle("hitsound.toggle.player", centerX - 100, 115, config == null || config.playForPlayers,
                value -> ConfigHandler.configData.playForPlayers = value);
        addToggle("hitsound.toggle.hostile", centerX + 3, 115, config == null || config.playForHostile,
                value -> ConfigHandler.configData.playForHostile = value);
        addToggle("hitsound.toggle.passive", centerX - 100, 137, config == null || config.playForPassive,
                value -> ConfigHandler.configData.playForPassive = value);

        // Test button - plays the current selection regardless of the toggles
        this.addRenderableWidget(Button.builder(Component.translatable("hitsound.screen.test"), button -> {
            if (HitSound.soundManager != null) {
                HitSound.soundManager.playHitSound();
            }
        }).bounds(centerX - 100, 165, 200, 20).build());

        // Done
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, button -> this.onClose())
                .bounds(centerX - 100, this.height - 30, 200, 20).build());
    }

    private static float sliderToPitch(double sliderValue) {
        return (float) (MIN_PITCH + sliderValue * (MAX_PITCH - MIN_PITCH));
    }

    private static double pitchToSlider(float pitch) {
        return (pitch - MIN_PITCH) / (MAX_PITCH - MIN_PITCH);
    }

    private static Component pitchLabel(float pitch) {
        return Component.translatable("hitsound.screen.pitch", String.format("%.2f", pitch));
    }

    private static SoundOption soundOptionFor(String id) {
        if (id != null) {
            for (SoundOption option : SOUND_OPTIONS) {
                if (option.id().equals(id)) {
                    return option;
                }
            }
        }
        return SOUND_OPTIONS.get(0);
    }

    /**
     * Adds a vanilla-style on/off toggle bound to a config field. The click
     * callback writes through to the config immediately so "Test Sound"
     * reflects the current toggles.
     */
    private void addToggle(String labelKey, int x, int y, boolean initial, Consumer<Boolean> setter) {
        this.addRenderableWidget(CycleButton.onOffBuilder(initial)
                .create(x, y, 97, 20, Component.translatable(labelKey),
                        (button, value) -> {
                            if (ConfigHandler.configData != null) {
                                setter.accept(value);
                            }
                        }));
    }

    @Override
    public void onClose() {
        // Covers both the Done button and Esc.
        ConfigHandler.saveConfig();
        super.onClose();
    }
}