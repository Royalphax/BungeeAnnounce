package fr.roytreo.bungeeannounce.command;

import fr.roytreo.bungeeannounce.manager.ChannelManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * @author Roytreo28
 */
public class ChannelCommand extends Command {

	public ChannelManager channel;
	
	public ChannelCommand(ChannelManager channel) {
		super(channel.getName().replaceAll(" ", "_").toLowerCase(), channel.getPermission(), channel.getCommand());
		this.channel = channel;
	}

	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			if (args.length == 0) {
				if (this.channel.hasPlayer(player)) {
					this.channel.leftPlayer(player);
				} else {
					this.channel.joinPlayer(player);
				}
			} else {
				if (!this.channel.hasPlayer(player))
					this.channel.joinPlayer(player);
				StringBuilder msgBuilder = new StringBuilder();
				for (int i = 0; i < args.length; i++)
					msgBuilder.append(args[i]).append(" ");
				this.channel.sendMessage(player, msgBuilder.toString().trim());
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You need to be a proxied player !"));
		}
	}
}
