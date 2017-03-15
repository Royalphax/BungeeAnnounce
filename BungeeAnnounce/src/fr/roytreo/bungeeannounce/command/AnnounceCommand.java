package fr.roytreo.bungeeannounce.command;

import fr.roytreo.bungeeannounce.BungeeAnnouncePlugin;
import fr.roytreo.bungeeannounce.handler.AnnounceType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * @author Roytreo28
 */
public class AnnounceCommand extends Command {
	private BungeeAnnouncePlugin plugin;

	public AnnounceCommand(BungeeAnnouncePlugin plugin) {
		super("announce", "bungeecord.command.announce", new String[] { "bungee:announce" });
		this.plugin = plugin;
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /announce <message>"));
			return;
		}
		StringBuilder announceBuilder = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			announceBuilder.append(args[i]).append(" ");
		}
		this.plugin.logSystem.announce(AnnounceType.ANNOUNCEMENT, sender, announceBuilder.toString().trim());
		this.plugin.send(AnnounceType.ANNOUNCEMENT, sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null, announceBuilder.toString().trim(), null, true, "");
	}
}
