package com.gregtechceu.gtceu.api.recipe.ingredient;

import net.fabricmc.fabric.api.recipe.v1.ingredient.DefaultCustomIngredients;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * @author KilaBash
 * @date 2023/2/21
 * @implNote NBTIngredient
 */
public class NBTIngredient {
    public static Ingredient createNBTIngredient(ItemStack itemStack) {
        return DefaultCustomIngredients.nbt(Ingredient.of(itemStack), itemStack.getTag(), true);
    }
}
