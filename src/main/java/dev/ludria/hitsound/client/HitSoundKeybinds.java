package dev.ludria.hitsound.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

/**
 * Holds the mod's keybindings. Instantiated lazily from the Options mixin the
 * first time vanilla builds its keybind array, so the binding shows up in
 * Options -> Controls -> HitSound and persists in options.txt like any
 * vanilla keybind. Default: H (unbound in vanilla 26.1).
 */
public final class HitSoundKeybinds {

    // Registers a "HitSound" category for the Controls screen.
    // Label resolves via lang key "key.category.hitsound.main".
    public static final KeyMapping.Category CATEGORY =
            KeyMapping.Category.register(Identifier.fromNamespaceAndPath("hitsound", "main"));

    // Label resolves via lang key "key.hitsound.menu".
    // 26.3 removed GLFW in favor of SDL; key codes are now SDL scancodes (H = 11)
    // and InputConstants.Type.KEYSYM was renamed to Type.KEYBOARD.
    public static final KeyMapping OPEN_MENU = new KeyMapping(
            "key.hitsound.menu",
            InputConstants.Type.KEYBOARD,
            11,
            CATEGORY
    );

    private HitSoundKeybinds() {
    }
}