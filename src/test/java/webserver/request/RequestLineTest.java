package webserver.request;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

import webserver.HttpMethod;

public class RequestLineTest {

	@Test
	public void createMethod() {
		final RequestLine requestLineGet = new RequestLine("GET /index.html HTTP/1.1");
		assertEquals(HttpMethod.GET, requestLineGet.getMethod());
		assertEquals("/index.html", requestLineGet.getPath());
		final RequestLine requestLinePost = new RequestLine("POST /user/create HTTP/1.1");

		assertEquals(HttpMethod.POST, requestLinePost.getMethod());
		assertEquals("/user/create", requestLinePost.getPath());
	}

	@Test
	public void requestParameter() {
		final RequestLine requestLineGet = new RequestLine("GET /user?userId=1&username=Park&password=1234 HTTP/1.1");
		assertEquals(HttpMethod.GET, requestLineGet.getMethod());
		assertEquals("/user", requestLineGet.getPath());
		final Map<String, String> paramsGet = requestLineGet.getParams();
		assertEquals("1", paramsGet.get("userId"));
		assertEquals("Park", paramsGet.get("username"));
		assertEquals("1234", paramsGet.get("password"));

		final RequestLine requestLinePost = new RequestLine("POST /user/create?userId=1&username=Park&password=1234 HTTP/1.1");

		assertEquals(HttpMethod.POST, requestLinePost.getMethod());
		assertEquals("/user/create", requestLinePost.getPath());
		final Map<String, String> paramsPost = requestLineGet.getParams();
		assertEquals("1", paramsPost.get("userId"));
		assertEquals("Park", paramsPost.get("username"));
		assertEquals("1234", paramsPost.get("password"));

	}

	@Test
	public void invalidRequestLineIllegalArguments() {
		try {
			new RequestLine("GET /index.html");
			fail("It must throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// success
		}
	}


}