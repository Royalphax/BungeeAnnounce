package fr.roytreo.bungeeannounce.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import fr.roytreo.bungeeannounce.BungeeAnnouncePlugin;
import fr.roytreo.bungeeannounce.manager.AnnouncementManager;
import fr.roytreo.bungeeannounce.manager.ConfigurationManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

/**
 * @author Roytreo28
 */
public class Logger {

	private File logFile;
	private Boolean registerLogs;
	
	@SuppressWarnings("deprecation")
	public Logger(BungeeAnnouncePlugin instance) {
		this.registerLogs = ConfigurationManager.Field.REGISTER_LOGS.getBoolean();
		if (this.registerLogs)
		{
			new File(instance.getDataFolder(), "logs/").mkdirs();
			java.util.Date actual = new java.util.Date();
			logFile = new File(instance.getDataFolder(), "logs/Started_at_" + actual.getHours() + "h_" + actual.getMinutes() + "m_" + actual.getSeconds() + "s_the_" + actual.getMonth() + "-" + actual.getDate() + "-" + actual.getYear() + ".log");
			if (!logFile.exists())
			{
				try {
					logFile.createNewFile();
				} catch (IOException e) {
					instance.getLogger().warning("We can't create the log file for BungeeAnnounce. Reason: " + e.getMessage());
				}
			}
		}
	}
	
	public void writeText(String text)
	{
		BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write(text);
            writer.newLine();
        } catch (Exception e) {
            return;
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
            	return;
            }
        }
	}
	
	public void announce(AnnouncementManager announcement, CommandSender sender, String message)
	{
		if (this.registerLogs && sender != null) {
			String typeUsed = announcement.toString().toUpperCase();
			java.util.Date actual = new java.util.Date();
			@SuppressWarnings("deprecation")
			String write = "[" + actual.getHours() + ":" + actual.getMinutes() + ":" + actual.getSeconds() + "] [" + sender.getName() + "/" + typeUsed + "]: " + ChatColor.stripColor(message);
			this.writeText(write);
		}
	}
}
