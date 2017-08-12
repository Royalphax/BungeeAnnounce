package fr.roytreo.bungeeannounce.manager;

import java.util.HashMap;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class MsgManager {

	public HashMap<String, String> map;
	
	public MsgManager() {
		this.map = new HashMap<>();
	}
	
	public void message(ProxiedPlayer from, ProxiedPlayer to) {
		if (hasReplier(to))
			map.remove(to.getName());
		map.put(to.getName(), from.getName());
	}
	
	public ProxiedPlayer getReplier(ProxiedPlayer player) {
		return ProxyServer.getInstance().getPlayer(map.get(player.getName()));
	}
	
	public boolean hasReplier(ProxiedPlayer player) {
		return map.containsKey(player.getName());
	}
	
	public boolean isReplierOnline(ProxiedPlayer player) {
		return (hasReplier(player) && getReplier(player) != null && getReplier(player).isConnected());
	}
	
}
