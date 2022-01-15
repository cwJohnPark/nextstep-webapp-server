package webserver;

import static java.lang.String.*;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpResponse {

	private static final Logger log = LoggerFactory.getLogger(HttpResponse.class);

	private final Map<String, String> headers = new HashMap<>();
	private final DataOutputStream dos;

	public HttpResponse(OutputStream os) {
		dos = new DataOutputStream(os);
	}

	public void forward(String path) {
		String contentType = "text/plain";

		if (path.endsWith(".html") || "/".equals(path)) {
			contentType = "text/html";
		} else if (path.endsWith(".css")) {
			contentType = "text/css";
		} else if (path.endsWith(".js")) {
			contentType = "text/javascript";
		} else if (path.endsWith(".woff")) {
			contentType = "text/font";
		}

		addContentTypeHeader(contentType);
		byte[] body = readContentFile(path);
		addContentLengthHeader(body.length);

		response200Header();
		responseBody(body);
	}

	private byte[] readContentFile(String path) {
		try {
			return Files.readAllBytes(new File("./webapp" + replaceDefaultPageIfNull(path)).toPath());
		} catch (IOException e) {
			log.error(e.getMessage());
		}
		throw new RuntimeException("Can not read file from path = " + path);
	}


	// Redirect는 content-type 을 쓰지 않는다.
	public void sendRedirect(String path) {
		try {
			dos.writeBytes("HTTP/1.1 302 Found\r\n");
			writeHeaders();
			dos.writeBytes(format("Location: %s\r\n", path));
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}


	public void forwardBody(byte[] body) {
		addContentTypeHeader("text/html");
		addContentLengthHeader(body.length);

		response200Header();
		responseBody(body);
	}

	private void response200Header() {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			writeHeaders();
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.writeBytes("\r\n");
			dos.flush();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	private void writeHeaders() throws IOException {
		for (Entry<String, String> header : headers.entrySet()) {
			dos.writeBytes(format("%s: %s\r\n", header.getKey(),header.getValue()));
		}
	}

	private void addContentLengthHeader(int length) {
		headers.put("Content-Length", String.valueOf(length));
	}

	private void addContentTypeHeader(String contentType) {
		headers.put("Content-Type", format("%s;charset=utf-8\r\n", contentType));
	}

	private String replaceDefaultPageIfNull(String requestPath) {
		return Objects.isNull(requestPath) || "/".equals(requestPath) ? "/index.html" : requestPath;
	}

	public void setCookie(String value) {
		headers.put("Set-Cookie", value);
	}
}
