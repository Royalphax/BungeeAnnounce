package fr.roytreo.bungeeannounce.command;

import fr.roytreo.bungeeannounce.BungeeAnnouncePlugin;
import fr.roytreo.bungeeannounce.manager.ConfigManager;
import fr.roytreo.bungeeannounce.manager.MsgManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * @author Roytreo28
 */
public class ReplyCommand extends Command {

	private MsgManager msgManager;

	public ReplyCommand(BungeeAnnouncePlugin plugin, MsgManager msgManager) {
		super("reply", "", "r", "bungee:reply");
		this.msgManager = msgManager;
	}

	public void execute(CommandSender sender, String[] args) {
		if (sender instanceof ProxiedPlayer) {
			ProxiedPlayer player = (ProxiedPlayer) sender;
			if (args.length == 0) {
				sender.sendMessage(new TextComponent(ChatColor.RED + "Usage: /reply <msg>"));
				return;
			}
			if (!this.msgManager.hasReplier(player)) {
				player.sendMessage(new TextComponent(ChatColor.RED + "You don't have any player to reply."));
				return;
			}
			if (this.msgManager.isReplierOnline(player)) {
				ProxiedPlayer to = this.msgManager.getReplier(player);
				StringBuilder msgBuilder = new StringBuilder();
				for (int i = 0; i < args.length; i++)
					msgBuilder.append(args[i]).append(" ");
				if (msgBuilder.toString().trim() == "")
					return;
				this.msgManager.message(player, to, msgBuilder.toString());
			} else {
				player.sendMessage(new TextComponent(ConfigManager.Field.PM_PLAYER_NOT_ONLINE.getString().replaceAll("%PLAYER%", this.msgManager.getReplierName(player))));
			}
		} else {
			sender.sendMessage(new TextComponent(ChatColor.RED + "You need to be a proxied player !"));
		}
	}
}
