package webserver.header;

import static org.junit.Assert.*;

import org.junit.Test;

public class CookieTest {

	@Test
	public void cookieParsingTest() throws Exception {
		String savedCookie = "logined=true; isLogined=false";
		final Cookie cookie = new Cookie(savedCookie);
		assertEquals(2, cookie.size());
		assertEquals("true", cookie.get("logined"));
		assertEquals("false", cookie.get("isLogined"));
	}

}