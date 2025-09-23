package com.gregtechceu.gtceu.api.recipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.gregtechceu.gtceu.api.data.chemical.material.Material;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;

public class GTRecipeAddon implements FinishedRecipe{

    /**
     * @author Mushroom-net
     * @date 2025/9/23
     * @implNote add recipe for given modID.
     */

    protected String RecipeModId = "gtceu";
    protected String RecipeType = ""; //necessary
    protected int InputAmount = 0; //necessary
    protected String InputModId  = "gtceu";
    protected Material InputMaterial = null;
    protected String InputType = ""; //necessary
    protected int OutputAmount = 0; //necessary
    protected String OutputModId = "gtceu";
    protected Material OutputMaterial = null;
    protected String OutputType = ""; //necessary

    public GTRecipeAddon(String RecipeModId, @Nonnull String RecipeType, int InputAmount, String InputModId, Material InputMaterial, @Nonnull String InputType, int OutputAmount, String OutputModId, Material OutputMaterial, @Nonnull String OutputType) {
        if (RecipeModId != "") {
            this.RecipeModId = RecipeModId;
        }
        this.RecipeType = RecipeType;
        this.InputAmount = InputAmount;
        if(InputModId != "") {
            this.InputModId = InputModId;
        }
        this.InputMaterial = InputMaterial;
        this.InputType = InputType;
        this.OutputAmount = OutputAmount;
        if(OutputModId != "") {
            this.OutputModId = OutputModId;
        }
        this.OutputMaterial = OutputMaterial;
        this.OutputType = OutputType;
    }

    @Override
    public void serializeRecipeData(@Nonnull JsonObject json) {
        json.addProperty("type", RecipeModId + ":" + RecipeType);
        
        JsonArray ingredients = new JsonArray();
        JsonObject TypeIngredient = new JsonObject();
        TypeIngredient.addProperty("item", InputModId + ":" + (InputMaterial == null ? "" : (InputMaterial.getName() + "_")) + InputType);
        TypeIngredient.addProperty("count", InputAmount);
        ingredients.add(TypeIngredient);
        
        json.add("ingredients", ingredients);
        JsonArray results = new JsonArray();
        JsonObject result = new JsonObject();
        result.addProperty("item", OutputModId + ":" + (OutputMaterial == null ? "" : (OutputMaterial.getName() + "_")) + OutputType);
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