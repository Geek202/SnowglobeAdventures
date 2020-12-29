package me.geek.tom.snowglobeadventures.world;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.World;
import xyz.nucleoid.fantasy.BubbleWorldHandle;

public class ActiveGlobeBubble {

    private final WorldManager manager;

    private final BubbleWorldHandle handle;
    private final BlockPos globePos;
    private final RegistryKey<World> globeWorld;

    public ActiveGlobeBubble(WorldManager manager, BubbleWorldHandle handle, BlockPos globePos, RegistryKey<World> globeWorld) {
        this.manager = manager;
        this.handle = handle;
        this.globePos = globePos;
        this.globeWorld = globeWorld;
    }

    public void joinPlayer(ServerPlayerEntity player) {
        this.handle.addPlayer(player);
    }

    public void removePlayer(ServerPlayerEntity player) {
        this.handle.removePlayer(player);
        if (this.handle.getPlayers().size() <= 1) {
            this.close();
        }
    }

    public void close() {
        this.manager.worldClosed(this);
        this.handle.getPlayers().forEach(this.handle::removePlayer);
        this.handle.delete();
    }

    public BlockPos getGlobePos() {
        return this.globePos;
    }

    public RegistryKey<World> getGlobeWorld() {
        return this.globeWorld;
    }

    public RegistryKey<World> getBubbleWorld() {
        return this.handle.asWorld().getRegistryKey();
    }
}
