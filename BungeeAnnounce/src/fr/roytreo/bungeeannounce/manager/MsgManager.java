package fr.roytreo.bungeeannounce.manager;

import java.util.HashMap;
import java.util.Map;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MsgManager {

	private Map<String, String> map;
	
	public MsgManager() {
		this.map = new HashMap<>();
	}
	
	public void message(ProxiedPlayer from, ProxiedPlayer to, String message) {
		from.sendMessage(new TextComponent(ConfigurationManager.Field.PM_SENT.getString().replaceAll("%RECEIVER%", to.getName()).replaceAll("%MESSAGE%", message.trim())));
		to.sendMessage(new TextComponent(ConfigurationManager.Field.PM_RECEIVED.getString().replaceAll("%SENDER%", from.getName()).replaceAll("%MESSAGE%", message.trim())));
		if (to == from)
			from.sendMessage(new TextComponent(ConfigurationManager.Field.PM_SENDER_EQUALS_RECEIVER.getString()));
		if (!map.containsKey(to.getName()))
			AnnouncementManager.sendToPlayer(AnnouncementManager.ACTION, null, to, ConfigurationManager.Field.REPLY_INFO.getString().replaceAll("%SENDER%", from.getName()), false);
		if (hasReplier(to))
			map.remove(to.getName());
		map.put(to.getName(), from.getName());
	}
	
	public ProxiedPlayer getReplier(ProxiedPlayer player) {
		return ProxyServer.getInstance().getPlayer(map.get(player.getName()));
	}
	
	public String getReplierName(ProxiedPlayer player) {
		return map.get(player.getName());
	}
	
	public boolean hasReplier(ProxiedPlayer player) {
		return map.containsKey(player.getName());
	}
	
	public boolean isReplierOnline(ProxiedPlayer player) {
		return (hasReplier(player) && getReplier(player) != null && getReplier(player).isConnected());
	}
	
}
