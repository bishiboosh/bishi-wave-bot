package eu.sweetlygeek.parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.google.wave.api.Blip;
import com.google.wave.api.Image;
import com.google.wave.api.TextView;
import com.google.wave.api.Wavelet;

import eu.sweetlygeek.http.NoSSLHttpClient;

/** Dropular blip parser
 * @author bishiboosh
 *
 */
public class DropularGetter extends BlipParser {
	
	private static final Logger LOGGER = Logger.getLogger(DropularGetter.class);
	
	public static final String DROPULAR_TAG = "dropular:";

	public static final String DROPULAR_URL = "http://dropular.net/api/";

	private static DropularGetter instance;
	private HttpClient client;
	private SAXReader reader;

	private DropularGetter() throws ParserConfigurationException
	{
		this.client = new NoSSLHttpClient();
		this.reader = new SAXReader();
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
				HttpGet get = new HttpGet(DROPULAR_URL + "get_pool/" + params[1] + "/random/10.xml");
				HttpResponse res = client.execute(get);
				int status = res.getStatusLine().getStatusCode();
				if (status == 200)
				{
					HttpEntity entity = res.getEntity();
					if (entity != null)
					{
						parseResponse(entity, currentWavelet);
					}
				}
			}
		} catch (ClientProtocolException e) {
			LOGGER.error("Error while analyzing request", e);
		} catch (IllegalStateException e) {
			LOGGER.error("Error while analyzing request", e);
		} catch (IOException e) {
			LOGGER.error("Error while analyzing request", e);
		} catch (DocumentException e) {
			LOGGER.error("Error while analyzing request", e);
		}
		// TODO : autre que keyword
	}

	private void parseResponse(HttpEntity entity, Wavelet wavelet) throws IllegalStateException, DocumentException, IOException {
		List<Image> images = new ArrayList<Image>();
		Document doc = this.reader.read(entity.getContent());
		Element root = doc.getRootElement();
		List<Element> drops = root.elements("drop");
		for (Element drop : drops)
		{
			String imgUrl = drop.elementText("image_big");
			if (imgUrl != null && !"".equals(imgUrl))
			{
				Image i = new Image();
				i.setUrl(imgUrl);
				images.add(i);
			}
		}
		if (!images.isEmpty())
		{
			Blip b = wavelet.appendBlip();
			TextView view = b.getDocument();
			for (Image i : images)
			{
				view.appendElement(i);
			}
		}
	}
}
