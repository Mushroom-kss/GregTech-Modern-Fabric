package com.gregtechceu.gtceu.client.instance;

import com.gregtechceu.gtceu.common.blockentity.KineticMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.kinetic.IKineticMachine;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
//import com.simibubi.create.foundation.utility.Iterate; TODO update it
import com.simibubi.create.foundation.render.AllInstanceTypes;

import dev.engine_room.flywheel.lib.instance.FlatLit;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.model.Models;

import net.createmod.catnip.data.Iterate;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

/**
 * @author KilaBash
 * @date 2023/4/1
 * @implNote SplitShaftInstance
 */
public class SplitShaftInstance extends KineticBlockEntityVisual<KineticMachineBlockEntity> {

    protected final ArrayList<RotatingInstance> keys;

    public SplitShaftInstance(VisualizationContext ctxt, KineticMachineBlockEntity tile) {

        super(ctxt, tile, 1);
        keys = new ArrayList<>(2);
        float speed = tile.getSpeed();

        Direction.Axis axis = rotationAxis(blockState);
        if (axis != null) {
            for (Direction dir : Iterate.directionsInAxis(axis)) {
                float splitSpeed = speed * (tile.getMetaMachine() instanceof IKineticMachine kineticMachine ? kineticMachine.getRotationSpeedModifier(dir) : 1);
                var tmp = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF));
                keys.add(tmp.createInstance().setup((KineticBlockEntity) tile, splitSpeed));
            }
        }
    }

    @Override
    public void update(float parcialTick) {

        Block block = blockState.getBlock();
        final Direction.Axis boxAxis = ((IRotate) block).getRotationAxis(blockState);
        Direction[] directions = (boxAxis != null ? Iterate.directionsInAxis(boxAxis) : new Direction[]{ Direction.UP, Direction.DOWN });

        for (int i : Iterate.zeroAndOne) {
            keys.get(i).setRotationalSpeed(blockEntity.getSpeed() * (blockEntity.getMetaMachine() instanceof IKineticMachine kineticMachine ? kineticMachine.getRotationSpeedModifier(directions[i]) : 1));
        }
    }

    @Override
    public void updateLight(float x) {
        for (RotatingInstance key : keys) {
            relight(pos, (FlatLit) key);
        }
    }

    @Override
    public void _delete() {
        keys.forEach(Instance::delete);
        keys.clear();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        ;
    }

}
