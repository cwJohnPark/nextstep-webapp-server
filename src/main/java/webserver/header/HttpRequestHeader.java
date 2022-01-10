package webserver.header;

import java.util.List;

import util.HttpRequestUtils;

public class HttpRequestHeader {

	private final Cookie cookie;
	private final int contentLength;

	private HttpRequestHeader(String cookieRequestLine, int contentLength) {
		this.cookie = Cookie.parseKeyValue(cookieRequestLine);
		this.contentLength = contentLength;
	}

	public static HttpRequestHeader parseHeaderLines(List<String> lines) {
		int contentLength = -1;
		String cookieRequestLine = "";

		for (String line : lines) {
			if (line.startsWith("Content-Length")) {
				contentLength = Integer.parseInt(HttpRequestUtils.parseHeader(line).getValue());
			}
			if (line.startsWith("Cookie")) {
				cookieRequestLine = line;
			}
		}
		return new HttpRequestHeader(cookieRequestLine, contentLength);
	}

	public int getContentLength() {
		return contentLength;
	}

	public boolean isLogined() {
		return cookie.isLogined();
	}
}
