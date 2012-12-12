package spambot;

import spambot.dummy.WebPageFactory;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Implementation of Crawler.
 *
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 10/12/2012
 * Time: 13:27
 *
 * Implementation of Crawler, which is basically a wrapper for WebPage which allows
 * for multiple Crawler objects to be managed by a single SpamBot (in multiple threads).
 */
public class CrawlerImpl implements Crawler {
    private final Object status_lock = new Object();
    private boolean crawling = true;
    private String seed;
    private Set<String> links = new HashSet<String>();
    private Set<String> emails = new HashSet<String>();
    private final SpamBot spam_bot;
    private boolean finished_with_seed = true;
    private final WebPageFactory factory;
    private boolean killed = false;

    public CrawlerImpl(SpamBot spam_bot, WebPageFactory factory) {
        this.factory = factory;
        this.spam_bot = spam_bot;
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
    public synchronized void kill() {
        killed = true;
    }

    @Override
    public void run() {

        while(true) {
            // lock 'this' so nothing can getLinks() or getEmails() while being populated.
            synchronized (this) {

                // lock 'status_lock' while setting up for the next run, so no-one else
                // is using 'kill()' or 'isCrawling()'.
                synchronized (status_lock) {
                    if (killed)
                        return;

                    crawling = true;
                }

                if (seed != null) {
                    try {
                        WebPage page = factory.create(seed);
                        links = page.getLinks();
                        emails = page.getEmails();
                    } catch (MalformedURLException e) {
                        System.out.println("Seed was not valid: " + seed);
                    }
                } else {
                    // These are new HashSets (instead of just clearing them)
                    // so that previous calls to 'getEmails()' and
                    // 'getLinks()' return immutable sets.
                    links = new HashSet<String>();
                    emails = new HashSet<String>();
                }
            }

            // This is not inside the synchronized block as it involves two-way
            // notifications (between it and the spam_bot).
            waitForAnotherSeed();
        }
    }

    private void waitForAnotherSeed() {

        // These are both done in the synchronized block so that as soon as
        // 'isCrawling()' returns true, the spam_bot can 'setSeed(new_seed)'
        // which in turn sets 'finished_with_seed'.  Had 'finished_with_seed = true'
        // not been in here, it's possible the spam_bot could set if to false,
        // then the following statement would have overriden it, making it hang forever.
        synchronized (status_lock) {
            crawling = false;
            finished_with_seed = true;
        }

        // The crawler hanging here waiting for synchronisation due to
        // other crawlers simultaneously trying to do spam_bot.notifyAll()
        // has the same effect as waiting at the proceeding 'wait()', because
        // isCrawling() == false, so the spam_bot will do 'setSeet(new_url)'
        // to give this another seed, and in doing so will set
        // 'finished_with_seed = false', thus skipping the 'wait()'.

        if (spam_bot != null)
            synchronized (spam_bot) {
                spam_bot.notifyAll();
            }

        while (finished_with_seed) {
            try {
                // Wait for a new seed
                synchronized (this) {
                    wait();
                }
            } catch (InterruptedException e) {
                System.out.println("Crawler was interrupted...");
            }
        }
    }
}
