package fr.royalpha.bungeeannounce.command;

import fr.royalpha.bungeeannounce.BungeeAnnouncePlugin;
import fr.royalpha.bungeeannounce.handler.PlayerAnnouncer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * @author Royalpha
 */
public class BAReloadCommand extends Command {

	private BungeeAnnouncePlugin plugin;

	public BAReloadCommand(BungeeAnnouncePlugin plugin) {
		super("ba:reload", "bungeecord.command.reload", "bungee:reload");
		this.plugin = plugin;
	}

	public void execute(CommandSender sender, String[] args) {
		reloadAnnouncement("\u00a77[" + sender.getName() + "]: \u00a7aReloading BungeeAnnounce plugin ...", sender);
		int tasks = plugin.getProxy().getScheduler().cancel(plugin);
		sender.sendMessage(new TextComponent(ChatColor.DARK_GRAY + "> " + ChatColor.RED + tasks + " task" + (tasks > 1 ? "s" : "") +" were cancelled."));
		this.plugin.getScheduledAnnouncement().clear();
		PlayerAnnouncer.playerAnnouncers.clear();
		sender.sendMessage(new TextComponent(ChatColor.DARK_GRAY + "> " + ChatColor.YELLOW + "Loading BungeeAnnounce ..."));
		this.plugin.load();
		sender.sendMessage(new TextComponent(ChatColor.DARK_GRAY + "> " + ChatColor.GREEN + "BungeeAnnounce plugin is now load."));
	}

	public void reloadAnnouncement(String announcement, CommandSender sender) {
		this.plugin.getLogger().info(ChatColor.stripColor(announcement));
	}
}
