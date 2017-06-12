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

public class CommandFlyReset implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (Main.perms.has(p, "flytime.reset")) {
				if (args.length != 1) {
					String usage = Main.plugin.getConfig().getString("lang.usage");
					usage = usage.replaceAll("%COMMAND%", "/flyreset <player>");
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + usage));
				}
				else {
					String uuid = UUIDFetcher.getUUID(args[0]);
					if (uuid.equals("notFound")) {
						String error = Main.plugin.getConfig().getString("lang.hasNotFlown");
						error = error.replaceAll("%PLAYER%", args[0]);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.prefix + error));
					} else if(new File(new File(Main.plugin.getDataFolder(), File.separator + "PlayerData"), File.separator + uuid + ".yml").exists()) {
						PlayerSaver.forceResetTime(uuid);
						String reset = Main.plugin.getConfig().getString("lang.flytimereset");
						reset = reset.replaceAll("%PLAYER%", args[0]);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + reset));
					} else {
						String error = Main.plugin.getConfig().getString("lang.hasNotFlown");
						error = error.replaceAll("%PLAYER%", args[0]);
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', Main.prefix + error));
					}
				}
			} else {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + Main.plugin.getConfig().getString("lang.noResetPermissionMessage")));
			}
			return true;
		} else {
			if (args.length != 1) Main.plugin.getLogger().info(ChatColor.RED + "Usage: /flyreset <name>");
			else {
				String uuid = UUIDFetcher.getUUID(args[0]).toString();
				if(new File(new File(Main.plugin.getDataFolder(), File.separator + "PlayerData"), File.separator + uuid + ".yml").exists()) {
					PlayerSaver.forceResetTime(uuid);
					Main.plugin.getLogger().info("Reset fly time for " + args[0]);
				} else Main.plugin.getLogger().info(args[0] + " has not flown.");
			}
			return true;

		}
	}

}
