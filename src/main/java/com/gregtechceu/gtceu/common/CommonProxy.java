package com.gregtechceu.gtceu.common;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.addon.AddonFinder;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconSet;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialIconType;
import com.gregtechceu.gtceu.api.data.tag.TagPrefix;
import com.gregtechceu.gtceu.api.data.worldgen.WorldGenLayers;
import com.gregtechceu.gtceu.api.gui.factory.CoverUIFactory;
import com.gregtechceu.gtceu.api.gui.factory.GTUIEditorFactory;
import com.gregtechceu.gtceu.api.gui.factory.MachineUIFactory;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.*;
import com.gregtechceu.gtceu.common.data.materials.GTFoods;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.data.GregTechDatagen;
import com.lowdragmc.lowdraglib.gui.factory.UIFactory;

public class CommonProxy {

    /**
     * If kjs is loaded, make sure our mod is loaded after it. {@link com.gregtechceu.gtceu.core.mixins.kjs.KubeJSMixin}
     */
    public static void onKubeJSSetup() {
        doinit();
    }

    public static void init() {
        if (!GTCEu.isKubeJSLoaded()) {
            doinit();
        }
    }

    public static void doinit() {
        GTCEu.LOGGER.info("GTCEu common proxy init!");
        ConfigHolder.init();
        GTCEu.initializeHighTier();
        UIFactory.register(MachineUIFactory.INSTANCE);
        UIFactory.register(CoverUIFactory.INSTANCE);
        UIFactory.register(GTUIEditorFactory.INSTANCE);
        GTPlacerTypes.init();
        GTRecipeCapabilities.init();
        GTRecipeConditions.init();
        GTElements.init();
        MaterialIconSet.init();
        MaterialIconType.init();
        GTMaterials.init();
        TagPrefix.init();
        GTSoundEntries.init();
        GTDamageTypes.init();
        GTCompassSections.init();
        GTCompassNodes.init();
        GTCovers.init();
        GTFluids.init();
        GTCreativeModeTabs.init();
        GTBlocks.init();
        GTBlockEntities.init();
        GTRecipeTypes.init();
        GTMachines.init();
        GTFoods.init();
        GTItems.init();
        GregTechDatagen.init();
        AddonFinder.getAddons().forEach(IGTAddon::initializeAddon);
        // fabric exclusive, squeeze this in here to register before stuff is used
        GTRegistries.REGISTRATE.registerRegistrate();
        WorldGenLayers.registerAll();
        GTFeatures.init();
        GTFeatures.register();
        // TODO: ignore useless tags
        // 基于材料属性自动检测并忽略无用的标签
        // 遍历所有材料
        /*for (Material material : GTRegistries.MATERIALS) {
            // 遍历所有标签前缀
            for (TagPrefix tagPrefix : TagPrefix.values()) {
                // 如果标签前缀启用了物品生成，但材料实际上不满足生成条件，则忽略该标签
                if (tagPrefix.doGenerateItem() && !tagPrefix.doGenerateItem(material)) {
                    // 但要排除一些特殊情况，这些材料虽然不满足标准条件但有其他用途
                    // 特别是那些可能在配方中作为输入或输出使用的物品
                    tagPrefix.setIgnored(material);
                    if (!ChemicalHelper.get(tagPrefix, material).isEmpty() || tagPrefix.isIgnored(material)) {
                        // 如果已经有对应的物品存在，则不忽略该标签
                        tagPrefix.removeIgnored(material);
                        continue;
                    }
                }
            }
        }*/
    }
}
