package spambot.dummy;

import spambot.WebPage;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A dummy WebPage (for testing).
 *
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 10/12/2012
 * Time: 15:22
 *
 * This is used for testing the SpamBotImpl and CrawlerImpl classes.  It emulates
 * some webpages with links and email addresses, where the links lead to other
 * webpages with more email addresses (along with malformed links and links to
 * outside the original website).
 */
public class DummyWebPage implements WebPage {
    public final static Set<String> ALL_LINKS = new HashSet<String>(Arrays.asList(new String[]{"http://correct.com/first", "http://correct.com/second", "http://correct.com/third"}));
    public final static Set<String> ALL_EMAILS = new HashSet<String>(Arrays.asList(new String[]{"first@first.com", "first@second.com", "first@third.com",
            "second@first.com", "second@second.com", "second@third.com", "third@first.com", "third@second.com", "third@third.com",
            "init@first.com", "init@second.com", "init@third.com"}));
    private final String url;
    private Set<String> links = new HashSet<String>(Arrays.asList(new String[]{"http://correct.com/first", "http://empty.com", "malformed"}));
    private Set<String> emails = new HashSet<String>(Arrays.asList(new String[]{"init@first.com", "init@second.com", "init@third.com"}));

    public DummyWebPage(String url) throws MalformedURLException {
        this.url = url;

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (url.equals("http://correct.com/first")) {
            links.add("http://correct.com/second");
            links.add("http://correct.com/third");

            emails.add("first@first.com");
            emails.add("first@second.com");
            emails.add("first@third.com");
        } else if (url.equals("http://correct.com/second")) {
            emails.add("second@first.com");
            emails.add("second@second.com");
            emails.add("second@third.com");
        } else if (url.equals("http://correct.com/third")) {
            links.add("http://not_correct.com");

            emails.add("third@first.com");
            emails.add("third@second.com");
            emails.add("third@third.com");
        } else if (url.equals("http://empty.com")) {
            emails.clear();
        } else if (url.equals("malformed")) {
            throw new MalformedURLException("malformed URL");
        } else if (url.equals("http://not_correct.com")) {
            throw new RuntimeException("Tried to follow a link to an external website (ie. not in the seed website)");
        }
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public Set<String> getLinks() {
        return Collections.unmodifiableSet(links);
    }

    @Override
    public Set<String> getEmails() {
        return Collections.unmodifiableSet(emails);
    }
}


