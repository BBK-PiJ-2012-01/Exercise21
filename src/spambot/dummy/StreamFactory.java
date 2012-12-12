/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package spambot.dummy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import spambot.Crawler;
import spambot.CrawlerImpl;
import spambot.SpamBot;

/**
 * A Factory class that creates Stream objects.
 *
 * Factory class for creating Streams.
 * Used to specify whether the WebPageImpl uses fake data
 * or a real stream (ie. for testing purposes).
 */
public class StreamFactory {
    private boolean test = false;
    public static final String STARTING_URL = "http://www.one.com";

    public static StreamFactory getReal() {
        return new StreamFactory(false);
    }

    public static StreamFactory getTest() {
        return new StreamFactory(true);
    }


    private StreamFactory(boolean test) {
        this.test = test;
    }

    public InputStream create(URL url) throws IOException {
        if (test) {
            if (url.getPath().equals(STARTING_URL)) {
                return getStream("<html><a href=www.google.com></a></html>");
            } else if (url.getPath().equals("http://www.two.com")) {
                return getStream("<html><A HREF=\"mailto:test@test.com\">");
            } else if (url.getPath().equals(
                        "<!DOCTYPE html PUB...>www.google.co.uk"));
                return getStream("something_long<A HREF=\"www.google.com\">");
        } else {
            return url.openStream();
        }
    }
    private InputStream getStream(String str) {
        return new ByteArrayInputStream(str.getBytes());
    }
}