package me.geek.tom.snowglobeadventures;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.geek.tom.snowglobeadventures.world.SnowyWorldChunkGenerator;
import me.geek.tom.snowglobeadventures.world.WorldManager;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static net.minecraft.server.command.CommandManager.literal;

public class SnowglobeAdventures implements ModInitializer {

    public static Logger LOGGER = LogManager.getLogger();

    public static final String MOD_ID = "snowglobe-adventures";
    public static final String MOD_NAME = "Adventures in a Snowglobe";

    public static final WorldManager worldManager = new WorldManager();

    @Override
    public void onInitialize() {
        log(Level.INFO, "Initializing");
        Registration.init();
        Registry.register(Registry.CHUNK_GENERATOR, modIdentifier("snowy_world"), SnowyWorldChunkGenerator.CODEC);
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(
                literal("leave_snowglobe").executes(this::leaveSnowglobe)
        ));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> worldManager.serverStopping());
    }

    private int leaveSnowglobe(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        ServerPlayerEntity player = ctx.getSource().getPlayer();
        worldManager.exitSnowglobe(player);

        return 0;
    }

    public static Identifier modIdentifier(String path) {
        return new Identifier(MOD_ID, path);
    }

    public static void log(Level level, String message){
        LOGGER.log(level, "["+MOD_NAME+"] " + message);
    }
}
