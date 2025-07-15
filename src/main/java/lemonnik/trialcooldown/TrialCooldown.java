package lemonnik.trialcooldown;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Path;

public class TrialCooldown implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("trialcooldown");
	public static Config CONFIG;

	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File CONFIG_FILE = new File(Path.of("config", "trialcooldown.json").toFile().getPath());

	@Override
	public void onInitialize() {
		reloadConfig();
		LOGGER.info("TrialCooldown loaded with cooldown: " + CONFIG.cooldown);

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			dispatcher.register(
					CommandManager.literal("trialcooldown")
							.requires(source -> source.hasPermissionLevel(2))
							.then(CommandManager.literal("reload")
									.executes(context -> {
										ServerCommandSource source = context.getSource();

										if (reloadConfig()) {
											source.sendMessage(Text.literal("§aTrialCooldown config reloaded!"));
										} else {
											source.sendMessage(Text.literal("§cFailed to reload TrialCooldown config."));
										}
										return 1;
									})
							)
							.then(CommandManager.literal("set")
									.then(CommandManager.argument("cooldown", IntegerArgumentType.integer(0))
											.executes(context -> {
												int value = IntegerArgumentType.getInteger(context, "cooldown");
												Config.cooldown = value;
												saveConfig();

												context.getSource().sendMessage(Text.literal("§aCooldown set to " + value + " ticks."));
												return 1;
											})
									)
							)
			);
		});
	}

	public boolean reloadConfig() {
		try {
			if (!CONFIG_FILE.exists()) {
				CONFIG = new Config();
				saveConfig();
				return true;
			}
			try (FileReader reader = new FileReader(CONFIG_FILE)) {
				CONFIG = GSON.fromJson(reader, Config.class);
				if (CONFIG == null) CONFIG = new Config();
			}
			return true;
		} catch (Exception e) {
			LOGGER.error("Failed to reload config", e);
			return false;
		}
	}

	private void saveConfig() {
		try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
			writer.write("// Trial Spawner cooldown\n");
			writer.write(GSON.toJson(CONFIG));
		} catch (Exception e) {
			LOGGER.error("Failed to save config", e);
		}
	}
}