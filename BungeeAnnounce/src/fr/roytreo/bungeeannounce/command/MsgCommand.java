package fr.roytreo.bungeeannounce.command;

import fr.roytreo.bungeeannounce.BungeeAnnouncePlugin;
import fr.roytreo.bungeeannounce.manager.ConfigurationManager;
import fr.roytreo.bungeeannounce.manager.MsgManager;
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

	private String[] commands;
	private MsgManager msgManager;

	public MsgCommand(BungeeAnnouncePlugin plugin, String... commands) {
		super("bungee:msg", "", commands);
		this.commands = commands;
		this.msgManager = new MsgManager();
		plugin.getProxy().getPluginManager().registerCommand(plugin, new ReplyCommand(plugin, this.msgManager));
	}

	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			if (args.length == 0) {
				sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /" + this.commands[0] + " <player> <msg>"));
				return;
			}
			String name = args[0];
			if (ProxyServer.getInstance().getPlayer(name) != null) {
				ProxiedPlayer to = ProxyServer.getInstance().getPlayer(name);
				StringBuilder msgBuilder = new StringBuilder();
				for (int i = 1; i < args.length; i++) {
					msgBuilder.append(args[i]).append(" ");
				}
				if (msgBuilder.toString().trim() == "")
					return;
				player.sendMessage(new TextComponent(ConfigurationManager.Field.PM_SENT.getString().replaceAll("%RECEIVER%", to.getName())
						.replaceAll("%MESSAGE%", msgBuilder.toString().trim())));
				to.sendMessage(new TextComponent(ConfigurationManager.Field.PM_RECEIVED.getString().replaceAll("%SENDER%", player.getName())
						.replaceAll("%MESSAGE%", msgBuilder.toString().trim())));
				if (to == player) {
					sender.sendMessage(new TextComponent(ConfigurationManager.Field.PM_SENDER_EQUALS_RECEIVER.getString()));
				}
				this.msgManager.message(player, to);
			} else {
				player.sendMessage(new TextComponent(ConfigurationManager.Field.PM_PLAYER_NOT_ONLINE.getString().replaceAll("%PLAYER%", name)));
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You need to be a proxied player !"));
		}
	}
}
