package fr.roytreo.bungeeannounce.handler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import fr.roytreo.bungeeannounce.BungeeAnnouncePlugin;
import fr.roytreo.bungeeannounce.manager.AnnouncementManager;
import fr.roytreo.bungeeannounce.manager.ConfigManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;

/**
 * @author Roytreo28
 */
public class Logger {

	private File logFile;
	private Boolean registerLogs;
	
	public Logger(BungeeAnnouncePlugin instance) {
		this.registerLogs = ConfigManager.Field.REGISTER_LOGS.getBoolean();
		if (this.registerLogs)
		{
			File logFolder = new File(instance.getDataFolder(), "logs/");
			if (!logFolder.exists())
				logFolder.mkdirs();
			Calendar cal = Calendar.getInstance();
			File dateFolder = new File(logFolder, (cal.get(Calendar.MONTH) + 1) + "-" + cal.get(Calendar.DAY_OF_MONTH) + "-" + cal.get(Calendar.YEAR) + "/");
			if (!dateFolder.exists())
				dateFolder.mkdirs();
			logFile = new File(dateFolder, "Started_at_" + cal.get(Calendar.HOUR_OF_DAY) + "h_" + cal.get(Calendar.MINUTE) + "m_" + cal.get(Calendar.SECOND) + "s.log");
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
			Calendar cal = Calendar.getInstance();
			int hours = cal.get(Calendar.HOUR_OF_DAY);
			int minutes = cal.get(Calendar.MINUTE);
			int seconds = cal.get(Calendar.SECOND);
			String write = "[" + (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes) + ":" + (seconds < 10 ? "0" + seconds : seconds) + "][" + sender.getName() + "/" + typeUsed + "]: " + ChatColor.stripColor(message);
			this.writeText(write);
		}
	}
}
