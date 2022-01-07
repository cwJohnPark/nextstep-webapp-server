package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import model.User;
import util.HttpRequestUtils;

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
			String line = br.readLine();
			if (line == null) {
				return;
			}
			log.info("request line: {}", line);

			final String url = line.split("\\s")[1];

			String requestPath = HttpRequestUtils.parseRequestPath(url);
			String requestParams = HttpRequestUtils.parseRequestParam(url);

			final User user = User.from(HttpRequestUtils.parseQueryString(requestParams));

			log.info("Request User = {}", user);

			while (!"".equals(line)) {
				line = br.readLine();
				log.info(line);
			}

			final byte[] body;
			if (requestPath.equals("/user/create")) {
				body = Files.readAllBytes(new File("./webapp/user/form.html").toPath());
			} else {
				body = Files.readAllBytes(new File("./webapp" + requestPath).toPath());
			}



			final DataOutputStream dos = new DataOutputStream(out);
			response200Header(dos, body.length);
			responseBody(dos, body);

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) throws IOException {
		dos.write(body, 0, body.length);
		dos.writeBytes("\r\n");
		dos.flush();
	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) throws IOException {
		dos.writeBytes("HTTP/1.1 200 OK \r\n");
		dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
		dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
		dos.writeBytes("\r\n");
	}
}
