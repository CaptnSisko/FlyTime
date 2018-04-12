package me.sisko.fly;

import java.util.Iterator;
import java.util.ConcurrentModificationException;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import me.sisko.commands.CommandFly;
import net.md_5.bungee.api.ChatColor;

public class FlyRefresher extends BukkitRunnable  {

	@Override
	public void run() {
		try {
			for (Iterator<Player> it  = Main.flyingPlayers.iterator(); it.hasNext();) {
				Player p = it.next();
				String uuid = p.getUniqueId().toString();
				int timeLeft = PlayerSaver.decrementTime(uuid);
				if (timeLeft == 10) {
					String warning = Main.plugin.getConfig().getString("lang.timeLeftWarning");
					warning = warning.replaceAll("%SECONDS%", timeLeft + "");
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + warning));
				}
				if (timeLeft <= 3 && timeLeft != 0){
					String warning = Main.plugin.getConfig().getString("lang.timeLeftWarning");
					warning = warning.replaceAll("%SECONDS%", timeLeft + "");
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + warning));
				}
				if (timeLeft <= 0) {
					p.setAllowFlight(false);
					p.sendMessage(ChatColor.translateAlternateColorCodes('&',Main.prefix + Main.plugin.getConfig().getString("lang.noFlightTime")));
					Main.flyingPlayers.remove(p);
					CommandFly.safeTp(p);
				} else {
					p.setAllowFlight(true);
				}
			}
		} catch (ConcurrentModificationException e) {}
	}
}
