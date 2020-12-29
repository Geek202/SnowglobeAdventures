package me.geek.tom.snowglobeadventures;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;

public class SnowglobeAdventuresClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BlockRenderLayerMap.INSTANCE.putBlock(Registration.SNOWGLOBE_BLOCK, RenderLayer.getTranslucent());
    }
}
