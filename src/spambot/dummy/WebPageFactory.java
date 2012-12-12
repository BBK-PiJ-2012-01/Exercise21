package spambot.dummy;

import spambot.WebPage;
import spambot.WebPageImpl;

import java.net.MalformedURLException;

/**
 * A factory class for creating WebPage objects.
 *
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 10/12/2012
 * Time: 15:14
 *
 * This is a factory class for creating WebPage objects.  It can return WebPageImpl
 * or DummyWebPage (for testing) classes.
 */
public class WebPageFactory {
    private boolean test = false;

    public static WebPageFactory getReal() {
        return new WebPageFactory(false);
    }

    public static WebPageFactory getTest() {
        return new WebPageFactory(true);
    }


    private WebPageFactory(boolean test) {
        this.test = test;
    }

    public WebPage create(String url) throws MalformedURLException {
        if (test)
            return new DummyWebPage(url);
        else
            return new WebPageImpl(url);
    }
}
