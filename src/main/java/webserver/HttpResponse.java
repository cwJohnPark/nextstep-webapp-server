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

public class HttpResponse {

	private final Map<String, String> headers = new HashMap<>();
	private final DataOutputStream dos;

	public HttpResponse(OutputStream os) {
		dos = new DataOutputStream(os);
	}

	public void forward(String path) throws IOException {
		dos.writeBytes("HTTP/1.1 200 OK \r\n");
		String contentType = "text/plain";

		if (path.endsWith(".html")) {
			contentType = "text/html";
		} else if (path.endsWith(".css")) {
			contentType = "text/css";
		} else if (path.endsWith(".js")) {
			contentType = "text/javascript";
		} else if (path.endsWith(".woff")) {
			contentType = "text/font";
		}

		dos.writeBytes(format("Content-Type: %s;charset=utf-8\r\n", contentType));
		byte[] body = Files.readAllBytes(new File("./webapp" + replaceDefaultPageIfNull(path)).toPath());
		dos.writeBytes("Content-Length: " + body.length + "\r\n");
		writeHeaders(dos);
		dos.writeBytes("\r\n");
		dos.write(body, 0, body.length);
		dos.flush();
	}

	private void writeHeaders(DataOutputStream dos) throws IOException {
		for (Entry<String, String> header : headers.entrySet()) {
			dos.writeBytes(format("%s: %s", header.getKey(),header.getValue()));
		}
	}

	// Redirect는 content-type 을 쓰지 않는다.
	public void sendRedirect(String path) throws IOException {
		dos.writeBytes("HTTP/1.1 302 Found \r\n");
		dos.writeBytes(format("Location: %s\r\n", path));
		writeHeaders(dos);
		dos.writeBytes("\r\n");
	}

	public void addHeader(String key, String value) {
		headers.put(key, value);
	}

	private String replaceDefaultPageIfNull(String requestPath) {
		return Objects.isNull(requestPath) || "/".equals(requestPath) ? "/index.html" : requestPath;
	}

}
