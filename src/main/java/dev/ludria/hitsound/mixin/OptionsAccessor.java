package dev.ludria.hitsound.mixin;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Exposes a setter for the final {@code keyMappings} array so the keybind
 * mixin can append its binding (same approach Fabric API uses).
 */
@Mixin(Options.class)
public interface OptionsAccessor {

    @Mutable
    @Accessor("keyMappings")
    void setKeyMappings(KeyMapping[] keyMappings);
}