package com.gregtechceu.gtceu.data.recipe.misc;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;
import com.gregtechceu.gtceu.api.data.chemical.material.info.MaterialFlags;
import com.gregtechceu.gtceu.api.data.chemical.material.properties.PropertyKey;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.api.recipe.GTRecipeAddon;
import com.lowdragmc.lowdraglib.LDLib;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class CreateRecipeAddon {

    /**
     * @autor Mushroom-net
     * @date 2025/9/23
     * @implNote Registers other mods' recipes for all materials that support it
     */

    public static void init(Consumer<FinishedRecipe> provider) {
        
        for (Material material : GTRegistries.MATERIALS) {
            if (!LDLib.isModLoaded(GTValues.MODID_CREATE) && !LDLib.isModLoaded(GTValues.MODID_CREATE_ADDITION)) {
                break;
            }
            if (LDLib.isModLoaded(GTValues.MODID_CREATE) && material.hasProperty(PropertyKey.INGOT) && material.hasFlag(MaterialFlags.GENERATE_PLATE) && material.getName() != null && !material.getName().isEmpty()) {
                provider.accept(new GTRecipeAddon("create", "pressing", 1, "gtceu", material, "ingot", 1, "gtceu", material, "plate"));
            }
            if (LDLib.isModLoaded(GTValues.MODID_CREATE_ADDITION) && material.hasProperty(PropertyKey.INGOT) && material.hasFlag(MaterialFlags.GENERATE_ROD) && material.getName() != null && !material.getName().isEmpty()) {
                provider.accept(new GTRecipeAddon("createaddition", "rolling", 1, "gtceu", material, "ingot", 2, "gtceu", material, "rod"));
            }
        }
    }
}