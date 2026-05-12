package com.gregtechceu.gtceu.common.block;

import com.gregtechceu.gtceu.GTCEu;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * @author KilaBash
 * @date 2023/3/9
 * @implNote BoilerBurnerCasingBlock
 */
public record BoilerBurnerType(@Getter String name, @Getter ResourceLocation bottom, @Getter ResourceLocation top, @Getter ResourceLocation side) {

    public static BoilerBurnerType BRONZE_BURNER = new BoilerBurnerType("bronze_burner", GTCEu.id("block/casings/solid/machine_casing_bronze_plated_bricks")
            , GTCEu.id("block/casings/solid/machine_casing_bronze_plated_bricks")
            , GTCEu.id("block/casings/burner/machine_casing_burner_bronze"));
    public static BoilerBurnerType STEEL_BURNER = new BoilerBurnerType("steel_burner", GTCEu.id("block/casings/solid/machine_casing_solid_steel")
            , GTCEu.id("block/casings/solid/machine_casing_solid_steel")
            , GTCEu.id("block/casings/burner/machine_casing_burner_steel"));
    public static BoilerBurnerType TITANIUM_BURNER = new BoilerBurnerType("titanium_burner", GTCEu.id("block/casings/solid/machine_casing_stable_titanium")
            , GTCEu.id("block/casings/solid/machine_casing_stable_titanium")
            , GTCEu.id("block/casings/burner/machine_casing_burner_titanium"));
    public static BoilerBurnerType TUNGSTENSTEEL_BURNER = new BoilerBurnerType("tungstensteel_burner", GTCEu.id("block/casings/solid/machine_casing_robust_tungstensteel")
            , GTCEu.id("block/casings/solid/machine_casing_robust_tungstensteel")
            , GTCEu.id("block/casings/burner/machine_casing_burner_tungstensteel"));

    @Nonnull
    @Override
    public String toString() {
        return name();
    }
}
