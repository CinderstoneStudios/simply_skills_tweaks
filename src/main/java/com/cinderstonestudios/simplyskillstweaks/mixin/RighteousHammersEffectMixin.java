package com.cinderstonestudios.simplyskillstweaks.mixin;

import com.cinderstonestudios.simplyskillstweaks.skill.RighteousHammersDamage;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.sweenus.simplyskills.effects.RighteousHammersEffect;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RighteousHammersEffect.class)
public abstract class RighteousHammersEffectMixin {

    @WrapOperation(
        method = "lambda$applyUpdateEffect$2",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z"
        )
    )
    private static boolean simply_skills_tweaks$scaleHammerDamage(LivingEntity target, DamageSource source, float amount, Operation<Boolean> original) {
        return original.call(target, source, RighteousHammersDamage.scale(source, amount));
    }
}
