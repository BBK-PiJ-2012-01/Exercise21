package spambot;

import org.junit.Before;
import org.junit.Test;
import spambot.dummy.CrawlerFactory;
import spambot.dummy.DummyWebPage;

import java.net.MalformedURLException;

import static org.junit.Assert.*;

/**
 * Tests the SpamBotImpl class.
 *
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 10/12/2012
 * Time: 16:57
 */
public class SpamBotImplTest {
    private SpamBot spam_bot;
    private String url = "http://correct.com";
    private final static int waiting_timeout = 10000;

    @Before
    public void setUp() throws Exception {
        spam_bot = new SpamBotImpl(CrawlerFactory.getTest());

    }

    @Test(expected = MalformedURLException.class)
    public void testSetBadSeed() throws Exception {
        spam_bot.setSeed("not a valid URL");
    }

    @Test(expected = MalformedURLException.class)
    public void testSetSeedWithoutProtocol() throws Exception {
        spam_bot.setSeed("www.dcs.bbk.ac.uk");
    }

    @Test
    public void testSetSeed() throws Exception {
        spam_bot.setSeed("http://www.dcs.bbk.ac.uk");
        spam_bot.setSeed("http://www.dcs.bbk.ac.uk/");
    }

    @Test
    public void testGetSeed() throws Exception {
        spam_bot.setSeed(url);
        assertEquals(url, spam_bot.getSeed());
    }

    @Test
    public void testMultiThread() throws Exception {
        spam_bot.setThreads(10);
        spam_bot.setSeed(url);
        spam_bot.scanSite();
    }

    @Test(timeout = waiting_timeout)
    public void testScanSite() throws Exception {
        spam_bot.setSeed(url);
        spam_bot.scanSite();
    }

    @Test(timeout = waiting_timeout)
    public void testGetEMails() throws Exception {
        spam_bot.setSeed(url);
        spam_bot.scanSite();
        System.out.println("Spambot got emails: " + spam_bot.getEMails());
        assertEquals(DummyWebPage.ALL_EMAILS, spam_bot.getEMails());
        //assertTrue(spam_bot.getEMails().size() > 0);
    }


}
