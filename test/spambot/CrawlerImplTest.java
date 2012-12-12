package spambot;

import org.junit.Before;
import org.junit.Test;
import spambot.dummy.DummySpambot;
import spambot.dummy.WebPageFactory;

import static org.junit.Assert.*;

/**
 * Tests the CrawlerImpl class.
 *
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 10/12/2012
 * Time: 13:27
 */
public class CrawlerImplTest {
    private Crawler crawler;
    private String url = "www.dcs.bbk.ac.uk";
    private static final int waiting_timeout = 2000;
    private WebPageFactory factory = WebPageFactory.getTest();
    private DummySpambot bot;

    @Before
    public void setUp() throws Exception {
        bot = new DummySpambot();
        crawler = new CrawlerImpl(bot, factory);
    }

    @Test
    public void testCrawlerDoesSomething() throws Exception {
        crawler.setSeed(url);
        new Thread(crawler).start();
        Thread.sleep(100);
        assertTrue(crawler.isCrawling());
        Thread.sleep(1000);
        assertFalse(crawler.isCrawling());
    }

    @Test(timeout = waiting_timeout)
    public void testCrawlerStopsTimeout() {
        crawler.setSeed(url);
        runCrawlerToCompletion();
    }

    @Test
    public void testLinksInitiallyEmpty() {
        assertTrue(crawler.getLinks().isEmpty());
    }

    @Test(timeout = waiting_timeout)
    public void testGetLinks() throws Exception {
        crawler.setSeed(url);
        runCrawlerToCompletion();
        //System.out.println("Found " + crawler.getLinks().size() + " links in crawler");
        //assertFalse(crawler.getLinks().isEmpty());

        assertEquals(3, crawler.getLinks().size());
    }

    @Test
    public void testEmailsInitiallyEmpty() {
        assertTrue(crawler.getEmails().isEmpty());
    }

    @Test(timeout = waiting_timeout)
    public void testGetEmails() throws Exception {
        crawler.setSeed(url);
        runCrawlerToCompletion();
        //System.out.println("Found " + crawler.getEmails().size() + " emails in crawler");
        //assertEquals(3, crawler.getLinks().size());
        System.out.println("Checking emails size...");
        assertEquals(3, crawler.getEmails().size());
    }

    @Test(timeout = waiting_timeout)
    public void testSetSpamBot() throws Exception {
        crawler.setSeed(url);
        new Thread(crawler).start();

        bot.waitForCrawlerToFinish(crawler);
    }

    private void runCrawlerToCompletion() {
        new Thread(crawler).start();
        bot.waitForCrawlerToFinish(crawler);
    }

}





