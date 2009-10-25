package eu.sweetlygeek.parsers;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.apache.commons.lang.RandomStringUtils;

import com.google.wave.api.Blip;
import com.google.wave.api.Gadget;
import com.google.wave.api.Wavelet;

import eu.sweetlygeek.persistent.GeneratedResponse;
import eu.sweetlygeek.servlets.RandomFapServlet;
import eu.sweetlygeek.utils.Utils;

public class RandomFap extends BlipParser {

	private static final String FAP_TAG = "bot:fap";

	private static final String GADGET_URL = "http://bishibot.appspot.com/_wave/robot/fap/";
	private static final String URL_NAME = "/_wave/robot/fap/";

	private Query queryUrl;

	public RandomFap()
	{
		PersistenceManager pm = Utils.getInstance().getPersistenceManager();
		queryUrl = pm.newQuery(RandomFapServlet.QUERY_URL);
	}

	@Override
	protected void analyzeRequest(String request, Wavelet currentWavelet) {
		Blip blip = currentWavelet.appendBlip();

		String xmlName = RandomStringUtils.randomAlphanumeric(10);

		while (urlExists(xmlName))
		{
			xmlName = RandomStringUtils.randomAlphanumeric(10);
		}

		String url = GADGET_URL + xmlName + ".xml";

		Gadget g = new Gadget(url);

		blip.getDocument().appendElement(g);
	}

	@Override
	protected String getTag() {
		return FAP_TAG;
	}

	@SuppressWarnings("unchecked")
	private boolean urlExists(String xmlName)
	{
		String url = URL_NAME + xmlName + ".xml";
		List<GeneratedResponse> urls = (List<GeneratedResponse>) queryUrl.execute(url);
		return !urls.isEmpty();
	}

}
