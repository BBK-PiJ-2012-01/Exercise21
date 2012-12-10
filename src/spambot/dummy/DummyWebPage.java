package spambot.dummy;

import spambot.WebPage;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: eatmuchpie
 * Date: 10/12/2012
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
class DummyWebPage implements WebPage {
    private final String url;
    private Set<String> links = new HashSet<String>(Arrays.asList(new String[]{"w1", "w2", "w3"}));
    private Set<String> emails = new HashSet<String>(Arrays.asList(new String[]{"e1", "e2", "e3"}));

    public DummyWebPage(String url) {
        this.url = url;

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
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


