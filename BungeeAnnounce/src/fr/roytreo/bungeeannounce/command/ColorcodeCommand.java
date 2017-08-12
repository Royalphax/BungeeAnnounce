package fr.roytreo.bungeeannounce.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * @author Roytreo28
 */
public class ColorcodeCommand extends Command {
	public ColorcodeCommand() {
		super("colorcode", "", "bungee:colorcode");
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(new TextComponent("Minecraft Colors:"));
			sender.sendMessage(new TextComponent("§0&0  §1&1  §2&2  §3&3"));
			sender.sendMessage(new TextComponent("§4&4  §5&5  §6&6  §7&7"));
			sender.sendMessage(new TextComponent("§8&8  §9&9  §a&a  §b&b"));
			sender.sendMessage(new TextComponent("§c&c  §d&d  §e&e"));
			sender.sendMessage(new TextComponent(""));
			sender.sendMessage(new TextComponent("Minecraft formats:"));
			sender.sendMessage(new TextComponent("&k §kmagic§r &l §lBold"));
			sender.sendMessage(new TextComponent("&m §mStrike§r &n §nUnderline"));
			sender.sendMessage(new TextComponent("&o §oItalic§r &r §rReset"));
		}
	}
}
