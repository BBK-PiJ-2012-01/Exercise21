/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spambot.dummy;

import java.io.InputStream;
import spambot.Crawler;
import spambot.CrawlerImpl;
import spambot.SpamBot;

/**
 * A Factory class that creates Crawler objects.
 *
 * Created with IntelliJ IDEA.
 * User: Sam Wright
 * Date: 11/12/2012
 * Time: 15:55
 *
 * Factory class for creating Crawler implementations.  Currently only
 * used to specify whether the CrawlerImpl uses the DummyWebPage or the
 * WebPageImpl class (ie. for testing purposes).
 */
public class StreamFactory {
    private boolean test = false;

    public static StreamFactory getReal() {
        return new StreamFactory(false);
    }

    public static StreamFactory getTest() {
        return new StreamFactory(true);
    }


    private StreamFactory(boolean test) {
        this.test = test;
    }

    public InputStream create(String url) {
        if (test) {
            // TODO: return test input stream
        } else {
            // TODO: return real input stream (from url)
        }
        
        // TODO: delete me
        return null;
    }
}