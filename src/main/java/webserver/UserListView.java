package webserver;

import static java.lang.String.*;

import java.util.Collection;

import model.User;

public class UserListView {

	public static byte[] createHTML(Collection<User> userList) {
		final StringBuilder sb = new StringBuilder();
		sb.append("<!DOCTYPE html>\n");
		sb.append("<html lang=\"kr\">\n");
		sb.append("<head>\n");
		sb.append("    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n");
		sb.append("    <meta charset=\"utf-8\">\n");
		sb.append("    <title>User List</title>\n");
		sb.append("</head>\n");
		sb.append("<body>\n");
		userList.forEach(
			user -> {
				sb.append("<h3>");
				sb.append(format("Id: %s\n", user.getUserId()));
				sb.append(format("Name: %s\n", user.getName()));
				sb.append(format("Email: %s\n", user.getEmail()));
				sb.append("</h3>");
			}
		);
		sb.append("</body>\n");
		sb.append("</html>");
		return sb.toString().getBytes();
	}
}
