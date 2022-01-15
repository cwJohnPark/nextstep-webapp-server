package webserver.header;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cookie {
	private final Map<String, String> cookies = new HashMap<>();

	public Cookie(String cookie) {
		parseCookie(cookie);
	}

	private void parseCookie(String cookie) {
		if (Objects.isNull(cookie) || "".equals(cookie)) {
			return;
		}

		Arrays.stream(cookie.split(";\\s"))
			.map(keyValue -> keyValue.split("="))
			.forEach(keyValue -> cookies.put(keyValue[0], keyValue[1]));
	}

	public String get(String key) {
		return cookies.get(key);
	}

	public int size() {
		return cookies.size();
	}
}
