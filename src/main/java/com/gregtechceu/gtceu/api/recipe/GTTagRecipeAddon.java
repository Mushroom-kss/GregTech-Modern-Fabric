package com.gregtechceu.gtceu.api.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class GTTagRecipeAddon implements FinishedRecipe {
    protected String RecipeModId = "gtceu";
    protected String RecipeType = ""; //necessary
    protected int InputAmount = 0; //necessary
    protected String InputTagId  = "c";
    protected Material InputMaterial = null;
    protected String InputType = ""; //necessary
    protected int OutputAmount = 0; //necessary
    protected String OutputTagId = "c";
    protected Material OutputMaterial = null;
    protected String OutputType = ""; //necessary

    public GTTagRecipeAddon(@Nonnull String RecipeModId, @Nonnull String RecipeType, int InputAmount, String InputTagId, Material InputMaterial, @Nonnull String InputType, int OutputAmount, String OutputTagId, Material OutputMaterial, @Nonnull String OutputType) {
        if (RecipeModId != "" || RecipeModId !=null) {
            this.RecipeModId = RecipeModId;
        }
        this.RecipeType = RecipeType;
        this.InputAmount = InputAmount;
        if(InputTagId != "" || InputTagId != null) {
            this.InputTagId = InputTagId;
        }
        this.InputMaterial = InputMaterial;
        this.InputType = InputType;
        this.OutputAmount = OutputAmount;
        if(OutputTagId != "" || OutputTagId != null) {
            this.OutputTagId = OutputTagId;
        }
        this.OutputMaterial = OutputMaterial;
        this.OutputType = OutputType;
    }

    @Override
    public void serializeRecipeData(@Nonnull JsonObject json) {
        json.addProperty("type", RecipeModId + ":" + RecipeType);
        
        JsonArray ingredients = new JsonArray();
        JsonObject TypeIngredient = new JsonObject();
        TypeIngredient.addProperty("item", InputTagId + ":" + (InputMaterial == null ? "" : (InputMaterial.getName() + "_")) + InputType);
        TypeIngredient.addProperty("count", InputAmount);
        ingredients.add(TypeIngredient);
        
        json.add("ingredients", ingredients);
        JsonArray results = new JsonArray();
        JsonObject result = new JsonObject();
        result.addProperty("item", OutputTagId + ":" + (OutputMaterial == null ? "" : (OutputMaterial.getName() + "_")) + OutputType);
        result.addProperty("count", OutputAmount);
        results.add(result);
        json.add("results", results);
    }

    @Override
    public ResourceLocation getId() {
        return new ResourceLocation(RecipeModId, RecipeType + "/" + (InputMaterial == null ? "" : (InputMaterial.getName() + "_")) + InputType + "_to" + (OutputMaterial == null ? "" : ("_" + OutputMaterial.getName() + "_")) + OutputType);
    }

    @Override
    public RecipeSerializer<?> getType() {
        return RecipeSerializer.SHAPELESS_RECIPE;
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
        return null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
        return null;
    }
}
