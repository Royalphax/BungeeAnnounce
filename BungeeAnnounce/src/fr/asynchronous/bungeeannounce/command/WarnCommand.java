package fr.asynchronous.bungeeannounce.command;

import fr.asynchronous.bungeeannounce.BungeeAnnouncePlugin;
import fr.asynchronous.bungeeannounce.handler.AnnounceType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * @author Roytreo28
 */
public class WarnCommand extends Command {
	private BungeeAnnouncePlugin plugin;

	public WarnCommand(BungeeAnnouncePlugin plugin) {
		super("bwarn", "bungeecord.command.announce", new String[] { "bungee:warn" });
		this.plugin = plugin;
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /bwarn <message>"));
			return;
		}
		StringBuilder msgBuilder = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			msgBuilder.append(args[i]).append(" ");
		}
		this.plugin.logSystem.announce(AnnounceType.WARN, sender, msgBuilder.toString().trim());
		this.plugin.send(AnnounceType.WARN, sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null, msgBuilder.toString().trim(), null, true, "");
	}
}
