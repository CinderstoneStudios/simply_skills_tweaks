package com.cinderstonestudios.simplyskillstweaks.gui;

import com.cinderstonestudios.simplyskillstweaks.config.ConfigManager;
import com.cinderstonestudios.simplyskillstweaks.config.SSTConfig;
import com.stalemated.lib.config.permissions.ClientConfigPermissions;
import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder;
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ConfigScreen {

    public static Screen create(Screen parent) {
        boolean canEditSynced = ClientConfigPermissions.OP_OR_SP.get();

        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("simply_skills_tweaks.config_screen.title"))
                .category(createRighteousHammersCategory(canEditSynced))
                .build()
                .generateScreen(parent);
    }

    private static ConfigCategory createRighteousHammersCategory(boolean canEdit) {
        Supplier<SSTConfig> configSupplier = ConfigManager::getConfig;
        BiConsumer<String, Object> updater = ConfigManager.MANAGER::updateOption;

        return ConfigCategory.createBuilder()
                .name(Text.translatable("simply_skills_tweaks.category.righteous_hammers"))
                .tooltip(Text.translatable("simply_skills_tweaks.category.righteous_hammers.desc"))
                .group(createGeneralGroup(canEdit, configSupplier, updater))
                .group(createPhysicalGroup(canEdit, configSupplier, updater))
                .group(createTankGroup(canEdit, configSupplier, updater))
                .group(createHolyLightningGroup(canEdit, configSupplier, updater))
                .build();
    }

    private static OptionGroup createGeneralGroup(
            boolean canEdit,
            Supplier<SSTConfig> cfg,
            BiConsumer<String, Object> updater
    ) {
        return OptionGroup.createBuilder()
                .name(Text.translatable("simply_skills_tweaks.group.general"))
                .description(OptionDescription.of(Text.translatable("simply_skills_tweaks.group.general.desc")))
                .option(createBooleanOption("toggleNewHammerFormula", canEdit,
                        cfg, c -> c.toggleNewHammerFormula, true, updater))
                .build();
    }

    private static OptionGroup createPhysicalGroup(
            boolean canEdit,
            Supplier<SSTConfig> cfg,
            BiConsumer<String, Object> updater
    ) {
        return OptionGroup.createBuilder()
                .name(Text.translatable("simply_skills_tweaks.group.physical"))
                .description(OptionDescription.of(Text.translatable("simply_skills_tweaks.group.physical.desc")))
                .option(createFloatOption("physicalAttackMultiplier", canEdit, 0.0f, 2.0f, 0.05f,
                        cfg, c -> c.physicalAttackMultiplier, 0.25f, updater))
                .build();
    }

    private static OptionGroup createTankGroup(
            boolean canEdit,
            Supplier<SSTConfig> cfg,
            BiConsumer<String, Object> updater
    ) {
        return OptionGroup.createBuilder()
                .name(Text.translatable("simply_skills_tweaks.group.tank"))
                .description(OptionDescription.of(Text.translatable("simply_skills_tweaks.group.tank.desc")))
                .option(createFloatOption("tankAttackMultiplier", canEdit, 0.0f, 1.0f, 0.01f,
                        cfg, c -> c.tankAttackMultiplier, 0.05f, updater))
                .option(createFloatOption("tankHealthMultiplier", canEdit, 0.0f, 0.25f, 0.005f, "%.3f",
                        cfg, c -> c.tankHealthMultiplier, 0.035f, updater))
                .option(createFloatOption("tankArmorMultiplier", canEdit, 0.0f, 2.0f, 0.05f,
                        cfg, c -> c.tankArmorMultiplier, 0.30f, updater))
                .option(createFloatOption("shieldBonusMultiplier", canEdit, 1.0f, 3.0f, 0.05f,
                        cfg, c -> c.shieldBonusMultiplier, 1.20f, updater))
                .build();
    }

    private static OptionGroup createHolyLightningGroup(
            boolean canEdit,
            Supplier<SSTConfig> cfg,
            BiConsumer<String, Object> updater
    ) {
        return OptionGroup.createBuilder()
                .name(Text.translatable("simply_skills_tweaks.group.holy_lightning"))
                .description(OptionDescription.of(Text.translatable("simply_skills_tweaks.group.holy_lightning.desc")))
                .option(createFloatOption("holyLightningAttackMultiplier", canEdit, 0.0f, 1.0f, 0.01f,
                        cfg, c -> c.holyLightningAttackMultiplier, 0.10f, updater))
                .option(createFloatOption("holyLightningSpellMultiplier", canEdit, 0.0f, 3.0f, 0.05f,
                        cfg, c -> c.holyLightningSpellMultiplier, 0.9f, updater))
                .option(createFloatOption("holyLightningArmorMultiplier", canEdit, 0.0f, 2.0f, 0.05f,
                        cfg, c -> c.holyLightningArmorMultiplier, 0.20f, updater))
                .build();
    }

    private static <C> Option<Boolean> createBooleanOption(
            String configKey,
            boolean canEdit,
            Supplier<C> configSupplier,
            Function<C, Boolean> getter,
            boolean defaultValue,
            BiConsumer<String, Object> updater
    ) {
        String langKey = "simply_skills_tweaks.option." + configKey.replace('.', '_');
        return Option.<Boolean>createBuilder()
                .name(Text.translatable(langKey))
                .description(OptionDescription.of(
                        Text.translatable(langKey + ".desc"),
                        canEdit ? Text.empty() : Text.translatable("simply_skills_tweaks.config_screen.op_required")
                ))
                .binding(
                        defaultValue,
                        () -> getter.apply(configSupplier.get()),
                        val -> updater.accept(configKey, val)
                )
                .controller(TickBoxControllerBuilder::create)
                .available(canEdit)
                .build();
    }

    private static <C> Option<Float> createFloatOption(
            String configKey,
            boolean canEdit,
            float min,
            float max,
            float step,
            Supplier<C> configSupplier,
            Function<C, Float> getter,
            float defaultValue,
            BiConsumer<String, Object> updater
    ) {
        return createFloatOption(configKey, canEdit, min, max, step, "%.2f", configSupplier, getter, defaultValue, updater);
    }

    private static <C> Option<Float> createFloatOption(
            String configKey,
            boolean canEdit,
            float min,
            float max,
            float step,
            String formatPattern,
            Supplier<C> configSupplier,
            Function<C, Float> getter,
            float defaultValue,
            BiConsumer<String, Object> updater
    ) {
        String langKey = "simply_skills_tweaks.option." + configKey.replace('.', '_');
        return Option.<Float>createBuilder()
                .name(Text.translatable(langKey))
                .description(OptionDescription.of(
                        Text.translatable(langKey + ".desc"),
                        canEdit ? Text.empty() : Text.translatable("simply_skills_tweaks.config_screen.op_required")
                ))
                .binding(
                        defaultValue,
                        () -> getter.apply(configSupplier.get()),
                        val -> updater.accept(configKey, val)
                )
                .controller(opt -> FloatSliderControllerBuilder.create(opt)
                        .range(min, max)
                        .step(step)
                        .formatValue(val -> Text.literal(String.format(Locale.ROOT, formatPattern, val)))
                )
                .available(canEdit)
                .build();
    }
}
