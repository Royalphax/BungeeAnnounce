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
public class SendTitleCommand extends Command {
	private BungeeAnnouncePlugin plugin;

	public SendTitleCommand(BungeeAnnouncePlugin plugin) {
		super("sendtitle", "bungeecord.command.sendtitle", "bungee:sendtitle");
		this.plugin = plugin;
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length < 4) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /sendtitle <fadeIn> <stay> <fadeOut> <title>"));
			return;
		}
		Integer fadeIn, stay, fadeOut;
		try {
		    fadeIn = Integer.parseInt(args[0])*20;
		    stay = Integer.parseInt(args[1])*20;
		    fadeOut = Integer.parseInt(args[2])*20;
		} catch (NumberFormatException e) {
			sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /sendtitle <fadeIn> <stay> <fadeOut> <title>"));
			return;
		}
		
		StringBuilder titleBuilder = new StringBuilder();
		for (int i = 3; i < args.length; i++) {
			titleBuilder.append(args[i]).append(" ");
		}
		this.plugin.getLoggerSystem().announce(AnnounceType.TITLE, sender, titleBuilder.toString().trim());
		this.plugin.send(AnnounceType.TITLE, sender instanceof ProxiedPlayer ? (ProxiedPlayer) sender : null, titleBuilder.toString().trim(), null, true, "", fadeIn, stay, fadeOut);
	}
}
