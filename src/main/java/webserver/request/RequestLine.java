package webserver.request;


import static java.lang.String.*;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import util.HttpRequestUtils;
import webserver.HttpMethod;

public class RequestLine {
	private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

	private final HttpMethod method;
	private final String path;
	private final Map<String, String> params = new HashMap<>();

	// ex) GET /api/path HTTP/1.1
	public RequestLine(String requestLine) {
		log.debug("request line: {}", requestLine);
		final String[] tokens = requestLine.split(" ");
		if (tokens.length != 3) {
			throw new IllegalArgumentException(format("HTTP Request Line이 형식에 맞지 않습니다. ('%s')", requestLine));
		}

		method = HttpMethod.valueOf(tokens[0]);
		path = HttpRequestUtils.parseRequestPath(tokens[1]);
		params.putAll(HttpRequestUtils.parseRequestParam(tokens[1]));
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getPath() {
		return path;
	}

	public Map<String, String> getParams() {
		return params;
	}
}
