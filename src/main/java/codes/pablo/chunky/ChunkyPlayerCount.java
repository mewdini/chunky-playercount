package codes.pablo.chunky;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import org.popcraft.chunky.api.ChunkyAPI;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;

public class ChunkyPlayerCount implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	private static MinecraftServer server;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		ServerLifecycleEvents.SERVER_STARTING.register(server -> {
            ChunkyPlayerCount.server = server;
        });

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            ServerPlayConnectionEvents.JOIN.register(this::onPlayJoin);
            ServerPlayConnectionEvents.DISCONNECT.register(this::onPlayDisconnect);
        });
	}

	private void onPlayJoin(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
		// TODO pause chunky task, if one is running
	}

	private void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
		if (serverEmpty()) {
			// TODO start chunky task if there is one paused
		}
	}

	// ChunkyAPI chunky = 

	private static MinecraftServer getServer() {
        return server;
    }

	private boolean serverEmpty() {
		return PlayerLookup.all(getServer()).isEmpty();
	}
}
