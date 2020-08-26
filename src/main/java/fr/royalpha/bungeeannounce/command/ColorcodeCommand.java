package fr.royalpha.bungeeannounce.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

/**
 * @author Royalpha
 */
public class ColorcodeCommand extends Command {
	public ColorcodeCommand() {
		super("colorcode", "", "bungee:colorcode");
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(new TextComponent("Minecraft Colors:"));
			sender.sendMessage(new TextComponent("\u00a70&0  \u00a71&1  \u00a72&2  \u00a73&3"));
			sender.sendMessage(new TextComponent("\u00a74&4  \u00a75&5  \u00a76&6  \u00a77&7"));
			sender.sendMessage(new TextComponent("\u00a78&8  \u00a79&9  \u00a7a&a  \u00a7b&b"));
			sender.sendMessage(new TextComponent("\u00a7c&c  \u00a7d&d  \u00a7e&e"));
			sender.sendMessage(new TextComponent(""));
			sender.sendMessage(new TextComponent("Minecraft formats:"));
			sender.sendMessage(new TextComponent("&k \u00a7kmagic\u00a7r &l \u00a7lBold"));
			sender.sendMessage(new TextComponent("&m \u00a7mStrike\u00a7r &n \u00a7nUnderline"));
			sender.sendMessage(new TextComponent("&o \u00a7oItalic\u00a7r &r \u00a7rReset"));
		}
	}
}
