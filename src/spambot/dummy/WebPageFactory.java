package spambot.dummy;

import spambot.WebPage;

/**
 * Created with IntelliJ IDEA.
 * User: eatmuchpie
 * Date: 10/12/2012
 * Time: 15:14
 * To change this template use File | Settings | File Templates.
 */
public class WebPageFactory {
    boolean test = false;

    private WebPageFactory(boolean test) {
        this.test = test;
    }

    public static WebPageFactory getReal() {
        return new WebPageFactory(false);
    }

    public static WebPageFactory getTest() {
        return new WebPageFactory(true);
    }

    public WebPage create(String url) {
        if (test)
            return new DummyWebPage(url);
        else
            throw new UnsupportedOperationException("Real web page not implemented yet");
    }

}
