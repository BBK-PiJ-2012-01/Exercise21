package spambot;

import org.junit.*;
import static org.junit.Assert.*;
import spambot.WebPageImpl;

public class WebPageImplTest {
	@Test
	public void testsGetContents() {
		WebPageImpl wp = new WebPageImpl("www.google.com");
		wp.getContents();
	}
	@Test
	public void testsAnalyse() {

	}
	@Test
	public void testsGetUrl() {

	}
	@Test
	public void testsGetLinks() {

	}
	@Test
	public void testsGetEmails() {

	}
}