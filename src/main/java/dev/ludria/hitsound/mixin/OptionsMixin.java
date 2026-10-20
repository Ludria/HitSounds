package dev.ludria.hitsound.mixin;

import dev.ludria.hitsound.client.HitSoundKeybinds;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Options;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

/**
 * Appends the mod's keybind to vanilla's keyMappings array at the tail of the
 * Options constructor. Options.load() runs AFTER the constructor (called from
 * Minecraft), so saved key_ entries in options.txt still apply to our binding.
 */
@Mixin(Options.class)
public abstract class OptionsMixin {

    @Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Ljava/io/File;)V", at = @At("TAIL"))
    private void hitsound$registerMenuKeybind(CallbackInfo ci) {
        Options self = (Options) (Object) this;
        KeyMapping[] old = self.keyMappings;
        KeyMapping[] updated = Arrays.copyOf(old, old.length + 1);
        updated[old.length] = HitSoundKeybinds.OPEN_MENU;
        ((OptionsAccessor) self).setKeyMappings(updated);
    }
}