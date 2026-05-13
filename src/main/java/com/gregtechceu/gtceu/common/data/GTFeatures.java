package com.gregtechceu.gtceu.common.data;


import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.worldgen.modifier.BiomePlacement;
import com.gregtechceu.gtceu.api.data.worldgen.modifier.DimensionFilter;
import com.gregtechceu.gtceu.api.data.worldgen.modifier.FrequencyModifier;
import com.gregtechceu.gtceu.data.recipe.CustomTags;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.GenerationStep;

/**
 * @author KilaBash
 * @date 2023/3/20
 * @implNote GTFeatures
 */
public class GTFeatures {
    public static final ResourceLocation NEW_ORE_VEIN_TOGGLE = GTCEu.id("vein_toggle");
    public static final ResourceLocation NEW_ORE_VEIN_RIDGED = GTCEu.id("vein_ridged");

    public static void init() {
        Object inst = FrequencyModifier.FREQUENCY_MODIFIER; // seemingly useless access to init the class in time
        inst = DimensionFilter.DIMENSION_FILTER;
        inst = BiomePlacement.BIOME_PLACEMENT;
    }

    public static void register() {
        // rubber tree
        BiomeModifications.addFeature(
                ctx -> ctx.hasTag(CustomTags.HAS_RUBBER_TREE),
                GenerationStep.Decoration.VEGETAL_DECORATION,
                GTPlacements.RUBBER_CHECKED
        );
    }

}
