package spambot;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: eatmuchpie
 * Date: 10/12/2012
 * Time: 16:57
 * To change this template use File | Settings | File Templates.
 */
public class SpamBotImplTest {
    private SpamBot spam_bot;
    private String url;

    @Before
    public void setUp() throws Exception {
        spam_bot = new SpamBotImpl();

    }

    @Test
    public void testSetSeed() throws Exception {
        spam_bot.setSeed("test");
    }

    @Test
    public void testGetSeed() throws Exception {
        assertEquals("test", spam_bot.getSeed());
    }

    @Test
    public void testSetThreads() throws Exception {

    }

    @Test
    public void testScanSite() throws Exception {

    }

    @Test
    public void testGetEMails() throws Exception {

    }


}
