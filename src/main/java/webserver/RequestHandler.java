package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import webserver.header.HttpRequestHeader;

public class RequestHandler extends Thread {
	private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
	private final Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void start() {
		log.debug("New Client Connect! Connected IP : {}, Port: {}",
			connection.getInetAddress(), connection.getPort());

		try (InputStream in = connection.getInputStream();
			 OutputStream out = connection.getOutputStream()) {

			final HttpRequest httpRequest = new HttpRequest(in);

			final HttpResponse httpResponse = new HttpResponse(out);

			String requestPath = httpRequest.getPath();

			// Join
			if (requestPath.equals("/user/create")) {
				joinUser(httpRequest);
				httpResponse.sendRedirect("/index.html");
				return;
			}

			// Login
			if (requestPath.equals("/user/login")) {
				final boolean isLoginSuccess = readUserLoginRequest(httpRequest);
				final String redirectPath = isLoginSuccess ? "/index.html" : "/user/login_failed.html";

				return;
			}

			// User List
			if (requestPath.equals("/user/list")) {

			}

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	public void joinUser(HttpRequest httpRequest) throws IOException {
		User user = User.from(httpRequest.getParameters());
		log.info("Request User = {}", user);
		DataBase.addUser(user);
	}

	private boolean readUserLoginRequest(HttpRequest httpRequest) throws IOException {
		boolean isLogined = Boolean.parseBoolean(httpRequest.getHeader("isLogined"));
		log.info("isLogined?={}", isLogined);

		final User requestUser = User.from(httpRequest.getParameters());

		final User loginUser = DataBase.findUserById(requestUser.getUserId());

		if (Objects.isNull(loginUser) || !loginUser.matchPassword(requestUser)) {
			log.info("User Login Failed: {}", requestUser);
			return false;
		}

		log.info("Success User Login: {}", requestUser);
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

}
