package com.gregtechceu.gtceu.integration.emi.multipage;

import java.util.Comparator;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTMachines;
//import com.lowdragmc.lowdraglib.emi.ModularUIEmiRecipeCategory;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;

public class MultiblockInfoEmiCategory extends EmiRecipeCategory {
    public MultiblockInfoEmiCategory(ResourceLocation id, EmiRenderable icon) {
        super(id, icon);
    }

    public MultiblockInfoEmiCategory(ResourceLocation id, EmiRenderable icon, EmiRenderable simplified) {
        super(id, icon, simplified);
    }

    public MultiblockInfoEmiCategory(ResourceLocation id, EmiRenderable icon, EmiRenderable simplified, Comparator<EmiRecipe> sorter) {
        super(id, icon, simplified, sorter);
    }
    public static final MultiblockInfoEmiCategory CATEGORY = new MultiblockInfoEmiCategory();

    private MultiblockInfoEmiCategory() {
        super(new ResourceLocation(GTCEu.MOD_ID + ":multiblock_info"), EmiStack.of(GTMachines.ELECTRIC_BLAST_FURNACE.getItem()));
    }

    public static void registerDisplays(EmiRegistry registry) {
        GTRegistries.MACHINES.values().stream()
                .filter(MultiblockMachineDefinition.class::isInstance)
                .map(MultiblockMachineDefinition.class::cast)
                .map(MultiblockInfoEmiRecipe::new)
                .forEach(registry::addRecipe);
    }

    public static void registerWorkStations(EmiRegistry registry) {
        for (var definition : GTRegistries.MACHINES.values()) {
            if (definition instanceof MultiblockMachineDefinition multiblockDefinition) {
                registry.addWorkstation(CATEGORY, EmiStack.of(multiblockDefinition.asStack()));
            }
        }
    }

    @Override
    public Component getName() {
        return Component.translatable("gtceu.jei.multiblock_info");
    }
}
