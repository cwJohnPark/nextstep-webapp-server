package webserver;

import static java.lang.String.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void start() {
		log.debug("New Client Connect! Connected IP : {}, Port: {}",
			connection.getInetAddress(), connection.getPort());

		try (InputStream in = connection.getInputStream();
			 OutputStream out = connection.getOutputStream()) {
			final BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
			String requestPath = readRequestPath(br);

			final DataOutputStream dos = new DataOutputStream(out);

			if (requestPath.equals("/user/create")) {
				final boolean isCreate = readUserCreateRequest(br);
				response302Header(dos, "/index.html");
				return;
			}

			final byte[] body;
			String responseContentType = "text/html";
			body = Files.readAllBytes(new File("./webapp" + requestPath).toPath());
			if (requestPath.endsWith(".css")) {
				responseContentType = "text/css";
			} else if (requestPath.endsWith(".js")) {
				responseContentType = "text/javascript";
			} else if (requestPath.endsWith(".woff")) {
				responseContentType = "text/font";
			}

			response200Header(dos, body.length, responseContentType);

			responseBody(dos, body);

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private String readRequestPath(BufferedReader br) throws IOException {
		String line = br.readLine();
		if (Objects.isNull(line)) {
			return "";
		}
		log.info("request line: {}", line);
		return line.split("\\s")[1];
	}

	private boolean readUserCreateRequest(BufferedReader br) throws IOException {
		String line = br.readLine();

		// read header
		int contentLength = -1;
		while (!"".equals(line)) {
			line = br.readLine();
			if (line.startsWith("Content-Length")) {
				contentLength = Integer.parseInt(HttpRequestUtils.parseHeader(line).getValue());
			}
			log.info(line);
		}

		// read body
		if (contentLength == -1) {
			return false;
		}
		String body = IOUtils.readData(br, contentLength);
		final User user = User.from(HttpRequestUtils.parseQueryString(body));
		log.info("Request User = {}", user);
		return true;
	}

	private void responseBody(DataOutputStream dos, byte[] body) throws IOException {
		dos.write(body, 0, body.length);
		dos.writeBytes("\r\n");
		dos.flush();
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) throws IOException {
		dos.writeBytes("HTTP/1.1 200 OK \r\n");
		dos.writeBytes(format("Content-Type: %s;charset=utf-8\r\n", contentType));
		dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
		dos.writeBytes("\r\n");
	}


	private void response302Header(DataOutputStream dos, String redirectPath) throws IOException {
		dos.writeBytes("HTTP/1.1 302 Found \r\n");
		dos.writeBytes(format("Content-Type: text/html;charset=utf-8\r\n"));
		dos.writeBytes(format("Location: %s\r\n", redirectPath));
		dos.writeBytes("\r\n");
	}
}
