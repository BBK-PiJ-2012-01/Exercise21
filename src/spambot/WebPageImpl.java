package spambot;

import java.io.*;
import java.util.Set;
import java.util.HashSet;
import java.net.URL;
import java.net.MalformedURLException;

public class WebPageImpl implements WebPage {

	private String urlString;
	private URL url;
	private Set<String> links;
	private Set<String> emails;
	
	public WebPageImpl(String str) throws MalformedURLException {
		urlString = str;
		System.out.println(urlString);
		url = new URL(str);
		links = new HashSet();
		emails = new HashSet();
		getContents();
	}
	
	
	
	/**
	* Reads in the webpage...
	**/
	private void getContents() {
		InputStream inputStream = null;
		DataInputStream dataInputStream;
		String line;
		try {
			inputStream = url.openStream();
			dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
			while ((line = dataInputStream.readLine()) != null) {
				analyse(line);
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

	private void analyse (String line) {
		for (int i = 0; i<line.length()-1; i++) {
			if (line.charAt(i) == '@') {
				System.out.println("FOUND AN AT");
			}
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

    /**
     * Returns the URL that identifies this web page. * @return the URL that identifies this web page.
     */
    public String getUrl() {
		return urlString;
	}

    /**
     * Returns all the links on this webpage. *
     * Implementing classes should return a read-only view of this
     * set, using Collections.unmodifiableSet(). *
     *
     * @return all the links on this webpage.
     */
    public Set<String> getLinks() {
		return links;
	}

    /**
     * Returns all the emails on this webpage. *
     * Implementing classes should return a read-only view of this
     * set, using Collections.unmodifiableSet(). *
     *
     * @return all the emails on this webpage.
     */
    public Set<String> getEmails() {
		return emails;
	}

// Also, implementing classes should override equals() to // ensure that p1.equals(p2)
// returns true if and only if // p1.getUrl().equals(p2.getUrl()) returns true

	public static void main (String[] args) throws MalformedURLException {
		WebPageImpl w = new WebPageImpl("http://www.bbk.ac.uk/");
		w.launch();
	}
	public void launch() {
		getContents();
		for (String next : links) {
			System.out.println(next);
		}
	}
}