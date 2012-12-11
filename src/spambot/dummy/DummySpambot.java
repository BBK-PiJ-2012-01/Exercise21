package spambot.dummy;

import spambot.Crawler;
import spambot.SpamBot;

import java.net.MalformedURLException;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: eatmuchpie
 * Date: 10/12/2012
 * Time: 15:23
 * To change this template use File | Settings | File Templates.
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
