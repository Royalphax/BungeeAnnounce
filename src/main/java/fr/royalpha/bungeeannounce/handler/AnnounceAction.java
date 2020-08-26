package fr.royalpha.bungeeannounce.handler;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author Royalpha
 */
public abstract interface AnnounceAction {
	public abstract void onAction(ProxiedPlayer player, TextComponent message, Integer... optionalTitleArgs);
}
