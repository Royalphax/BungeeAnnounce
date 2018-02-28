package fr.roytreo.bungeeannounce.announcement.action;

import fr.roytreo.bungeeannounce.manager.AnnouncementManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * @author Roytreo28
 */
public class SendActionCommand extends Command {

	public SendActionCommand() {
		super("sendaction", "bungeecord.command.sendaction", "bungee:sendaction");
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /sendaction <action>"));
			return;
		}
		StringBuilder actionBuilder = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			actionBuilder.append(args[i]).append(" ");
		}
		AnnouncementManager.sendToServer(AnnouncementManager.ACTION, sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null, actionBuilder.toString().trim(), null, true, "");
	}
}
