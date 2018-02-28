package fr.roytreo.bungeeannounce.announcement.action;

import fr.roytreo.bungeeannounce.handler.AnnounceAction;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ActionAction implements AnnounceAction {

	@Override
	public void onAction(ProxiedPlayer player, TextComponent message, Integer... optionalTitleArgs) {
		player.sendMessage(ChatMessageType.ACTION_BAR, message);
	}

}
