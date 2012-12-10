package spambot;

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
 * should at least launch a crawler per processor in your machine2, and possibly
 * more, because I/O waits over the network connection will make your crawlers
 * waste a lot of time.
 */
public interface Crawler {
}
