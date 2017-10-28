package me.sisko.commands;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sisko.fly.Main;
import me.sisko.fly.PlayerSaver;
import net.md_5.bungee.api.ChatColor;

public class CommandFly implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (Main.perms.has(p, "flytime.unlimitedfly")) {
				if (p.getAllowFlight()) {
					p.setAllowFlight(false);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + Main.plugin.getConfig().getString("lang.disabledUnlimited")));
					safeTp(p);
				} else {
					p.setAllowFlight(true);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + Main.plugin.getConfig().getString("lang.enabledUnlimited")));
				}
			} else if (Main.perms.has(p, "flytime.fly")) {
				if (PlayerSaver.canFly(p.getUniqueId().toString())) {
					if (Main.flyingPlayers.contains(p)) {
						Main.flyingPlayers.remove(p);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + Main.plugin.getConfig().getString("lang.disabled")));
						p.setAllowFlight(false);
						safeTp(p);
					} else {
						Main.flyingPlayers.add(p);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + Main.plugin.getConfig().getString("lang.enabled")));
					}
					int secondsLeft = PlayerSaver.getFlightTime(p.getUniqueId().toString());
					String timeLeft = Main.plugin.getConfig().getString("lang.timeLeft");
					timeLeft = timeLeft.replaceAll("%MINUTES%", secondsLeft / 60 + "");
					timeLeft = timeLeft.replaceAll("%SECONDS%", secondsLeft % 60 + "");
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + timeLeft));
				} else {
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + Main.plugin.getConfig().getString("lang.noFlightTime")));
				}
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + Main.plugin.getConfig().getString("lang.noFlyPermissionMessage")));
			}
			return true;
		} else {
			Main.plugin.getLogger().info("You can't fly from console!");
			return true;
		}
	}
	public static void safeTp(Player p) {
		if (!Main.plugin.getConfig().getBoolean("autoTeleportToGround")) return;
		Location l = p.getLocation();
		p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.prefix + Main.plugin.getConfig().getString("lang.attemptGroundTp")));
		while (l.getBlock().getType().equals(Material.AIR)) {
			l = new Location(p.getWorld(), l.getX(), l.getY() - 1, l.getZ());
			if (l.getY() <= 0) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.prefix + Main.plugin.getConfig().getString("lang.groundTpFail")));
				return;
			}
		}
		p.teleport(new Location(p.getWorld(), l.getX(), l.getY() + 1, l.getZ()));
		p.setFallDistance(0);

	}
}
