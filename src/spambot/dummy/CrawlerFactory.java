package spambot.dummy;

import spambot.Crawler;
import spambot.CrawlerImpl;
import spambot.SpamBot;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 11/12/2012
 * Time: 15:55
 *
 * Factory class for creating Crawler implementations.  Currently only
 * used to specify whether the CrawlerImpl uses the DummyWebPage or the
 * WebPageImpl class (ie. for testing purposes).
 */
public class CrawlerFactory {
    private boolean test = false;

    public static CrawlerFactory getReal() {
        return new CrawlerFactory(false);
    }

    public static CrawlerFactory getTest() {
        return new CrawlerFactory(true);
    }


    private CrawlerFactory(boolean test) {
        this.test = test;
    }

    public Crawler create(SpamBot spam_bot) {
        if (test)
            return new CrawlerImpl(spam_bot, WebPageFactory.getTest());
        else
            return new CrawlerImpl(spam_bot, WebPageFactory.getReal());
    }
}
