package fr.roytreo.bungeeannounce;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.roytreo.bungeeannounce.command.BAReloadCommand;
import fr.roytreo.bungeeannounce.command.ColorcodeCommand;
import fr.roytreo.bungeeannounce.command.ForceBroadcastCommand;
import fr.roytreo.bungeeannounce.command.MsgCommand;
import fr.roytreo.bungeeannounce.handler.Logger;
import fr.roytreo.bungeeannounce.handler.PlayerAnnouncer;
import fr.roytreo.bungeeannounce.manager.AnnouncementManager;
import fr.roytreo.bungeeannounce.manager.ChannelManager;
import fr.roytreo.bungeeannounce.manager.ConfigManager;
import fr.roytreo.bungeeannounce.manager.URLManager;
import fr.roytreo.bungeeannounce.stat.DataRegister;
import fr.roytreo.bungeeannounce.task.ScheduledAnnouncement;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.event.EventHandler;

/**
 * @author Roytreo28
 */
public class BungeeAnnouncePlugin extends Plugin implements Listener {
	
	public static final String user_id = "%%__USER__%%";
	public static final String download_id = "%%__NONCE__%%";
	
	private static BungeeAnnouncePlugin instance;
	private static Logger logSystem;
	
	private Boolean update;
	private final Boolean localhost;
	private ConfigManager configManager;
	private List<ScheduledAnnouncement> scheduledAnnouncement;
	
	public BungeeAnnouncePlugin() {
		this.update = false;
		this.localhost = false;
		this.scheduledAnnouncement = new ArrayList<>();
	}

	@Override
	public void onEnable() {
		instance = this;
		
		/** Load config file **/
		this.configManager = new ConfigManager(this);
		
		/** Initialize the log system **/
		logSystem = new Logger(this);
		
		/** Load config content **/
		this.scheduledAnnouncement = this.configManager.loadScheduledAnnouncement();
		this.configManager.loadAutoPlayerAnnouncement();
		this.configManager.loadChannels();
		
		/** Register commands **/
		PluginManager pM = getProxy().getPluginManager();
		for (AnnouncementManager aM : AnnouncementManager.values())
			pM.registerCommand(this, aM.getCommandClass());
		pM.registerCommand(this, new ForceBroadcastCommand(this));
		pM.registerCommand(this, new BAReloadCommand(this));
		pM.registerCommand(this, new ColorcodeCommand());
		if (ConfigManager.Field.ENABLE_PRIVATE_MESSAGING.getBoolean()) {
			String cmmds = ConfigManager.Field.COMMAND_FOR_PRIVATE_MESSAGING.getString().replaceAll(" ,", ",").replaceAll(", ", ",");
			pM.registerCommand(this, new MsgCommand(this, cmmds.split(",")));
		}
		pM.registerListener(this, this);
		
		/** Check for new Updates **/
		getProxy().getScheduler().runAsync(this, new Runnable() {
			public void run() {
				if (!URLManager.checkVersion(getDescription().getVersion(), false, URLManager.Link.GITHUB_PATH)) {
					getLogger().info("A new version more efficient of the plugin is available. It will be automatically updated when the server will switch off.");
					update = true;
				}
			}
		});
		new DataRegister(instance, localhost, false);
	}
	
	@Override
	public void onDisable() {
		if (this.update) {
			getLogger().info("Stay informed about what the update bring new at https://www.spigotmc.org/resources/bungee-announce-1-8-1-9-1-10.10002/updates");
			URLManager.update(this, URLManager.getLatestVersion(), false, URLManager.Link.GITHUB_PATH);
		}
	}
	
	/**
	 * To do better, the load method should be executed in the onEnable to avoid repeating lines of code. However, since it is only used for the BAReload command, I decided to separate it from the onEnable.
	 */
	public void load() {
		this.configManager = new ConfigManager(this);
		logSystem = new Logger(this);
		
		this.scheduledAnnouncement = this.configManager.loadScheduledAnnouncement();
		this.configManager.loadAutoPlayerAnnouncement();
		
		if (ConfigManager.Field.ENABLE_PRIVATE_MESSAGING.getBoolean())
			getProxy().getPluginManager().registerCommand(this, new MsgCommand(this, ConfigManager.Field.COMMAND_FOR_PRIVATE_MESSAGING.getString()));
	}
	
	@EventHandler
	public void onConnect(final net.md_5.bungee.api.event.ServerConnectedEvent event) {
		final ProxiedPlayer player = event.getPlayer();
		List<PlayerAnnouncer> autoPlayerAnnouncements = PlayerAnnouncer.getAnnouncementList(player, event.getServer());
		if (!autoPlayerAnnouncements.isEmpty()) {
			for (PlayerAnnouncer playerAnnouncer : autoPlayerAnnouncements)
				getProxy().getScheduler().schedule(this, new Runnable() {
					public void run() {
						AnnouncementManager.sendToServer(playerAnnouncer.getAnnouncement(), getProxy().getConsole(), playerAnnouncer.getMessage(), playerAnnouncer.getBroadcastServers(), false, "", playerAnnouncer.getOptionalTitleArgs());
					}
				}, 500, TimeUnit.MILLISECONDS);
		}
	}
	
	@EventHandler
	public void onChat(final net.md_5.bungee.api.event.ChatEvent event) {
		if (!event.isCommand() && event.getSender().isConnected() && event.getSender() instanceof ProxiedPlayer) {
			final ProxiedPlayer player = (ProxiedPlayer) event.getSender();
			final List<ChannelManager> channels = ChannelManager.getPlayerChannels(player);
			if (channels.size() == 1) {
				ChannelManager channel = channels.get(0);
				channel.sendMessage(player, event.getMessage());
				event.setCancelled(true);
			}
		}
	}
	
	public List<ScheduledAnnouncement> getScheduledAnnouncement() {
		return this.scheduledAnnouncement;
	}
	
	public ConfigManager getConfigManager() {
		return this.configManager;
	}
	
	public static ProxyServer getProxyServer() {
		return instance.getProxy();
	}
	
	public static Logger getLoggerSystem() {
		return logSystem;
	}
}
