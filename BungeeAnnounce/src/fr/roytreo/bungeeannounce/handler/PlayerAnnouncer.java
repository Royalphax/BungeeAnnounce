package fr.asynchronous.bungeeannounce.handler;

import java.util.ArrayList;
import java.util.List;

import fr.asynchronous.bungeeannounce.BungeeAnnouncePlugin;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author Roytreo28
 */
public class PlayerAnnouncer {

	public static ArrayList<PlayerAnnouncer> playerAnnouncers;
	
	static {
		playerAnnouncers = new ArrayList<>();
	}
	
	private AnnounceType announceType;
	private String message;
	private String playerName;
	private String permission;
	private List<ServerInfo> servers;
	private Integer[] optionalTitleArgs;

	public PlayerAnnouncer(BungeeAnnouncePlugin plugin, String playerName, AnnounceType announceType, String message, List<String> servers, String permission, Integer... optionalTitleArgs) {
		this.playerName = playerName;
		this.announceType = announceType;
		this.message = message;
		this.servers = new ArrayList<>();
		this.permission = permission;
		this.optionalTitleArgs = optionalTitleArgs;
		for (String entry : servers) {
			if (entry.equalsIgnoreCase("all"))
				break;
			ServerInfo info = plugin.getProxy().getServerInfo(entry);
			if (info != null) {
				this.servers.add(info);
			} else {
				plugin.getLogger().warning("Server \"" + entry + "\" for player \"" + playerName + "\" doesn't exist. Anyway, skipping this server.");
				continue;
			}
		}
		playerAnnouncers.add(this);
	}

	public String getPlayerName() {
		return this.playerName;
	}
	
	public AnnounceType getAnnounceType() {
		return this.announceType;
	}
	
	public String getMessage() {
		return this.message;
	}
	
	public List<ServerInfo> getServers() {
		return this.servers;
	}
	
	public String getPermission() {
		return this.permission;
	}
	
	public Integer[] getOptionalTitleArgs() {
		return this.optionalTitleArgs;
	}
	
	public static List<PlayerAnnouncer> getPlayerAnnouncer(ProxiedPlayer player)
	{
		List<PlayerAnnouncer> output = new ArrayList<>();
		for (PlayerAnnouncer playerAnnouncer : playerAnnouncers)
			if (player.getName().equals(playerAnnouncer.getPlayerName()) || player.hasPermission(playerAnnouncer.getPermission()))
				output.add(playerAnnouncer);
		return output;
	}
	
	public static boolean existPlayerAnnouncer(ProxiedPlayer player)
	{
		if (getPlayerAnnouncer(player).isEmpty())
			return false;
		return true;
	}
}
