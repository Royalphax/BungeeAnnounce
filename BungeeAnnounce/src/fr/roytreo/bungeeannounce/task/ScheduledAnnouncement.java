package fr.roytreo.bungeeannounce.task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.roytreo.bungeeannounce.BungeeAnnouncePlugin;
import fr.roytreo.bungeeannounce.manager.AnnouncementManager;
import net.md_5.bungee.api.config.ServerInfo;

/**
 * @author Roytreo28
 */
public class ScheduledAnnouncement implements Runnable {

	private BungeeAnnouncePlugin plugin;
	private AnnouncementManager announcement;
	private String message;
	private List<ServerInfo> servers;
	private String permission;
	private Integer[] optionalTitleArgs;
	private Boolean allServers;

	public ScheduledAnnouncement(BungeeAnnouncePlugin plugin, AnnouncementManager announcement, String message, List<String> servers, String permission, int delay, int interval, Integer... optionalTitleArgs) {
		this.plugin = plugin;
		this.announcement = announcement;
		this.message = message;
		this.servers = new ArrayList<>();
		this.permission = permission;
		this.optionalTitleArgs = optionalTitleArgs;
		this.allServers = false;
		
		if (servers.isEmpty() || servers.contains("all")) {
			this.allServers = true;
		} else {
			for (String entry : servers) {
				ServerInfo info = plugin.getProxy().getServerInfo(entry);
				if (info != null) {
					this.servers.add(info);
				} else {
					plugin.getLogger().warning("Server \"" + entry + "\" for message \"" + message + "\" doesn't exist!");
					return;
				}
			}
		}
		plugin.getProxy().getScheduler().schedule(plugin, this, delay, interval, TimeUnit.SECONDS);
	}

	@Override
	public void run() {
		AnnouncementManager.sendToServer(this.announcement, this.plugin.getProxy().getConsole(), this.message, (this.allServers ? null : this.servers), false, this.permission, this.optionalTitleArgs);
	}
}
