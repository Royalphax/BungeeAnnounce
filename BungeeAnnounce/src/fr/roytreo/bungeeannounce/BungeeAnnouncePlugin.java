package fr.asynchronous.bungeeannounce;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.asynchronous.bungeeannounce.command.AnnounceCommand;
import fr.asynchronous.bungeeannounce.command.BAReloadCommand;
import fr.asynchronous.bungeeannounce.command.ColorcodeCommand;
import fr.asynchronous.bungeeannounce.command.MsgCommand;
import fr.asynchronous.bungeeannounce.command.SendActionCommand;
import fr.asynchronous.bungeeannounce.command.SendSubtitleCommand;
import fr.asynchronous.bungeeannounce.command.SendTitleCommand;
import fr.asynchronous.bungeeannounce.command.WarnCommand;
import fr.asynchronous.bungeeannounce.handler.AnnounceType;
import fr.asynchronous.bungeeannounce.handler.Logger;
import fr.asynchronous.bungeeannounce.handler.PlayerAnnouncer;
import fr.asynchronous.bungeeannounce.task.SchedulerTask;
import fr.asynchronous.bungeeannounce.util.BAUtils;
import fr.asynchronous.core.handler.URLManager;
import fr.asynchronous.core.stat.DataRegister;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.Title;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

/**
 * @author Roytreo28
 */
public class BungeeAnnouncePlugin extends Plugin implements Listener {
	private static BungeeAnnouncePlugin instance;
	private Configuration config;
	private Boolean localhost;
	public Boolean register_logs;
	public String announce_prefix;
	public String action_prefix;
	public String subtitle_prefix;
	public String title_prefix;
	public String warn_prefix;
	public String pm_sent;
	public Boolean update;
	public String pm_received;
	public String pm_player_not_online;
	public String pm_sender_equals_receiver;
	public Logger logSystem;
	public HashMap<String, ScheduledTask> messageTask;

	@Override
	public void onEnable() {
		instance = this;
		this.messageTask = new HashMap<>();
		this.update = false;
		this.localhost = false;
		this.load();
		getProxy().getPluginManager().registerCommand(this, new AnnounceCommand(this));
		getProxy().getPluginManager().registerCommand(this, new BAReloadCommand(this));
		getProxy().getPluginManager().registerCommand(this, new ColorcodeCommand());
		getProxy().getPluginManager().registerCommand(this, new SendTitleCommand(this));
		getProxy().getPluginManager().registerCommand(this, new SendSubtitleCommand(this));
		getProxy().getPluginManager().registerCommand(this, new SendActionCommand(this));
		getProxy().getPluginManager().registerCommand(this, new WarnCommand(this));
		getProxy().getPluginManager().registerListener(this, this);

		getProxy().getScheduler().runAsync(this, new Runnable() {
			public void run() {
				if (!URLManager.checkVersion(getDescription().getVersion(), false,
						URLManager.Values.GITHUB_PATH)) {
					getLogger().warning("A new version more efficient of the plugin is available. It will be automatically updated when the server will switch off.");
					update = true;
				}
				new DataRegister(instance, localhost, false);
			}
		});
	}

	@Override
	public void onDisable() {
		if (this.update) {
			getLogger().info("Stay informed about what the update bring new at https://www.spigotmc.org/resources/bungee-announce-1-8-1-9-1-10.10002/updates");
			URLManager.update(this, URLManager.getLatestVersion(), false, URLManager.Values.GITHUB_PATH);
		}
	}
	
	public static BungeeAnnouncePlugin getInstanceAPI()
	{
		return instance;
	}

	public void load() {
		if (!getDataFolder().exists())
			getDataFolder().mkdirs();
		File file = new File(getDataFolder(), "config.yml");
		if (!file.exists()) {
			getLogger().info("Thanks for using BungeeAnnounce from Asynchronous.");
			this.getLogger().info("Generating configuration file ...");
			try (InputStream in = getResourceAsStream("config.yml")) {
				Files.copy(in, file.toPath());
			} catch (IOException e) {
				e.printStackTrace();
				this.getLogger().warning("Error when generating configuration file !");
			} finally {
				this.getLogger().info("Configuration file was generated with success !");
			}
		}
		try {
			this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		register_logs = this.config.getBoolean("enable-announcement-logs");
		announce_prefix = ChatColor.translateAlternateColorCodes('&', (this.config.getString("announce-prefix")));
		action_prefix = ChatColor.translateAlternateColorCodes('&', (this.config.getString("action-prefix")));
		subtitle_prefix = ChatColor.translateAlternateColorCodes('&', (this.config.getString("title-prefix")));
		title_prefix = ChatColor.translateAlternateColorCodes('&', (this.config.getString("title-prefix")));
		warn_prefix = ChatColor.translateAlternateColorCodes('&', (this.config.getString("warn-prefix")));
		pm_sent = ChatColor.translateAlternateColorCodes('&', (this.config.getString("private-message-send")));
		pm_received = ChatColor.translateAlternateColorCodes('&', (this.config.getString("private-message-received")));
		pm_player_not_online = ChatColor.translateAlternateColorCodes('&', (this.config.getString("private-message-player-not-online")));
		pm_sender_equals_receiver = ChatColor.translateAlternateColorCodes('&', (this.config.getString("private-message-sender-equals-receiver")));
		this.logSystem = new Logger(this);
		
		List<String> stringList = new ArrayList<>();
		
		Configuration schedulerSection = this.config.getSection("scheduler");
		for (String taskName : schedulerSection.getKeys()) {
			try {
				AnnounceType announceType = AnnounceType.getType(schedulerSection.getString(taskName + ".type"));
				if (announceType == null) {
					getLogger().warning("Error when loading message \"" + taskName + "\", type can't be null!");
					continue;
				}
				this.messageTask.put(taskName,
						getProxy().getScheduler().schedule(this,
								new SchedulerTask(this, taskName, announceType,
										schedulerSection.getString(taskName + ".message"),
										schedulerSection.getStringList(taskName + ".servers"),
										schedulerSection.getString(taskName + ".permission") == null ? ""
												: schedulerSection.getString(taskName + ".permission"),
										BAUtils.getOptionalTitleArgsFromConfig(announceType,
												schedulerSection.getString(taskName + ".type"))),
								schedulerSection.getInt(taskName + ".delay"),
								schedulerSection.getInt(taskName + ".interval"), TimeUnit.SECONDS));
				stringList.add(taskName);
			} catch (Exception ex) {
				this.getLogger().warning("Error when loading message " + taskName + " in config.yml");
				this.getLogger().warning("|> Report: " + ex.getMessage());
				BAUtils.registerException(this, ex);
			}
		}
		if (stringList.size() > 0)
			getLogger().info("Automatic announcement" + (stringList.size() > 1 ? "s" : "") + " " + stringList.toString() + " " + (stringList.size() > 1 ? "were" : "was") + " successfully setup" + (stringList.size() > 1 ? "s" : "") + ".");
		stringList.clear();

		Configuration playerAnnouncerSection = this.config.getSection("player-announcer");
		for (String playerName : playerAnnouncerSection.getKeys()) {
			try {
				AnnounceType announceType = AnnounceType.getType(playerAnnouncerSection.getString(playerName + ".type"));
				if (announceType == null) {
					getLogger().warning("Error when loading \"" + playerName + "\", type can't be null!");
					continue;
				}
				new PlayerAnnouncer(this, playerName, announceType,
						playerAnnouncerSection.getString(playerName + ".message"),
						playerAnnouncerSection.getStringList(playerName + ".servers"),
						playerAnnouncerSection.getString(playerName + ".permission") == null ? ""
								: playerAnnouncerSection.getString(playerName + ".permission"),
						BAUtils.getOptionalTitleArgsFromConfig(announceType,
								playerAnnouncerSection.getString(playerName + ".type")));
				stringList.add(playerName);
			} catch (Exception ex) {
				this.getLogger().warning("Error when loading " + playerName + " in config.yml");
				this.getLogger().warning("|> Report: " + ex.getMessage());
				BAUtils.registerException(this, ex);
			}
		}
		if (stringList.size() > 0)
			getLogger().info("Automatic announcement for player" + (stringList.size() > 1 ? "s" : "") + " " + stringList.toString() + " " + (stringList.size() > 1 ? "were" : "was") + " successfully setup" + (stringList.size() > 1 ? "s" : "") + ".");
		stringList.clear();
		
		if (config.getBoolean("enable-private-message"))
			getProxy().getPluginManager().registerCommand(this, new MsgCommand(this, (config.getString("command-for-private-message") == null ? "msg" : config.getString("command-for-private-message"))));
	
		getLogger().info("Configuration file was successfully loaded.");
	}

	private void sendTitle(CommandSender sender, String message, List<ServerInfo> servers, boolean prefix, String permission, Integer... optionalTitleArgs) {
		if (servers == null || servers.isEmpty()) {
			for (ProxiedPlayer player : getProxy().getPlayers()) {
				if (!permission.equals("") && !player.hasPermission(permission)) continue;
				ServerInfo server = player.getServer().getInfo();
				message = BAUtils.translatePlaceholders(message, sender, player, server, this);
				Title title = getProxy().createTitle();
				if (optionalTitleArgs != null && optionalTitleArgs.length >= 3) {
					if (optionalTitleArgs[0] != 0 || optionalTitleArgs[1] != 0 || optionalTitleArgs[2] != 0) {
						title.fadeIn(optionalTitleArgs[0]);
						title.stay(optionalTitleArgs[1]);
						title.fadeOut(optionalTitleArgs[2]);
					}
				}
				title.title(new TextComponent((prefix ? this.title_prefix : "") + ChatColor.translateAlternateColorCodes('&', message)));
				title.send(player);
			}
		} else {
			for (ServerInfo server : servers) {
				for (ProxiedPlayer player : server.getPlayers()) {
					if (!permission.equals("") && !player.hasPermission(permission)) continue;
					message = BAUtils.translatePlaceholders(message, sender, player, server, this);
					Title title = getProxy().createTitle();
					if (optionalTitleArgs != null && optionalTitleArgs.length >= 3) {
						if (optionalTitleArgs[0] != 0 || optionalTitleArgs[1] != 0 || optionalTitleArgs[2] != 0) {
							title.fadeIn(optionalTitleArgs[0]);
							title.stay(optionalTitleArgs[1]);
							title.fadeOut(optionalTitleArgs[2]);
						}
					}
					title.title(new TextComponent((prefix ? this.title_prefix : "") + ChatColor.translateAlternateColorCodes('&', message)));
					title.send(player);
				}
			}
		}
	}
	
	private void sendSubtitle(CommandSender sender, String message, List<ServerInfo> servers, boolean prefix, String permission, Integer... optionalTitleArgs) {
		if (servers == null || servers.isEmpty()) {
			for (ProxiedPlayer player : getProxy().getPlayers()) {
				if (!permission.equals("") && !player.hasPermission(permission)) continue;
				ServerInfo server = player.getServer().getInfo();
				message = BAUtils.translatePlaceholders(message, sender, player, server, this);
				Title title = getProxy().createTitle();
				if (optionalTitleArgs != null && optionalTitleArgs.length >= 3) {
					if (optionalTitleArgs[0] != 0 || optionalTitleArgs[1] != 0 || optionalTitleArgs[2] != 0) {
						title.fadeIn(optionalTitleArgs[0]);
						title.stay(optionalTitleArgs[1]);
						title.fadeOut(optionalTitleArgs[2]);
					}
				}
				title.title(new TextComponent(""));
				title.subTitle(new TextComponent((prefix ? this.subtitle_prefix : "") + ChatColor.translateAlternateColorCodes('&', message)));
				title.send(player);
			}
		} else {
			for (ServerInfo server : servers) {
				for (ProxiedPlayer player : server.getPlayers()) {
					if (!permission.equals("") && !player.hasPermission(permission)) continue;
					message = BAUtils.translatePlaceholders(message, sender, player, server, this);
					Title title = getProxy().createTitle();
					if (optionalTitleArgs != null && optionalTitleArgs.length >= 3) {
						if (optionalTitleArgs[0] != 0 || optionalTitleArgs[1] != 0 || optionalTitleArgs[2] != 0) {
							title.fadeIn(optionalTitleArgs[0]);
							title.stay(optionalTitleArgs[1]);
							title.fadeOut(optionalTitleArgs[2]);
						}
					}
					title.title(new TextComponent(""));
					title.subTitle(new TextComponent((prefix ? this.subtitle_prefix : "") + ChatColor.translateAlternateColorCodes('&', message)));
					title.send(player);
				}
			}
		}
	}

	private void sendAction(CommandSender sender, String message, List<ServerInfo> servers, boolean prefix, String permission) {
		if (servers == null || servers.isEmpty()) {
			for (ProxiedPlayer player : getProxy().getPlayers()) {
				if (!permission.equals("") && !player.hasPermission(permission)) continue;
				ServerInfo server = player.getServer().getInfo();
				message = BAUtils.translatePlaceholders(message, sender, player, server, this);
				player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent((prefix ? this.action_prefix : "") + ChatColor.translateAlternateColorCodes('&', message)));
			}
		} else {
			for (ServerInfo server : servers) {
				for (ProxiedPlayer player : server.getPlayers()) {
					if (!permission.equals("") && !player.hasPermission(permission)) continue;
					message = BAUtils.translatePlaceholders(message, sender, player, server, this);
					player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent((prefix ? this.action_prefix : "") + ChatColor.translateAlternateColorCodes('&', message)));
				}
			}
		}
	}

	private void warn(CommandSender sender, String message, List<ServerInfo> servers, boolean prefix, String permission) {
		if (servers == null || servers.isEmpty()) {
			for (ProxiedPlayer player : getProxy().getPlayers()) {
				if (!permission.equals("") && !player.hasPermission(permission)) continue;
				ServerInfo server = player.getServer().getInfo();
				message = BAUtils.translatePlaceholders(message, sender, player, server, this);
				player.sendMessage(BAUtils.parse((prefix ? this.warn_prefix : "") + message));
			}
		} else {
			for (ServerInfo server : servers) {
				for (ProxiedPlayer player : server.getPlayers()) {
					if (!permission.equals("") && !player.hasPermission(permission)) continue;
					message = BAUtils.translatePlaceholders(message, sender, player, server, this);
					player.sendMessage(BAUtils.parse((prefix ? this.warn_prefix : "") + message));
				}
			}
		}
	}

	private void announce(CommandSender sender, String message, List<ServerInfo> servers, boolean prefix, String permission) {
		if (servers == null || servers.isEmpty()) {
			for (ProxiedPlayer player : getProxy().getPlayers()) {
				if (!permission.equals("") && !player.hasPermission(permission)) continue;
				ServerInfo server = player.getServer().getInfo();
				message = BAUtils.translatePlaceholders(message, sender, player, server, this);
				player.sendMessage(BAUtils.parse((prefix ? this.announce_prefix : "") + message));
			}
		} else {
			for (ServerInfo server : servers) {
				for (ProxiedPlayer player : server.getPlayers()) {
					if (!permission.equals("") && !player.hasPermission(permission)) continue;
					message = BAUtils.translatePlaceholders(message, sender, player, server, this);
					player.sendMessage(BAUtils.parse((prefix ? this.announce_prefix : "") + message));
				}
			}
		}
	}

	/**
	 * Send any type of anouncement with a lot of possibilities by specifying all of each following parameters. 
	 *
	 * @author Roytreo28
	 * @param type The announce type (title/subtitle/warn/announce/action).
	 * @param sender The sender who's supposed to had sent this announcement. Put <b>null</b> if ignored.
	 * @param message Message of the announcement.
	 * @param servers Servers on which the announcement will be displayed. Put <b>null</b> if you want to display the announcement on all your bungee servers.
	 * @param prefix Does the announcement use pre defined prefix in config.yml.
	 * @param permission Permission which is required to see this announcement. Put an empty string if ignored.
	 * @param optionalTitleArgs Optional title arguments. Put three integers and they will be used for fadeIn, stay and fadeOut values.
	 */
	public void send(AnnounceType type, CommandSender sender, String message, List<ServerInfo> servers, boolean prefix, String permission, Integer... optionalTitleArgs) {
		switch (type) {
		case ACTION:
			this.sendAction(sender, message, servers, prefix, permission);
			break;
		case ANNOUNCEMENT:
			this.announce(sender, message, servers, prefix, permission);
			break;
		case SUBTITLE:
			this.sendSubtitle(sender, message, servers, prefix, permission, optionalTitleArgs);
			break;
		case TITLE:
			this.sendTitle(sender, message, servers, prefix, permission, optionalTitleArgs);
			break;
		case WARN:
			this.warn(sender, message, servers, prefix, permission);
			break;
		}
	}

	@EventHandler
	public void onConnect(final ServerConnectedEvent event) {
		final ProxiedPlayer player = event.getPlayer();
		if (PlayerAnnouncer.existPlayerAnnouncer(player)) {
			for (PlayerAnnouncer playerAnnouncer : PlayerAnnouncer.getPlayerAnnouncer(player))
				getProxy().getScheduler().schedule(this, new Runnable() {
					public void run() {
						send(playerAnnouncer.getAnnounceType(), getProxy().getConsole(), playerAnnouncer.getMessage(), playerAnnouncer.getServers(), false, "", playerAnnouncer.getOptionalTitleArgs());
					}
				}, 500, TimeUnit.MILLISECONDS);
		}
	}
}
