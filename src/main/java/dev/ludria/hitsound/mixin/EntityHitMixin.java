package dev.ludria.hitsound.mixin;

import dev.ludria.hitsound.HitSound;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class EntityHitMixin {

    @Inject(method = "attack", at = @At("HEAD"))
    private void onAttack(Entity target, CallbackInfo ci) {
        // Skip ServerPlayer so the integrated/dedicated server side doesn't
        // double-play the sound; RemotePlayer.attack is never invoked client-side.
        // The target is passed through so the per-type toggles can gate playback.
        if (target instanceof LivingEntity living && !((Object) this instanceof ServerPlayer)) {
            HitSound.soundManager.playHitSound(living);
        }
    }
}