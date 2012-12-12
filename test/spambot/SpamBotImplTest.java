package spambot;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.junit.Before;
import org.junit.Test;
import spambot.dummy.CrawlerFactory;
import spambot.dummy.DummyWebPage;

import java.net.MalformedURLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    @Test
    public void testCondenseUrl() throws Exception {
        assertEquals("http://bob.com/a/b/c", condenseUrl("http://bob.com////a//b/c/"));
    }
    
    @Test
    public void testCondenseUrlUsage() throws Exception {
        spam_bot.setSeed(url);
        spam_bot.scanSite();
        
        List<String> links_containing_three = new LinkedList<String>();
        for (String link : getLinks()) {
            if (link.contains("three"))
                links_containing_three.add(link);
        }
        
        assertEquals(1, links_containing_three.size());
    }
    
    private List<String> getLinks() {
        Field links_field;
        List<String> links;
        
        try {
            links_field = SpamBotImpl.class.getField("links");
            links_field.setAccessible(true);
            links = (List<String>) links_field.get(spam_bot);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Bad argument in getting links", ex);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Couldn't access links", ex);
        } catch (NoSuchFieldException ex) {
            throw new RuntimeException("Couldn't find links", ex);
        } catch (SecurityException ex) {
            throw new RuntimeException("Couldn't access links", ex);
        }
        
        return links;
    }
    
    private String condenseUrl(String url) {
        Method m;
        String result;
        try {
            m = SpamBotImpl.class.getMethod("condenseUrl", String.class);
            m.setAccessible(true);
            result = (String) m.invoke(spam_bot, url);
        } catch (IllegalAccessException ex) {
            throw new RuntimeException("Couldn't access condenseUrl method", ex);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Couldn't access condenseUrl method", ex);
        } catch (InvocationTargetException ex) {
            throw new RuntimeException("Couldn't access condenseUrl method", ex);
        } catch (NoSuchMethodException ex) {
            throw new RuntimeException("Couldn't find condenseUrl method", ex);
        } catch (SecurityException ex) {
            throw new RuntimeException("Couldn't access condenseUrl method", ex);
        }
        
        return result;
    }
}
