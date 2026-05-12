package com.gregtechceu.gtceu.integration.emi.orevein;

import java.util.Comparator;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.worldgen.bedrockfluid.BedrockFluidDefinition;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.common.data.GTItems;
import com.gregtechceu.gtceu.common.data.GTMaterials;
//import com.lowdragmc.lowdraglib.emi.ModularUIEmiRecipeCategory;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.stack.EmiStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;

public class GTBedrockFluidEmiCategory extends EmiRecipeCategory {
    public static final GTBedrockFluidEmiCategory CATEGORY = new GTBedrockFluidEmiCategory();

    public GTBedrockFluidEmiCategory() {
        super(GTCEu.id("bedrock_fluid_diagram"), EmiStack.of(GTMaterials.Oil.getFluid().getBucket().asItem()));
    }

    public GTBedrockFluidEmiCategory(ResourceLocation id, EmiRenderable icon) {
        super(id, icon);
    }

    public GTBedrockFluidEmiCategory(ResourceLocation id, EmiRenderable icon, EmiRenderable simplified) {
        super(id, icon, simplified);
    }

    public GTBedrockFluidEmiCategory(ResourceLocation id, EmiRenderable icon, EmiRenderable simplified, Comparator<EmiRecipe> sorter) {
        super(id, icon, simplified, sorter);
    }

    public static void registerDisplays(EmiRegistry registry) {
        for (BedrockFluidDefinition fluid : GTRegistries.BEDROCK_FLUID_DEFINITIONS) {
            registry.addRecipe(new GTBedrockFluid(fluid));
        }
    }

    public static void registerWorkStations(EmiRegistry registry) {
        registry.addWorkstation(CATEGORY, EmiStack.of(GTItems.PROSPECTOR_LV.asStack()));
        registry.addWorkstation(CATEGORY, EmiStack.of(GTItems.PROSPECTOR_HV.asStack()));
        registry.addWorkstation(CATEGORY, EmiStack.of(GTItems.PROSPECTOR_LUV.asStack()));
    }

    @Override
    public Component getName() {
        return Component.translatable("gtceu.jei.bedrock_fluid_diagram");
    }
}
