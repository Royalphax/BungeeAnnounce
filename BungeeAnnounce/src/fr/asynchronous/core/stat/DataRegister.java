package fr.asynchronous.core.stat;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import fr.asynchronous.core.handler.MySQL;
import fr.asynchronous.core.handler.URLManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ConfigurationAdapter;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.conf.Configuration;

/**
 * @author Roytreo28
 */
public class DataRegister {

	public MySQL database;
	public final Configuration config = new Configuration();

	public DataRegister(final Plugin plugin, final Boolean localhost, final Boolean debug) {
		String content;
		String java_column;
		Boolean javaCol = false;
		try {
			java_column = new URLManager("http://%BASE_URL%/home/core/java_column.txt", localhost).read();
			if (java_column.equals("true"))
				javaCol = true;
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
		}
		try {
			content = new URLManager("http://%BASE_URL%/home/core/database.txt", localhost).read();
		} catch (Exception e) {
			if (debug)
				e.printStackTrace();
			return;
		}
		final String[] contentSplitted = content.split("_");
		this.database = new MySQL(plugin, (localhost ? "localhost" : contentSplitted[0]), contentSplitted[1],
				contentSplitted[2], contentSplitted[3], contentSplitted[4]);
		String ip = "unknown";
		Integer port = 0;
		Map<String, ServerInfo> map = plugin.getProxy().getServers();
		for (Map.Entry<String, ServerInfo> entry : map.entrySet()) {
			ServerInfo server = entry.getValue();
			ip = server.getAddress().getAddress().getHostAddress();
			port = server.getAddress().getPort();
			break;
		}
		final String server_ip = ip;
		final Integer server_port = port;
		final String country = Locale.getDefault().getDisplayCountry(Locale.ENGLISH);
		final String server_location = (country.contains("?") ? "unknown" : country);
		@SuppressWarnings("deprecation")
		final String server_version = "bungee-" + plugin.getProxy().getGameVersion();
		final String os_name = System.getProperty("os.name");
		final String os_arch = System.getProperty("os.arch");
		final String os_version = System.getProperty("os.version");
		final String java_version = System.getProperty("java.version");
		final String plugin_version = plugin.getDescription().getVersion();
		ConfigurationAdapter configAdapter = ProxyServer.getInstance().getConfigurationAdapter();
		configAdapter.load();
		String uuid = "unknown";
		try {
			uuid = configAdapter.getString("stats", uuid);
		} catch (Exception ex) {
			if (debug)
				ex.printStackTrace();
			return;
		}
		String id = "";
		try {
			id = UUID.fromString(uuid).toString();
		} catch (IllegalArgumentException exception) {
			return;
		}
		try {
			database.openConnection();
			ResultSet res = database.querySQL("SELECT * FROM data WHERE server_id='" + id + "'");
			if (!res.first()) {
				database.updateSQL(
						"INSERT INTO data(server_id, server_ip, server_port, server_location, server_version, os_name, os_arch, os_version, "
								+ (javaCol ? "java_version, " : "")
								+ "plugin_name, plugin_version, updated_at, created_at) VALUES('" + id + "', '"
								+ server_ip + "', " + server_port + ", '" + server_location + "', '" + server_version
								+ "', '" + os_name + "', '" + os_arch + "', '" + os_version + "', '"
								+ (javaCol ? java_version + "', '" : "") + plugin.getDescription().getName() + "', '"
								+ plugin_version + "', NOW(), NOW())");
			} else {
				database.updateSQL("UPDATE data SET server_ip='" + server_ip + "', server_port=" + server_port
						+ ", server_location='" + server_location + "', server_version='" + server_version
						+ "', os_version='" + os_version + "', "
						+ (javaCol ? "java_version='" + java_version + "', " : "") + "plugin_name='"
						+ plugin.getDescription().getName() + "', plugin_version='" + plugin_version
						+ "', updated_at=NOW() WHERE server_id='" + id + "'");
			}
		} catch (ClassNotFoundException | SQLException e) {
			if (debug)
				e.printStackTrace();
			return;
		} finally {
			try {
				database.closeConnection();
			} catch (SQLException e) {
				if (debug)
					e.printStackTrace();
				return;
			}
		}
	}

	@SuppressWarnings("unused")
	private String getMAC() throws SocketException {
		String firstInterface = null;
		Map<String, String> addressByNetwork = new HashMap<>();
		Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();

		while (networkInterfaces.hasMoreElements()) {
			NetworkInterface network = networkInterfaces.nextElement();

			byte[] bmac = network.getHardwareAddress();
			if (bmac != null) {
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < bmac.length; i++) {
					sb.append(String.format("%02X%s", bmac[i], (i < bmac.length - 1) ? "-" : ""));
				}

				if (sb.toString().isEmpty() == false) {
					addressByNetwork.put(network.getName(), sb.toString());
				}

				if (sb.toString().isEmpty() == false && firstInterface == null) {
					firstInterface = network.getName();
				}
			}
		}

		if (firstInterface != null) {
			return addressByNetwork.get(firstInterface);
		}

		return "null";
	}
}
