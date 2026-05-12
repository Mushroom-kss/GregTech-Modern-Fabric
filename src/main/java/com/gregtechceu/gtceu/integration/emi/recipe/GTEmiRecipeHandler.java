package com.gregtechceu.gtceu.integration.emi.recipe;

import com.lowdragmc.lowdraglib.gui.modular.ModularUIContainer;
import com.lowdragmc.lowdraglib.gui.widget.SlotWidget;
import com.lowdragmc.lowdraglib.jei.IngredientIO;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.Collections;
import java.util.List;

public class GTEmiRecipeHandler implements StandardRecipeHandler<AbstractContainerMenu> {
    @Override
    public List<Slot> getInputSources(AbstractContainerMenu handler) {
        if (handler instanceof ModularUIContainer modularUIContainer) {
            return modularUIContainer.getModularUI().getSlotMap().values().stream()
                    .filter(e -> e.getIngredientIO() == IngredientIO.INPUT || e.isPlayerContainer)
                    .map(SlotWidget::getHandler)
                    .toList();
        }
        return Collections.emptyList();
    }

    @Override
    public List<Slot> getCraftingSlots(AbstractContainerMenu handler){
        if (handler instanceof ModularUIContainer modularUIContainer) {
            return modularUIContainer.getModularUI().getSlotMap().values().stream()
                    .filter(e -> e.getIngredientIO() == IngredientIO.INPUT)
                    .map(SlotWidget::getHandler)
                    .toList();
        }
        return Collections.emptyList();
    }


    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe instanceof GTEmiRecipe;
    }
}
