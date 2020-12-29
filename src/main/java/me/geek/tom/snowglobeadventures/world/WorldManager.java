package me.geek.tom.snowglobeadventures.world;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.geek.tom.snowglobeadventures.world.generator.SnowglobeStructurePlacer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameMode;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import xyz.nucleoid.fantasy.BubbleWorldConfig;
import xyz.nucleoid.fantasy.BubbleWorldSpawner;
import xyz.nucleoid.fantasy.Fantasy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class WorldManager {

    private static final Logger LOGGER = LogManager.getLogger();

    private final List<ActiveGlobeBubble> activeBubbles = new ArrayList<>();
    private final Map<Identifier, ActiveGlobeBubble> activeBubblesByWorld = new Object2ObjectOpenHashMap<>();

    public void enterSnowglobe(MinecraftServer server, ServerPlayerEntity player,
                               BlockPos globePos, RegistryKey<World> globeWorld,
                               SnowglobeStructurePlacer structurePlacer) {

        Optional<ActiveGlobeBubble> b = activeBubbles.stream().filter(bbl -> bbl.getGlobePos().equals(globePos)
                && bbl.getGlobeWorld().getValue().equals(globeWorld.getValue())).findFirst();

        if (b.isPresent()) {
            player.sendMessage(new LiteralText("Sending you inside the snowglobe...").formatted(Formatting.GOLD), true);
            b.get().joinPlayer(player);
            return;
        }

        player.sendMessage(new LiteralText("Opening a world for you...").formatted(Formatting.GOLD), true);

        Fantasy.get(server).openBubble(
                new BubbleWorldConfig()
                        .setSpawner(BubbleWorldSpawner.atSurface(0, 0))
                        .setDifficulty(Difficulty.PEACEFUL)
                        .setDefaultGameMode(GameMode.ADVENTURE)
                        .setGameRule(GameRules.DO_DAYLIGHT_CYCLE, false)
                        .setGameRule(GameRules.DO_WEATHER_CYCLE, false)
                        .setGameRule(GameRules.RANDOM_TICK_SPEED, 0)
                        .setRaining(true) // This will be snow as the SnowyWorldChunkGenerator sets the biome to SNOWY_TUNDRA
                        .setGenerator(new SnowyWorldChunkGenerator(server.getRegistryManager().get(Registry.BIOME_KEY), structurePlacer))
        ).handleAsync((handle, t) -> {
            if (t != null) {
                player.sendMessage(new LiteralText("Failed to open a bubble for your world!").formatted(Formatting.RED), true);
                LOGGER.error("Failed to open bubble world!", t);
                return null;
            }
            // /tp @s -213.5 4.5 -134.5 90 15

            ActiveGlobeBubble globeBubble = new ActiveGlobeBubble(this, handle, globePos, globeWorld);
            this.activeBubbles.add(globeBubble);
            this.activeBubblesByWorld.put(globeBubble.getBubbleWorld().getValue(), globeBubble);
            globeBubble.joinPlayer(player);
            player.playSound(SoundEvents.BLOCK_PORTAL_TRAVEL, 1f, 1f);

            return null;
        }, server);
    }

    public void exitSnowglobe(ServerPlayerEntity player) {
        RegistryKey<World> world = player.getServerWorld().getRegistryKey();
        if (!this.activeBubblesByWorld.containsKey(world.getValue())) {
            player.sendMessage(new LiteralText("You aren't in a snowglobe world, so cannot leave!").formatted(Formatting.RED), true);
            return;
        }
        ActiveGlobeBubble bubble = this.activeBubblesByWorld.get(world.getValue());
        assert bubble != null;
        bubble.removePlayer(player);
        player.sendMessage(new LiteralText("You left the snowglobe!").formatted(Formatting.LIGHT_PURPLE), true);
    }

    public void worldClosed(ActiveGlobeBubble bubble) {
        this.activeBubblesByWorld.remove(bubble.getBubbleWorld().getValue());
        this.activeBubbles.remove(bubble);
    }

    public void serverStopping() {
        this.activeBubbles.clear();
        this.activeBubblesByWorld.clear();
    }
}
