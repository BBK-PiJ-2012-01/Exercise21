package spambot;

import java.io.*;
import java.util.Set;
import java.net.URL;
import java.net.MalformedURLException;

public class WebPageImpl implements WebPage {

	URL url = null;
	
	public WebPageImpl(String str) {
		try {
			url = new URL("http://"+str);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	* Reads in the webpage...
	**/
	public String getContents() {
		InputStream inputStream = null;
		DataInputStream dataInputStream;
		String line;
		try {
			inputStream = url.openStream();
			dataInputStream = new DataInputStream(new BufferedInputStream(inputStream));
			while ((line = dataInputStream.readLine()) != null) {
				System.out.println(line);
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
		return "";
	}
	public static void main (String[] args) {
		WebPageImpl w = new WebPageImpl("www.dcs.bbk.ac.uk");
		w.launch();
	}
	public void launch() {
		getContents();
	}


    /**
     * Returns the URL that identifies this web page. * @return the URL that identifies this web page.
     */
    public String getUrl() {
		return "some url";
	}

    /**
     * Returns all the links on this webpage. *
     * Implementing classes should return a read-only view of this
     * set, using Collections.unmodifiableSet(). *
     *
     * @return all the links on this webpage.
     */
    public Set<String> getLinks() {
		return null;
	}

    /**
     * Returns all the emails on this webpage. *
     * Implementing classes should return a read-only view of this
     * set, using Collections.unmodifiableSet(). *
     *
     * @return all the emails on this webpage.
     */
    public Set<String> getEmails() {
		return null;
	}

// Also, implementing classes should override equals() to // ensure that p1.equals(p2)
// returns true if and only if // p1.getUrl().equals(p2.getUrl()) returns true
}