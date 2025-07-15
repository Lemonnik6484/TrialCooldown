package lemonnik.trialcooldown;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.api.ModInitializer;

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
		loadConfig();
		LOGGER.info("TrialCooldown loaded with cooldown: " + CONFIG.cooldown);
	}

	private void loadConfig() {
		try {
			if (!CONFIG_FILE.exists()) {
				CONFIG_FILE.getParentFile().mkdirs();
				CONFIG = new Config();
				saveConfig();
				LOGGER.info("Created default config.");
			} else {
				try (FileReader reader = new FileReader(CONFIG_FILE)) {
					CONFIG = GSON.fromJson(reader, Config.class);
					if (CONFIG == null) CONFIG = new Config();
				}
			}
		} catch (Exception e) {
			LOGGER.error("Failed to load config", e);
			CONFIG = new Config();
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