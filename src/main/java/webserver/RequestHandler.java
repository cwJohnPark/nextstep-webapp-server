package webserver;

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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.HttpResponseUtils;
import util.IOUtils;
import webserver.header.HttpRequestHeader;

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

			// Join
			if (requestPath.equals("/user/create")) {
				final boolean isCreate = readUserCreateRequest(br);
				HttpResponseUtils.response302Header(dos, "/index.html");
				return;
			}

			// Login
			if (requestPath.equals("/user/login")) {
				final boolean isLoginSuccess = readUserLoginRequest(br);
				final String redirectPath = isLoginSuccess ? "/index.html" : "/user/login_failed.html";

				HttpResponseUtils.response302Header(dos, redirectPath, isLoginSuccess);
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

			HttpResponseUtils.response200Header(dos, body.length, responseContentType);

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

	private boolean readUserLoginRequest(BufferedReader br) throws IOException {
		final HttpRequestHeader header = readHeader(br);
		int contentLength = header.getContentLength();

		log.info("isLogined?={}", header.isLogined());

		if (contentLength == -1) {
			return false;
		}

		final User requestUser = User.from(
			HttpRequestUtils.parseQueryString(
				IOUtils.readData(br, contentLength)));

		final User loginUser = DataBase.findUserById(requestUser.getUserId());
		if (Objects.isNull(loginUser) || !loginUser.matchPassword(requestUser)) {
			log.info("User Login Failed: {}", requestUser);
			return false;
		}

		log.info("Success User Login: {}", requestUser);
		return true;
	}

	private boolean readUserCreateRequest(BufferedReader br) throws IOException {
		// read header
		int contentLength = readHeader(br).getContentLength();

		// read body
		if (contentLength == -1) {
			return false;
		}
		String body = IOUtils.readData(br, contentLength);
		final User user = User.from(HttpRequestUtils.parseQueryString(body));
		log.info("Request User = {}", user);
		DataBase.addUser(user);
		return true;
	}

	private HttpRequestHeader readHeader(BufferedReader br) throws IOException {
		final List<String> lines = new ArrayList<>();
		String line = br.readLine();
		lines.add(line);
		while (!"".equals(line)) {
			line = br.readLine();
			lines.add(line);
			log.info(line);
		}
		return HttpRequestHeader.parseHeaderLines(lines);
	}

	private void responseBody(DataOutputStream dos, byte[] body) throws IOException {
		dos.write(body, 0, body.length);
		dos.writeBytes("\r\n");
		dos.flush();
	}


}
