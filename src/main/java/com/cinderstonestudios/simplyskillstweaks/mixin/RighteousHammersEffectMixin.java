package com.cinderstonestudios.simplyskillstweaks.mixin;

import com.cinderstonestudios.simplyskillstweaks.util.RighteousHammersDamageHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.sweenus.simplyskills.effects.RighteousHammersEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(RighteousHammersEffect.class)
public abstract class RighteousHammersEffectMixin {

    @Redirect(
        method = "lambda$applyUpdateEffect$2",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/LivingEntity;damage(Lnet/minecraft/entity/damage/DamageSource;F)Z"
        )
    )
    private static boolean redirectRighteousHammersDamage(LivingEntity target, DamageSource source, float amount) {
        float scaledDamage = RighteousHammersDamageHelper.calculateDamage(source, amount);
        return target.damage(source, scaledDamage);
    }
}