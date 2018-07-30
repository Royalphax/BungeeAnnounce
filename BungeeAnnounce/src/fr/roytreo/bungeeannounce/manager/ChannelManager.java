package fr.roytreo.bungeeannounce.manager;

import java.util.ArrayList;
import java.util.List;

import fr.roytreo.bungeeannounce.command.ChannelCommand;
import fr.roytreo.bungeeannounce.util.BAUtils;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class ChannelManager {

	private static List<ProxiedPlayer> tipPlayers = new ArrayList<>();
	private static String tipMessage = "";
	private static List<ChannelManager> channels = new ArrayList<>();
	
	private String name;
	private String permission;
	private String command;
	private String description;
	private String format;
	private String joinMessage;
	private String leftMessage;
	private List<ProxiedPlayer> players;
	
	public ChannelManager(Plugin plugin, String name, String permission, String command, String description, String format, String joinMessage, String leftMessage) {
		this.name = name;
		this.permission = permission;
		this.description = description;
		this.joinMessage = joinMessage;
		this.leftMessage = leftMessage;
		this.format = format;
		this.command = command;
		this.players = new ArrayList<>();
		
		if (check()) {
			plugin.getProxy().getPluginManager().registerCommand(plugin, new ChannelCommand(this));
			channels.add(this);
			plugin.getLogger().info("Channel \"" + this.name + "\" successfully registred !");
		} else {
			plugin.getLogger().info("You can't register the channel \"" + name + "\" because there is already one with the same name or command.");
		}
	}
	
	public boolean check() {
		for (ChannelManager channel : channels)
			if (channel.getCommand().equalsIgnoreCase(this.command) || channel.getName().equalsIgnoreCase(this.name))
				return false;
		return true;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getCommand() {
		return this.command;
	}
	
	public String getPermission() {
		return this.permission;
	}
	
	public String getJoinMessage() {
		return translatePlaceholders(this.joinMessage);
	}
	
	public String getLeftMessage() {
		return translatePlaceholders(this.leftMessage);
	}
	
	public String getDescription() {
		return translatePlaceholders(this.description);
	}
	
	public String getFormat() {
		return translatePlaceholders(this.format);
	}
	
	public boolean hasPlayer(ProxiedPlayer player) {
		return this.players.contains(player);
	}
	
	public void joinPlayer(ProxiedPlayer player) {
		players.add(player);
		for (ProxiedPlayer channelPlayer : ProxyServer.getInstance().getPlayers())
			if (channelPlayer.hasPermission(this.permission) || this.players.contains(channelPlayer))
				channelPlayer.sendMessage(new TextComponent(BAUtils.colorizz(BAUtils.translatePlaceholders(getJoinMessage(), player, channelPlayer, channelPlayer.getServer().getInfo()))));
		player.sendMessage(new TextComponent(BAUtils.colorizz(getDescription())));
		if (!tipPlayers.contains(player) && getPlayerChannels(player).size() > 1) {
			tipPlayers.add(player);
			player.sendMessage(new TextComponent(BAUtils.colorizz(tipMessage)));
		}
	}
	
	public void leftPlayer(ProxiedPlayer player) {
		for (ProxiedPlayer channelPlayer : ProxyServer.getInstance().getPlayers())
			if (channelPlayer.hasPermission(this.permission) || this.players.contains(channelPlayer))
				channelPlayer.sendMessage(new TextComponent(BAUtils.colorizz(BAUtils.translatePlaceholders(getLeftMessage(), player, channelPlayer, channelPlayer.getServer().getInfo()))));
		players.remove(player);
	}
	
	public void sendMessage(ProxiedPlayer sender, String message) {
		final String msg = translatePlaceholders(this.format.replaceAll("%MESSAGE%", message));
		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers())
			if (player.hasPermission(this.permission) || this.players.contains(player))
				player.sendMessage(new TextComponent(BAUtils.colorizz(BAUtils.translatePlaceholders(msg, sender, player, player.getServer().getInfo()))));
	}
	
	private String translatePlaceholders(String input) {
		input = input.replaceAll("%CHANNEL_NAME%", this.name);
		input = input.replaceAll("%CHANNEL_DESCRIPTION%", this.description);
		return input;
	}
	
	public static boolean hasChannel(ProxiedPlayer player) {
		return (!getPlayerChannels(player).isEmpty());
	}
	
	public static List<ChannelManager> getPlayerChannels(ProxiedPlayer player) {
		List<ChannelManager> playerChannels = new ArrayList<>();
		for (ChannelManager channel : channels)
			if (channel.players.contains(player))
				playerChannels.add(channel);
		return playerChannels;
	}
	
	public static List<ChannelManager> getChannels() {
		return channels;
	}
	
	public static void setTipMessage(String message) {
		tipMessage = message;
	}
}