package eu.sweetlygeek.parsers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.wave.api.Blip;
import com.google.wave.api.Wavelet;

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

	private DropularGetter() throws ParserConfigurationException
	{
		this.fetcher = URLFetchServiceFactory.getURLFetchService();
	}

	public static DropularGetter getInstance() throws ParserConfigurationException
	{
		if (instance == null)
		{
			instance = new DropularGetter();
		}
		return instance;
	}

	@Override
	public void analyzeBlip(Blip blip, Wavelet currentWavelet) {
		String text = blip.getDocument().getText();
		List<String> words = Arrays.asList(StringUtils.split(text));
		List<String> requests = new ArrayList<String>();
		boolean found = false;
		for (String word : words)
		{
			if (StringUtils.contains(word, DROPULAR_TAG))
			{
				found = true;
				requests.add(word);
			}
		}
		if (!found)
		{
			return;
		}
		else
		{
			for (String request : requests)
			{
				analyzeRequest(request, currentWavelet);
			}
		}
	}

	private void analyzeRequest(String request, Wavelet currentWavelet)
	{
		try {
			String[] params = request.split(":");
			if (params.length == 2)
			{
				// Par default : pool
				URL url = new URL(DROPULAR_URL + "get_pool/" + params[1] + "/random/10.xml");
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
		}
		catch (ParserConfigurationException e) {
			LOGGER.error("Error while analyzing request", e);
		} catch (SAXException e) {
			LOGGER.error("Error while analyzing request", e);
		}
		// TODO : autre que keyword
	}

	private void parseResponse(byte[] content, Wavelet wavelet) throws ParserConfigurationException, SAXException, IOException {
		// TODO
	}
}
