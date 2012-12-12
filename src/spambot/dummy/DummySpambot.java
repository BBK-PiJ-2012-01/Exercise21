package spambot.dummy;

import spambot.Crawler;
import spambot.SpamBot;

import java.net.MalformedURLException;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 10/12/2012
 * Time: 15:23
 *
 * This dummy spambot has a method 'waitForCrawlerToFinish' which waits
 * for the crawler to notify this.  Otherwise it hangs.
 *
 * It is used in testing the CrawlerImpl class.
 */
public class DummySpambot implements SpamBot {

    @Override
    public void setSeed(String seedUrl) throws MalformedURLException {
    }

    @Override
    public String getSeed() {
        return null;
    }

    @Override
    public void setThreads(int count) {
    }

    @Override
    public void scanSite() {
    }

    @Override
    public Set<String> getEMails() {
        return null;
    }

    /**
     * Waits for the given crawler to stop crawling and notify this spambot.
     *
     * @param crawler The crawler to wait for.
     */
    public synchronized void waitForCrawlerToFinish(Crawler crawler) {
        while (crawler.isCrawling()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Interrupted, oh well...");
            }
            System.out.println("Crawler ended");
        }
    }
}
