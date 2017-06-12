package me.sisko.fly;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class PlayerSaver {
	private static File getPlayerFile(String uuid) {
		File userdata = new File(Main.plugin.getDataFolder(), File.separator + "PlayerData");
		File f = new File(userdata, File.separator + uuid + ".yml");
		FileConfiguration playerData = YamlConfiguration.loadConfiguration(f);
		if (!f.exists()) {
			try {
				playerData.set("secondsRemaining", Main.plugin.getConfig().getInt("flyTime"));
				playerData.set("resetTime", System.currentTimeMillis() / 1000 + Main.plugin.getConfig().getInt("flyResetTime"));
				playerData.save(f);
			} catch (IOException exception) {
				exception.printStackTrace();
			}
		}
		return f;
	}

	public static void resetTime(String uuid) {
		if (new File(new File(Main.plugin.getDataFolder(), File.separator + "PlayerData"), File.separator + uuid + ".yml").exists()) {
			File playerFile = getPlayerFile(uuid);
			FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
			if (Main.plugin.getConfig().getInt("flyResetTime") > 0 && System.currentTimeMillis() / 1000 >= playerConfig.getInt("resetTime")) {
				Main.plugin.getLogger().info("Resetting fly time for " + uuid);
				try {
					playerConfig.set("secondsRemaining", Main.plugin.getConfig().getInt("flyTime"));
					playerConfig.set("resetTime", System.currentTimeMillis() / 1000 + Main.plugin.getConfig().getInt("flyResetTime"));
					playerConfig.save(playerFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void forceResetTime(String uuid) {
		File playerFile = getPlayerFile(uuid);
		FileConfiguration playerConfig = YamlConfiguration.loadConfiguration(playerFile);
		try {
			playerConfig.set("secondsRemaining", Main.plugin.getConfig().getInt("flyTime"));
			playerConfig.set("resetTime", System.currentTimeMillis() / 1000 + Main.plugin.getConfig().getInt("flyResetTime"));
			playerConfig.save(playerFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
