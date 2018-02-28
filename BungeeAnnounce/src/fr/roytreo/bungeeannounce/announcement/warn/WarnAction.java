package fr.roytreo.bungeeannounce.announcement.warn;

import fr.roytreo.bungeeannounce.handler.AnnounceAction;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class WarnAction implements AnnounceAction {

	@Override
	public void onAction(ProxiedPlayer player, TextComponent message, Integer... optionalTitleArgs) {
		player.sendMessage(message);
	}

}
