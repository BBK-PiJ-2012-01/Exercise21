package spambot;

import spambot.dummy.WebPageFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: eatmuchpie
 * Date: 10/12/2012
 * Time: 13:27
 * To change this template use File | Settings | File Templates.
 */
public class CrawlerImpl implements Crawler {
    private Object status_lock = new Object();
    private boolean crawling = false;
    private String seed;
    private Set<String> links = new HashSet<String>();
    private Set<String> emails = new HashSet<String>();
    private SpamBot spam_bot;
    private boolean finished_with_seed = true;
    private final WebPageFactory factory;

    public CrawlerImpl(WebPageFactory factory) {
        this.factory = factory;
        if (factory == null)
            throw new NullPointerException("Factory can't be null");
    }

    @Override
    public boolean isCrawling() {
        synchronized (status_lock) {
            return crawling;
        }
    }

    @Override
    public synchronized void setSeed(String seed) {
        this.seed = seed;
        finished_with_seed = false;
    }

    @Override
    public synchronized Set<String> getLinks() {
        return Collections.unmodifiableSet(links);
    }

    @Override
    public synchronized Set<String> getEmails() {
        return Collections.unmodifiableSet(emails);
    }

    @Override
    public synchronized void setSpamBot(SpamBot spam_bot) {
        this.spam_bot = spam_bot;
    }

    @Override
    public void run() {

        while(true) {
            synchronized (status_lock) {
                crawling = true;
                links = new HashSet<String>();
                emails = new HashSet<String>();
            }

            if (seed != null) {
                synchronized (this) {
                    WebPage page = factory.create(seed);
                    links = page.getLinks();
                    emails = page.getEmails();
                    System.out.println("Crawler: there are " + links.size() + " links");
                }
            }
            waitForAnotherSeed();
        }
    }

    private synchronized void waitForAnotherSeed() {
        if (spam_bot != null)
            spam_bot.notifyAll();

        finished_with_seed = true;

        synchronized (status_lock) {
            crawling = false;
        }

        while (finished_with_seed) {
            try {
                // Wait for a new seed
                wait();
            } catch (InterruptedException e) {
                System.out.println("Crawler was interrupted...");
            }
        }

    }


}
