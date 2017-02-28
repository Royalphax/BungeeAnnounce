package fr.asynchronous.bungeeannounce.command;

import fr.asynchronous.bungeeannounce.BungeeAnnouncePlugin;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * @author Roytreo28
 */
public class MsgCommand extends Command {

	private BungeeAnnouncePlugin plugin;
	private String command;

	public MsgCommand(BungeeAnnouncePlugin plugin, String command) {
		super(command, "", new String[] { "bungee:msg" });
		this.command = command;
		this.plugin = plugin;
	}

	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			if (args.length == 0) {
				sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.command + " <player> <msg>"));
				return;
			}
			String name = args[0];
			if (ProxyServer.getInstance().getPlayer(name) != null) {
				ProxiedPlayer to = ProxyServer.getInstance().getPlayer(name);
				StringBuilder msgBuilder = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					msgBuilder.append(args[i]).append(" ");
				}
				player.sendMessage(new TextComponent(this.plugin.pm_sent.replaceAll("%RECEIVER%", to.getName())
						.replaceAll("%MESSAGE%", msgBuilder.toString().trim())));
				to.sendMessage(new TextComponent(this.plugin.pm_received.replaceAll("%SENDER%", player.getName())
						.replaceAll("%MESSAGE%", msgBuilder.toString().trim())));
				if (to == player) {
					sender.sendMessage(new TextComponent(this.plugin.pm_sender_equals_receiver));
				}
			} else {
				player.sendMessage(new TextComponent(this.plugin.pm_player_not_online.replaceAll("%PLAYER%", name)));
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You need to be a ProxiedPlayer !"));
		}
	}
}
