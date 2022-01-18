package webserver;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.Test;

import webserver.request.HttpRequest;

public class HttpRequestTest{
	private final String testDirectory = "./src/test/resources/";

	@Test
	public void request_GET() throws Exception {
		final HttpRequest request = validateRequest(testDirectory + "Http_GET.txt");

		assertEquals(HttpMethod.GET, request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("jpark", request.getParameter("userId"));
	}

	@Test
	public void request_POST() throws Exception {
		final HttpRequest request = validateRequest(testDirectory+ "Http_POST.txt");

		assertEquals(HttpMethod.POST, request.getMethod());
		assertEquals("/user/create", request.getPath());
		assertEquals("keep-alive", request.getHeader("Connection"));
		assertEquals("jpark", request.getParameter("userId"));
	}

	HttpRequest validateRequest(String requestFilePath) throws Exception {
		InputStream in = new FileInputStream(new File(requestFilePath));
		return new HttpRequest(in);
	}
}