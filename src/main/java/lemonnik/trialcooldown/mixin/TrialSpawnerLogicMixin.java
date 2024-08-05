package lemonnik.trialcooldown.mixin;

import net.minecraft.block.spawner.TrialSpawnerLogic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(TrialSpawnerLogic.class)
public class TrialSpawnerLogicMixin {

    @ModifyVariable(
            method = "<init>(Lnet/minecraft/block/spawner/TrialSpawnerConfig;Lnet/minecraft/block/spawner/TrialSpawnerConfig;Lnet/minecraft/block/spawner/TrialSpawnerData;IILnet/minecraft/block/spawner/TrialSpawnerLogic$TrialSpawner;Lnet/minecraft/block/spawner/EntityDetector;Lnet/minecraft/block/spawner/EntityDetector$Selector;)V",
            at = @At("HEAD"),
            ordinal = 0,
            argsOnly = true
    )
    private static int modifyCooldownLength(int cooldownLength) {
        return 18000;
    }
}
