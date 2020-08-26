package fr.royalpha.bungeeannounce.announcement.announce;

import fr.royalpha.bungeeannounce.manager.AnnouncementManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * @author Royalpha
 */
public class AnnounceCommand extends Command {
	
	public AnnounceCommand() {
		super("announce", "bungeecord.command.announce", "bungee:announce");
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
		AnnouncementManager.sendToServer(AnnouncementManager.ANNOUNCE, sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null, announceBuilder.toString().trim(), null, true, "");
	}
}
