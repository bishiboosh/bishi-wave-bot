package eu.sweetlygeek.parsers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.wave.api.Wavelet;

import eu.sweetlygeek.handlers.DropularHandler;

/** Dropular blip parser
 * @author bishiboosh
 *
 */
public class DropularGetter extends BlipParser {

	private static final Logger LOGGER = Logger.getLogger(DropularGetter.class);

	public static final String DROPULAR_TAG = "dropular:";

	public static final String DROPULAR_URL = "http://dropular.net/api/";

	private static DropularGetter instance;
	private URLFetchService fetcher;
	private SAXParser parser;

	private DropularGetter() throws SAXException, ParserConfigurationException
	{
		this.fetcher = URLFetchServiceFactory.getURLFetchService();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		this.parser = spf.newSAXParser();
	}

	public synchronized static DropularGetter getInstance() throws ParserConfigurationException, SAXException
	{
		if (instance == null)
		{
			instance = new DropularGetter();
		}
		return instance;
	}

	protected void analyzeRequest(String request, Wavelet currentWavelet)
	{
		try {
			String[] params = request.split(":");
			if (params.length >= 2)
			{
				// Par default : pool
				int results = 10;
				try {
					results = params.length >= 3 ? Integer.parseInt(params[2]) : results;
				} catch (NumberFormatException e) {
					// Nothing
				}
				URL url = new URL(DROPULAR_URL + "get_pool/" + params[1] + "/random/" + results + ".xml");
				HTTPResponse res = this.fetcher.fetch(url);
				if (res != null)
				{
					parseResponse(res.getContent(), currentWavelet);
				}
			}
		} catch (MalformedURLException e) {
			LOGGER.error("Error while analyzing request", e);
		} catch (IOException e) {
			LOGGER.error("Error while analyzing request", e);
		} catch (SAXException e) {
			LOGGER.error("Error while analyzing request", e);
		}
	}

	private void parseResponse(byte[] content, Wavelet wavelet) throws SAXException, IOException {
		DropularHandler handler = new DropularHandler();
		parseXMLResponse(content, wavelet, parser, handler);
	}

	@Override
	protected String getTag() {
		return DROPULAR_TAG;
	}
}
