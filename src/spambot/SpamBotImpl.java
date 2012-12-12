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
    private int timeout_ms = -1;

    public SpamBotImpl(CrawlerFactory factory) {
        this.factory = factory;
        setThreads(Runtime.getRuntime().availableProcessors());
    }

    @Override
    public void setSeed(String seedUrl) throws MalformedURLException {
        // Check the seedUrl is valid
        new URL(seedUrl);

        this.seedUrl = condenseUrl(seedUrl);
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
        Set<String> new_links = new HashSet<String>();
        List<String> reformatted_new_links = new LinkedList<String>();
    
        int index_of_final_slash = seedUrl.lastIndexOf('/');
        String base_url;

        if (index_of_final_slash <= "https://".length())
            base_url = seedUrl;
        else
            base_url = seedUrl.substring(0, index_of_final_slash);

        untried_links.add(seedUrl);
        int crawlers_crawling;
        long start_time = System.currentTimeMillis();

        do {
            crawlers_crawling = 0;
            new_links.clear();
            reformatted_new_links.clear();
            
            for (Crawler crawler : crawlers){
                if (crawler.isCrawling()) {
                    ++crawlers_crawling;
                } else {
                    // Get data from crawler
                    new_links.addAll(crawler.getLinks());
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
            
            // For all 'new_links' which start with the 'base_url',
            // Condense multiple '/' (eg. "http://google.com///dir//2/"
            // becomes "http://google.com/dir/2") and put into
            // 'reformatted_new_links'.
            
            for (String link : new_links) {
                if (link.startsWith(base_url)) {
                    reformatted_new_links.add(condenseUrl(link));
                }
            }
            
            // Remove all tried links from 'reformatted_new_links'
            reformatted_new_links.removeAll(links);

            // Add remaining links to 'untried_links'...
            untried_links.addAll(reformatted_new_links);

            // ... and to 'new_links' (so the next loop knows not to try these again).
            links.addAll(reformatted_new_links);

            // If all crawlers are crawling, or there are NO MORE links to try but some crawlers are
            // still crawling (and thus could return new links) ...
            if (crawlers_crawling == crawler_count || (untried_links.isEmpty() && crawlers_crawling > 0)) {
                // ...wait for one of them to finish and notify 'this'.
                try {
                    if (timeout_ms > 0) {
                        // If no timeout is set, wait forever:
                        wait();
                    } else {
                        // otherwise, wait only until the timeout
                        long remaining_time = timeout_ms - (System.currentTimeMillis() - start_time);
                        if (remaining_time > 0)
                            wait();
                    }
                } catch (InterruptedException e) {
                    // If interrupted, the do-while loop I'm already in will do nothing
                    // except wait() again, so there's no need for another do-while here.
                    System.out.println("Crawler interrupted, not a problem.");
                }
            }
            
            // If we've exceeded the timeout, don't try more links (wait for
            // crawlers to end)
            if (timeout_ms > 0 && System.currentTimeMillis() - start_time > timeout_ms) {
                untried_links.clear();
            }

        } while(!untried_links.isEmpty() || crawlers_crawling != 0);
    }
    
    /**
     * Removes duplicate '/' from the given url.
     * 
     * @param url The url to condense.
     * @return The condensed url.
     */
    public String condenseUrl(String url) {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(url.substring(0, 10));
        boolean found_slash = false;
        
        for (char ch : url.substring(10).toCharArray()) {
            if (ch == '/') {
                if (!found_slash) {
                    found_slash = true;
                    sbuf.append(ch);
                }
            } else {
                sbuf.append(ch);
                found_slash = false;
            }
        }
        
        if (url.endsWith("/"))
            sbuf.deleteCharAt(sbuf.length() - 1);
        
        return sbuf.toString();
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
        int timeout;
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
            } catch (java.lang.NumberFormatException e) {
                System.out.println("That's not a number!");
                continue;
            }
            
            System.out.print("Enter maximum time (in ms) to run for (negative for no timeout): ");
            try {
                input = br.readLine();
                timeout = Integer.valueOf(input);
            } catch (IOException e) {
                System.out.println("IOExcepion caught, try again...");
                continue;
            } catch (java.lang.NumberFormatException e) {
                System.out.println("That's not a number!");
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
            spam_bot.setTimeout(timeout);

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

    @Override
    public void setTimeout(int timeout_ms) {
        this.timeout_ms = timeout_ms;
    }
}
