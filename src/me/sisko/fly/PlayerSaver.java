package me.sisko.fly;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;


@SuppressWarnings("WeakerAccess")
public class PlayerSaver {

	private static void setTimes(FileConfiguration config, File file){
		try {
			config.set("secondsRemaining", Main.plugin.getConfig().getInt("flyTime"));
			config.set("resetTime", System.currentTimeMillis() / 1000 + Main.plugin.getConfig().getInt("flyResetTime"));
			config.save(file);
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}

	private static File getPlayerFile(String uuid) {
		File userdata = new File(Main.plugin.getDataFolder(), File.separator + "PlayerData");
		File playerFile = new File(userdata, File.separator + uuid + ".yml");
		FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
		if (!playerFile.exists()) {
			setTimes(playerData,playerFile);
		}
		return playerFile;
	}

	public static void resetTime(String uuid) {
		if (new File(new File(Main.plugin.getDataFolder(), File.separator + "PlayerData"), File.separator + uuid + ".yml").exists()) {
			File playerFile = getPlayerFile(uuid);
			FileConfiguration playerData = YamlConfiguration.loadConfiguration(playerFile);
			if (Main.plugin.getConfig().getInt("flyResetTime") > 0 && System.currentTimeMillis() / 1000 >= playerData.getInt("resetTime")) {
				Main.plugin.getLogger().info("Resetting fly time for " + uuid);
				setTimes(playerData,playerFile);
			}
		}
	}

	public static void forceResetTime(String uuid) {
		File playerFile = getPlayerFile(uuid);
		FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		setTimes(playerConfig,playerFile);
	}

	public static int decrementTime(String uuid) {
		File playerFile = getPlayerFile(uuid);
		YamlConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		int secondsLeft = YamlConfiguration.loadConfiguration(playerFile).getInt("secondsRemaining");
		if (secondsLeft <= 0) return 0;
		else {
			try {
				playerConfig.set("secondsRemaining", secondsLeft - 1);
				playerConfig.save(playerFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return secondsLeft - 1;
	}

	public static boolean canFly(String uuid) {
		return YamlConfiguration.loadConfiguration(getPlayerFile(uuid)).getInt("secondsRemaining") > 0;
	}

	public static int getFlightTime(String uuid) {
		return YamlConfiguration.loadConfiguration(getPlayerFile(uuid)).getInt("secondsRemaining");
	}
}
