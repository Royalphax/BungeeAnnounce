package fr.roytreo.bungeeannounce.handler;

import java.util.ArrayList;
import java.util.List;

import fr.roytreo.bungeeannounce.BungeeAnnouncePlugin;
import fr.roytreo.bungeeannounce.manager.AnnouncementManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.connection.Server;

/**
 * @author Roytreo28
 */
public class PlayerAnnouncer {

	public static ArrayList<PlayerAnnouncer> playerAnnouncers;
	
	static {
		playerAnnouncers = new ArrayList<>();
	}
	
	private AnnouncementManager announcement;
	private String message;
	private String playerName;
	private String permission;
	private List<ServerInfo> requiredServers;
	private List<ServerInfo> broadcastServers;
	private Integer[] optionalTitleArgs;

	public PlayerAnnouncer(BungeeAnnouncePlugin plugin, String playerName, AnnouncementManager announcement, String message, List<String> requiredServers, List<String> broadcastServers, String permission, Integer... optionalTitleArgs) {
		this.playerName = playerName;
		this.announcement = announcement;
		this.message = message;
		this.requiredServers = new ArrayList<>();
		this.broadcastServers = new ArrayList<>();
		this.permission = permission;
		this.optionalTitleArgs = optionalTitleArgs;
		for (String entry : requiredServers) {
			if (entry.equalsIgnoreCase("all")) {
				this.requiredServers.clear();
				break;
			}
			ServerInfo info = plugin.getProxy().getServerInfo(entry);
			if (info != null) {
				this.requiredServers.add(info);
			} else {
				plugin.getLogger().warning("Required server \"" + entry + "\" for player \"" + playerName + "\" doesn't exist. Skipping it ...");
			}
		}
		for (String entry : broadcastServers) {
			if (entry.equalsIgnoreCase("all")) {
				this.broadcastServers.clear();
				break;
			}
			ServerInfo info = plugin.getProxy().getServerInfo(entry);
			if (info != null) {
				this.broadcastServers.add(info);
			} else {
				plugin.getLogger().warning("Broadcast server \"" + entry + "\" for player \"" + playerName + "\" doesn't exist. Skipping it ...");
			}
		}
		playerAnnouncers.add(this);
	}

	public String getPlayerName() {
		return this.playerName;
	}
	
	public AnnouncementManager getAnnouncement() {
		return this.announcement;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public List<ServerInfo> getRequiredServers() {
		return this.requiredServers;
	}
	
	public List<ServerInfo> getBroadcastServers() {
		return this.broadcastServers;
	}
	
	public String getPermission() {
		return this.permission;
	}
	
	public Integer[] getOptionalTitleArgs() {
		return this.optionalTitleArgs;
	}
	
	public static List<PlayerAnnouncer> getAnnouncementList(ProxiedPlayer player, Server connectingServer)
	{
		List<PlayerAnnouncer> output = new ArrayList<>();
		if (connectingServer != null && connectingServer.getInfo() != null)
			for (PlayerAnnouncer playerAnnouncer : playerAnnouncers)
				if ((player.getName().equals(playerAnnouncer.getPlayerName())
						|| player.hasPermission(playerAnnouncer.getPermission())) 
						&& (playerAnnouncer.getRequiredServers().isEmpty() 
								|| playerAnnouncer.getRequiredServers().contains(connectingServer.getInfo())))
					output.add(playerAnnouncer);
		return output;
	}
	
	public static boolean hasAnnouncement(ProxiedPlayer player, Server connectingServer)
	{
		return !getAnnouncementList(player, connectingServer).isEmpty();
	}
}
