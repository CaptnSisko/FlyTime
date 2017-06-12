package me.sisko.commands;

import java.io.File;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sisko.fly.PlayerSaver;
import me.sisko.uuid.UUIDFetcher;
import net.md_5.bungee.api.ChatColor;
import me.sisko.fly.Main;

public class CommandFlyTime implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (Main.perms.has(p, "flytime.fly")) {
				if (args.length == 0) {
					if (PlayerSaver.canFly(p.getUniqueId().toString())) {
						int secondsLeft = PlayerSaver.getFlightTime(p.getUniqueId().toString());
						String timeLeft = Main.plugin.getConfig().getString("lang.timeLeft");
						timeLeft = timeLeft.replaceAll("%MINUTES%", secondsLeft / 60 + "");
						timeLeft = timeLeft.replaceAll("%SECONDS%", secondsLeft % 60 + "");
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + timeLeft));
					} else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + Main.plugin.getConfig().getString("lang.noFlightTime")));
					}
				} else if (args.length == 1) {
					if (Main.perms.has(p, "flytime.others")) {
						String uuid = UUIDFetcher.getUUID(args[0]);
						if (uuid.equals("notFound")) {
							String error = Main.plugin.getConfig().getString("lang.hasNotFlown");
							error = error.replaceAll("%PLAYER%", args[0]);
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.prefix + error));
						} else if (new File(new File(Main.plugin.getDataFolder(), File.separator + "PlayerData"), File.separator + uuid + ".yml").exists()) {
							int secondsLeft = PlayerSaver.getFlightTime(uuid);
							String timeLeft = Main.plugin.getConfig().getString("lang.timeLeftPlayer");
							timeLeft = timeLeft.replace("%PLAYER%", args[0]);
							timeLeft = timeLeft.replaceAll("%MINUTES%", secondsLeft / 60 + "");
							timeLeft = timeLeft.replaceAll("%SECONDS%", secondsLeft % 60 + "");
							p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + timeLeft));
						} else {
							String error = Main.plugin.getConfig().getString("lang.hasNotFlown");
							error = error.replaceAll("%PLAYER%", args[0]);
							p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.prefix + error));
						}
					} else {
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + Main.plugin.getConfig().getString("lang.noFlyTimePermissionMessage")));
					}
				} else {
					String usage = Main.plugin.getConfig().getString("lang.usage");
					usage = usage.replaceAll("%COMMAND%", "/flytime [player]");
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + usage));
				}
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + Main.plugin.getConfig().getString("lang.noFlyPermissionMessage")));
			}
			return true;
		} else {
			if (args.length == 0) Main.plugin.getLogger().info("Console must use /flytime <player>");
			else if (args.length == 1) {
				String uuid = UUIDFetcher.getUUID(args[0]).toString();
				if (new File(new File(Main.plugin.getDataFolder(), File.separator + "PlayerData"), File.separator + uuid + ".yml").exists()) {
					int secondsLeft = PlayerSaver.getFlightTime(uuid);
					Main.plugin.getLogger().info(args[0] + " has " + secondsLeft/60 + " minutes and " +  secondsLeft%60 + " seconds left.");
				} else {
					Main.plugin.getLogger().info("Player " + args[0] + " has not flown.");
				}
			} else {
				Main.plugin.getLogger().info("Usage: /flytime <player>");
			}
			return true;
		}
	}

}
