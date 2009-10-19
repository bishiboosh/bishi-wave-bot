package eu.sweetlygeek;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.wave.api.Blip;
import com.google.wave.api.Wavelet;

/** Dropular blip parser
 * @author bishiboosh
 *
 */
public class DropularGetter implements BlipParser {
	
	private static final String DROPTACULAR_TAG = "droptacular";

	public static final String DROPULAR_URL = "http://dropular.net/api/";
	
	private static DropularGetter instance;
	private HttpClient client;
	
	private DropularGetter()
	{
		this.client = new DefaultHttpClient();
	}
	
	public static DropularGetter getInstance()
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
			if (StringUtils.contains(word, DROPTACULAR_TAG))
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
				analyzeRequest(request);
			}
		}
	}
	
	private void analyzeRequest(String request)
	{
		String[] params = request.split(":");
		if (params.length == 2)
		{
			// Par défault : keyword
			HttpGet g = new HttpGet(DROPULAR_URL + "get_keyword/" + params[1] + "/latest/10.xml");
			//HttpResponse r = client.execute(g);
		}
	}
}
