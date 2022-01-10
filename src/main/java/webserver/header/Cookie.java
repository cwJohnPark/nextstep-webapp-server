package webserver.header;

import java.util.Objects;

public class Cookie {
	private final boolean isLogined;

	private Cookie(boolean isLogined) {
		this.isLogined = isLogined;
	}

	public static Cookie parseKeyValue(String line) {
		if (Objects.isNull(line) || "".equals(line)) {
			return new Cookie(false);
		}
		if (line.startsWith("Cookie") && line.contains("logined=true")) {
			return new Cookie(true);
		}
		return new Cookie(false);
	}

	public boolean isLogined() {
		return isLogined;
	}
}
