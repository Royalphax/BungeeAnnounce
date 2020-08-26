package fr.royalpha.bungeeannounce.announcement.warn;

import fr.royalpha.bungeeannounce.manager.AnnouncementManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * @author Royalpha
 */
public class WarnCommand extends Command {

	public WarnCommand() {
		super("bwarn", "bungeecord.command.warn", "bungee:warn");
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
		AnnouncementManager.sendToServer(AnnouncementManager.WARN, sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null, msgBuilder.toString().trim(), null, true, "");
	}
}
