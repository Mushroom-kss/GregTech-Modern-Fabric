package com.gregtechceu.gtceu.api.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.gregtechceu.gtceu.GTCEu;

import it.unimi.dsi.fastutil.ints.IntList;

import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredient;
import net.fabricmc.fabric.api.recipe.v1.ingredient.CustomIngredientSerializer;
import net.fabricmc.fabric.impl.recipe.ingredient.CustomIngredientImpl;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SizedIngredient extends Ingredient implements CustomIngredient {
    public static final ResourceLocation TYPE = GTCEu.id("sized");

    protected final int amount;
    protected final Ingredient inner;
    protected ItemStack[] itemStacks = null;

    protected SizedIngredient(Ingredient inner, int amount) {
        super(Stream.empty());
        this.amount = amount;
        this.inner = inner;
    }

    protected SizedIngredient(@NotNull TagKey<Item> tag, int amount) {
        this(Ingredient.of(tag), amount);
    }

    protected SizedIngredient(ItemStack itemStack) {
        this((itemStack.hasTag() || itemStack.getDamageValue() > 0) ? NBTIngredient.createNBTIngredient(itemStack)
                : Ingredient.of(itemStack), itemStack.getCount());
    }

    public static SizedIngredient create(ItemStack inner) {
        return new SizedIngredient(inner);
    }

    public static SizedIngredient create(Ingredient inner, int amount) {
        return new SizedIngredient(inner, amount);
    }

    public static SizedIngredient create(Ingredient inner) {
        if (inner instanceof CustomIngredientImpl customIngredient
                && customIngredient.getCustomIngredient() instanceof SizedIngredient sizedIngredient) {
            return SizedIngredient.copy(sizedIngredient);
        }
        return new SizedIngredient(inner, 1);
    }

    public static SizedIngredient create(TagKey<Item> tag, int amount) {
        return new SizedIngredient(tag, amount);
    }

    public static SizedIngredient copy(Ingredient ingredient) {
        if (ingredient instanceof SizedIngredient sizedIngredient) {
            var copied = SizedIngredient.create(sizedIngredient.inner, sizedIngredient.amount);
            if (sizedIngredient.itemStacks != null) {
                copied.itemStacks = Arrays.stream(sizedIngredient.itemStacks).map(ItemStack::copy)
                        .toArray(ItemStack[]::new);
            }
            return copied;
        }
        return SizedIngredient.create(ingredient);
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public @Nullable SizedIngredient getCustomIngredient() {
        return this;
    }

    public Ingredient getInner() {
        return inner;
    }

    public static SizedIngredient fromJson(JsonObject json) {
        return Serializer.INSTANCE.read(json);
    }

    @Override
    public @NotNull JsonElement toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", TYPE.toString());
        json.addProperty("fabric:type", TYPE.toString());
        json.addProperty("count", amount);
        json.add("ingredient", inner.toJson());
        return json;
    }

    @Override
    public boolean test(@Nullable ItemStack stack) {
        return inner.test(stack);
    }

    @Override
    public ItemStack @NotNull [] getItems() {
        if (itemStacks == null)
            itemStacks = Arrays.stream(inner.getItems()).map(i -> {
                ItemStack ic = i.copy();
                ic.setCount(amount);
                return ic;
            }).toArray(ItemStack[]::new);
        return itemStacks;
    }

    @Override
    public @NotNull IntList getStackingIds() {
        return inner.getStackingIds();
    }

    @Override
    public boolean isEmpty() {
        return inner.isEmpty();
    }

    @Override
    public boolean requiresTesting() {
        return true;
    }

    @Override
    public List<ItemStack> getMatchingStacks() {
        return Arrays.stream(getItems()).toList();
    }

    @Override
    public CustomIngredientSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    public static class Serializer implements CustomIngredientSerializer<SizedIngredient> {

        public static Serializer INSTANCE = new Serializer();

        @Override
        public ResourceLocation getIdentifier() {
            return SizedIngredient.TYPE;
        }

        @Override
        public SizedIngredient read(JsonObject json) {
            int amount = json.get("count").getAsInt();
            JsonElement data = json.get("ingredient");
            Ingredient inner = Ingredient.fromJson(data == null ? JsonNull.INSTANCE : data);
            return new SizedIngredient(inner, amount);
        }

        @Override
        public void write(JsonObject json, SizedIngredient ingredient) {
            json.addProperty("count", ingredient.getAmount());
            json.add("ingredient", ingredient.getInner().toJson());
        }

        @Override
        public SizedIngredient read(FriendlyByteBuf buffer) {
            int amount = buffer.readVarInt();
            return new SizedIngredient(Ingredient.fromNetwork(buffer), amount).getCustomIngredient();
        }

        @Override
        public void write(FriendlyByteBuf buffer, SizedIngredient ingredient) {
            buffer.writeVarInt(ingredient.getAmount());
            ingredient.getInner().toNetwork(buffer);
        }
    }
}
