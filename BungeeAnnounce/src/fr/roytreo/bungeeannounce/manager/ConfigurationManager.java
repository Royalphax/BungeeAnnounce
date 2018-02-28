package fr.roytreo.bungeeannounce.manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import fr.roytreo.bungeeannounce.BungeeAnnouncePlugin;
import fr.roytreo.bungeeannounce.handler.PlayerAnnouncer;
import fr.roytreo.bungeeannounce.task.ScheduledAnnouncement;
import fr.roytreo.bungeeannounce.util.BAUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigurationManager {
	
	private BungeeAnnouncePlugin plugin;
	private Configuration config;

	public ConfigurationManager(BungeeAnnouncePlugin plugin) {
		this.plugin = plugin;
		if (!plugin.getDataFolder().exists())
			plugin.getDataFolder().mkdirs();
		File file = new File(plugin.getDataFolder(), "config.yml");
		if (!file.exists()) {
			getLogger().info("Thanks for using BungeeAnnounce from Asynchronous.");
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
		Field.init(this.config);
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
		int i = 0;
		
		Configuration playerAnnouncerSection = this.config.getSection("player-announcer");
		for (String playerName : playerAnnouncerSection.getKeys()) {
			try {
				String type = playerAnnouncerSection.getString(playerName + ".type", "");
				
				AnnouncementManager announcement = AnnouncementManager.getAnnouncement(playerAnnouncerSection.getString(playerName + ".type"));
				if (announcement == null) {
					getLogger().log(Level.WARNING, "Error when loading automatic player announcement \"%s\", the field 'type' wasn't recognized.", playerName);
					continue;
				}
				
				String message = playerAnnouncerSection.getString(playerName + ".message", "<No message was set for this announcement>");
				List<String> servers = playerAnnouncerSection.getStringList(playerName + ".servers");
				String permission = playerAnnouncerSection.getString(playerName + ".permission", "");
				Integer[] optionalTitleArgs = BAUtils.getOptionalTitleArgsFromConfig(announcement, type);
				
				output.add(new PlayerAnnouncer(this.plugin, playerName, announcement, message, servers, permission, optionalTitleArgs));
				
				i++;
			} catch (Exception ex) {
				getLogger().warning("Error when loading automatic player announcement \"" + playerName + "\" in config.yml");
				new ExceptionManager(ex).register(this.plugin, true);
			}
		}
		if (i > 0)
			getLogger().log(Level.INFO, Integer.toString(i) + " automatic player announcement" + (i > 1 ? "s" : "") + " " + (i > 1 ? "were" : "was") + " correctly loaded.");
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
		PM_SENDER_EQUALS_RECEIVER("private-message-sender-equals-receiver", String.class, "&7Are you schizophrenic ? :O");
	
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
