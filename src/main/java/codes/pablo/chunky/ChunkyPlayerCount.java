package codes.pablo.chunky;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.world.World;

import org.popcraft.chunky.api.ChunkyAPI;
import org.popcraft.chunky.ChunkyProvider;

public class ChunkyPlayerCount implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("ChunkyPlayerCount");
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
		getChunkyAPI().pauseTask(getWorldName()); // handles if there is no task to pause
	}

	private void onPlayDisconnect(ServerPlayNetworkHandler handler, MinecraftServer server) {
		if (serverSize() <= 1) { // counts player that just disconnected, race condition?
			getChunkyAPI().continueTask(getWorldName()); // handles if there is no task to continue
		}
	}

	private static MinecraftServer getServer() {
		return server;
	}

	private int serverSize() {
		return PlayerLookup.all(getServer()).size();
	}

	private String getWorldName() {
		return getServer().getWorld(World.OVERWORLD).getRegistryKey().getValue().toString();
	}

	// Chunky needs to be loaded
	private ChunkyAPI getChunkyAPI() {
		return ChunkyProvider.get().getApi();
	}
}
