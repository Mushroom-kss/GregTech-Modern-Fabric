package com.gregtechceu.gtceu.data.recipe;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class CustomTags {

    // Added Vanilla tags
    public static final TagKey<Item> TAG_PISTONS = TagUtil.createItemTag("pistons");
    public static final TagKey<Item> GLASS_BLOCKS = TagUtil.createItemTag("glass_blocks");
    public static final TagKey<Item> GLASS_PANES = TagUtil.createItemTag("glass_panes");
    public static final TagKey<Item> SEEDS = TagUtil.createItemTag("seeds");

    // Added Gregtech tags
    public static final TagKey<Item> TRANSISTORS = TagUtil.createModItemTag("transistors");
    public static final TagKey<Item> RESISTORS = TagUtil.createModItemTag("resistors");
    public static final TagKey<Item> CAPACITORS = TagUtil.createModItemTag("capacitors");
    public static final TagKey<Item> DIODES = TagUtil.createModItemTag("diodes");
    public static final TagKey<Item> INDUCTORS = TagUtil.createModItemTag("inductors");

    public static final TagKey<Item> ULV_CIRCUITS = TagUtil.createItemTag("ulv_circuits");
    public static final TagKey<Item> LV_CIRCUITS = TagUtil.createItemTag("lv_circuits");
    public static final TagKey<Item> MV_CIRCUITS = TagUtil.createItemTag("mv_circuits");
    public static final TagKey<Item> HV_CIRCUITS = TagUtil.createItemTag("hv_circuits");
    public static final TagKey<Item> EV_CIRCUITS = TagUtil.createItemTag("ev_circuits");
    public static final TagKey<Item> IV_CIRCUITS = TagUtil.createItemTag("iv_circuits");
    public static final TagKey<Item> LuV_CIRCUITS = TagUtil.createItemTag("luv_circuits");
    public static final TagKey<Item> ZPM_CIRCUITS = TagUtil.createItemTag("zpm_circuits");
    public static final TagKey<Item> UV_CIRCUITS = TagUtil.createItemTag("uv_circuits");
    public static final TagKey<Item> UHV_CIRCUITS = TagUtil.createItemTag("uhv_circuits");
    public static final TagKey<Item> UEV_CIRCUITS = TagUtil.createItemTag("uev_circuits");
    public static final TagKey<Item> UIV_CIRCUITS = TagUtil.createItemTag("uiv_circuits");
    public static final TagKey<Item> UXV_CIRCUITS = TagUtil.createItemTag("uxv_circuits");
    public static final TagKey<Item> OpV_CIRCUITS = TagUtil.createItemTag("opv_circuits");
    public static final TagKey<Item> MAX_CIRCUITS = TagUtil.createItemTag("max_circuits");

    public static final TagKey<Item> ULV_BATTERIES = TagUtil.createItemTag("ulv_batteries");
    public static final TagKey<Item> LV_BATTERIES = TagUtil.createItemTag("lv_batteries");
    public static final TagKey<Item> MV_BATTERIES = TagUtil.createItemTag("mv_batteries");
    public static final TagKey<Item> HV_BATTERIES = TagUtil.createItemTag("hv_batteries");
    public static final TagKey<Item> EV_BATTERIES = TagUtil.createItemTag("ev_batteries");
    public static final TagKey<Item> IV_BATTERIES = TagUtil.createItemTag("iv_batteries");
    public static final TagKey<Item> LuV_BATTERIES = TagUtil.createItemTag("luv_batteries");
    public static final TagKey<Item> ZPM_BATTERIES = TagUtil.createItemTag("zpm_batteries");
    public static final TagKey<Item> UV_BATTERIES = TagUtil.createItemTag("uv_batteries");
    public static final TagKey<Item> UHV_BATTERIES = TagUtil.createItemTag("uhv_batteries");


    // Platform-dependent tags
    public static final TagKey<Item> TAG_WOODEN_CHESTS = TagUtil.createItemTag("chests");

    public static final TagKey<Block> NEEDS_WOOD_TOOL = TagUtil.createUnprefixedTag(BuiltInRegistries.BLOCK, "fabric:needs_tool_level_0");
    public static final TagKey<Block> NEEDS_GOLD_TOOL = TagUtil.createUnprefixedTag(BuiltInRegistries.BLOCK, "fabric:needs_tool_level_0");
    public static final TagKey<Block> NEEDS_NETHERITE_TOOL = TagUtil.createUnprefixedTag(BuiltInRegistries.BLOCK, "fabric:needs_tool_level_4");
    public static final TagKey<Block> NEEDS_NAQ_ALLOY_TOOL = TagUtil.createUnprefixedTag(BuiltInRegistries.BLOCK, "fabric:needs_tool_level_5");
    public static final TagKey<Block> NEEDS_NEUTRONIUM_TOOL = TagUtil.createUnprefixedTag(BuiltInRegistries.BLOCK, "fabric:needs_tool_level_6");

    @SuppressWarnings("unchecked")
    public static final TagKey<Block>[] TOOL_TIERS = new TagKey[] {
            NEEDS_WOOD_TOOL,
            BlockTags.NEEDS_STONE_TOOL,
            BlockTags.NEEDS_IRON_TOOL,
            BlockTags.NEEDS_DIAMOND_TOOL,
            NEEDS_NETHERITE_TOOL,
            NEEDS_NAQ_ALLOY_TOOL,
            NEEDS_NEUTRONIUM_TOOL,
    };

    public static final TagKey<Block> ENDSTONE_ORE_REPLACEABLES = TagUtil.createBlockTag("end_stone_ore_replaceables");
    public static final TagKey<Block> CONCRETE = TagUtil.createBlockTag("concrete");
    public static final TagKey<Block> CONCRETE_POWDER = TagUtil.createBlockTag("concrete_powder");
    public static final TagKey<Block> GLASS_BLOCKS_BLOCK = TagUtil.createBlockTag("glass_blocks", false);
    public static final TagKey<Block> GLASS_PANES_BLOCK = TagUtil.createBlockTag("glass_panes");
    public static final TagKey<Block> CREATE_SEATS = TagUtil.optionalTag(BuiltInRegistries.BLOCK, new ResourceLocation(GTValues.MODID_CREATE, "seats"));
    public static final TagKey<Block> ORE_BLOCKS = TagUtil.createBlockTag("ores");


    public static final TagKey<Biome> IS_SWAMP = TagUtil.createTag(Registries.BIOME, "is_swamp", false);
    public static final TagKey<Biome> IS_SANDY = TagUtil.createModTag(Registries.BIOME, "is_sandy");
    public static final TagKey<Biome> HAS_RUBBER_TREE = TagUtil.createModTag(Registries.BIOME, "has_rubber_tree");


    public static final TagKey<Fluid> STEAM = TagUtil.createFluidTag("steam");
}
