package spambot;

import org.junit.*;
import static org.junit.Assert.*;
import spambot.dummy.StreamFactory;
//import spambot.WebPageImpl;

public class WebPageImplTest {
	@Test
	public void testsItAll() throws Exception {
		WebPageImpl wp = new WebPageImpl(StreamFactory.STARTING_URL, StreamFactory.getTest());
	}

}