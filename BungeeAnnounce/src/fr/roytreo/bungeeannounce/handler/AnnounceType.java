package fr.roytreo.bungeeannounce.handler;

/**
 * @author Roytreo28
 */
public enum AnnounceType {
	ACTION("action"), ANNOUNCEMENT("announce"), WARN("warn"), SUBTITLE("subtitle"), TITLE("title");

	private String rawType;

	private AnnounceType(String rawType) {
		this.rawType = rawType;
	}

	@Override
	public String toString() {
		return this.rawType;
	}

	public static AnnounceType getType(String supposedAnnounceType) {
		for (AnnounceType announceType : values()) {
			if (announceType.toString().equals(supposedAnnounceType) || supposedAnnounceType.startsWith(announceType.toString())) {
				return announceType;
			}
		}
		return null;
	}
}
