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
public class SendActionCommand extends Command {
	private BungeeAnnouncePlugin plugin;

	public SendActionCommand(BungeeAnnouncePlugin plugin) {
		super("sendaction", "bungeecord.command.sendaction", new String[] { "bungee:sendaction" });
		this.plugin = plugin;
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
		this.plugin.logSystem.announce(AnnounceType.ACTION, sender, actionBuilder.toString().trim());
		this.plugin.send(AnnounceType.ACTION, sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null, actionBuilder.toString().trim(), null, true, "");
	}
}
