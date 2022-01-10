package util;

import static java.lang.String.*;

import java.io.DataOutputStream;
import java.io.IOException;

public class HttpResponseUtils {

	public static void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) throws
		IOException {
		dos.writeBytes("HTTP/1.1 200 OK \r\n");
		dos.writeBytes(format("Content-Type: %s;charset=utf-8\r\n", contentType));
		dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
		dos.writeBytes("\r\n");
	}

	public static void response302Header(DataOutputStream dos, String redirectPath) throws IOException {
		dos.writeBytes("HTTP/1.1 302 Found \r\n");
		dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
		dos.writeBytes(format("Location: %s\r\n", redirectPath));
		dos.writeBytes("\r\n");
	}

	public static void response302Header(DataOutputStream dos, String redirectPath, boolean isLogined) throws IOException {
		dos.writeBytes("HTTP/1.1 302 Found \r\n");
		dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
		dos.writeBytes(format("Location: %s\r\n", redirectPath));
		dos.writeBytes(getCookieResponseLine(isLogined));
		dos.writeBytes("\r\n");
	}

	public static String getCookieResponseLine(boolean isLoginSuccess) throws IOException {
		return format("Set-Cookie: logined=%s", isLoginSuccess ? "true" : "false");
	}
}
