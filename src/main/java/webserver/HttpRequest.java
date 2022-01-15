package webserver;

import static util.HttpRequestUtils.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.HttpRequestUtils;
import util.IOUtils;
import webserver.header.Cookie;

public class HttpRequest {

	private String method;
	private String path;
	private Cookie cookie;
	private Map<String, String> header = new HashMap<>();
	private Map<String, String> parameter = new HashMap<>();

	public HttpRequest(InputStream in) throws IOException {
		final BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
		String requestLines = br.readLine();

		final String[] requestLine = requestLines.split("\\s");
		method = requestLine[0];
		path = HttpRequestUtils.parseRequestPath(requestLine[1]);

		addHeader(br);
		addParameter(requestLine[1], br);
		setCookie();
	}

	private void addParameter(String pathWithParameters, BufferedReader br) throws IOException {
		if ("GET".equals(method)) {
			parameter.putAll(parseQueryString(parseRequestParam(pathWithParameters)));
			return;
		}

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

	public String getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public String getHeader(String key) {
		return header.get(key);
	}

	public String getParameter(String key) {
		return parameter.get(key);
	}

	public Map<String, String> getParameters() {
		return new HashMap<>(parameter);
	}

	private void setCookie() {
		cookie = new Cookie(header.get("Cookie"));
	}

	public String getCookie(String key) {
		return cookie.get(key);
	}
}
