package fr.asynchronous.bungeeannounce.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import fr.asynchronous.bungeeannounce.BungeeAnnouncePlugin;
import fr.asynchronous.bungeeannounce.handler.AnnounceType;
import fr.asynchronous.bungeeannounce.handler.Executor;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;

/**
 * @author Roytreo28
 */
public class BAUtils {
	
	public static String separator = "::";
	public static String splittedSeparator = ":";

	public static TextComponent parse(String input)
	{
		String used = colorizz(input);
		
		if (!isNecessaryToParse(used))
			return new TextComponent(used);
		
		ArrayList<TextComponent> outputList = new ArrayList<>();
		String[] split = used.split(" ");
		for (int i = 0; i < split.length; i++)
		{
			outputList.add(new TextComponent(split[i]));
		}
		
		TextComponent output = new TextComponent("");
		
		for (int nbr = 0; nbr < outputList.size(); nbr++)
		{
			TextComponent in = outputList.get(nbr);
			TextComponent out = new TextComponent("");
			StringBuilder valueBuilder = new StringBuilder("");
			String text = in.getText();
			if (nbr > 0) out.addExtra(" ");
			if (isNecessaryToParse(text))
			{
				String[] txtSplit = text.split(separator);
				if (valueBuilder.toString().trim().equals("")) {
					for (int i = 2; i < txtSplit.length; i++)
					{
						if (txtSplit[i] != null)
						{
							valueBuilder.append((i > 2 ? separator : "") + txtSplit[i]);
							continue;
						}
						break;
					}
				}
				String value = valueBuilder.toString().trim();
				out.addExtra((txtSplit[0].replace('_', ' ')));
				for (Executor exec : Executor.values())
				{
					if (txtSplit[1].equals(exec.getString()))
					{
						exec.getEA().onParse(out, value);
					}
				}
			} else {
				out.addExtra(text);
			}
			output.addExtra(out);
		}
		return output;
	}
	
	private static Boolean isNecessaryToParse(String s)
	{
		for (Executor exec : Executor.values())
		{
			if (s.contains(separator + exec.getString() + separator))
				return true;
		}
		return false;
	}
	
	public static String colorizz(String uncolorizedString) {
		String[] split = uncolorizedString.split("");
		ArrayList<String> bigSplit = new ArrayList<>();
		for (int i = 0; i < split.length; i++)
			bigSplit.add(split[i]);
		
		StringBuilder output = new StringBuilder();
		
		StringBuilder color = new StringBuilder();
		for (int i = 0; i < bigSplit.size(); i++)
		{
			String str = bigSplit.get(i);
			if (str.equals(splittedSeparator) && bigSplit.get(i+1).equals(splittedSeparator))
			{
				int jump = jumpAfterNextSeparator(bigSplit, i+2);
				for (int j = i; j < jump; j++)
					output.append(bigSplit.get(j));
				output.append(" ");
				i = jump;
				continue;
			} else if (str.equals("[") 
					&& bigSplit.get(i+1).equals("l")
					&& bigSplit.get(i+2).equals("n")
					&& bigSplit.get(i+3).equals("]"))
			{
				output.append("[ln]");
				i = i+3;
				continue;
			}
			if (str.equals("&"))
			{
				if (i > 1 && !isCombiningColors(bigSplit, i)) {
					color = new StringBuilder();
				}
				color.append(str);
			} else if (isWaitingForColor(color)) {
				color.append(str);
			} else {
				output.append(color.toString().trim() + str);
			}
		}
		return ChatColor.translateAlternateColorCodes('&', (output.toString().trim().replace("[ln]", "\n")));
	}
	
	private static boolean isWaitingForColor(StringBuilder builder)  
	{
		String[] split = builder.toString().trim().split("");
		return split[split.length-1].equals("&");
	}
	
	private static boolean isCombiningColors(ArrayList<String> split, int index)  
	{
		return split.get(index-2).equals("&");
	}
	
	private static int jumpAfterNextSeparator(ArrayList<String> split, int index)
	{
		for (int i = index; i < split.size(); i++)
		{
			String str = split.get(i);
			if (str.equals(" "))
				return i;
		}
		return split.size();
	}
	
	public static String translatePlaceholders(String input, CommandSender sender, ProxiedPlayer receiver, ServerInfo server, BungeeAnnouncePlugin instance)
	{
		String output = input;
		if (receiver != null) 
		{
			output = output.replaceAll("%PLAYER_NAME%", receiver.getName());
			output = output.replaceAll("%PLAYER_DISPLAY_NAME%", receiver.getDisplayName());
			output = output.replaceAll("%PLAYER_PING%", receiver.getPing() + "");
			output = output.replaceAll("%PLAYER_UUID%", receiver.getUniqueId().toString());
		} else {
			output = output.replaceAll("%PLAYER_NAME%", "null");
			output = output.replaceAll("%PLAYER_DISPLAY_NAME%", "null");
			output = output.replaceAll("%PLAYER_PING%", "?");
			output = output.replaceAll("%PLAYER_UUID%", "null");
		}
		if (sender instanceof ProxiedPlayer)
		{
			ProxiedPlayer proxiedSender = (ProxiedPlayer) sender;
			output = output.replaceAll("%SENDER_NAME%", proxiedSender.getName());
			output = output.replaceAll("%SENDER_DISPLAY_NAME%", proxiedSender.getDisplayName());
			output = output.replaceAll("%SENDER_PING%", proxiedSender.getPing() + "");
			output = output.replaceAll("%SENDER_UUID%", proxiedSender.getUniqueId().toString());

		} else if (sender != null) {
			output = output.replaceAll("%SENDER_NAME%", sender.getName());
			output = output.replaceAll("%SENDER_DISPLAY_NAME%", sender.getName());
			output = output.replaceAll("%SENDER_PING%", "0");
			output = output.replaceAll("%SENDER_UUID%", "null");
		} else {
			output = output.replaceAll("%SENDER_NAME%", "null");
			output = output.replaceAll("%SENDER_DISPLAY_NAME%", "null");
			output = output.replaceAll("%SENDER_PING%", "?");
			output = output.replaceAll("%SENDER_UUID%", "null");
		}
		if (server != null)
		{
			output = output.replaceAll("%SERVER_NAME%", server.getName());
			output = output.replaceAll("%SERVER_MOTD%", server.getMotd());
			output = output.replaceAll("%SERVER_ONLINE_PLAYERS%", server.getPlayers().size() + "");
		} else {
			output = output.replaceAll("%SERVER_NAME%", "null");
			output = output.replaceAll("%SERVER_MOTD%", "null");
			output = output.replaceAll("%SERVER_ONLINE_PLAYERS%", "?");
		}
		output = output.replaceAll("%BUNGEE_ONLINE_PLAYERS%", instance.getProxy().getOnlineCount() + "");
		return output;
	}
	
	public static Integer[] getOptionalTitleArgsFromConfig(AnnounceType announceType, String rawType)
	{
		Integer[] emptyOutput = {};
		if (announceType == AnnounceType.TITLE || announceType == AnnounceType.SUBTITLE)
		{
			String[] splittedRawType = rawType.split("_");
			if (splittedRawType.length >= 4)
			{
				Integer fadeIn, stay, fadeOut;
				try {
				    fadeIn = Integer.parseInt(splittedRawType[1])*20;
				    stay = Integer.parseInt(splittedRawType[2])*20;
				    fadeOut = Integer.parseInt(splittedRawType[3])*20;
				    Integer[] filledOutput = {fadeIn, stay, fadeOut};
				    return filledOutput;
				} catch (NumberFormatException e) {
					return emptyOutput;
				}
			}
		}
		return emptyOutput;
	}
	
	public static void registerException(BungeeAnnouncePlugin instance, Exception ex)
	{
		BufferedWriter writer = null;
        try {
            String timeLog = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime());
            File folder = new File(instance.getDataFolder(), "reports/");
            if (!folder.exists()) folder.mkdirs();
            File logFile = new File(instance.getDataFolder(), "reports/" + timeLog + ".txt");

            StringWriter errors = new StringWriter();
            ex.printStackTrace(new PrintWriter(errors));
            
            try {
            	writer = new BufferedWriter(new FileWriter(logFile, true));
				writer.write(errors.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
        } finally {
            try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
	}
}
