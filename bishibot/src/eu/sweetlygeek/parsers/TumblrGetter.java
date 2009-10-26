package eu.sweetlygeek.parsers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;
import com.google.wave.api.Blip;
import com.google.wave.api.Wavelet;

import eu.sweetlygeek.utils.Utils;

/** Parser for Romu tumblr
 * @author bishiboosh
 *
 */
public class TumblrGetter extends BlipParser {
	
	private static final Logger LOGGER = Logger.getLogger(TumblrGetter.class);
	
	public static final String TUMBLR_TAG = "tumblr:";
	
	private static final String TUMBLR_URL = "http://romv.tumblr.com/api/read/json";
	
	private URLFetchService fetcher;
	private URLCodec codec;
	
	public TumblrGetter() throws ParserConfigurationException, SAXException
	{
		this.fetcher = URLFetchServiceFactory.getURLFetchService();
		this.codec = new URLCodec();
	}
	
	/* (non-Javadoc)
	 * @see eu.sweetlygeek.parsers.BlipParser#analyzeRequest(java.lang.String, com.google.wave.api.Wavelet)
	 */
	@Override
	protected void analyzeRequest(String request, Wavelet currentWavelet) {
		String[] params = request.split(":");
		if (params.length >= 2)
		{
			boolean hasNumber = true;
			int results = -1;
			try {
				results = Math.abs(Integer.parseInt(params[1]));
			} catch (NumberFormatException e) {
				results = 10;
				hasNumber = false;
			}
			String tag = null;
			if (params.length >= 3)
			{
				tag = params[2];
			}
			else if (!hasNumber)
			{
				tag = params[1];
			}
			
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("num", "50");
			paramMap.put("type", "photo");
			if (tag != null)
			{
				paramMap.put("search", tag);
			}
			try {
				URL url = getURLWithParams(paramMap);
				HTTPResponse res = this.fetcher.fetch(url);
				// On cherche sur les 100 premiers donc on refait deux fois
				// la requete
				paramMap.put("start", "50");
				URL url2 = getURLWithParams(paramMap);
				HTTPResponse res2 = this.fetcher.fetch(url2);
				if (res != null && res2 != null)
				{
					parseResponse(res.getContent(), res2.getContent(), results, currentWavelet);
				}
			} catch (MalformedURLException e) {
				LOGGER.error("Error while analyzing request", e);
			} catch (IOException e) {
				LOGGER.error("Error while analyzing request", e);
			} catch (JSONException e) {
				LOGGER.error("Error while analyzing request", e);
			}
		}
	}
	
	private void parseResponse(byte[] content1, byte[] content2, int results, Wavelet wavelet) throws IOException, JSONException {
		Map<String, String> bigMap = new HashMap<String, String>();
		Map<String, String> miniMap = new HashMap<String, String>();
		
		parseContent(content1, bigMap, miniMap);
		parseContent(content2, bigMap, miniMap);
		
		bigMap = Utils.pickAtRandom(bigMap, results);
		miniMap = Utils.copyMapsFromKey(miniMap, bigMap.keySet());
		
		Blip blip = wavelet.appendBlip();
		for (String key : bigMap.keySet())
		{
			addImage(blip, miniMap.get(key), bigMap.get(key));
		}
	}

	private void parseContent(byte[] content, Map<String, String> bigMap,
			Map<String, String> miniMap) throws IOException, JSONException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(content)));
		String result = br.readLine();
		if (result != null)
		{
			int d = result.indexOf('{');
			int f = result.lastIndexOf('}') + 1;
			String sJson = result.substring(d, f);
			JSONObject tumblr = new JSONObject(sJson);
			JSONArray posts = tumblr.getJSONArray("posts");
			for (int i = 0; i < posts.length(); i++)
			{
				JSONObject post = posts.getJSONObject(i);
				parsePost(post, bigMap, miniMap);
			}
		}
	}

	private void parsePost(JSONObject post, Map<String, String> bigMap,
			Map<String, String> miniMap) throws JSONException {
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		String id = post.getString("id");
		Iterator<String> keys = post.keys();
		while (keys.hasNext())
		{
			String key = keys.next();
			if (StringUtils.contains(key, "photo-url"))
			{
				int size = Integer.parseInt(key.split("-")[2]);
				if (size < min)
				{
					miniMap.put(id, post.getString(key));
					min = size;
				}
				if (size > max)
				{
					bigMap.put(id, post.getString(key));
					max = size;
				}
			}
		}
	}

	private URL getURLWithParams(Map<String, String> params) throws MalformedURLException
	{
		StringBuffer buf = new StringBuffer(TUMBLR_URL);
		if (params.size() != 0)
		{
			buf.append('?');
		}
		try {
			Iterator<Map.Entry<String, String>> eIt = params.entrySet().iterator();
			while (eIt.hasNext())
			{
				Map.Entry<String, String> e = eIt.next();
				buf.append(codec.encode(e.getKey()) + "=" + codec.encode(e.getValue()));
				if (eIt.hasNext())
				{
					buf.append("&");
				}
			}
		} catch (EncoderException e) {
			LOGGER.error("Error while building url", e);
		}
		return new URL(buf.toString());
	}

	@Override
	protected String getTag() {
		return TUMBLR_TAG;
	}

}
