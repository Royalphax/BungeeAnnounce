package fr.roytreo.bungeeannounce.handler;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public abstract interface AnnounceAction {
	public abstract void onAction(ProxiedPlayer player, TextComponent message, Integer... optionalTitleArgs);
}
