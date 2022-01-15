package webserver;

import static java.lang.String.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;

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
				httpResponse.setCookie(format("isLogined=%s", isLoginSuccess));
				httpResponse.sendRedirect(redirectPath);
				return;
			}

			// User List
			if (requestPath.equals("/user/list")) {
				responseGetUserList(httpRequest, httpResponse);
				return;
			}

			httpResponse.forward(httpRequest.getPath());

		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}

	private void responseGetUserList(HttpRequest httpRequest, HttpResponse httpResponse) {
		final boolean isLogined = isUserLogined(httpRequest);
		if (!isLogined) {
			httpResponse.sendRedirect("/user/login.html");
			return;
		}

		final byte[] body = UserListView.createHTML(DataBase.findAll());
		httpResponse.forwardBody(body);
	}

	public void joinUser(HttpRequest httpRequest) {
		User user = User.from(httpRequest.getParameters());
		log.info("Request User = {}", user);
		DataBase.addUser(user);
	}

	private boolean readUserLoginRequest(HttpRequest httpRequest) throws IOException {

		final User requestUser = User.from(httpRequest.getParameters());

		final User loginUser = DataBase.findUserById(requestUser.getUserId());

		if (Objects.isNull(loginUser) || !loginUser.matchPassword(requestUser)) {
			log.info("User Login Failed: {}", requestUser);
			return false;
		}

		log.info("Success User Login: {}", requestUser);
		return true;
	}

	private boolean isUserLogined(HttpRequest httpRequest) {
		boolean isLogined = false;
		final String CookieIsLoginedKey = "isLogined";
		if (!Objects.isNull(httpRequest.getCookie(CookieIsLoginedKey))) {
			isLogined = Boolean.parseBoolean(httpRequest.getCookie(CookieIsLoginedKey));
		}

		log.info("isLogined?={}", isLogined);
		return isLogined;
	}

}
