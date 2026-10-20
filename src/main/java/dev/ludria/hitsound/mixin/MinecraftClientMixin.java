package dev.ludria.hitsound.mixin;

import dev.ludria.hitsound.client.HitSoundKeybinds;
import dev.ludria.hitsound.client.HitsoundConfigScreen;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Opens the Hitsounds config screen when the keybind is pressed in-game.
 * The click counter is drained every tick regardless of screen state, so a
 * press never "leaks" into reopening the screen after another screen closes.
 */
@Mixin(Minecraft.class)
public abstract class MinecraftClientMixin {

    @Inject(method = "tick()V", at = @At("TAIL"))
    private void hitsound$handleMenuKeybind(CallbackInfo ci) {
        boolean pressed = HitSoundKeybinds.OPEN_MENU.consumeClick();
        Minecraft self = (Minecraft) (Object) this;
        // 26.2+: screen state lives on Minecraft.gui (Gui), not on Minecraft itself
        if (pressed && self.gui.screen() == null) {
            self.gui.setScreen(new HitsoundConfigScreen());
        }
    }
}