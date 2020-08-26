package fr.royalpha.bungeeannounce.announcement.warn;

import fr.royalpha.bungeeannounce.handler.AnnounceAction;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author Royalpha
 */
public class WarnAction implements AnnounceAction {

	@Override
	public void onAction(ProxiedPlayer player, TextComponent message, Integer... optionalTitleArgs) {
		player.sendMessage(message);
	}

}
