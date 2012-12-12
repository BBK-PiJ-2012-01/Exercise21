package spambot;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.net.URLConnection;
import spambot.dummy.StreamFactory;

/**
 * An implementation of WebPage which reads in a webpage (URL string) and picks
 * out links and emails.
 **/
public class WebPageImpl implements WebPage {

	private final String urlString;
	private final URL url;
	private final Set<String> links;
	private final Set<String> emails;
	private final static Pattern EMAIL_REG_EX = Pattern.compile("[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})");
        private final StreamFactory factory;
        
	/**
	* @param str the string of the url for the web page
	**/
	public WebPageImpl(String str, StreamFactory factory) throws MalformedURLException {
		urlString = str;
		url = new URL(str);
		links = new HashSet();
		emails = new HashSet();
		this.factory = factory;
                getContents();
                
	}
	
	
	
	/**
	* Reads in the webpage and calls analyse.
	**/
	private void getContents() {
		InputStream inputStream;
		DataInputStream dataInputStream;
		String line;
		try {
			inputStream = factory.create(url);
		} catch (IOException e) {
			System.out.println("Couldn't access "+urlString);
                        return;
		}
		try {
			dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
			String firstLine = dataInputStream.readLine().toLowerCase();
			if (firstLine.startsWith("<!doctype html")
					|| firstLine.startsWith("<html") ) {
				while ((line = dataInputStream.readLine()) != null) {
					analyse(line);
				}
			}
		} catch (IOException e) {
			 e.printStackTrace();
		} finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	* Populates 'links' and 'emails' with those found in the webpage.
	* @param line the next line read in from the web page
	**/
	private void analyse (String line) {
		Matcher m = EMAIL_REG_EX.matcher(line);
		while (m.find()) {
			emails.add( m.group(0));
		}
		for (int i = 0; i<line.length()-1; i++) {
			if (line.charAt(i) == '<'&&line.charAt(i+1)=='a') {
				try {
					if (line.substring(i).startsWith("<a href=")) {
						String link = line.substring(i+9,line.indexOf('"', i+9));
						if (!link.startsWith("http")) {
							if(urlString.endsWith("/")) {
								links.add(urlString+link);
							} else {
								links.add(urlString+"/"+link);
							}
						} else {
							links.add(link);
						}
					}
				} catch (StringIndexOutOfBoundsException e) {
				}
			}
			
		}
	}

	@Override
    public String getUrl() {
		return urlString;
	}

	@Override
    public Set<String> getLinks() {
		return links;
	}

	@Override
    public Set<String> getEmails() {
		return emails;
	}

	public static void main (String[] args) throws MalformedURLException {
		WebPageImpl w = new WebPageImpl("http://www.google.com");
		w.launch();
	}
	public void launch() {
		getContents();
		for (String next : links) {
			System.out.println(next);
		}
		for (String next : emails) {
			System.out.println(next);
		}
	}
}