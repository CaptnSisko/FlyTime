package me.sisko.fly;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.mcstats.Metrics;

import me.sisko.commands.*;
import net.milkbowl.vault.permission.Permission;

public class Main extends JavaPlugin implements Listener {
	public static Main plugin;
	public static Permission perms;
	public static ArrayList<Player> flyingPlayers = new ArrayList<Player>();
	public static String prefix = "&3[&bFlyTime&3]&r ";

	@Override
	public void onEnable() {
		if(getServer().getPluginManager().getPlugin("Vault") == null) {
			getLogger().severe("Vault not found! Disabling...");
			getServer().getPluginManager().disablePlugin(this);
		}
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		plugin = this;

		getCommand("fly").setExecutor(new CommandFly());
		getCommand("flytime").setExecutor(new CommandFlyTime());
		getCommand("flyreset").setExecutor(new CommandFlyReset());
		getCommand("flyreload").setExecutor(new CommandFlyReload());

		getConfig().addDefault("flyTime", 1800);
		getConfig().addDefault("flyResetTime", 86400);
		getConfig().addDefault("teleportOnLogout", true);
		getConfig().addDefault("autoTeleportToGround", true);
		getConfig().addDefault("lang.noFlyPermissionMessage", "&cYou can't fly!");
		getConfig().addDefault("lang.noFlyTimePermissionMessage", "&cYou don't have permission to check others' fly time!");
		getConfig().addDefault("lang.noResetPermissionMessage", "&cYou don't have permission to reset fly times!");
		getConfig().addDefault("lang.noReloadPermissionMessage", "&cYou don't have permission to reload FlyTimes!");
		getConfig().addDefault("lang.timeLeft", "&aYou have %MINUTES% minutes and %SECONDS% seconds of flight remaining.");
		getConfig().addDefault("lang.timeLeftPlayer", "&a%PLAYER% has %MINUTES% minutes and %SECONDS% seconds of flight remaining.");
		getConfig().addDefault("lang.timeLeftWarning", "&cWarning: %SECONDS% of flight remaining");
		getConfig().addDefault("lang.enabled", "&aFlight enabled.");
		getConfig().addDefault("lang.enabledUnlimited", "&aUnlimited flight enabled.");
		getConfig().addDefault("lang.disabled", "&cFlight disabled.");
		getConfig().addDefault("lang.disabledUnlimited", "&cUnlimited flight disabled.");
		getConfig().addDefault("lang.noFlightTime", "&cYou're out of flight time!");
		getConfig().addDefault("lang.hasNotFlown", "&c%PLAYER% has not flown");
		getConfig().addDefault("lang.usage", "&cUsage: %COMMAND%");
		getConfig().addDefault("lang.flytimereset", "&aReset fly time for %PLAYER%");
		getConfig().addDefault("lang.pluginreload", "&aFlyTime reloaded!");
		getConfig().addDefault("lang.attemptGroundTp", "&aAttempting to teleport you to the ground safely...");
		getConfig().addDefault("lang.groundTpFail", "&cCouldn't find a safe landing spot!");



		if (!getDataFolder().exists()) getDataFolder().mkdirs();
		if (!new File(getDataFolder(), "config.yml").exists()) { 
			getLogger().info("Config.yml not found, creating!");
			getConfig().options().copyDefaults(true);
			saveConfig();
		} else {
			getLogger().info("Config.yml found, loading!");
		}
		new FlyRefresher().runTaskTimer(this, 0, 20);
		getServer().getPluginManager().registerEvents(this, this);
		
	    try {
	        Metrics metrics = new Metrics(this);
	        metrics.start();
	    } catch (IOException e) {
	    	getLogger().warning("Could not connect to pluginmetrics server!");
	    }
	}

	@Override
	public void onDisable() {
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		PlayerSaver.resetTime(e.getPlayer().getUniqueId().toString());
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		flyingPlayers.remove(p);
		p.setAllowFlight(false);
		if (getConfig().getBoolean("teleportOnLogout")) {
			Location l = p.getLocation();
			while (l.getBlock().getType().equals(Material.AIR)) {
				l = new Location(p.getWorld(), l.getX(), l.getY() - 1, l.getZ());
				if (l.getY() <= 0) return;
			}
			p.teleport(new Location(p.getWorld(), l.getX(), l.getY() + 1, l.getZ()));
			p.setFallDistance(0);
		}
	}
}
