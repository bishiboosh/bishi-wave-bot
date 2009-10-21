package eu.sweetlygeek.parsers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.wave.api.Blip;
import com.google.wave.api.Wavelet;

import eu.sweetlygeek.handlers.ImageUrlHandler;

/** Tumblr blip parser
 * @author bishiboosh
 *
 */
public class TumblrGetter extends BlipParser {

	private static final Logger LOGGER = Logger.getLogger(TumblrGetter.class);

	public static final String TUMBLR_TAG = "tumblr:";

//	public static final String TUMBLR_DOMAINE = "";
//	public static final String TUMBLR_URL = "http://" + TUMBLR_DOMAINE + ".tumblr.com/api/read";

	private static TumblrGetter instance;
	private URLFetchService fetcher;
	private SAXParser parser;

	private TumblrGetter() throws SAXException, ParserConfigurationException
	{
		this.fetcher = URLFetchServiceFactory.getURLFetchService();
		SAXParserFactory spf = SAXParserFactory.newInstance();
		this.parser = spf.newSAXParser();
	}

	public static TumblrGetter getInstance() throws ParserConfigurationException, SAXException
	{
		if (instance == null)
		{
			instance = new TumblrGetter();
		}
		return instance;
	}

	@Override
	public void analyzeBlip(Blip blip, Wavelet currentWavelet) {
		analyzeBlip(blip, currentWavelet, TUMBLR_TAG);
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
				URL url = new URL("http://" + params[1] + ".tumblr.com/api/read");
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
		StringBuffer xmlBuf = new StringBuffer();
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content)));
		String line = br.readLine();
		while (line != null)
		{
			xmlBuf.append(line);
			line = br.readLine();
		}
		// On enlève les & qui trainent
		String cleanXML = StringUtils.replace(xmlBuf.toString(), "&", "&amp;");
		
		ImageUrlHandler handler = new ImageUrlHandler();
		InputSource is = new InputSource(new StringReader(cleanXML));
		this.parser.parse(is, handler);
		
		Blip blip = wavelet.appendBlip();
		Map<String, String> bigMap = handler.getBigMap();
		Map<String, String> miniMap = handler.getMiniMap();
		for (String dropId : bigMap.keySet())
		{
			addImage(blip, miniMap.get(dropId), bigMap.get(dropId));
		}
	}
}