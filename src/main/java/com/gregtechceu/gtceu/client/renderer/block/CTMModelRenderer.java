package com.gregtechceu.gtceu.client.renderer.block;

import com.google.common.base.Suppliers;
import com.gregtechceu.gtceu.GTCEu;
import com.lowdragmc.lowdraglib.LDLib;
import com.lowdragmc.lowdraglib.client.renderer.impl.IModelRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * @author KilaBash
 * @date 2019/12/7
 * @implNote CTMModelRenderer
 */
public class CTMModelRenderer extends IModelRenderer {
    public static Supplier<Boolean> LOW_PRECISION = Suppliers.memoize(GTCEu::isSodiumRubidiumEmbeddiumLoaded);
    
    // 控制是否启用动态重烘焙，默认为false以提升性能
    private final boolean enableReBake;
    
    public CTMModelRenderer(ResourceLocation modelLocation) {
        this(modelLocation, false);
    }
    
    public CTMModelRenderer(ResourceLocation modelLocation, boolean enableReBake) {
        super(modelLocation);
        this.enableReBake = enableReBake;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public boolean reBakeCustomQuads() {
        // 默认禁用重烘焙以提升性能
        // 只有在明确需要动态纹理更新时才启用
        return enableReBake;
    }

    @Override
    public float reBakeCustomQuadsOffset() {
        // positive value means that the model will be rendered outward, negative value means inward
        //return LOW_PRECISION.get() ? -0.1002f : -0.1001f;
        return 0;
    }
}
