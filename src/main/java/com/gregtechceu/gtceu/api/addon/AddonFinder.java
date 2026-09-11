package com.gregtechceu.gtceu.api.addon;

import net.fabricmc.loader.api.entrypoint.EntrypointContainer;
import net.fabricmc.loader.api.FabricLoader;

import java.util.List;
import java.util.stream.Collectors;

import lombok.NonNull;

public class AddonFinder {

    protected static List<IGTAddon> cache = null;

    public static List<IGTAddon> getAddons() {
        if (cache == null) {
            cache = getEntrypointInstances("gtceu_addon", IGTAddon.class);
        }

        return cache;
    }


    private static <T> List<T> getEntrypointInstances(String entrypointKey, Class<T> instanceClass) {
        FabricLoader fabricLoader = FabricLoader.getInstance();
        List<EntrypointContainer<T>> pluginContainers = fabricLoader.getEntrypointContainers(entrypointKey, instanceClass);
        return pluginContainers.stream()
                .map((@NonNull EntrypointContainer<T> container) -> container.getEntrypoint())
                .collect(Collectors.toList());
    }
}
