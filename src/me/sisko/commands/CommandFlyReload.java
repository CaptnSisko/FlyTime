package me.sisko.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.sisko.fly.Main;

public class CommandFlyReload implements CommandExecutor {
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			if (Main.perms.has(p, "flytime.reload")) {
				Main.plugin.reloadConfig();
				p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + Main.plugin.getConfig().getString("lang.pluginreload")));
			} else p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + Main.plugin.getConfig().getString("lang.noReloadPermissionMessage")));
		} else {
			Main.plugin.reloadConfig();
			Main.plugin.getLogger().info("Config reloaded.");
		}
		return true;
	}
}
