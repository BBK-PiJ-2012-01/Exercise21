package spambot;

import spambot.dummy.CrawlerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

/**
 * Implementation of SpamBot.
 *
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 10/12/2012
 * Time: 16:53
 *
 * This implements SpamBot.  It manages many crawlers (one crawler per thread) as
 * determined by 'setThreads' (the default is the system's maximum number of processors).
 *
 * The crawlers find the links and emails from their seed webpages, and here they are
 * combined (removing duplicates).  Links not yet checked that are within the original
 * seed URL's base (eg. the base of "http://google.com/foo?bar=1" is "http://google.com")
 * are then given to available crawlers.
 *
 * Whenever possible it waits for crawlers to do their work (instead of wastefully checking
 * if they're done yet over and over).
 */
public class SpamBotImpl implements SpamBot {
    private final CrawlerFactory factory;
    private String seedUrl;
    private int crawler_count = 0;
    private Queue<Crawler> crawlers = new LinkedList<Crawler>();
    private Set<String> links = new HashSet<String>();
    private Set<String> emails = new HashSet<String>();

    public SpamBotImpl(CrawlerFactory factory) {
        this.factory = factory;
        setThreads(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public void setSeed(String seedUrl) throws MalformedURLException {
        // Check the seedUrl is valid
        new URL(seedUrl);

        this.seedUrl = seedUrl;
    }

    @Override
    public String getSeed() {
        return seedUrl;
    }

    @Override
    public void setThreads(int count) {
        if (count < 1)
            throw new RuntimeException("Can't set number of threads to less than 1");

        if (count < crawler_count)
            deleteThreads(crawler_count - count);
        else if (count > crawler_count)
            createThreads(count - crawler_count);

        crawler_count = count;
    }

    /**
     * Deletes the given number of threads (ie. crawlers).
     *
     * @param count The number of threads to kill
     */
    private void deleteThreads(int count) {
        for (int i = 0; i < count; ++i) {
            Crawler crawler = crawlers.remove();
            crawler.kill();
        }
    }

    /**
     * Creates the given number of threads (ie. crawlers).
     *
     * @param count The number of threads to create
     */
    private void createThreads(int count) {
        for (int i = 0; i < count; ++i) {
            Crawler crawler = factory.create(this);
            new Thread(crawler).start();
            crawlers.add(crawler);
        }
    }

    @Override
    public synchronized void scanSite() {
        Queue<String> untried_links = new LinkedList<String>();
        Set<String> all_links = new HashSet<String>();

        int index_of_final_slash = seedUrl.lastIndexOf('/');
        String base_url;

        if (index_of_final_slash <= "http://".length())
            base_url = seedUrl;
        else
            base_url = seedUrl.substring(0, index_of_final_slash);

        untried_links.add(seedUrl);
        int crawlers_crawling;

        do {
            crawlers_crawling = 0;
            all_links.clear();
            all_links.addAll(links);

            for (Crawler crawler : crawlers){
                if (crawler.isCrawling()) {
                    ++crawlers_crawling;
                } else {
                    // Get data from crawler
                    all_links.addAll(crawler.getLinks());
                    emails.addAll(crawler.getEmails());

                    // If there are links to try, give one to the crawler
                    // and notify it.
                    if (!untried_links.isEmpty()) {
                        String new_seed = untried_links.remove();
                        crawler.setSeed(new_seed);

                        synchronized (crawler) {
                            crawler.notifyAll();
                        }
                        ++crawlers_crawling;
                    }
                }
            }

            // Remove all tried links from 'all_links'
            all_links.removeAll(links);

            // Add remaining links which start with base_url to 'untried_links'...
            for (String link : all_links) {
                if (link.startsWith(base_url))
                    untried_links.add(link);
            }

            // ... and to 'all_links' (so the next loop knows not to try these again).
            links.addAll(all_links);

            // If all crawlers are crawling, or there are NO MORE links to try but some crawlers are
            // still crawling (and thus could return new links) ...
            if (crawlers_crawling == crawler_count || (untried_links.isEmpty() && crawlers_crawling > 0)) {
                // ...wait for one of them to finish and notify 'this'.
                try {
                    wait();
                } catch (InterruptedException e) {
                    // If interrupted, the do-while loop I'm already in will do nothing
                    // except wait() again, so there's no need for another do-while here.
                    System.out.println("Crawler interrupted, not a problem.");
                }
            }

        } while(!untried_links.isEmpty() || crawlers_crawling != 0);
    }

    @Override
    public Set<String> getEMails() {
        return Collections.unmodifiableSet(emails);
    }

    /**
     * A small script to test the SpamBot interactively.
     *
     * @param args Not used.
     */
    public static void main(String[] args) {
        String input, seed = "";
        int max_threads;
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        do {
            System.out.print("\nEnter seed url (or enter nothing to quit): ");
            try {
                seed = br.readLine();
            } catch (IOException e) {
                System.out.println("IOExcepion caught, try again...");
                continue;
            }

            System.out.print("Enter maximum concurrently-runnning crawlers: ");
            try {
                input = br.readLine();
                max_threads = Integer.valueOf(input);
            } catch (IOException e) {
                System.out.println("IOExcepion caught, try again...");
                continue;
            }

            SpamBot spam_bot = new SpamBotImpl(CrawlerFactory.getReal());
            try {
                spam_bot.setSeed(seed);
            } catch (MalformedURLException e) {
                System.out.println("That url was not accepted.  Remember to specify the protocol (eg. 'http://'");
                continue;
            }

            spam_bot.setThreads(max_threads);

            System.out.format("Starting to scan website '%s' with at most %d crawlers\n", seed, max_threads);
            long start = System.currentTimeMillis();
            spam_bot.scanSite();
            long stop = System.currentTimeMillis();
            Set<String> emails = spam_bot.getEMails();
            System.out.format("Took %dms to find %d email addresses :\n", (stop - start), emails.size());

            for (String email : emails) {
                System.out.println(email);
            }


        } while (!seed.isEmpty());
    }
}
