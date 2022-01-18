package webserver.request;

import static util.HttpRequestUtils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.IOUtils;
import webserver.HttpMethod;
import webserver.header.Cookie;

public class HttpRequest {

	private final RequestLine requestLine;
	private final Map<String, String> header = new HashMap<>();
	private final Map<String, String> parameter = new HashMap<>();
	private final Cookie cookie;

	public HttpRequest(InputStream in) throws IOException {
		final BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		String requestLines = br.readLine();
		requestLine = new RequestLine(requestLines);

		addHeader(br);
		addParameter(br);

		cookie = new Cookie(header.get("Cookie"));
	}

	private void addParameter(BufferedReader br) throws IOException {
		if (Objects.isNull(header.get("Content-Type")) ||
			Objects.isNull(header.get("Content-Length"))) {
			return;
		}
		String body = IOUtils.readData(br, Integer.parseInt(header.get("Content-Length")));
		parameter.putAll(parseQueryString(body));
	}

	private void addHeader(BufferedReader br) throws IOException {
		String line = br.readLine();
		while (!"".equals(line)) {
			if (line == null) {
				return;
			}
			final String[] keyValue = line.split(":\\s");
			header.put(keyValue[0], keyValue[1]);
			line = br.readLine();
		}
	}

	public HttpMethod getMethod() {
		return requestLine.getMethod();
	}

	public String getPath() {
		return requestLine.getPath();
	}

	public String getHeader(String key) {
		return header.get(key);
	}

	public String getParameter(String key) {
		if (!requestLine.getParams().isEmpty()) {
			return requestLine.getParams().get(key);
		}
		return parameter.get(key);
	}

	public Map<String, String> getParameters() {
		if (!requestLine.getParams().isEmpty()) {
			return requestLine.getParams();
		}
		return new HashMap<>(parameter);
	}

	public String getCookie(String key) {
		return cookie.get(key);
	}
}
