package fr.roytreo.bungeeannounce.announcement.announce;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AnnounceAction implements fr.roytreo.bungeeannounce.handler.AnnounceAction {

	@Override
	public void onAction(ProxiedPlayer player, TextComponent message, Integer... optionalTitleArgs) {
		player.sendMessage(message);
	}

}
