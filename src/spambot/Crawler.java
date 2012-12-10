package spambot;

import java.net.URL;
import java.util.Set;

/**
 * A crawler is typically an autonomous agent that runs in a separate thread.
 * In terms of behaviour there are no specific requirements, hence the absence
 * of any suggestion of an interface.
 *
 * Crawlers have to interact with three types of data. First, they need to
 * know / remember all the links to be visited; they also need to know / remember
 * those links that have already been visited, so as to not visit and parse the
 * same page twice. The third type of data they need to store are the emails they
 * get from webpages.
 *
 * Crawlers need a way to terminate their execution. It is up to you to decide
 * when crawlers will end. Some possibilities for your considerations are time
 * limits, number of email addresses found, number of unsuccessful attempts to
 * read a link, or to find new links.
 *
 * In a first stage of design, it may help to have only one active crawler (i.e.
 * one thread). Once the functionality is ready, you should be able to span several
 * crawlers in parallel, all of them sharing the information described above. You
 * should at least launch a crawler per processor in your machine, and possibly
 * more, because I/O waits over the network connection will make your crawlers
 * waste a lot of time.
 */
public interface Crawler extends Runnable {

    /**
     * If the crawler is waiting for a new seed (because it has just been
     * initialised, or it has finished with its old seed) then this is false,
     * and the crawler object is waiting.  Otherwise, true (and the crawler
     * is running in another thread).
     *
     * @return Whether the crawler is still crawling.
     */
    boolean isCrawling();

    /**
     * Sets the crawler's seed url.  If the crawler is running, this will hang
     * until it is done (to avoid this, check that "crawler.isReadyForSeed()").
     *
     * @param seed The new seed url.
     */
    void setSeed(String seed);

    /**
     * Gets the links recovered from the crawler.
     * This will hang until the crawler stops running.
     *
     * @return The links the crawler found.
     */
    Set<String> getLinks();

    /**
     * Gets the emails recovered from the crawler.
     * This will hang until the crawler stops running.
     *
     * @return The emails the crawler found.
     */
    Set<String> getEmails();

    /**
     * When the crawler has finished crawling, it will notify the spam_bot
     *
     * @param spam_bot
     */
    void setSpamBot(SpamBot spam_bot);

}