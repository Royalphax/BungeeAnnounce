package fr.roytreo.bungeeannounce.announcement.subtitle;

import fr.roytreo.bungeeannounce.BungeeAnnouncePlugin;
import fr.roytreo.bungeeannounce.handler.AnnounceAction;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class SubtitleAction implements AnnounceAction {

	@Override
	public void onAction(ProxiedPlayer player, TextComponent message, Integer... optionalTitleArgs) {
		Title title = BungeeAnnouncePlugin.getProxyServer().createTitle();
		if (optionalTitleArgs != null && optionalTitleArgs.length >= 3) {
			if (optionalTitleArgs[0] != 0 || optionalTitleArgs[1] != 0 || optionalTitleArgs[2] != 0) {
				title.fadeIn(optionalTitleArgs[0]);
				title.stay(optionalTitleArgs[1]);
				title.fadeOut(optionalTitleArgs[2]);
			}
		}
		title.title(new TextComponent(""));
		title.subTitle(message);
		title.send(player);
	}

}
