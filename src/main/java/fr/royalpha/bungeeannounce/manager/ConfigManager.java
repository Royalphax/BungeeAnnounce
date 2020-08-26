package fr.royalpha.bungeeannounce.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.royalpha.bungeeannounce.BungeeAnnouncePlugin;
import fr.royalpha.bungeeannounce.task.ScheduledAnnouncement;
import fr.royalpha.bungeeannounce.util.BAUtils;
import fr.royalpha.bungeeannounce.handler.PlayerAnnouncer;
import fr.royalpha.bungeeannounce.handler.PlayerAnnouncer.ConnectionType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

/**
 * @author Royalpha
 */
public class ConfigManager {
	
	private BungeeAnnouncePlugin plugin;
	private Configuration config;
	private Configuration channelConfig;

	public ConfigManager(BungeeAnnouncePlugin plugin) {
		this.plugin = plugin;
		if (!plugin.getDataFolder().exists())
			plugin.getDataFolder().mkdirs();
		File file = new File(plugin.getDataFolder(), "config.yml");
		if (!file.exists()) {
			getLogger().info("Thanks for using BungeeAnnounce from Asynchronous. Don't forget to review it !");
			getLogger().info("We are a team of developers and we would really appreciate if you could follow our twitter page where we post news about our plugins <3 https://twitter.com/AsyncDevTeam");
			getLogger().info("Generating configuration file ...");
			try (InputStream in = plugin.getResourceAsStream("config.yml")) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				new ExceptionManager(e).register(plugin, true);
				getLogger().warning("Error when generating configuration file !");
			} finally {
				getLogger().info("Configuration file was generated with success !");
			}
		}
		try {
			this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException e) {
			new ExceptionManager(e).register(plugin, true);
		}
		// Channel config file
		File channelsFile = new File(plugin.getDataFolder(), "channels.yml");
		if (!channelsFile.exists()) {
			try (InputStream in = plugin.getResourceAsStream("channels.yml")) {
				Files.copy(in, channelsFile.toPath());
			} catch (IOException e) {
				new ExceptionManager(e).register(plugin, true);
				getLogger().warning("Error when generating channels configuration file !");
			} finally {
				getLogger().info("Channels configuration file was generated with success !");
			}
		}
		try {
			this.channelConfig = ConfigurationProvider.getProvider(YamlConfiguration.class).load(channelsFile);
		} catch (IOException e) {
			new ExceptionManager(e).register(plugin, true);
		}
		Field.init(this.config);
	}
	
	public void loadChannels() {
		if (!this.channelConfig.getBoolean("enable-channels"))
			return;
		ChannelManager.setTipMessage(this.channelConfig.getString("multi-channels-tip"));
		Configuration channelsSection = this.channelConfig.getSection("channels");
		for (String channelName : channelsSection.getKeys()) {
			String permission = channelsSection.getString(channelName + ".permission", "");
			String command = channelsSection.getString(channelName + ".toggle-command", "");
			String format = channelsSection.getString(channelName + ".format", "");
			String description = channelsSection.getString(channelName + ".description", "");
			String onJoin = channelsSection.getString(channelName + ".on-join", "");
			String onLeft = channelsSection.getString(channelName + ".on-quit", "");
			new ChannelManager(this.plugin, channelName, permission, command, description, format, onJoin, onLeft);
		}
	}
	
	public List<ScheduledAnnouncement> loadScheduledAnnouncement() {
		List<ScheduledAnnouncement> output = new ArrayList<>();
		int i = 0;
		
		Configuration schedulerSection = this.config.getSection("scheduler");
		for (String taskName : schedulerSection.getKeys()) {
			try {
				String type = schedulerSection.getString(taskName + ".type", "");
				
				AnnouncementManager announcement = AnnouncementManager.getAnnouncement(type);
				if (announcement == null) {
					getLogger().log(Level.WARNING, "Error when loading announcement \"%s\", the field 'type' wasn't recognized.", taskName);
					continue;
				}
				
				String message = schedulerSection.getString(taskName + ".message", "<No message was set for this announcement>");
				List<String> servers = schedulerSection.getStringList(taskName + ".servers");
				String permission = schedulerSection.getString(taskName + ".permission", "");
				int delay = schedulerSection.getInt(taskName + ".delay", 5);
				int interval = schedulerSection.getInt(taskName + ".interval", 10);
				Integer[] optionalTitleArgs = BAUtils.getOptionalTitleArgsFromConfig(announcement, type);
			
				if (interval < 0) {
					getLogger().info("The scheduled announcement \"" + taskName + "\" has a negative interval. So it was frozen. In other words, the only way to broadcast it is to use the command: /forceBroadcast " + taskName);
				} else {
					output.add(new ScheduledAnnouncement(this.plugin, announcement, message, servers, permission, delay, interval, optionalTitleArgs));
				}
				i++;
				
			} catch (Exception ex) {
				getLogger().warning("Error when loading announcement \"" + taskName + "\" in config.yml");
				new ExceptionManager(ex).register(this.plugin, true);
			}
		}
		if (i > 0)
			getLogger().log(Level.INFO, Integer.toString(i) + " scheduled announcement" + (i > 1 ? "s" : "") + " " + (i > 1 ? "were" : "was") + " correctly loaded.");
		return output;
	}
	
	public List<PlayerAnnouncer> loadAutoPlayerAnnouncement() {
		List<PlayerAnnouncer> output = new ArrayList<>();
		// JOIN ANNOUNCEMENTS
		int i = 0;
		
		for (PlayerAnnouncer announcer : loadAutoPlayerAnnouncementSection("player-join-announcer", ConnectionType.CONNECT_SERVER)) {
			output.add(announcer);
			i++;
		}
		
		if (i == 0) {
			getLogger().log(Level.INFO, "The latest version changed the \"player-announcer\" config section name into \"player-join-announcer\" because a new section named \"player-quit-announcer\" which allows you to create announcements when players quit your network was added.");
			getLogger().log(Level.INFO, "So, it appears that you haven't updated the section name. The plugin will not do it for you but we will still exceptionally load the \"player-announcer\" section until you rename it \"player-join-announcer\".");
			for (PlayerAnnouncer announcer : loadAutoPlayerAnnouncementSection("player-announcer", ConnectionType.CONNECT_SERVER)) {
				output.add(announcer);
				i++;
			}
		}
		
		if (i > 0) {
			getLogger().log(Level.INFO, Integer.toString(i) + " automatic player join announcement" + (i > 1 ? "s" : "") + " " + (i > 1 ? "were" : "was") + " correctly loaded.");
		}
		
		// LEFT ANNOUNCEMENTS
		i = 0;
		
		for (PlayerAnnouncer announcer : loadAutoPlayerAnnouncementSection("player-quit-announcer", ConnectionType.LEAVE_PROXY)) {
			output.add(announcer);
			i++;
		}

		if (i > 0) {
			getLogger().log(Level.INFO, Integer.toString(i) + " automatic player left announcement" + (i > 1 ? "s" : "") + " " + (i > 1 ? "were" : "was") + " correctly loaded.");
		}
		
		return output;
	}
	
	public List<PlayerAnnouncer> loadAutoPlayerAnnouncementSection(String section, ConnectionType announceType) {
		List<PlayerAnnouncer> output = new ArrayList<>();
		
		final Configuration playerAnnouncerSection = this.config.getSection(section);
		for (String playerName : playerAnnouncerSection.getKeys()) {
			try {
				String type = playerAnnouncerSection.getString(playerName + ".type", "");
				
				AnnouncementManager announcement = AnnouncementManager.getAnnouncement(playerAnnouncerSection.getString(playerName + ".type"));
				if (announcement == null) {
					getLogger().log(Level.WARNING, "Error when loading automatic player announcement \"%s\", the field 'type' wasn't recognized.", playerName);
					continue;
				}
				
				String message = playerAnnouncerSection.getString(playerName + ".message", "<No message was set for this announcement>");
				List<String> requiredServers = playerAnnouncerSection.getStringList(playerName + ".required-servers");
				List<String> broadcastServers = playerAnnouncerSection.getStringList(playerName + ".broadcast-servers");
				List<String> servers = playerAnnouncerSection.getStringList(playerName + ".servers");
				if (requiredServers.isEmpty() && broadcastServers.isEmpty() && !servers.isEmpty()) {
					getLogger().info("Be aware that you're using the old configuration method for the player annoncer section. The parameter 'servers' has been replaced by 'broadcast-servers' and a new parameter 'required-servers' was added. To learn more, save your actual config.yml somewhere and let the plugin generates a new one, then read the instructions in it.");
					broadcastServers = servers;
					requiredServers.add("all");
				}
				String permission = playerAnnouncerSection.getString(playerName + ".permission", "");
				Integer[] optionalTitleArgs = BAUtils.getOptionalTitleArgsFromConfig(announcement, type);
				
				output.add(new PlayerAnnouncer(this.plugin, announceType, playerName, announcement, message, requiredServers, broadcastServers, permission, optionalTitleArgs));
			} catch (Exception ex) {
				getLogger().warning("Error when loading automatic player announcement \"" + playerName + "\" in config.yml");
				new ExceptionManager(ex).register(this.plugin, true);
			}
		}
		return output;
	}
	
	public Logger getLogger() {
		return this.plugin.getLogger();
	}
	
	public enum Field {
		
		ENABLE_PRIVATE_MESSAGING("enable-private-message", Boolean.class, true),
		COMMAND_FOR_PRIVATE_MESSAGING("command-for-private-message", String.class, "msg"),
		REGISTER_LOGS("enable-announcement-logs", Boolean.class, false),
		ANNOUNCE_PREFIX("announce-prefix", String.class, ""),
		ACTION_PREFIX("action-prefix", String.class, ""),
		SUBTITLE_PREFIX("subtitle-prefix", String.class, ""),
		TITLE_PREFIX("title-prefix", String.class, ""),
		WARN_PREFIX("warn-prefix", String.class, "&f[&4&lWARN&f] &b"),
		PM_SENT("private-message-send", String.class, "&3Send to &e%RECEIVER%: &d%MESSAGE%"),
		PM_RECEIVED("private-message-received", String.class, "&3Received from &e%SENDER%: &d%MESSAGE%"),
		PM_PLAYER_NOT_ONLINE("private-message-player-not-online", String.class, "&c%PLAYER% is unreachable :("),
		PM_SENDER_EQUALS_RECEIVER("private-message-sender-equals-receiver", String.class, "&7Are you schizophrenic ? :O"),
		REPLY_INFO("reply-info", String.class, "&7Use &a/reply &7to respond to &b%SENDER%");
	
		private String configField;
		private Class<?> type;
		private Object def;
		private Object value;
		
		private Field(String configField, Class<?> type, Object def) {
			this.configField = configField;
			this.type = type;
			this.def = def;
		}
		
		public String getField() {
			return this.configField;
		}
		
		public Class<?> getType() {
			return this.type;
		}
		
		public boolean getBoolean() {
			return (boolean) this.value;
		}
		
		public String getString() {
			return (String) this.value;
		}
		
		public Object getDefault() {
			return this.def;
		}
		
		private void setValue(Object obj) {
			this.value = obj;
		}
		
		public static void init(Configuration config) {
			for (Field field : values()) {
				if (field.getType() == String.class) {
					field.setValue(ChatColor.translateAlternateColorCodes('&', config.getString(field.getField(), (String) field.getDefault())));
				} else if (field.getType() == Boolean.class) {
					field.setValue(config.getBoolean(field.getField(), (boolean) field.getDefault()));
				}
			}
		}
	}
	
	public Configuration getConfig() {
		return this.config;
	}
}
