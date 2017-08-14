package fr.roytreo.bungeeannounce;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.roytreo.bungeeannounce.command.AnnounceCommand;
import fr.roytreo.bungeeannounce.command.BAReloadCommand;
import fr.roytreo.bungeeannounce.command.ColorcodeCommand;
import fr.roytreo.bungeeannounce.command.MsgCommand;
import fr.roytreo.bungeeannounce.command.SendActionCommand;
import fr.roytreo.bungeeannounce.command.SendSubtitleCommand;
import fr.roytreo.bungeeannounce.command.SendTitleCommand;
import fr.roytreo.bungeeannounce.command.WarnCommand;
import fr.roytreo.bungeeannounce.handler.AnnounceType;
import fr.roytreo.bungeeannounce.handler.Logger;
import fr.roytreo.bungeeannounce.handler.PlayerAnnouncer;
import fr.roytreo.bungeeannounce.manager.ConfigurationManager;
import fr.roytreo.bungeeannounce.manager.URLManager;
import fr.roytreo.bungeeannounce.stat.DataRegister;
import fr.roytreo.bungeeannounce.task.ScheduledAnnouncement;
import fr.roytreo.bungeeannounce.util.BAUtils;
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
import net.md_5.bungee.event.EventHandler;

/**
 * @author Roytreo28
 */
public class BungeeAnnouncePlugin extends Plugin implements Listener {
	
	public static final String user_id = "%%__USER__%%";
	public static final String download_id = "%%__NONCE__%%";
	
	private static BungeeAnnouncePlugin instance;
	
	private Boolean update;
	private Logger logSystem;
	private final Boolean localhost;
	private ConfigurationManager configManager;
	private List<ScheduledAnnouncement> scheduledAnnouncement;
	
	public BungeeAnnouncePlugin() {
		this.update = false;
		this.localhost = false;
		this.scheduledAnnouncement = new ArrayList<>();
	}

	@Override
	public void onEnable() {
		instance = this;
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
	
	public static BungeeAnnouncePlugin getInstanceAPI()
	{
		return instance;
	}

	public void load() {
		this.configManager = new ConfigurationManager(this);
		this.scheduledAnnouncement = this.configManager.loadScheduledAnnouncement();
		this.configManager.loadAutoPlayerAnnouncement();
		
		this.logSystem = new Logger(this);
		
		if (ConfigurationManager.Field.ENABLE_PRIVATE_MESSAGING.getBoolean())
			getProxy().getPluginManager().registerCommand(this, new MsgCommand(this, ConfigurationManager.Field.COMMAND_FOR_PRIVATE_MESSAGING.getString()));
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
				title.title(new TextComponent((prefix ? ConfigurationManager.Field.TITLE_PREFIX.getString() : "") + ChatColor.translateAlternateColorCodes('&', message)));
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
					title.title(new TextComponent((prefix ? ConfigurationManager.Field.TITLE_PREFIX.getString() : "") + ChatColor.translateAlternateColorCodes('&', message)));
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
				title.subTitle(new TextComponent((prefix ? ConfigurationManager.Field.SUBTITLE_PREFIX.getString() : "") + ChatColor.translateAlternateColorCodes('&', message)));
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
					title.subTitle(new TextComponent((prefix ? ConfigurationManager.Field.SUBTITLE_PREFIX.getString() : "") + ChatColor.translateAlternateColorCodes('&', message)));
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
				player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent((prefix ? ConfigurationManager.Field.ACTION_PREFIX.getString() : "") + ChatColor.translateAlternateColorCodes('&', message)));
			}
		} else {
			for (ServerInfo server : servers) {
				for (ProxiedPlayer player : server.getPlayers()) {
					if (!permission.equals("") && !player.hasPermission(permission)) continue;
					message = BAUtils.translatePlaceholders(message, sender, player, server, this);
					player.sendMessage(ChatMessageType.ACTION_BAR, new TextComponent((prefix ? ConfigurationManager.Field.ACTION_PREFIX.getString() : "") + ChatColor.translateAlternateColorCodes('&', message)));
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
				player.sendMessage(BAUtils.parse((prefix ? ConfigurationManager.Field.WARN_PREFIX.getString() : "") + message));
			}
		} else {
			for (ServerInfo server : servers) {
				for (ProxiedPlayer player : server.getPlayers()) {
					if (!permission.equals("") && !player.hasPermission(permission)) continue;
					message = BAUtils.translatePlaceholders(message, sender, player, server, this);
					player.sendMessage(BAUtils.parse((prefix ? ConfigurationManager.Field.WARN_PREFIX.getString() : "") + message));
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
				player.sendMessage(BAUtils.parse((prefix ? ConfigurationManager.Field.ANNOUNCE_PREFIX.getString() : "") + message));
			}
		} else {
			for (ServerInfo server : servers) {
				for (ProxiedPlayer player : server.getPlayers()) {
					if (!permission.equals("") && !player.hasPermission(permission)) continue;
					message = BAUtils.translatePlaceholders(message, sender, player, server, this);
					player.sendMessage(BAUtils.parse((prefix ? ConfigurationManager.Field.ANNOUNCE_PREFIX.getString() : "") + message));
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
		if (PlayerAnnouncer.hasAnnouncement(player)) {
			for (PlayerAnnouncer playerAnnouncer : PlayerAnnouncer.getAnnouncementList(player))
				getProxy().getScheduler().schedule(this, new Runnable() {
					public void run() {
						send(playerAnnouncer.getAnnounceType(), getProxy().getConsole(), playerAnnouncer.getMessage(), playerAnnouncer.getServers(), false, "", playerAnnouncer.getOptionalTitleArgs());
					}
				}, 500, TimeUnit.MILLISECONDS);
		}
	}
	
	public Logger getLoggerSystem() {
		return this.logSystem;
	}
	
	public List<ScheduledAnnouncement> getScheduledAnnouncement() {
		return this.scheduledAnnouncement;
	}
}
