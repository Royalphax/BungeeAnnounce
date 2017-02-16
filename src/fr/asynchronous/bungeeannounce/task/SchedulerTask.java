package fr.asynchronous.bungeeannounce.task;

import java.util.ArrayList;
import java.util.List;

import fr.asynchronous.bungeeannounce.BungeeAnnouncePlugin;
import fr.asynchronous.bungeeannounce.handler.AnnounceType;
import net.md_5.bungee.api.config.ServerInfo;

/**
 * @author Roytreo28
 */
public class SchedulerTask implements Runnable {

	private BungeeAnnouncePlugin plugin;
	private AnnounceType announceType;
	private String message;
	private List<ServerInfo> servers;
	private String permission;
	private Integer[] optionalTitleArgs;
	private Boolean allServers;

	public SchedulerTask(BungeeAnnouncePlugin plugin, String taskName, AnnounceType announceType, String message, List<String> servers, String permission, Integer... optionalTitleArgs) {
		this.plugin = plugin;
		this.announceType = announceType;
		this.message = message;
		this.servers = new ArrayList<>();
		this.permission = permission;
		this.optionalTitleArgs = optionalTitleArgs;
		this.allServers = false;
		for (String entry : servers) {
			if (entry.equals("all"))
				break;
			ServerInfo info = plugin.getProxy().getServerInfo(entry);
			if (info != null) {
				this.servers.add(info);
			} else {
				plugin.getLogger().warning("Server \"" + entry + "\" for message \"" + taskName + "\" doesn't exist!");
				plugin.messageTask.get(taskName).cancel();
				break;
			}
		}
		if (servers.isEmpty() || servers.contains("all"))
			this.allServers = true;
	}

	@Override
	public void run() {
		this.plugin.send(this.announceType, this.plugin.getProxy().getConsole(), this.message, (this.allServers ? null : this.servers), false, this.permission, this.optionalTitleArgs);
	}
}
